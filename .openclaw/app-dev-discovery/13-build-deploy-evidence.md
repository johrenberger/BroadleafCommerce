# Build, Deployment, and Operations

## Build System

**Maven 3.x** — multi-module project with `pom.xml` at root.

```bash
# Full build
mvn clean install

# Skip tests
mvn clean install -DskipTests

# Specific module
mvn clean install -pl core/broadleaf-framework-web
```

## Module Build Order

1. `common/` — broadleaf-common
2. `core/` — framework, framework-web, profile, profile-web
3. `integration/` — integration tests
4. `admin/` — admin modules

## Docker

- No Dockerfile in root of this repo
- Docker deployment likely handled in enterprise edition or implementer projects
- `docker-compose` not present in this repo

## CI/CD

### GitHub Actions
- `.github/pull_request_template.md` present (PR template only)
- No GitHub Actions workflow files found in this fork

### Jenkins
- `Jenkinsfile` present (Jenkins CI configuration)
- Likely used for release builds and deployment

## Deployment

Broadleaf Commerce CE is a **framework/library**, not a standalone application. Deployers:
1. Include Broadleaf JARs as dependencies
2. Configure Spring application context
3. Deploy to servlet container (Tomcat, Jetty, Undertow)

### Typical Deployment
- WAR packaging (via `maven-war-plugin`)
- Servlet container with Spring Boot or plain Spring
- Database: any JPA-compatible RDBMS

## Environment Variables

Configuration via Spring `PropertySource`:
- Database connection properties
- Solr URL
- Cache configuration
- Email/SMTP settings
- Admin LDAP settings

## Runtime Ports

- Servlet container port (default 8080 for embedded, configurable)
- No fixed ports in framework itself

## Evidence

- [pom.xml](https://github.com/johrenberger/BroadleafCommerce/blob/8645873661b34fe0954cebe382aba59336714db0/pom.xml)
- [Jenkinsfile](https://github.com/johrenberger/BroadleafCommerce/blob/8645873661b34fe0954cebe382aba59336714db0/Jenkinsfile)