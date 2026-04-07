# Sample Bank

Sample Bank is a Spring Boot backend for customer, account, balance, and transaction operations.

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

Current `develop` flow:

1. run `TransactionServiceIT`
2. build the Docker image
3. push an immutable image to GHCR
4. deploy that image to Kubernetes through Helm
5. verify the Kubernetes rollout and deployed image

Published image repository:

`ghcr.io/mehrdad-ghaderi/sample-bank`

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

## Repository Notes

- `notes/` is local and not tracked
- `jenkins_home/` is local Jenkins state and not tracked
- `facenet/` is a separate nested repository
