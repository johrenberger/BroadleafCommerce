# Error Handling, Logging, and Observability

## Error Handling

### Global Exception Handling
- Spring MVC `@ExceptionHandler` methods in base controllers
- `BroadleafDefaultExceptionHandler` (if present) for framework-level exceptions
- No custom exception hierarchy fully reviewed

### Input Validation
- JSR-303 Bean Validation annotations on entities and DTOs
- `blcValidator` custom validators for forms (e.g., `ShippingInfoFormValidator`)
- XSS prevention via `XssFilter` and `XssRequestWrapper`

### Order Locking
- `OrderLockAcquisitionFailureException` for concurrent order modification attempts
- `DatabaseOrderLockManager` for distributed locking

## Logging

### Logging Framework
- **Facade:** SLF4J 2.0.17
- **Implementation:** Logback 1.5.32
- **Config:** `logback.xml` (not reviewed in detail)

### Log Configuration
- Structured logging via SLF4J API
- No explicit log levels observed in code (uses standard logger.debug/info/error)

## Observability

### Health/Status
- No `/health` endpoint detected in this version
- No Actuator or similar monitoring library

### Metrics
- No Prometheus, Datadog, or similar metrics library found
- JaCoCo for test coverage only

### Distributed Tracing
- No OpenTelemetry or similar tracing
- No request ID propagation

## Evidence

- [core/broadleaf-framework-web/src/main/java/org/broadleafcommerce/core/web/order/security/exception/](https://github.com/johrenberger/BroadleafCommerce/blob/8645873661b34fe0954cebe382aba59336714db0/core/broadleaf-framework-web/src/main/java/org/broadleafcommerce/core/web/order/security/exception/)