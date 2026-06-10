# Security Analysis

## Authentication

### Site Authentication
- Spring Security 6.5.10
- Customer login with email/password
- Session-based authentication
- `BroadleafAuthenticationSuccessHandler` for post-login cart merge

### Admin Authentication
- Spring Security with LDAP support (`spring-ldap-core` 3.3.6)
- Database-backed authentication also supported
- Role-based access control

### OAuth2 / Social Login
- `spring-security-oauth2-client` 6.5.10
- Supports Google, Facebook, etc.

## Authorization

- Spring Security method-level security (`@PreAuthorize`)
- Admin permission system via `AdminSecurityConfig`
- `blcAdmin` role for admin access

## Input Validation & Sanitization

### XSS Prevention
- `XssFilter` wraps request parameters
- `XssRequestWrapper` sanitizes input
- `blcVariableExpression` Thymeleaf expressions auto-escaped

### SQL Injection Prevention
- JPA/Hibernate prepared statements (parameterized queries)
- No raw SQL concatenation found

### HTML Sanitization
- OWASP AntiSamy 1.7.8 for rich content
- OWASP ESAPI 2.7.0.0 for security utilities

## PCI Considerations

- Payment account data referenced separately
- PCI-compliant encryption scheme support via API
- Verbose logging for payment interaction history
- Tokenization for saved payment methods

## Secrets Handling

- Database passwords via JNDI or environment variables (framework-level)
- No hardcoded secrets in source code
- Admin credentials in `AdminSecurityConfig`

## Security Dependencies

| Library | Version | Purpose |
|---------|---------|---------|
| Spring Security | 6.5.10 | Auth framework |
| OWASP ESAPI | 2.7.0.0 | Security utilities |
| OWASP AntiSamy | 1.7.8 | HTML sanitization |
| Nimbus JOSE JWT | 10.0.2 | JWT handling |

## Security Testing

- OWASP Dependency Check via `security-check` Maven profile
- No explicit penetration testing framework in codebase

## Observations

- No CSRF tokens explicitly reviewed (likely handled by Spring Security)
- No explicit CORS configuration reviewed
- No rate limiting at framework level (likely delegated to servlet container or CDN)

## Evidence

- [core/broadleaf-framework-web/src/main/java/org/broadleafcommerce/core/web/security/](https://github.com/johrenberger/BroadleafCommerce/blob/8645873661b34fe0954cebe382aba59336714db0/core/broadleaf-framework-web/src/main/java/org/broadleafcommerce/core/web/security/)
- [pom.xml](https://github.com/johrenberger/BroadleafCommerce/blob/8645873661b34fe0954cebe382aba59336714db0/pom.xml)