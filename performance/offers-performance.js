import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';
import { BASE_URL, THRESHOLDS, authHeaders } from './config.js';

const errorRate = new Rate('error_rate');
const offersDuration = new Trend('offers_duration');

export const options = {
    scenarios: {
        offers_load: {
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
        offers_duration: ['p(95)<500'],
    },
};

export default function () {
    // Scenario 1 — Authenticated offers request
    const offersResponse = http.get(
        `${BASE_URL}/offers`,
        { headers: authHeaders() }
    );

    const offersSuccess = check(offersResponse, {
        'authenticated offers request returns 200': (r) =>
            r.status === 200,
        'response contains offers': (r) => {
            const body = JSON.parse(r.body);
            return body.offers !== undefined && body.offers.length > 0;
        },
        'offers response time under 500ms': (r) =>
            r.timings.duration < 500,
    });

    offersDuration.add(offersResponse.timings.duration);
    errorRate.add(!offersSuccess);

    sleep(0.5);

    // Scenario 2 — Unauthenticated offers request returns 401
    const unauthResponse = http.get(`${BASE_URL}/offers`);

    check(unauthResponse, {
        'unauthenticated offers request returns 401': (r) =>
            r.status === 401,
        'auth error is fast': (r) => r.timings.duration < 500,
    });

    sleep(0.5);
}