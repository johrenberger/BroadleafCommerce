# API Evidence

## API Style

**Broadleaf Commerce CE is not primarily a REST API platform.** It is a framework for building e-commerce sites. The "API" is in the form of:

1. **Spring MVC Controllers** — HTTP endpoints for web requests
2. **Internal Service APIs** — Java interfaces for business logic
3. **Extension APIs** — Plugin/extension patterns via `ExtensionHandler` classes

## Controller Endpoints (Site)

Based on `BroadleafCartController`, `BroadleafCheckoutController`, etc.:

| Pattern | Controller | Description |
|---------|------------|-------------|
| `/cart` | BroadleafCartController | Cart operations |
| `/checkout` | BroadleafCheckoutController | Multi-stage checkout |
| `/checkout/shipping` | BroadleafShippingInfoController | Shipping info |
| `/checkout/billing` | BroadleafBillingInfoController | Billing info |
| `/checkout/payment` | BroadleafPaymentInfoController | Payment info |
| `/account` | Account controllers | Login, register, profile |
| `/catalog` | Catalog controllers | Products, categories |
| `/search` | BroadleafSearchController | Search results |

## Controller Endpoints (Admin)

Based on `org.broadleafcommerce.openadmin.web.controller`:

| Pattern | Description |
|---------|-------------|
| `/admin` | Admin home |
| `/admin/entity` | Entity CRUD operations |
| `/admin/module` | Admin module navigation |

## RESTful Service Layer

Core services expose internal Java APIs (not HTTP REST):

- `OrderService` — Order management
- `CartService` — Cart operations
- `PricingService` — Price calculation
- `FulfillmentService` — Shipping calculation
- `InventoryService` — Stock checks
- `PaymentService` — Payment processing
- `CustomerService` — Customer management

## Extension Pattern

Broadleaf uses a `ExtensionManager` + `ExtensionHandler` pattern for customization:

```java
public interface ExtensionHandler {
    public boolean canHandle();
    public void handle();
}
```

Handlers are called in sequence until one handles the request.

## Evidence

- [core/broadleaf-framework-web/src/main/java/org/broadleafcommerce/core/web/controller/](https://github.com/johrenberger/BroadleafCommerce/blob/8645873661b34fe0954cebe382aba59336714db0/core/broadleaf-framework-web/src/main/java/org/broadleafcommerce/core/web/controller/)