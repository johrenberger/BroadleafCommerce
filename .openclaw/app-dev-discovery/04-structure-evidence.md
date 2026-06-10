# Project Structure and Entry Point Mapping

## Module Breakdown

### common/
- `broadleaf-common` — Shared utilities, common exceptions, base classes

### core/
- `broadleaf-framework` — Core domain entities, services, workflows, pricing, inventory, promotions
- `broadleaf-framework-web` — Web layer: Spring MVC controllers, processors, handlers, checkout, cart, catalog
- `broadleaf-profile` — Customer/user profile domain entities and services
- `broadleaf-profile-web` — Profile web: registration, login, password management

### admin/
- `broadleaf-admin-module` — Admin module for order/customer management
- `broadleaf-contentmanagement-module` — CMS for pages, content, media
- `broadleaf-open-admin-platform` — Admin UI platform (Spring MVC admin controllers)
- `broadleaf-admin-functional-tests` — Selenium/Geb functional tests

### integration/
- Integration test suite

## Key Entry Points

### Web Application (Site)
- `core/broadleaf-framework-web/src/main/java/org/broadleafcommerce/core/web/` — Main web controllers
- `core/broadleaf-framework-web/src/main/resources/bl-framework-web-applicationContext.xml` — Web Spring context

### Admin Application
- `admin/broadleaf-open-admin-platform/src/main/java/org/broadleafcommerce/openadmin/` — Admin controllers
- `admin/broadleaf-open-admin-platform/src/main/java/org/broadleafcommerce/admin/` — Admin-specific controllers

### Core Domain
- `core/broadleaf-framework/src/main/java/org/broadleafcommerce/` — Core entities and services
- `core/broadleaf-profile/src/main/java/org/broadleafcommerce/profile/` — Profile entities

## Recommended Reading Path

1. Start with `core/broadleaf-framework-web` controllers for request handling
2. `core/broadleaf-framework` for domain model and business logic
3. `core/broadleaf-profile` for customer management
4. `admin/broadleaf-open-admin-platform` for admin UI

## Evidence

- [core/broadleaf-framework-web/src/main/java/org/broadleafcommerce/core/web/](https://github.com/johrenberger/BroadleafCommerce/blob/8645873661b34fe0954cebe382aba59336714db0/core/broadleaf-framework-web/src/main/java/org/broadleafcommerce/core/web/)
- [core/broadleaf-framework/src/main/java/org/broadleafcommerce/](https://github.com/johrenberger/BroadleafCommerce/blob/8645873661b34fe0954cebe382aba59336714db0/core/broadleaf-framework/src/main/java/org/broadleafcommerce/)