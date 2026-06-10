# Key Components Analysis

## 1. Web Controllers (broadleaf-framework-web)

**Package:** `org.broadleafcommerce.core.web.controller.*`

**Key Controllers:**
- `BroadleafCartController` — Cart operations (add, update, remove items)
- `BroadleafCheckoutController` — Multi-stage checkout flow
- `BroadleafShippingInfoController` — Shipping address and method
- `BroadleafBillingInfoController` — Billing information
- `BroadleafPaymentInfoController` — Payment processing
- `BroadleafCatalogController` — Product/category browsing
- `BroadleafSearchController` — Search results
- `BroadleafProductController` — Product detail pages
- `BroadleafCategoryController` — Category pages
- `BroadleafLoginController` — Authentication
- `BroadleafRegisterController` — User registration
- `BroadleafOrderHistoryController` — Order history

**Responsibility:** Handle HTTP requests for storefront (site) functionality

## 2. Admin Controllers (broadleaf-open-admin-platform)

**Package:** `org.broadleafcommerce.openadmin.web.controller`

**Key Controllers:**
- `AdminAbstractController` — Base admin controller
- `ModuleController` — Admin module navigation
- `EntityController` — CRUD operations for entities
- `ForeignKeyController` — Foreign key lookups

**Responsibility:** Admin UI request handling

## 3. Domain Entities (broadleaf-framework)

**Package:** `org.broadleafcommerce.core.order.domain`

**Key Entities:**
- `Order` — Main order entity
- `OrderItem` — Line items
- `FulfillmentGroup` — Shipping groupings
- `PaymentInfo` — Payment details

**Package:** `org.broadleafcommerce.core.catalog.domain`

**Key Entities:**
- `Product` — Product definition
- `Sku` — Stock keeping unit
- `Category` — Category hierarchy
- `ProductOption` — Product customization options

## 4. Services (broadleaf-framework)

**Key Services:**
- `OrderService` — Order management
- `CartService` — Cart operations
- `PricingService` — Price calculation with promotions
- `FulfillmentService` — Shipping calculations
- `PaymentService` — Payment processing
- `InventoryService` — Stock management

## 5. Security (broadleaf-framework-web)

**Package:** `org.broadleafcommerce.core.web.order.security`

**Key Components:**
- `BroadleafAuthenticationSuccessHandler` — Post-login cart merge
- `CartStateFilter` — Cart state management
- `MergeCartProcessorImpl` — Cart merging on login
- `XssFilter` / `XssRequestWrapper` — XSS attack prevention

## 6. Checkout Workflow (broadleaf-framework-web)

**Package:** `org.broadleafcommerce.core.web.checkout`

**Checkout Stages:**
- `CheckoutStageType` — Enum: INFO, SHIPPING, BILLING, PAYMENT, CONFIRMATION
- `CheckoutSectionDTO` — Checkout section data
- `CheckoutFormService` — Form handling

**Validators:**
- `ShippingInfoFormValidator`
- `BillingInfoFormValidator`
- `CreditCardInfoFormValidator`
- `PaymentInfoFormValidator`

## 7. Admin Security

**Package:** `org.broadleafcommerce.openadmin.web.security`

**Key Components:**
- Admin authentication and authorization
- Permission-based access control

##8. CMS (broadleaf-contentmanagement-module)

**Package:** `org.broadleafcommerce.core.cms`

**Key Components:**
- `ContentManagementService` — Page and content management
- `ContentRuleProcessor` — Targeted content rules

## Evidence

- [core/broadleaf-framework-web/src/main/java/org/broadleafcommerce/core/web/controller/](https://github.com/johrenberger/BroadleafCommerce/blob/8645873661b34fe0954cebe382aba59336714db0/core/broadleaf-framework-web/src/main/java/org/broadleafcommerce/core/web/controller/)
- [core/broadleaf-framework/src/main/java/org/broadleafcommerce/](https://github.com/johrenberger/BroadleafCommerce/blob/8645873661b34fe0954cebe382aba59336714db0/core/broadleaf-framework/src/main/java/org/broadleafcommerce/)