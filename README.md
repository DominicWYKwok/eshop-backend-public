# Catto — E-commerce Backend

Spring Boot REST API powering Catto's product catalogue, shopping cart, user profiles, and checkout flow.

### Links to the deployed frontend:
### [Catto shop](https://store.catto.shop/)

## Project description

Catto is a full-stack e-commerce portfolio project. This repository contains the Java backend, which connects MongoDB persistence, Firebase authentication, and Stripe Checkout to support the shopping journey from product discovery to transaction history.

The live demo links to the deployed frontend. Start by browsing products and trying search, filters, and sorting, then sign up to explore the shopping cart. Transaction endpoints require a verified email address.

## Features

- **Product discovery** — product listing and details, search, filtering, sorting, and price range queries.
- **Shopping cart** — authenticated cart access with add, update, and remove operations.
- **Accounts and profiles** — Firebase authentication, profile retrieval and updates, and email verification checks for user creation and transactions.
- **Checkout and transactions** — Stripe Checkout session creation, transaction details, and transaction history.
- **Contact form** — a public endpoint for submitting customer messages.

### Backend design highlights

- Validates Firebase JWTs against the configured issuer and project audience using Spring Security OAuth2 Resource Server.
- Looks up individual transactions by both user and transaction ID to enforce ownership.
- Separates controllers, services, and repositories, with DTOs for API requests and responses.
- Supports container image builds with Jib and a development profile for MongoDB query logging and Stripe management endpoints.

## Tech stack

| Area | Technology |
| --- | --- |
| Language and framework | Java 17, Spring Boot 3.3.1, Spring Web |
| Authentication | Firebase Authentication, Spring Security OAuth2 Resource Server |
| Database | MongoDB, Spring Data MongoDB |
| Payments | Stripe Java SDK, Stripe Checkout |
| Testing | JUnit 5, Mockito, MockMvc, Testcontainers |
| Build and packaging | Gradle, Jib |

## Run locally

### Requirements

- Java 17
- MongoDB
- Firebase JWT issuer
- Firebase project ID
- Stripe test account

### Environment variables

Set the following environment variables with your own local or test configuration:

```bash
export JWT_ISSUER_URI="https://securetoken.google.com/your-firebase-project-id"
export FIREBASE_PROJECT_ID="your-firebase-project-id"
export STRIPE_SECRET_KEY="your-stripe-test-key"
export MONGODB_URI="mongodb://localhost:27017/eshop"
export MONGODB_DATABASE="eshop"
export SPRING_PROFILES_ACTIVE="development"
```

`SPRING_PROFILES_ACTIVE=development` enables Spring Data MongoDB and MongoDB driver TRACE logging, plus development-only Stripe management endpoints. TRACE logging is intentionally limited to the development profile because it can be very verbose.

### Start the application

```bash
./gradlew bootRun
```

The server runs on `http://localhost:8080`.

### Build and test

```bash
./gradlew compileJava
./gradlew test
```

The standard test command runs the service unit tests and standalone MockMvc tests. These tests use mocks, so they do not call the real Stripe API or require a running MongoDB instance.

Standalone MockMvc tests exercise controller behaviour without the full Spring Security filter chain. They do not establish end-to-end authentication or payment correctness.

After running the tests, open the generated HTML report:

```text
build/reports/tests/test/index.html
```

MongoDB integration tests use Testcontainers and require Docker. Run only the integration-tagged tests with:

```bash
./gradlew test -PincludeIntegrationTests
```

The integration test command is intentionally separate from the standard unit test command because it requires an available Docker daemon.

Authenticated endpoints require a Firebase JWT in the request:

```text
Authorization: Bearer <firebase-id-token>
```

Shopping cart endpoints require a valid Firebase ID token but allow an unverified email. Creating a store user through `PUT /user/me` and accessing transactions require the token to contain `email_verified: true`. After the user verifies their email, the frontend must refresh the Firebase user and force-refresh the ID token before calling those protected endpoints.
