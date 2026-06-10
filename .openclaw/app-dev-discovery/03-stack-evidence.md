# Technology Stack Evidence

## Language& Version

- **Java:** 17 (source/target/release 17, from `maven-compiler-plugin` config)
- **Groovy:** 4.0.20 (for Spock tests)

## Core Frameworks

| Technology | Version | Role |
|------------|---------|------|
| Spring Framework | 6.2.18 | Dependency injection, web MVC |
| Spring Security | 6.5.10 | Authentication, authorization |
| Spring Boot | 3.5.14 | Autoconfiguration |
| Hibernate | 5.6.15.Final | ORM, JPA |
| Hibernate Envers | 5.6.15.Final | Audit logging |

## Build & Test

| Technology | Version | Role |
|------------|---------|------|
| Maven | 3.x | Build tool |
| JUnit | 4.13.2 | Unit testing |
| Spock | 2.4-M4-groovy-4.0 | BDD testing (Groovy) |
| Geb | 7.0 | Browser automation for functional tests |
| JaCoCo | 0.8.13 | Code coverage |
| GMavenPlus | 4.2.0 | Groovy compilation in Maven |

## Persistence & Database

| Technology | Version | Role |
|------------|---------|------|
| Hibernate JPA | 5.6.15.Final | ORM |
| ehcache3 | 3.10.8 | Caching |
| HSQLDB | 2.7.4 | In-memory test database |
| Apache Commons DBCP2 | 2.9.0 | Connection pooling |

## Search

| Technology | Version | Role |
|------------|---------|------|
| Apache Solr | 9.9.0 | Search platform (solr-solrj client) |

## Messaging & Scheduling

| Technology | Version | Role |
|------------|---------|------|
| Quartz | 2.5.2 | Job scheduling |
| JMS (Jakarta) | 3.1.0 | Message queuing |

## Web & Presentation

| Technology | Version | Role |
|------------|---------|------|
| Jakarta Servlet | 6.0.0 | Servlet API |
| Thymeleaf | (from Spring Boot) | Template engine |
| Jackson | 2.21.2 | JSON/XML processing |

## Security

| Technology | Version | Role |
|------------|---------|------|
| Spring Security | 6.5.10 | Auth framework |
| OWASP ESAPI | 2.7.0.0 | Security library |
| OWASP AntiSamy | 1.7.8 | HTML sanitization |
| Nimbus JOSE JWT | 10.0.2 | JWT handling |

## Other Notable

| Technology | Version | Role |
|------------|---------|------|
| MVEL2 | 2.5.2.Final | Rule expression language |
| Lombok | 1.18.44 | Boilerplate reduction |
| Logback | 1.5.32 | Logging |
| SLF4J | 2.0.17 | Logging facade |
| Apache Tika | 3.2.3 | Content extraction |
| ImageIO (TwelveMonkeys) | 3.12.0 | CMYK JPEG support |
| Netty | 4.2.6.Final | NIO networking |

## Evidence

- [pom.xml](https://github.com/johrenberger/BroadleafCommerce/blob/8645873661b34fe0954cebe382aba59336714db0/pom.xml)