import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';
import { BASE_URL, USERS, JSON_HEADERS, THRESHOLDS } from './config.js';

const errorRate = new Rate('error_rate');
const authDuration = new Trend('auth_duration');

export const options = {
    scenarios: {
        auth_load: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                { duration: '10s', target: 5 },
                { duration: '20s', target: 5 },
                { duration: '10s', target: 0 },
            ],
        },
    },
    thresholds: {
        ...THRESHOLDS,
        auth_duration: ['p(95)<500'],
    },
};

const VALID_PAYLOAD = JSON.stringify({
    username: USERS.standard.username,
    password: USERS.standard.password,
});

const INVALID_PAYLOAD = JSON.stringify({
    username: USERS.invalid.username,
    password: USERS.invalid.password,
});

export default function () {
    // Scenario 1 — Valid credentials return 200 with token
    const validResponse = http.post(
        `${BASE_URL}/auth/login`,
        VALID_PAYLOAD,
        { headers: JSON_HEADERS }
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
        { headers: JSON_HEADERS }
    );

    check(invalidResponse, {
        'invalid login returns 401': (r) => r.status === 401,
        'error response is fast': (r) => r.timings.duration < 500,
    });

    sleep(0.5);
}