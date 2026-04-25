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
│       └── resources/templates/       ← Thymeleaf HTML pages
│
├── src/test/
│   ├── java/com/bankportal/
│   │   ├── config/                    ← ConfigManager (environment config)
│   │   ├── utils/                     ← DriverManager, WireMock, Screenshot,
│   │   │                                 TestListener
│   │   ├── ui/
│   │   │   ├── pages/                 ← Page Object Model classes
│   │   │   ├── steps/                 ← Cucumber UI step definitions
│   │   │   └── runners/               ← UI test runner
│   │   └── api/
│   │       ├── clients/               ← REST Assured service clients
│   │       ├── models/                ← POJO request/response models
│   │       ├── steps/                 ← Cucumber API step definitions
│   │       └── runners/               ← API test runner
│   │
│   └── resources/
│       ├── features/
│       │   ├── ui/                    ← UI Gherkin feature files
│       │   └── api/                   ← API Gherkin feature files
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

## Test Coverage

### API Scenarios (7 total)

| Feature | Scenario | Tag |
|---|---|---|
| Auth API | Valid credentials return token | @smoke |
| Auth API | Invalid credentials return 401 | @smoke |
| Offers API | Authenticated request returns offers | @regression |
| Offers API | Unauthenticated request rejected | @regression |
| Product API | Retrieve all products returns 200 | @smoke |
| Product API | Active/inactive product counts correct | @smoke |
| Product API | Retrieve product by ID | @smoke |

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

---

## How to Run

### Prerequisites

- Java 17+
- Maven 3.9+
- Chrome browser (for local UI tests)
- Spring Boot mock site running (for UI tests)

### Start the Mock Bank Site

```
bash
cd mock-bank-site
mvn spring-boot:run
```

Wait for:
Started MockBankApplication in X.XXX seconds

The site will be available at `http://localhost:8080`

### Run API Tests Only

```
bash
mvn test -Dtest=ApiTestRunner
```

No browser or mock site required — WireMock handles all API mocking automatically.

### Run UI Tests Only

```
bash
mvn test -Dtest=UiTestRunner
```

Runs with visible Chrome by default. Mock site must be running first.

### Run UI Tests in Headless Mode

```
bash
mvn test -Dtest=UiTestRunner -Dheadless=true
```

### Run Full Suite

```
bash
mvn test
```

### Run by Tag

```
bash
# Smoke tests only
mvn test -Dtest=ApiTestRunner -Dcucumber.filter.tags="@smoke"

# Regression tests only  
mvn test -Dtest=UiTestRunner -Dcucumber.filter.tags="@regression"
```

### OWASP Dependency Check

```
bash
mvn dependency-check:check -DnvdApiKey="your-nvd-api-key"
```

Reports generated at: `target/dependency-check-report/dependency-check-report.html`

---

## Test Reports

After any test run, reports are available at:

| Report | Location |
|---|---|
| Cucumber HTML (API) | `target/cucumber-reports/api-report.html` |
| Cucumber HTML (UI) | `target/cucumber-reports/ui-report.html` |
| TestNG Results | `target/surefire-reports/index.html` |
| Test Logs | `target/logs/test-run.log` |
| Screenshots (on failure) | `target/screenshots/` |

---

## CI/CD Pipeline

The GitHub Actions pipeline runs automatically on every push to `main` and
on all pull requests.

### Pipeline Stages

``` 
Push to main
│
▼
Build & Compile ──────────────────────────────┐
│                                             │
▼                                             ▼
API Tests (WireMock)                OWASP Dependency Check
│                                    (continue-on-error)
▼
UI Tests (Headless Chrome)
│
▼
Quality Gate
```

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

## Future Improvements

Given more time or a production context, the following would be prioritised:

- **Contract testing** — Pact framework to validate consumer-provider API contracts
- **Parallel execution** — TestNG parallel suite configuration with thread count tuning
- **Performance smoke tests** — REST Assured response time assertions at API level
- **Cross-browser support** — Firefox and Edge runners in the CI pipeline
- **OWASP ZAP integration** — DAST scanning as a separate pipeline stage against
  a deployed environment
- **Allure TestOps** — centralised test history, trend analysis, and flaky test detection
- **Visual regression** — Percy or Applitools for screenshot comparison testing

---

## Security

This framework includes OWASP Dependency Check to audit third-party dependencies
against the National Vulnerability Database. The build is configured to flag any
dependency with a CVSS score of 7.0 (High) or above.

No credentials, API keys, or sensitive data are stored in source code.
All secrets are managed via GitHub Actions Secrets or environment variables.

---

## Author

Sean Ferguson
SDET | QA Automation Engineer
Java | Selenium | REST Assured | Cucumber | TestNG | Maven | Spring Boot