import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

const errorRate = new Rate('error_rate');
const productsDuration = new Trend('products_duration');

export const options = {
    scenarios: {
        products_load: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                { duration: '10s', target: 10 },
                { duration: '20s', target: 10 },
                { duration: '10s', target: 0 },
            ],
        },
    },
    thresholds: {
        http_req_duration: ['p(95)<500'],
        error_rate: ['rate<0.01'],
        products_duration: ['p(95)<500'],
    },
};

const BASE_URL = 'http://localhost:8089';

export default function () {
    // Scenario 1 — Get all products
    const allProductsResponse = http.get(`${BASE_URL}/products`);

    const allProductsSuccess = check(allProductsResponse, {
        'get all products returns 200': (r) => r.status === 200,
        'response contains products array': (r) => {
            const body = JSON.parse(r.body);
            return body.products !== undefined && body.products.length > 0;
        },
        'response time under 500ms': (r) => r.timings.duration < 500,
    });

    productsDuration.add(allProductsResponse.timings.duration);
    errorRate.add(!allProductsSuccess);

    sleep(0.5);

    // Scenario 2 — Get single product by ID
    const singleProductResponse = http.get(`${BASE_URL}/products/P001`);

    check(singleProductResponse, {
        'get product by id returns 200': (r) => r.status === 200,
        'single product has name field': (r) => {
            const body = JSON.parse(r.body);
            return body.name !== undefined;
        },
        'single product response time under 500ms': (r) =>
            r.timings.duration < 500,
    });

    sleep(0.5);
}