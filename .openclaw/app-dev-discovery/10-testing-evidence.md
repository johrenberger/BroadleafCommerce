# Testing Analysis

## Test Frameworks

| Framework | Version | Purpose |
|-----------|---------|---------|
| JUnit | 4.13.2 | Unit testing |
| Spock | 2.4-M4-groovy-4.0 | BDD tests (Groovy) |
| Geb | 7.0 | Browser automation (functional tests) |
| EasyMock | 5.2.0 | Mocking |
| GreenMail | 2.1.0-alpha-1 | In-memory SMTP for email tests |

## Test Structure

```
core/broadleaf-framework-web/src/test/
├── groovy/ # Spock tests
└── resources/           # Test resources

admin/broadleaf-admin-functional-tests/
├── pom.xml
└── (Selenium/Geb tests)
```

## Test Commands

```bash
# Run all tests
mvn test

# Run specific module
mvn test -pl core/broadleaf-framework-web

# Integration tests
mvn integration-test -pl integration
```

## CI Test Execution

- Maven Surefire for unit tests
- JaCoCo for coverage reporting
- OWASP Dependency Check via `security-check` profile

## Test Coverage

- JaCoCo configured in root pom.xml
- Coverage reports generated at `${project.build.directory}/site/jacoco/`

## Test Gaps (Observations)

- No dedicated API contract tests found
- Python e2e tests not present in this repo (may be in separate enterprise repo)
- Functional tests in `broadleaf-admin-functional-tests` use Geb (Groovy-based Selenium)

## Evidence

- [pom.xml](https://github.com/johrenberger/BroadleafCommerce/blob/8645873661b34fe0954cebe382aba59336714db0/pom.xml)
- [admin/broadleaf-admin-functional-tests/pom.xml](https://github.com/johrenberger/BroadleafCommerce/blob/8645873661b34fe0954cebe382aba59336714db0/admin/broadleaf-admin-functional-tests/pom.xml)