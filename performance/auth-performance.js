import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

// Custom metrics
const errorRate = new Rate('error_rate');
const authDuration = new Trend('auth_duration');

// Test configuration
export const options = {
    scenarios: {
        auth_load: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                { duration: '10s', target: 5 },  // Ramp up to 5 users
                { duration: '20s', target: 5 },  // Hold at 5 users
                { duration: '10s', target: 0 },  // Ramp down
            ],
        },
    },
    thresholds: {
        // 95th percentile response time under 500ms
        http_req_duration: ['p(95)<500'],
        // Error rate under 1%
        error_rate: ['rate<0.01'],
        // Custom auth duration metric
        auth_duration: ['p(95)<500'],
    },
};

const BASE_URL = 'http://localhost:8089';

// Valid login payload
const VALID_PAYLOAD = JSON.stringify({
    username: 'standard_user',
    password: 'password123',
});

// Invalid login payload
const INVALID_PAYLOAD = JSON.stringify({
    username: 'invalid_user',
    password: 'wrongpassword',
});

const HEADERS = { 'Content-Type': 'application/json' };

export default function () {
    // Scenario 1 — Valid credentials return 200 with token
    const validResponse = http.post(
        `${BASE_URL}/auth/login`,
        VALID_PAYLOAD,
        { headers: HEADERS }
    );

    const validSuccess = check(validResponse, {
        'valid login returns 200': (r) => r.status === 200,
        'response contains token': (r) => {
            const body = JSON.parse(r.body);
            return body.token !== undefined && body.token.length > 0;
        },
        'response time under 500ms': (r) => r.timings.duration < 500,
    });

    authDuration.add(validResponse.timings.duration);
    errorRate.add(!validSuccess);

    sleep(0.5);

    // Scenario 2 — Invalid credentials return 401
    const invalidResponse = http.post(
        `${BASE_URL}/auth/login`,
        INVALID_PAYLOAD,
        { headers: HEADERS }
    );

    check(invalidResponse, {
        'invalid login returns 401': (r) => r.status === 401,
        'error response is fast': (r) => r.timings.duration < 500,
    });

    sleep(0.5);
}