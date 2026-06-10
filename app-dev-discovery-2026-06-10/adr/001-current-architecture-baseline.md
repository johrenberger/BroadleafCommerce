# ADR-0001: Current Architecture Baseline (BroadleafCommerce CE @ bb978302)

- **Status:** Accepted
- **Date:** 2026-06-10
- **Deciders:** app-dev-discovery workflow (auto-generated baseline)
- **Repo / Commit:** johrenberger/BroadleafCommerce @ `bb97830278d5912941aea36a372d3d4e87406e6a`
- **Source:** [https://github.com/johrenberger/BroadleafCommerce/blob/bb97830278d5912941aea36a372d3d4e87406e6a/](https://github.com/johrenberger/BroadleafCommerce/blob/bb97830278d5912941aea36a372d3d4e87406e6a/)

## Context and Problem Statement

Before any refactor, modernization, or migration proposal is entertained,
the implementer community needs a faithful, evidence-backed snapshot of
"what BroadleafCommerce CE actually is today" at a pinned commit. Without
that baseline, every subsequent ADR would re-litigate ground truth (e.g.
"How many modules do we have?", "Which Spring version is pinned?",
"How is persistence wired?").

This ADR is the discovery baseline, not a forward-looking decision. It is
the system of record for the as-discovered state. Any future ADR that
contradicts anything in §"Decision Outcome" must either update this ADR
or call out the divergence explicitly.

## Decision Drivers

- Need a single, commit-pinned description of the as-discovered architecture
  for downstream ADRs to reference.
- Want evidence (analyzer output, file paths) baked into the ADR so the
  baseline is auditable, not anecdotal.
- Want the baseline to be the threshold for "what we know vs. what we
  are guessing" — confidence ratings on each axis.
- Want to expose — not hide — the things that are unconventional or
  fragile (extension model, hand-rolled Spring config, no container
  build), so they become first-class design constraints.

## Considered Options

1. **As-discovered baseline (this ADR)** — capture exactly what is in the
   repo at `bb978302` and label it "accepted as the truth for now."
2. **Hypothetical target architecture** — describe what we wish it was
   (microservices, Spring Boot 3, Jakarta-only). Rejected: the repo is
   not there, and pretending otherwise would mislead implementers.
3. **Skip the baseline ADR** — go straight to feature ADRs. Rejected:
   without a baseline, every feature ADR would have to re-establish
   ground truth, and contradictions would multiply.

## Decision Outcome

**Chosen option:** "As-discovered baseline" — this document becomes the
reference snapshot. Future ADRs that propose changes (Spring Boot 3
upgrade, containerization, JPA-only refactor, etc.) must cite this ADR
as the starting state.

### Positive Consequences

- Future ADRs have a single, pinned source of truth.
- Confidence ratings expose which claims are fact vs. inference.
- The unconventional choices (no Docker, hand-rolled JPA merging,
  Spring XML + Java config hybrid) are documented so they can be
  discussed deliberately rather than "discovered" in code review.

### Negative Consequences

- The baseline can drift out of date if not regenerated on each
  significant commit. Mitigation: re-run the `app-dev-discovery`
  workflow on each new commit, treat the resulting doc as the new
  baseline, and supersede this ADR with a new commit-pinned one.

## Captured Baseline State

### Build System

- **Type:** Multi-module Maven (4 top-level modules).
- **Root pom:** [`pom.xml`](https://github.com/johrenberger/BroadleafCommerce/blob/bb97830278d5912941aea36a372d3d4e87406e6a/pom.xml)
  declares `<groupId>` and `<version>` 7.0.8-SNAPSHOT, the parent for
  every Broadleaf artifact.
- **Modules:** `common`, `core`, `integration`, `admin` — see
  [`pom.xml`](https://github.com/johrenberger/BroadleafCommerce/blob/bb97830278d5912941aea36a372d3d4e87406e6a/pom.xml) `<modules>` block.
- **Compiler target:** Java 1.8 (per `Jenkinsfile` and `pom.xml`).
- **CI:** Jenkins (single `Jenkinsfile` at repo root, no GH Actions,
  no GitLab CI, no CircleCI).

### Module Responsibilities

| Module | Purpose | Anchor |
| --- | --- | --- |
| `common` | Cross-cutting infrastructure: `EnableBroadleaf*AutoConfiguration`, JPA merge, payment service scaffolding, security filter wiring, shared utilities. | [common/pom.xml](https://github.com/johrenberger/BroadleafCommerce/blob/bb97830278d5912941aea36a372d3d4e87406e6a/common/pom.xml) |
| `core` | Domain: catalog (Product, Sku, Category), order (Order, OrderItem, FulfillmentGroup), offer/promotion, search/Solr, profile/customer, tax, payment domain. | [core/pom.xml](https://github.com/johrenberger/BroadleafCommerce/blob/bb97830278d5912941aea36a372d3d4e87406e6a/core/pom.xml) |
| `integration` | Cross-module Spring Boot test harnesses (`AdminApplication`, `SiteApplication`) and integration test scaffolding. | [integration/pom.xml](https://github.com/johrenberger/BroadleafCommerce/blob/bb97830278d5912941aea36a372d3d4e87406e6a/integration/pom.xml) |
| `admin` | The Broadleaf Open Admin platform: schema-driven admin UI, `AdminBasicEntityController`, content management, catalog actions, login/SSO. | [admin/pom.xml](https://github.com/johrenberger/BroadleafCommerce/blob/bb97830278d5912941aea36a372d3d4e87406e6a/admin/pom.xml) |

### Persistence

- **JPA on Hibernate** ([`hibernate-core-jakarta`](https://github.com/johrenberger/BroadleafCommerce/blob/bb97830278d5912941aea36a372d3d4e87406e6a/pom.xml)).
  Hibernate 6.x with the Jakarta-namespace artifacts (note the
  `-jakarta` classifier).
- **160 JPA entities** detected across modules
  ([`OrderItemImpl.java`](https://github.com/johrenberger/BroadleafCommerce/blob/bb97830278d5912941aea36a372d3d4e87406e6a/core/broadleaf-framework/src/main/java/org/broadleafcommerce/core/order/domain/OrderItemImpl.java),
  [`SkuImpl.java`](https://github.com/johrenberger/BroadleafCommerce/blob/bb97830278d5912941aea36a372d3d4e87406e6a/core/broadleaf-framework/src/main/java/org/broadleafcommerce/core/catalog/domain/SkuImpl.java),
  [`CustomerImpl.java`](https://github.com/johrenberger/BroadleafCommerce/blob/bb97830278d5912941aea36a372d3d4e87406e6a/core/broadleaf-profile/src/main/java/org/broadleafcommerce/profile/core/domain/CustomerImpl.java),
  etc.).
- **Hand-rolled JPA merging** — [`MergePersistenceUnitManager.java`](https://github.com/johrenberger/BroadleafCommerce/blob/bb97830278d5912941aea36a372d3d4e87406e6a/common/src/main/java/org/broadleafcommerce/common/extensibility/jpa/MergePersistenceUnitManager.java)
  combines multiple `persistence.xml` files at startup so that
  Broadleaf modules + the implementer's own entities coexist.
- **Bytecode rewriting for extensibility** — [`DirectCopyClassTransformer.java`](https://github.com/johrenberger/BroadleafCommerce/blob/bb97830278d5912941aea36a372d3d4e87406e6a/common/src/main/java/org/broadleafcommerce/common/extensibility/jpa/copy/DirectCopyClassTransformer.java)
  uses ASM to duplicate `@Entity` fields at class-load time so that
  implementer subclasses can override individual JPA mappings without
  redeclaring the entire entity.

### Web / API Surface

- **Spring MVC** controllers — **103 Spring MVC routes** detected, all
  in the `admin` module. The site/storefront controllers live in
  implementer projects, not this repo.
- **Spring Boot auto-configuration** — opt-in via
  [`@EnableBroadleafAdminAutoConfiguration`](https://github.com/johrenberger/BroadleafCommerce/blob/bb97830278d5912941aea36a372d3d4e87406e6a/common/src/main/java/org/broadleafcommerce/common/config/EnableBroadleafAdminAutoConfiguration.java)
  and
  [`@EnableBroadleafSiteAutoConfiguration`](https://github.com/johrenberger/BroadleafCommerce/blob/bb97830278d5912941aea36a372d3d4e87406e6a/common/src/main/java/org/broadleafcommerce/common/config/EnableBroadleafSiteAutoConfiguration.java)
  (plus their `Root` and `Servlet` variants).
- **Spring Security** for admin authentication ([`pom.xml`](https://github.com/johrenberger/BroadleafCommerce/blob/bb97830278d5912941aea36a372d3d4e87406e6a/admin/broadleaf-open-admin-platform/pom.xml)
  brings in Spring Security; [`AdminUserDetailsServiceImpl.java`](https://github.com/johrenberger/BroadleafCommerce/blob/bb97830278d5912941aea36a372d3d4e87406e6a/admin/broadleaf-open-admin-platform/src/main/java/org/broadleafcommerce/openadmin/server/security/service/user/AdminUserDetailsServiceImpl.java)
  implements the user-details service).
- **No OpenAPI / Swagger spec** — confirmed; routes must be read from
  controller source.
- **No GraphQL or gRPC** — REST/MVC only.

### Data Flow Patterns

- **Schema-driven admin UI** — [`AdminBasicEntityController.java`](https://github.com/johrenberger/BroadleafCommerce/blob/bb97830278d5912941aea36a372d3d4e87406e6a/admin/broadleaf-open-admin-platform/src/main/java/org/broadleafcommerce/openadmin/web/controller/entity/AdminBasicEntityController.java)
  is a single generic controller that dispatches to per-entity
  metadata providers (`AdminPresentation` annotations drive form
  layout).
- **Pricing pipeline** — [`OrderServiceImpl.java`](https://github.com/johrenberger/BroadleafCommerce/blob/bb97830278d5912941aea36a372d3d4e87406e6a/core/broadleaf-framework/src/main/java/org/broadleafcommerce/core/order/service/OrderServiceImpl.java)
  runs offer/promotion rules (MVEL) against the cart/order.
- **Solr indexer** — [`SolrIndexServiceImpl.java`](https://github.com/johrenberger/BroadleafCommerce/blob/bb97830278d5912941aea36a372d3d4e87406e6a/core/broadleaf-framework/src/main/java/org/broadleafcommerce/core/search/service/solr/index/SolrIndexServiceImpl.java)
  reindexes products on catalog changes.
- **JMS for async email** — emails (forgot-password, order confirmation)
  are dispatched via JMS (geronimo JMS 1.1 spec in [`pom.xml`](https://github.com/johrenberger/BroadleafCommerce/blob/bb97830278d5912941aea36a372d3d4e87406e6a/pom.xml)).

### Deploy / Runtime

- **No Docker, no Kubernetes manifests, no Terraform, no Helm chart** in
  this repo. Deployment is left entirely to the implementer.
- **Jenkins** is the only CI tool. The `Jenkinsfile` is at the repo
  root.
- **JDK 8** is the build target.

### Quantitative Summary

| Axis | Value | Source |
| --- | ---: | --- |
| Top-level Maven modules | 4 | [`pom.xml`](https://github.com/johrenberger/BroadleafCommerce/blob/bb97830278d5912941aea36a372d3d4e87406e6a/pom.xml) |
| JPA entities | 160 (after dedup) | `db_schema.json` |
| Spring MVC routes | 103 | `routes.json` |
| Java entry points | 17 | `entry_points.json` |
| Maven dependencies (top-level) | 123 | `dependencies.json` |
| Total files in repo | 3,842 | `repo_inventory.json` |
| Test files | 330 (~9.9% test/source ratio) | `tests.json` |
| Hardcoded URL findings (hygiene) | 449 | `hygiene_findings.json` |

## Pros and Cons of the Options

### As-discovered baseline (chosen)

- Good, because it commits the truth to paper, with commit-pinned links.
- Good, because downstream ADRs can diff against it.
- Good, because it surfaces unconventional choices as deliberate
  design constraints.
- Bad, because it locks in current oddities (no containerization,
  JDK 8) that some implementers will read as "we don't care" rather
  than "this is what was discovered, propose a change."

### Hypothetical target architecture

- Good, because it would feel forward-looking.
- Bad, because it would be fiction relative to the repo state.
- Bad, because implementers would be misled into assuming features
  exist that don't.

### Skip the baseline

- Good, because it saves time on day one.
- Bad, because every subsequent ADR re-pays the discovery tax.
- Bad, because contradictions across ADRs become invisible.

## Implementation Notes

- This ADR is the source of truth for "what Broadleaf CE looks like
  today" at the pinned commit. To regenerate after a new commit:
  1. Re-run `app-dev-discovery` (analyzer + LLM synthesizer).
  2. Produce a new commit-pinned onboarding doc.
  3. Create a new ADR-XXXX that supersedes this one, with the
     diff against `bb978302` called out in the prose.

## Links / References

- Onboarding document: `docs/2026-06-10-BroadleafCommerce-app-dev-discovery.md`
- Phase-15 contradiction log: [`.openclaw/app-dev-discovery/15-contradiction-detection.md`](https://github.com/johrenberger/BroadleafCommerce/blob/bb97830278d5912941aea36a372d3d4e87406e6a/.openclaw/app-dev-discovery/15-contradiction-detection.md)
- Phase-14 hygiene log: [`.openclaw/app-dev-discovery/14-risk-hygiene-evidence.md`](https://github.com/johrenberger/BroadleafCommerce/blob/bb97830278d5912941aea36a372d3d4e87406e6a/.openclaw/app-dev-discovery/14-risk-hygiene-evidence.md)
- License: [README.md](https://github.com/johrenberger/BroadleafCommerce/blob/bb97830278d5912941aea36a372d3d4e87406e6a/README.md)
