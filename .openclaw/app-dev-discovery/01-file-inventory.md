# File Inventory

## Module Structure (Maven Multi-Module)

```
BroadleafCommerce (root pom — version 7.0.8-SNAPSHOT)
├── common/ # broadleaf-common
│   └── pom.xml
├── core/                            # core aggregator
│   ├── broadleaf-framework/          # Core domain entities, services
│   ├── broadleaf-framework-web/      # Web layer (MVC, controllers, processors)
│   ├── broadleaf-profile/           # Customer/user profile domain
│   └── broadleaf-profile-web/       # Profile web (registration, login)
│   └── pom.xml
├── admin/                           # Admin aggregator
│   ├── broadleaf-admin-module/       # Admin module
│   ├── broadleaf-contentmanagement-module/  # CMS
│   ├── broadleaf-open-admin-platform/  # Admin UI platform
│   ├── broadleaf-admin-functional-tests/
│   └── pom.xml
├── integration/                      # Integration tests
│   └── pom.xml
└── pom.xml (root)
```

## Key File Types

| Type | Count (approx) | Purpose |
|------|----------------|---------|
| `.java` | 2985 | Source files |
| `.xml` | ~200 | Spring configs, Hibernate mappings |
| `.groovy` | ~50 | Spock tests |
| `.md` |5 | Documentation |

## High-Value Files (Reviewed)

| Path | Role |
|------|------|
| `pom.xml` | Root Maven POM — versions, dependencies, modules |
| `core/broadleaf-framework/pom.xml` | Framework module POM |
| `core/broadleaf-framework-web/pom.xml` | Web module POM |
| `admin/broadleaf-open-admin-platform/pom.xml` | Admin platform POM |
| `core/broadleaf-framework-web/src/main/resources/bl-framework-web-applicationContext.xml` | Main web Spring context |
| `core/broadleaf-framework-web/src/main/java/org/broadleafcommerce/core/web/` | Web controllers, services |
| `core/broadleaf-framework/src/main/java/org/broadleafcommerce/` | Core domain |
| `admin/broadleaf-open-admin-platform/src/main/java/org/broadleafcommerce/openadmin/` | Admin controllers |

## Exclusion Notes

- `target/` directories excluded (build artifacts)
- `.git/` excluded
- No node_modules in this Java project