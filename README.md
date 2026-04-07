# Sample Bank

Sample Bank is a Spring Boot backend for customer, account, balance, and transaction operations.

## Stack

- Java 21
- Spring Boot
- PostgreSQL
- Maven
- Docker / Docker Compose
- Jenkins
- Kubernetes

## Runtime Profiles

- `local`: host-machine development against `localhost:5432`
- `docker`: app container talking to PostgreSQL at `postgres:5432`
- `test`: integration tests
- `prod`: environment-driven runtime configuration

## Local Development

Start PostgreSQL:

```bash
docker compose up -d postgres
```

Run the app:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

## Docker Compose

The Docker runtime uses:

- [docker-compose.yml](C:/Users/mehrd/work/sample-bank/docker-compose.yml)
- local `.env` for runtime values
- [application-docker.properties](C:/Users/mehrd/work/sample-bank/src/main/resources/application-docker.properties) for env-based Docker profile configuration

Start the Compose runtime:

```bash
docker compose --env-file .env up -d postgres
docker compose --env-file .env up -d app
```

Docker Compose remains the local/dev runtime path.

## CI/CD

The pipeline lives in [Jenkinsfile](C:/Users/mehrd/work/sample-bank/Jenkinsfile).

Current flow:

1. run `TransactionServiceIT`
2. build the Docker image
3. tag and push the image to GHCR
4. on `develop`, deploy the immutable GHCR image to Kubernetes through Helm

Images are published to:

`ghcr.io/mehrdad-ghaderi/sample-bank`

## Kubernetes

Current model:

- app runs in Kubernetes
- PostgreSQL stays outside Kubernetes
- deployment is Helm-managed
- deployment requires an explicit immutable GHCR image tag

## Helm

The Helm chart lives in [helm](C:/Users/mehrd/work/sample-bank/helm).

Current goal:

- keep the same Kubernetes runtime model
- make image repository and image tag values-driven
- align the deployment packaging approach with `facenet`
- make Helm/Kubernetes the official CD deployment path

Render or install the Helm chart with an explicit image tag:

```bash
helm upgrade --install sample-bank ./helm --namespace sample-bank --create-namespace --set image.tag=develop-12-a1b2c3d
```

## Repository Notes

- `notes/` is local and not tracked
- `jenkins_home/` is local Jenkins state and not tracked
- `facenet/` is a separate nested repository
