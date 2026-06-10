# Dependencies, Integrations, and APIs

## Major External Integrations

### Search — Apache Solr
- **Library:** `solr-solrj` 9.9.0
- **Purpose:** Domain search with faceted navigation
- **Config:** SolrClient via Spring configuration

### Job Scheduling — Quartz
- **Library:** `quartz` 2.5.2
- **Purpose:** Scheduled tasks (e.g., abandoned cart emails, inventory updates)

### Email — Spring Mail + JMS
- **Mode:** Synchronous or asynchronous via JMS
- **Templates:** Thymeleaf for email content
- **Provider:** Configurable SMTP or JMS-backed mail

### Caching — ehcache3
- **Library:** `ehcache` 3.10.8 (jakarta classifier)
- **Purpose:** Hibernate second-level cache, result caching

### LDAP — Spring LDAP
- **Library:** `spring-ldap-core` 3.3.6
- **Purpose:** Admin authentication against corporate LDAP

### Payment — Spring Security OAuth2 Client
- **Library:** `spring-security-oauth2-client` 6.5.10
- **Purpose:** Social login (Google, Facebook, etc.)

### Content Extraction — Apache Tika
- **Library:** `tika-core` 3.2.3
- **Purpose:** Extract metadata from uploaded files

## No API Documentation Mechanism Detected

- No Swagger/OpenAPI spec found
- No JSDoc on controllers
- API documented via README and tutorials on broadleafcommerce.com

## Evidence

- [pom.xml](https://github.com/johrenberger/BroadleafCommerce/blob/8645873661b34fe0954cebe382aba59336714db0/pom.xml)