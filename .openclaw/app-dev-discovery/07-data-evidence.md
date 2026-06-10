# Database and Schema Analysis

## Database Type

**Relational Database** — Broadleaf supports multiple databases:
- **Primary:** Any JPA-compatible RDBMS (PostgreSQL, MySQL, Oracle, SQL Server)
- **Default in tests:** HSQLDB (in-memory)
- **Connection Pooling:** Apache Commons DBCP2

## ORM Layer

**Hibernate 5.6.15.Final** with JPA annotations.

## Key Entities

### Order Domain (`org.broadleafcommerce.core.order.domain`)

| Entity | Description |
|--------|-------------|
| `Order` | Main order — customer, status, totals |
| `OrderItem` | Line item — product, quantity, price |
| `FulfillmentGroup` | Shipping grouping — address, method, cost |
| `FulfillmentGroupItem` | Items in a fulfillment group |
| `PaymentInfo` | Payment — type, amount, payment info |
| `OrderLock` | Distributed lock for concurrent updates |

### Catalog Domain (`org.broadleafcommerce.core.catalog.domain`)

| Entity | Description |
|--------|-------------|
| `Product` | Product definition with options |
| `Sku` | Stock keeping unit — inventory, pricing |
| `Category` | Category hierarchy with parent/child |
| `ProductOption` | Customization options (e.g., size, color) |
| `ProductOptionValue` | Values for options |
| `CrossSellProduct` | Cross-sell associations |

### Customer/Profile Domain (`org.broadleafcommerce.profile.domain`)

| Entity | Description |
|--------|-------------|
| `Customer` | Customer account — email, password |
| `Address` | Shipping/billing addresses |
| `CustomerPayment` | Saved payment methods |

### CMS Domain (`org.broadleafcommerce.core.cms.domain`)

| Entity | Description |
|--------|-------------|
| `Page` | CMS pages |
| `ContentField` | Structured content fields |
| `ContentRule` | Targeting rules |

## Persistence Configuration

- JPA with Hibernate provider
- Hibernate Envers for audit logging
- ehcache3 for second-level caching
- Bean Validation (JSR-303) for input validation

## Schema Management

- Hibernate `hbm2ddl.auto` not confirmed (likely `validate` or `none` in production)
- No explicit Flyway/Liquibase migration files found in repo
- Schema likely managed via JPA entity annotations + manual SQL patches

## Evidence

- [core/broadleaf-framework/src/main/java/org/broadleafcommerce/core/order/domain/](https://github.com/johrenberger/BroadleafCommerce/blob/8645873661b34fe0954cebe382aba59336714db0/core/broadleaf-framework/src/main/java/org/broadleafcommerce/core/order/domain/)
- [core/broadleaf-framework/src/main/java/org/broadleafcommerce/core/catalog/domain/](https://github.com/johrenberger/BroadleafCommerce/blob/8645873661b34fe0954cebe382aba59336714db0/core/broadleaf-framework/src/main/java/org/broadleafcommerce/core/catalog/domain/)