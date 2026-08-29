import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

const errorRate = new Rate('error_rate');
const customerDuration = new Trend('customer_duration');

export const options = {
    scenarios: {
        customer_load: {
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
        http_req_duration: ['p(95)<500'],
        error_rate: ['rate<0.01'],
        customer_duration: ['p(95)<500'],
    },
};

const BASE_URL = 'http://localhost:8089';

// Mock JWT token matching WireMock stub expectation
const AUTH_TOKEN = 'mock-jwt-token-for-testing';

export default function () {
    // Scenario 1 — Authenticated customer profile retrieval
    const authHeaders = {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${AUTH_TOKEN}`,
    };

    const customerResponse = http.get(
        `${BASE_URL}/customers/C001`,
        { headers: authHeaders }
    );

    const customerSuccess = check(customerResponse, {
        'authenticated customer request returns 200': (r) =>
            r.status === 200,
        'response contains customer data': (r) => {
            const body = JSON.parse(r.body);
            return body.id !== undefined || body.customerId !== undefined;
        },
        'customer response time under 500ms': (r) =>
            r.timings.duration < 500,
    });

    customerDuration.add(customerResponse.timings.duration);
    errorRate.add(!customerSuccess);

    sleep(0.5);

    // Scenario 2 — Unauthenticated request returns 401
    const unauthResponse = http.get(`${BASE_URL}/customers/C001`);

    check(unauthResponse, {
        'unauthenticated request returns 401': (r) => r.status === 401,
        'auth error response is fast': (r) => r.timings.duration < 500,
    });

    sleep(0.5);
}