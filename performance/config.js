// performance/config.js
// Shared configuration for all k6 performance scripts
// Single source of truth — mirrors values from config.properties
// Update here to apply changes across all scripts

// WireMock base URL — matches wiremock.host and wiremock.port
// in src/test/resources/config.properties
export const BASE_URL = 'http://localhost:8089';

// Test users — mirrors config.properties test user credentials
export const USERS = {
    standard: {
        username: 'standard_user',
        password: 'password123',
    },
    invalid: {
        username: 'invalid_user',
        password: 'wrongpassword',
    },
    locked: {
        username: 'locked_user',
        password: 'password123',
    },
};

// Mock JWT token — mirrors the token returned by WireMock auth stub
// Used for authenticated API requests
export const AUTH_TOKEN = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.mocktoken';

// Test data IDs — mirrors DynamoDB seed data IDs
export const TEST_IDS = {
    customer: 'C001',
    product: 'P001',
};

// Performance thresholds — applied consistently across all scripts
// p95 response time under 500ms, error rate under 1%
export const THRESHOLDS = {
    http_req_duration: ['p(95)<500'],
    error_rate: ['rate<0.01'],
};

// Common HTTP headers
export const JSON_HEADERS = { 'Content-Type': 'application/json' };

export function authHeaders() {
    return {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${AUTH_TOKEN}`,
    };
}