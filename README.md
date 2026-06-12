# Webshop Template Backend

Spring Boot REST API template for a webshop. JWT auth, product catalogue, order management, Stripe payments.

## Stack

| Layer | Tech |
|---|---|
| Framework | Spring Boot 4.0 (Java 21) |
| Security | Spring Security + JWT (jjwt 0.12) |
| Database | H2 (dev) / configurable (prod) |
| Migrations | Liquibase |
| Payments | Stripe |
| API Docs | SpringDoc OpenAPI (Swagger UI) |

---

## Features


## Getting Started

### Prerequisites
- Java 21
- Maven
- Stripe account (test keys)

### Setup

1. Copy the local properties template:
   ```bash
   cp src/main/resources/application-local.properties.template src/main/resources/application-local.properties
   ```

2. Fill in `application-local.properties`:
   ```
   Copy application-local.properties.template to application-local.properties and fill in the required values (JWT secret, Stripe keys, etc).
   ```

3. Run:
   ```bash
   ./mvn spring-boot:run
   ```

App starts on `http://localhost:8080`.

### Stripe Webhook (local dev)
Use [Stripe CLI](https://stripe.com/docs/stripe-cli) to forward webhooks:
```bash
stripe listen --forward-to localhost:8080/payments/webhook
```

---

## API Docs

Swagger UI available at: `http://localhost:8080/swagger-ui.html`

H2 console (dev): `http://localhost:8080/h2-console`

---

## Configuration

| Property | Description |
|---|---|
| `jwt.secret` | JWT signing secret |
| `jwt.expiration-ms` | Token TTL (default: 86400000 = 24h) |
| `stripe.api-key` | Stripe secret key |
| `stripe.webhook-secret` | Stripe webhook signing secret |
| `stripe.success-url` | Redirect URL after successful payment |
| `stripe.cancel-url` | Redirect URL after cancelled payment |

---

## CORS

Configured to allow `http://localhost:5173` (Vite default). Change in `SecurityConfig` for other origins.

## Roles

| Role | Capabilities |
|---|---|
| `USER` | Browse articles, place orders, pay |
| `ADMIN` | All USER actions + manage articles, view all orders, update order status |
