# Bank Portal Automation Framework

A Java-based test automation framework built for a Tier-1 UK retail banking
marketing and digital services portal. Designed to validate both UI functionality
and backend microservices using industry-standard tooling and enterprise-grade
design patterns.

---

## Business Context

The NorthBank Marketing & Services portal serves approximately 10 million daily
users and acts as the customer's primary digital touchpoint. It allows customers
to view financial products, register interest, access authenticated service
features, and retrieve personalised offers via backend microservices.

This framework validates:
- **UI layer** — customer journeys across the marketing portal
- **API layer** — microservice contract integrity across Auth, Product,
  Customer Profile, and Offers services

---

## Technology Stack

| Area | Technology | Version |
|---|---|---|
| Language | Java | 17 |
| Build Tool | Maven | 3.9+ |
| UI Automation | Selenium WebDriver | 4.18.1 |
| API Automation | REST Assured | 5.4.0 |
| BDD Framework | Cucumber | 7.15.0 |
| Test Runner | TestNG | 7.9.0 |
| API Mocking | WireMock | 3.4.2 |
| Mock Site | Spring Boot + Thymeleaf | 3.2.3 |
| Reporting | Allure + Cucumber HTML | 2.25.0 |
| Logging | SLF4J + Logback | 2.0.12 |
| Security Scan | OWASP Dependency Check | 9.0.10 |
| Performance Testing | k6 | 2.2.0 |
| Accessibility Testing | axe-core (axe-selenium-java) | 4.9.1 |
| CI/CD | GitHub Actions | - |

---

## Architecture
```
Browser (Selenium)
│
▼
Spring Boot Mock Site (localhost:8080)
│
▼
API Gateway (WireMock localhost:8089)
├── Auth Service          POST /auth/login
├── Product Service       GET  /products, /products/{id}
├── Customer Profile      GET  /customers/{id}
└── Offers Service        GET  /offers
```
---

## Framework Structure

```
bank-portal-automation/
├── mock-bank-site/                    ← Spring Boot mock portal
│   └── src/main/
│       ├── java/com/mockbank/         ← Controller and entry point
│       └── resources/templates/       ← Thymeleaf HTML pages (WCAG 2.1 compliant)
│
├── performance/                       ← k6 performance test scripts
│   ├── config.js                      ← Shared config (base URL, users, thresholds)
│   ├── auth-performance.js            ← Auth endpoint load test
│   ├── products-performance.js        ← Products endpoint load test
│   ├── customer-performance.js        ← Customer endpoint load test
│   └── offers-performance.js          ← Offers endpoint load test
│
├── src/test/
│   ├── java/com/bankportal/
│   │   ├── config/                    ← ConfigManager (environment config)
│   │   ├── utils/                     ← DriverManager, WireMock, Screenshot,
│   │   │                                 TestListener, AxeHelper
│   │   ├── ui/
│   │   │   ├── pages/                 ← Page Object Model classes
│   │   │   ├── steps/                 ← Cucumber UI and accessibility step definitions
│   │   │   └── runners/               ← UI, accessibility test runners
│   │   └── api/
│   │       ├── clients/               ← REST Assured service clients
│   │       ├── models/                ← POJO request/response models
│   │       ├── steps/                 ← Cucumber API step definitions
│   │       └── runners/               ← API test runner
│   │
│   └── resources/
│       ├── features/
│       │   ├── ui/                    ← UI Gherkin feature files
│       │   ├── api/                   ← API Gherkin feature files
│       │   └── accessibility/         ← WCAG 2.1 accessibility feature files
│       ├── wiremock/mappings/         ← WireMock stub definitions
│       ├── config.properties          ← Environment configuration
│       ├── logback-test.xml           ← Logging configuration
│       └── testng.xml                 ← TestNG suite definition
│
└── .github/workflows/ci.yml           ← GitHub Actions pipeline
```
---

## Design Patterns

| Pattern | Where Used | Purpose |
|---|---|---|
| Page Object Model | `ui/pages/` | Encapsulate UI locators and interactions |
| Service Client Pattern | `api/clients/` | Abstract REST Assured calls behind typed clients |
| Singleton (Holder) | `ConfigManager` | Single config instance, thread-safe lazy init |
| ThreadLocal WebDriver | `DriverManager` | Parallel-safe browser instance per thread |
| Test Data Builder | WireMock stubs | Deterministic, isolated API responses |
| BDD Layering | Feature files + Steps | Business-readable tests with technical encapsulation |

---

## Framework Resilience

### Flaky Test Handling

The framework includes a `RetryAnalyzer` wired globally via
`IAnnotationTransformer`. Failed tests are automatically retried up to
two times before being marked as failed. Every retry attempt is logged
as a WARNING so flaky tests are visible in reports rather than silently
passing.

The retry limit is intentionally low — the goal is a safety net, not a
workaround. Tests that consistently require retrying are candidates for
investigation and are tracked via the WARNING log entries in
`target/logs/test-run.log`.

### Performance Smoke Testing

A parameterised response time assertion step is available across all
API scenarios:

```gherkin
And the response time is under 500 milliseconds
```

The millisecond threshold is configurable per scenario, allowing
different SLA targets for different services. This provides a
lightweight performance regression gate on every pipeline run —
catching N+1 queries or synchronous dependency calls before they reach
production.

This is distinct from load testing — it validates single-user SLA
compliance rather than concurrent throughput. For concurrent throughput
testing, four k6 scripts are provided in the `performance/` folder
covering all API endpoints with a ramping virtual user pattern and
p95 response time thresholds. See the Performance Testing section for
details.

---

## Test Coverage

### API Scenarios (8 total)

| Feature | Scenario | Tag |
|---|---|---|
| Auth API | Valid credentials return token | @smoke |
| Auth API | Invalid credentials return 401 | @smoke |
| Offers API | Authenticated request returns offers | @regression |
| Offers API | Unauthenticated request rejected | @regression |
| Product API | Retrieve all products returns 200 | @smoke |
| Product API | Active/inactive product counts correct | @smoke |
| Product API | Retrieve product by ID | @smoke |
| Product API | API responds within 500ms threshold | @performance |

### UI Scenarios (11 total)

| Feature | Scenario | Tag |
|---|---|---|
| Homepage | Bank branding and hero content displayed | @smoke |
| Homepage | CTA navigates to products page | @smoke |
| Homepage | Nav link navigates to login page | @smoke |
| Products | All products displayed with correct count | @regression |
| Products | Active credit card product visible | @regression |
| Products | Inactive product clearly labelled | @regression |
| Login | Valid customer logs in successfully | @regression |
| Login | Invalid credentials show error message | @regression |
| Login | Locked account shows appropriate message | @regression |
| Dashboard | Personalised offers displayed | @regression |
| Dashboard | Correct customer segment displayed | @regression |

### Accessibility Scenarios (4 total)

| Feature | Scenario | Tag |
|---|---|---|
| Accessibility | Homepage meets WCAG 2.1 standards | @smoke |
| Accessibility | Products page meets WCAG 2.1 standards | @smoke |
| Accessibility | Login page meets WCAG 2.1 standards | @smoke |
| Accessibility | Dashboard page meets WCAG 2.1 standards | @smoke |

### Performance Scripts (4 total — k6, run separately)

| Script | Scenarios | Threshold |
|---|---|---|
| auth-performance.js | Valid login → 200 + token, Invalid → 401 | p95 < 500ms |
| products-performance.js | All products, single product by ID | p95 < 500ms |
| customer-performance.js | Authenticated → 200, Unauthenticated → 401 | p95 < 500ms |
| offers-performance.js | Authenticated → 200, Unauthenticated → 401 | p95 < 500ms |

**Total automated test count: 23 Java scenarios + 4 k6 performance scripts**

---

## How to Run

### Prerequisites

- Java 17+
- Maven 3.9+
- Chrome browser (for local UI and accessibility tests)
- Spring Boot mock site running (for UI and accessibility tests)
- k6 installed (for performance scripts only)

### Start the Mock Bank Site

```bash
cd mock-bank-site
mvn spring-boot:run
```

Wait for:

Started MockBankApplication in X.XXX seconds


The site will be available at `http://localhost:8080`

### Run API Tests Only

```bash
mvn test -Dtest=ApiTestRunner
```

No browser or mock site required — WireMock handles all API mocking automatically.

### Run UI Tests Only

```bash
mvn test -Dtest=UiTestRunner
```

Runs with visible Chrome by default. Mock site must be running first.

### Run UI Tests in Headless Mode

```bash
mvn test -Dtest=UiTestRunner -Dheadless=true
```

### Run Accessibility Tests Only

```bash
mvn test -Dtest=AccessibilityTestRunner
```

Mock site must be running first. Runs with visible Chrome by default.
axe-core scans all four pages for WCAG 2.1 AA violations.

### Run Accessibility Tests in Headless Mode

```bash
mvn test -Dtest=AccessibilityTestRunner -Dheadless=true
```

### Run Full Java Suite

```bash
mvn test
```

Runs API, UI and accessibility suites in parallel. Mock site must be
running for UI and accessibility tests.

### Run by Tag

```bash
# Smoke tests only (both API and UI runners)
mvn test -Dcucumber.filter.tags="@smoke"

# Regression tests only
mvn test -Dcucumber.filter.tags="@regression"

# Performance assertion tests only (single-user response time)
mvn test -Dtest=ApiTestRunner -Dcucumber.filter.tags="@performance"

# Accessibility tests only
mvn test -Dcucumber.filter.tags="@accessibility"
```

### Run k6 Performance Scripts

WireMock must be running standalone before executing k6 scripts.
Start WireMock from the project root:

```bash
java -jar wiremock-standalone.jar --port 8089 \
  --root-dir src/test/resources/wiremock
```

Then run individual scripts:

```bash
k6 run performance/auth-performance.js
k6 run performance/products-performance.js
k6 run performance/customer-performance.js
k6 run performance/offers-performance.js
```

Note: Stop the WireMock standalone JAR before running the Java test
suite — both use port 8089 and will conflict if running simultaneously.

### OWASP Dependency Check

```bash
mvn dependency-check:check -DnvdApiKey="your-nvd-api-key"
```

Reports generated at:
`target/dependency-check-report/dependency-check-report.html`

---
## Test Reports

After any test run, reports are available at:

| Report | Location |
|---|---|
| Cucumber HTML (API) | `target/cucumber-reports/api-report.html` |
| Cucumber HTML (UI) | `target/cucumber-reports/ui-report.html` |
| Cucumber HTML (Accessibility) | `target/cucumber-reports/accessibility-report.html` |
| TestNG Results | `target/surefire-reports/index.html` |
| Test Logs | `target/logs/test-run.log` |
| Screenshots (on failure) | `target/screenshots/` |

---

## CI/CD Pipeline

The GitHub Actions pipeline runs automatically on every push to `main`
and on all pull requests.

### Pipeline Stages

| Step | Job | Depends On | Notes |
|---|---|---|---|
| 1 | Build & Compile | — | Compiles all source and test classes |
| 2a | API Tests (WireMock) | Build | REST Assured scenarios, no browser needed |
| 2b | OWASP Dependency Check | Build | Runs in parallel with API Tests, continue-on-error |
| 3 | UI Tests (Headless Chrome) | API Tests | Selenium scenarios, Spring Boot portal started |
| 4 | Accessibility Tests (axe-core) | UI Tests | WCAG 2.1 scanning across four pages |
| 5 | Quality Gate | API, UI, Accessibility | Fails pipeline if any test job failed |

Steps 2a and 2b run in parallel after Build completes. All subsequent
steps run sequentially — each requires the previous to pass before
starting.

### Job Summary

| Job | Depends On | Purpose |
|---|---|---|
| Build & Compile | — | Compiles all source and test classes |
| OWASP Dependency Check | Build | CVE scan of third-party dependencies |
| API Tests | Build | REST Assured scenarios via WireMock |
| UI Tests | API Tests | Selenium scenarios against headless Chrome |
| Accessibility Tests | UI Tests | axe-core WCAG 2.1 scanning across four pages |
| Quality Gate | API, UI, Accessibility | Fails pipeline if any test job failed |

### Artifacts Uploaded Per Run

| Artifact | Contents | Retention |
|---|---|---|
| `api-test-reports` | Cucumber HTML and JSON, Surefire reports | 30 days |
| `ui-test-reports` | Cucumber HTML and JSON, screenshots, logs | 30 days |
| `accessibility-test-reports` | Cucumber HTML and JSON, Surefire reports | 30 days |
| `owasp-dependency-report` | CVE scan HTML report | 30 days |

### Note on k6 Performance Scripts

k6 scripts are not currently part of the CI pipeline — they run
manually against a local WireMock instance. Adding k6 as a dedicated
pipeline job is listed in Future Improvements. The existing
`@performance` tagged API scenario provides a lightweight single-user
response time gate on every pipeline run in the interim.

---


**API tests run before UI tests** — they complete in under 10 seconds and act
as a fast feedback gate. If API tests fail, UI tests do not run.

### Environment Variables / Secrets

| Variable | Where | Purpose |
|---|---|---|
| `NVD_API_KEY` | GitHub Secrets | OWASP NVD database access |
| `headless` | Maven system property | Override headless mode in CI |

---

## Assumptions and Trade-offs

### Assumptions

- API contracts are backward compatible throughout the test lifecycle
- UI page structure on the mock site changes infrequently
- Chrome is the canonical browser target — cross-browser matrix is out of scope
- Load and performance testing is handled by a dedicated performance team
- Security penetration testing is out of scope for this framework

### Trade-offs

| Decision | Trade-off |
|---|---|
| WireMock over live services | Deterministic and pipeline-friendly; doesn't test real service behaviour |
| Spring Boot mock site over public demo site | Full locator control; requires maintaining a separate application |
| TestNG over JUnit | Better parallel execution support; less familiar to some teams |
| Cucumber BDD over pure TestNG | Business readability; adds abstraction layer that can slow step writing |
| data-test attributes | Requires development team buy-in to maintain attributes on elements |

---

## Security

This framework includes OWASP Dependency Check to audit third-party dependencies
against the National Vulnerability Database. The build is configured to flag any
dependency with a CVSS score of 7.0 (High) or above.

No credentials, API keys, or sensitive data are stored in source code.
All secrets are managed via GitHub Actions Secrets or environment variables.

---

## Performance Testing

k6 performance scripts validate API endpoint response times under load.
Scripts run against WireMock stubs on port 8089, targeting the same
four endpoints covered by the REST Assured API test suite.

### Running the Scripts

Start WireMock standalone before running k6:

```bash
java -jar wiremock-standalone.jar --port 8089 \
  --root-dir src/test/resources/wiremock
```

Run individual scripts:

```bash
k6 run performance/auth-performance.js
k6 run performance/products-performance.js
k6 run performance/customer-performance.js
k6 run performance/offers-performance.js
```

### Load Pattern

All scripts use a ramping virtual user pattern across three stages:

| Stage | Duration | Virtual Users |
|---|---|---|
| Ramp up | 10s | 0 → 5 (or 10 for products) |
| Steady state | 20s | Hold at peak |
| Ramp down | 10s | Peak → 0 |

### Thresholds

All scripts share common thresholds defined in `performance/config.js`:

| Threshold | Value | Meaning |
|---|---|---|
| `http_req_duration p(95)` | < 500ms | 95% of requests complete under 500ms |
| `error_rate` | < 1% | Less than 1% of primary checks fail |

### Shared Configuration

All scripts import from `performance/config.js` — a single source of truth
mirroring the values in `config.properties`:

- `BASE_URL` — WireMock base URL
- `USERS` — test user credentials
- `AUTH_TOKEN` — mock JWT token
- `TEST_IDS` — customer and product IDs
- `THRESHOLDS` — shared threshold definitions
- `authHeaders()` — authenticated request header builder

### Coverage

| Script | Scenarios | p95 Result |
|---|---|---|
| auth-performance.js | Valid login → 200 + token, Invalid → 401 | 5ms |
| products-performance.js | All products, single product by ID | 3.5ms |
| customer-performance.js | Authenticated → 200, Unauthenticated → 401 | 4ms |
| offers-performance.js | Authenticated → 200, Unauthenticated → 401 | 2.8ms |

### Tool Choice — k6 over Gatling

k6 was chosen over Gatling for three reasons. First, it appears
consistently in current job specifications. Second, its built-in
threshold syntax (`p(95)<500`) integrates naturally as a CI pipeline
gate — k6 exits with a non-zero code on threshold breach. Third,
JavaScript scripts are more universally readable in a code review
context than Gatling's Scala DSL. The underlying performance testing
concepts — virtual users, ramp-up patterns, percentile thresholds —
transfer directly between k6, Gatling, and JMeter.

---

## Accessibility Testing

axe-core WCAG 2.1 accessibility scanning is integrated into the test
suite via the `axe-selenium-java` library. The axe engine is injected
into the Selenium browser session and analyses the DOM against WCAG
2.1 AA success criteria.

### Coverage

Four pages scanned — one scenario per page:

| Page | URL | Result |
|---|---|---|
| Homepage | `/` | ✅ No violations |
| Products | `/products` | ✅ No violations |
| Login | `/login` | ✅ No violations |
| Dashboard | `/dashboard` | ✅ No violations |

### Violation Severity

axe-core classifies violations by impact level:

| Impact | Meaning | Test behaviour |
|---|---|---|
| Critical | Complete barrier for some users | Fails test |
| Serious | Significant barrier for some users | Fails test |
| Moderate | Some difficulty for some users | Logged only |
| Minor | Minor inconvenience | Logged only |

### Violations Fixed During Implementation

Running axe-core identified real WCAG 2.1 violations in the mock site
templates, which were fixed as part of this work:

| Violation | Rule | Fix Applied |
|---|---|---|
| Missing lang attribute | `html-has-lang` | Added `lang="en"` to all four HTML templates |
| Hero text contrast | `color-contrast` | `#555` → `#454545` (ratio 3.5 → 7.0) |
| CTA button contrast | `color-contrast` | `#e63946` → `#c1121f` (ratio 4.16 → 5.9) |
| Login hint contrast | `color-contrast` | `#888` → `#595959` (ratio 3.5 → 7.0) |
| Product text contrast | `color-contrast` | `#555` → `#454545` (ratio 3.5 → 7.0) |
| Offer text contrast | `color-contrast` | `#555` → `#454545` (ratio 3.5 → 7.0) |

### Running Accessibility Tests

```bash
mvn test "-Dcucumber.filter.tags=@accessibility"
```

### Limitations

axe-core automated scanning catches approximately 30-40% of WCAG 2.1
issues. The following require manual testing with assistive technology:

- **Screen reader compatibility** — VoiceOver (macOS/iOS), NVDA or
  JAWS (Windows) for reading order, dynamic content announcements,
  and meaningful alternative text validation
- **Keyboard navigation** — tab order, focus management, keyboard traps
- **Cognitive accessibility** — plain language, consistent navigation,
  error prevention (WCAG Guideline 3)

In a production context, axe-core in CI would be complemented by
periodic manual testing with a screen reader and keyboard-only
navigation review at key release milestones.

### Tool Choice — axe-core over WAVE and VoiceOver

axe-core was chosen for CI integration because it is designed for
programmatic use — it returns structured results, maps violations
directly to WCAG 2.1 success criteria, and has a low false positive
rate suited to automated gating. WAVE provides visual in-browser
feedback useful for developers during active development but has no
practical CI integration. VoiceOver is a screen reader for manual
assistive technology testing — a different category entirely, covering
the manual layer that automated tools cannot replace.


---

## Future Improvements

Given more time or a production context, the following would be
prioritised:

- **Contract testing** — Pact framework to validate consumer-provider
  API contracts, ensuring provider deployments cannot break consumer
  expectations
- **Parallel execution** — TestNG parallel suite configuration with
  thread count tuning, leveraging the existing ThreadLocal WebDriver
  architecture
- **Cross-browser support** — Firefox and Edge runners added to the
  CI pipeline alongside the existing headless Chrome configuration
- **OWASP ZAP integration** — DAST scanning as a separate pipeline
  stage against a deployed environment, covering the OWASP Top 10
  vulnerabilities
- **Allure TestOps** — centralised test history, trend analysis, and
  flaky test detection across pipeline runs
- **Visual regression** — Percy or Applitools for screenshot comparison
  testing to catch unintended UI changes alongside functional assertions
- **k6 CI integration** — k6 performance scripts added as a dedicated
  pipeline job running against a WireMock instance, providing automated
  performance regression gating on every push
- **Screen reader testing** — manual VoiceOver and NVDA validation
  complementing the existing axe-core automated layer, covering the
  60-70% of WCAG issues that automated tools cannot detect

---

### Completed Improvements

The following items from the original Future Improvements list have
been delivered on this branch:

| Item | Status | Details |
|---|---|---|
| k6 performance testing | ✅ Completed | Four scripts covering all API endpoints, p95 under 500ms, shared config module |
| axe-core accessibility | ✅ Completed | WCAG 2.1 AA scanning across all four pages, violations fixed in mock site templates |

---

## Author

Sean Ferguson
SDET | QA Automation Engineer
Java | Selenium | REST Assured | Cucumber | TestNG | Maven | Spring Boot