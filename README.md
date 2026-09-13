# E-shop Backend

## Project description

A Spring Boot backend for an e-commerce application. It supports product browsing, authenticated shopping carts, transaction processing, and Stripe Checkout payments.

## Features

- Product listing, search, filtering, sorting, and price range queries
- Firebase JWT authentication
- MongoDB persistence for products, users, carts, and transactions
- Stripe Checkout session creation
- Jib-based container image build
- Development profile with MongoDB query logging

## Tech stack

- Java 17
- Spring Boot
- Spring Web
- Spring Security OAuth2 Resource Server
- Spring Data MongoDB
- Stripe Java SDK
- Gradle and Jib

## Run locally

### Requirements

- Java 17
- MongoDB
- Firebase JWT issuer
- Firebase project ID
- Stripe test account

### Environment variables

The application requires the following environment variables. No real credentials are included in this repository.

```bash
export JWT_ISSUER_URI="https://securetoken.google.com/your-firebase-project-id"
export FIREBASE_PROJECT_ID="your-firebase-project-id"
export STRIPE_SECRET_KEY="your-stripe-test-key"
export MONGODB_URI="mongodb://localhost:27017/eshop"
export MONGODB_DATABASE="MongoDB"
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

The latest test run completed successfully with 31 tests and 0 failures. The HTML test report is generated at:

```text
build/reports/tests/test/index.html
```

MongoDB integration tests use Testcontainers and require Docker. They can be run separately with:

```bash
./gradlew test -PincludeIntegrationTests
```

The integration test command is intentionally separate from the standard unit test command because it requires an available Docker daemon.

Authenticated endpoints require a Firebase JWT in the request:

```text
Authorization: Bearer <firebase-id-token>
```

Shopping cart endpoints require a valid Firebase ID token but allow an unverified email. Creating a store user through `PUT /user/me` and accessing transactions require the token to contain `email_verified: true`. After the user verifies their email, the frontend must refresh the Firebase user and force-refresh the ID token before calling those protected endpoints.
