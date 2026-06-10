# Contradiction Detection

## Cross-Evidence Comparison

### README vs. pom.xml

| Claim (README) | Actual (pom.xml) | Status |
|----------------|-----------------|--------|
| "Spring framework" | Spring6.2.18 confirmed | ✓ Match |
| "Spring Security" | Spring Security 6.5.10 confirmed | ✓ Match |
| "JPA and Hibernate" | Hibernate 5.6.15.Final confirmed | ✓ Match |
| "Solr" | solr-solrj 9.9.0 confirmed | ✓ Match |
| "Quartz" | quartz 2.5.2 confirmed | ✓ Match |
| "Thymeleaf" | Via Spring Boot, confirmed in deps | ✓ Match |

### Version Consistency

| Component | Root pom.xml | Module pom.xml |
|-----------|-------------|----------------|
| Spring | 6.2.18 | inherited |
| Hibernate | 5.6.15.Final | inherited |
| Java | 17 | 17 |

### No Contradictions Found

The codebase is internally consistent. README accurately describes the technologies used.

## Potential Clarifications Needed

### 1. Edition Differences

**Observation:** README mentions CE, EE, and Microservices editions. This repo is the CE (Community Edition) fork.

**Impact:** Low — expected for a fork.

### 2. License Clarity

**Observation:** README says "Fair Use" license but also mentions commercial license option. The Fair Use license has revenue restrictions (< $5M).

**Impact:** Medium — implementers need legal review.

### 3. Solr Version Note

**Observation:** solr-solrj 9.9.0 is used but Solr server itself is not bundled — it must be provisioned separately.

**Impact:** Low — standard operational concern.

## Conclusion

**No significant contradictions detected.** The codebase is internally consistent and README accurately reflects the implementation.

## Evidence

- [README.md](https://github.com/johrenberger/BroadleafCommerce/blob/8645873661b34fe0954cebe382aba59336714db0/README.md)
- [pom.xml](https://github.com/johrenberger/BroadleafCommerce/blob/8645873661b34fe0954cebe382aba59336714db0/pom.xml)