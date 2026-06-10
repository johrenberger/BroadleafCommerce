# Execution and Data Flow Analysis

## 1. Authentication Flow

**Site Authentication:**
1. User submits login via `BroadleafLoginController`
2. Spring Security handles credential validation
3. `BroadleafAuthenticationSuccessHandler` merges guest cart with user cart
4. Session created with customer association

**Admin Authentication:**
1. Admin login via `AdminLoginController`
2. Spring Security LDAP or database-backed auth
3. Permission checks via `AdminSecurityConfig`

## 2. Cart Flow

**Add to Cart:**
1. `BroadleafCartController.addCartItem()` receives SKU ID and quantity
2. `CartService.addItem()` validates inventory
3. `PricingService.applyPricing()` calculates price (including promotions)
4. Order updated in database
5. Response with updated cart summary

**Cart State:**
1. `CartStateFilter` runs on every request
2. Looks up or creates cart for current session/customer
3. `CartState` thread-local holds current cart
4. `OrderStateAOP` manages order state aspects

## 3. Checkout Flow

**Multi-Stage Checkout:**
1. **Info Stage** — Collect customer info
2. **Shipping Stage** — `BroadleafShippingInfoController` — shipping address and method
3. **Billing Stage** — `BroadleafBillingInfoController` — billing address
4. **Payment Stage** — `BroadleafPaymentInfoController` — payment details
5. **Confirmation** — `BroadleafOrderConfirmationController` — final review and submit

**Order Submission:**
1. `CheckoutFormService.validateCheckout()` validates all stages
2. `OrderService.saveOrder()` persists final order
3. `FulfillmentService.calculateFulfillmentGroups()` finalizes shipping
4. `PaymentService.processPayment()` charges payment
5. `OrderLockManager` acquires lock to prevent concurrent modifications
6. Inventory decremented, confirmation displayed

## 4. Product/SKU Pricing Flow

**Dynamic Pricing:**
1. `DynamicSkuPricingFilter` intercepts SKU lookup
2. `DefaultDynamicSkuPricingService` applies pricing rules
3. MVEL expressions evaluate promotion conditions
4. Final price cached with `CacheKeyResolver`

## 5. Search Flow

**Solr Integration:**
1. `BroadleafSearchController` receives search query
2. `SearchService` sends query to Solr via SolrJ
3. Faceted search results returned with filters
4. `SearchFacetDTOService` transforms results to DTOs

## 6. Admin Entity Management

**CRUD Operations:**
1. `EntityController` handles all entity operations
2. `AdminModuleService` loads appropriate handlers
3. `PersistencePackage` manages JPA entity lifecycle
4. Custom field handlers for extension attributes

## 7. Content Management Flow

**Page Rendering:**
1. CMS page requested
2. `ContentManagementService` loads page content
3. `ContentRuleProcessor` evaluates targeting rules
4. Thymeleaf template rendered with content

## Evidence

- [core/broadleaf-framework-web/src/main/java/org/broadleafcommerce/core/web/controller/cart/](https://github.com/johrenberger/BroadleafCommerce/blob/8645873661b34fe0954cebe382aba59336714db0/core/broadleaf-framework-web/src/main/java/org/broadleafcommerce/core/web/controller/cart/)
- [core/broadleaf-framework-web/src/main/java/org/broadleafcommerce/core/web/controller/checkout/](https://github.com/johrenberger/BroadleafCommerce/blob/8645873661b34fe0954cebe382aba59336714db0/core/broadleaf-framework-web/src/main/java/org/broadleafcommerce/core/web/controller/checkout/)