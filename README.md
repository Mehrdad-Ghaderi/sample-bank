# Sample Bank

Sample Bank is a Spring Boot backend for customer onboarding, account management, JWT authentication, and money movement operations.

The project is intentionally built as more than a CRUD demo. It includes retry-safe transaction commands, object-level ownership checks, database migrations, layered tests, Docker packaging, and a local Jenkins-to-Kubernetes delivery path.

## Features

- Customer and account APIs with request validation and stable response DTOs.
- JWT login with stateless Bearer-token authentication.
- Object-level authorization so users can only access their own customers, accounts, and transactions.
- Transfer, deposit, and withdrawal commands with idempotency keys for safe retries.
- Transaction safety using pessimistic locking and deterministic account lock ordering.
- Stable paginated API responses that avoid exposing Spring Data `PageImpl` JSON.
- PostgreSQL schema management with Liquibase and Hibernate validation.
- Unit, controller, and Testcontainers-backed integration tests.
- Docker image build, GHCR publishing, Helm deployment, and Kubernetes rollout verification through Jenkins.

## Stack

- Java 21
- Spring Boot 3
- Spring Security / OAuth2 Resource Server
- Spring Data JPA
- PostgreSQL
- Liquibase
- Maven
- Docker
- Jenkins
- Helm
- Kubernetes
- Terraform

## API Overview

Base path: `/api/v1`

| Area | Endpoints |
| --- | --- |
| Auth | `POST /auth/login` |
| Customers | `GET /customers`, `POST /customers`, `GET /customers/{customerId}`, `PATCH /customers/{customerId}`, `PATCH /customers/{customerId}/activate`, `PATCH /customers/{customerId}/deactivate` |
| Customer accounts | `POST /customers/{customerId}/accounts`, `GET /customers/{customerId}/accounts` |
| Accounts | `GET /accounts`, `GET /accounts/{accountId}`, `PATCH /accounts/{accountId}` |
| Transactions | `GET /transactions`, `POST /transactions/transfers`, `POST /transactions/deposits`, `POST /transactions/withdrawals` |

Authenticated endpoints require:

```http
Authorization: Bearer <jwt>
```

Transaction command endpoints also require:

```http
Idempotency-Key: <unique-client-generated-key>
```

## Transaction Model

Transaction creation is exposed as three business commands instead of one generic transaction request:

- `transfer`: customer account to customer account
- `deposit`: bank treasury account to customer account
- `withdraw`: customer account to bank treasury account

The bank treasury account is an internal system account identified by `AccountRole.BANK_TREASURY`. Clients do not supply that account id for deposits or withdrawals. The service resolves the correct treasury account by currency and keeps that routing rule inside the backend.

Idempotency keys make money-moving retries safe. Repeating the same command with the same key returns the original transaction. Reusing the same key with a different request is rejected as a conflict.

## Architecture Notes

The code follows a layered backend structure:

- `api`: controllers, request/response DTOs, exception handling, and API path constants
- `domain`: entities, repositories, business services, and domain rules
- `security`: JWT creation, authentication, and ownership integration
- `resources/db/changelog`: Liquibase migrations

DTOs are named by role, such as `CreateCustomerRequest`, `AccountResponse`, and `TransactionResponse`. This keeps public API contracts separate from JPA entities and avoids leaking persistence or framework-specific JSON shapes to clients.

## Database Migrations

Database schema changes are managed with Liquibase. The master changelog lives at [src/main/resources/db/changelog/db.changelog-master.yaml](src/main/resources/db/changelog/db.changelog-master.yaml).

Hibernate validates the schema at startup, while Liquibase owns creating and evolving database objects. This keeps schema changes versioned, repeatable, and safer than relying on automatic Hibernate schema updates in deployed environments.

## Runtime Profiles

- `local`: host-machine development against `localhost:5432`
- `docker`: container runtime against `postgres:5432`
- `test`: integration tests
- `prod`: environment-driven runtime configuration

## Local Development

Prerequisites:

- Java 21
- Docker
- Maven wrapper from this repository

Start PostgreSQL:

```bash
docker compose up -d postgres
```

Run the application with the local profile:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

The local profile expects:

- database: `sample_bank`
- username: `sample_bank`
- password: `sample_bank`
- host: `localhost`
- port: `5432`

## Tests

Run unit and controller tests:

```bash
./mvnw test
```

Run integration tests:

```bash
./mvnw -DskipUnitTests=true verify
```

Integration tests use Testcontainers and require Docker to be available.

## Container Runtime

For local container runs, use [docker-compose.yml](docker-compose.yml) with local `.env` values:

```bash
docker compose --env-file .env up -d postgres
docker compose --env-file .env up -d app
```

Docker Compose is the local/dev runtime path.

## CI/CD

The pipeline lives in [Jenkinsfile](Jenkinsfile).

The Jenkins pipeline provides a branch-aware delivery workflow:

1. Run unit and integration test gates.
2. Build a Docker image.
3. Push immutable, traceable image tags to GHCR.
4. Deploy selected branches to a local Kubernetes environment through Helm.
5. Verify rollout status, deployed image identity, replica availability, and application health.

Published image repository:

```text
ghcr.io/mehrdad-ghaderi/sample-bank
```

For local Jenkins runs, [docker-compose.jenkins.yml](docker-compose.jenkins.yml) expects the host kubeconfig and Minikube certificate directory to be mounted into the Jenkins container. Use [.env.jenkins.example](.env.jenkins.example) as the template for machine-specific paths.

## Kubernetes

Kubernetes deployment is Helm-managed.

Current model:

- the application runs in Kubernetes
- PostgreSQL remains outside Kubernetes
- runtime secrets are applied before deployment
- deployment uses an explicit immutable GHCR image tag
- the Helm chart lives in [helm](helm)

Example deploy:

```bash
helm upgrade --install sample-bank ./helm --namespace sample-bank --create-namespace --set image.tag=develop-24-8d9f17b
```

## Engineering Decisions Worth Reviewing

- Money-moving POST operations use command-specific request DTOs instead of one overloaded transaction DTO.
- Idempotency keys protect clients from duplicate transfers when retries happen.
- Account balance updates use pessimistic locking and deterministic ordering to reduce race conditions and deadlock risk.
- Liquibase owns schema evolution, while Hibernate only validates the schema.
- API responses use explicit response DTOs and stable pagination instead of returning entities or framework internals.
- Tests are split by responsibility: controller tests for HTTP behavior, service tests for business rules, and integration tests for database-backed transaction behavior.
