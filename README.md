# Sample Bank

Sample Bank is a Spring Boot backend for customer, account, balance, and transaction operations.

## Transaction Model

Transaction creation is exposed as three business commands instead of one generic request:

- `transfer`: customer account to customer account
- `deposit`: bank treasury account to customer account
- `withdraw`: customer account to bank treasury account

The bank treasury account is an internal system account identified by `AccountRole.BANK_TREASURY`.
Clients do not need to supply that account id for deposits or withdrawals. The service resolves the
correct treasury account by currency and keeps that routing rule inside the backend.

## Stack

- Java 21
- Spring Boot
- PostgreSQL
- Maven
- Docker
- Jenkins
- Kubernetes
- Helm

## Runtime Profiles

- `local`: host-machine development against `localhost:5432`
- `docker`: container runtime against `postgres:5432`
- `test`: integration tests
- `prod`: environment-driven runtime configuration

## Local Development

Start PostgreSQL:

```bash
docker compose up -d postgres
```

Run the application:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

## Container Runtime

For local container runs, use [docker-compose.yml](C:/Users/mehrd/work/sample-bank/docker-compose.yml) with local `.env` values:

```bash
docker compose --env-file .env up -d postgres
docker compose --env-file .env up -d app
```

Docker Compose is the local/dev runtime path.

## CI/CD

The pipeline lives in [Jenkinsfile](C:/Users/mehrd/work/sample-bank/Jenkinsfile).

The Jenkins pipeline provides a branch-aware delivery workflow:

1. run unit and integration test gates
2. build a Docker image
3. push an immutable, traceable image tag to GHCR
4. deploy selected branches to a local Kubernetes environment through Helm
5. verify the Kubernetes rollout, deployed image identity, replica availability, and application health

Published image repository:

`ghcr.io/mehrdad-ghaderi/sample-bank`

For local Jenkins runs, [docker-compose.jenkins.yml](C:/Users/mehrd/work/sample-bank/docker-compose.jenkins.yml) expects the host kubeconfig and Minikube certificate directory to be mounted into the Jenkins container. Use [.env.jenkins.example](C:/Users/mehrd/work/sample-bank/.env.jenkins.example) as the template for those machine-specific paths.

## Kubernetes

Kubernetes deployment is Helm-managed.

Current model:

- application runs in Kubernetes
- PostgreSQL remains outside Kubernetes
- deployment uses an explicit immutable GHCR image tag
- chart lives in [helm](C:/Users/mehrd/work/sample-bank/helm)

Example deploy:

```bash
helm upgrade --install sample-bank ./helm --namespace sample-bank --create-namespace --set image.tag=develop-24-8d9f17b
```
