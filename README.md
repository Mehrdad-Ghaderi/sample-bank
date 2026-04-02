# Sample Bank

Sample Bank is a Spring Boot backend for banking operations such as client management, multi-currency account handling, deposits, withdrawals, transfers, transaction history, and balance tracking.

## Tech Stack

- Java 21
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Maven
- Docker and Docker Compose
- Jenkins
- GitHub Container Registry (GHCR)

## Core Features

- Add and manage clients
- Support USD, EUR, GBP, and CAD
- Update client information
- Deposit, withdraw, and transfer funds
- Show balances and recent transactions
- Freeze accounts
- Keep historical records when a client is removed

## Runtime Profiles

The application uses environment-specific Spring profiles:

- `local`: host-machine development against `localhost:5432`
- `docker`: app container talking to PostgreSQL at `postgres:5432`
- `test`: integration tests against `localhost:5432`
- `prod`: environment-driven configuration from external variables

Shared defaults live in [src/main/resources/application.properties](C:/Users/mehrd/work/sample-bank/src/main/resources/application.properties).

## Local Development

Prerequisites:

- Java 21
- Docker Desktop
- Git

Start PostgreSQL:

```bash
docker compose up -d postgres
```

Run the app from the host using the `local` profile:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

The default Spring Security credentials come from `application.properties`. A `401 Unauthorized` response from `/actuator` means the app is running and security is active.

## Docker Runtime

The repo [Dockerfile](C:/Users/mehrd/work/sample-bank/Dockerfile) uses a multi-stage build:

1. build the jar with Maven and JDK 21
2. copy the jar into a smaller JRE image

Run the application container with Compose:

```bash
docker compose up -d postgres
docker compose up -d app
```

The base Compose file [docker-compose.yml](C:/Users/mehrd/work/sample-bank/docker-compose.yml) uses:

- `postgres:15` for the database
- `APP_IMAGE`, defaulting to `sample-bank-app:latest`, for the app service

That `APP_IMAGE` variable is important because it lets Compose consume a previously built image instead of rebuilding from source.

## Jenkins CI/CD Flow

The pipeline lives in [Jenkinsfile](C:/Users/mehrd/work/sample-bank/Jenkinsfile).

Current flow:

1. Jenkins checks out the configured branch from SCM.
2. Jenkins verifies the workspace.
3. Jenkins computes a traceable image tag from branch name, Jenkins build number, and short commit SHA.
4. Jenkins runs `TransactionServiceIT` against PostgreSQL.
5. Jenkins builds the Docker image locally.
6. Jenkins tags the image for GHCR.
7. Jenkins pushes the immutable tag to GHCR.
8. Jenkins pushes `latest` only for `develop` and `main`.

This design gives every build an immutable artifact while preventing feature branches from moving the shared `latest` tag.

## GHCR Image Naming

Images are published to:

`ghcr.io/mehrdad-ghaderi/sample-bank`

Tag model:

- immutable tag: `<branch>-<jenkins-build-number>-<short-commit-sha>`
- mutable tag: `latest`

Examples:

- `ghcr.io/mehrdad-ghaderi/sample-bank:develop-12-a1b2c3d`
- `ghcr.io/mehrdad-ghaderi/sample-bank:cicd-ghcr-push-11-07437b8`

Important:

- every branch can push its own immutable tag
- only `develop` and `main` should publish `latest`

That is why a feature branch build can succeed even when no `latest` tag appears in GHCR.

## Jenkins Credentials For GHCR

Jenkins should store one credential with:

- kind: `Username with password`
- ID: `ghcr-io`
- username: GitHub username
- password: GitHub Personal Access Token

Minimum token scopes:

- `write:packages`
- `read:packages`

The token is not committed to Git. Jenkins reads it at runtime and uses it for `docker login ghcr.io`.

## Deploy From GHCR With Compose

The override file [docker-compose.ghcr.yml](C:/Users/mehrd/work/sample-bank/docker-compose.ghcr.yml) tells Compose to pull the app image from GHCR instead of using only a locally built image.

Start PostgreSQL:

```bash
docker compose up -d postgres
```

Log in to GHCR:

```bash
docker login ghcr.io
```

Run the latest registry image:

```bash
docker compose -f docker-compose.yml -f docker-compose.ghcr.yml up -d app
```

Run a specific immutable image:

```bash
APP_IMAGE=ghcr.io/mehrdad-ghaderi/sample-bank:develop-12-a1b2c3d docker compose -f docker-compose.yml -f docker-compose.ghcr.yml up -d app
```

Using an immutable tag is the stronger deployment practice because it guarantees the running container matches one specific CI build.

## Verification

Check running services:

```bash
docker compose ps
docker ps -a
```

Check the actuator endpoint:

```bash
curl http://localhost:8080/actuator
```

If `/actuator` returns `401 Unauthorized`, the container is running and the endpoint is protected by Spring Security.

## Repository Notes

- `notes/` is ignored by Git and used for local learning notes
- `jenkins_home/` is local Jenkins state and is ignored by Git
- `facenet/` is a separate nested repository and is not part of this repo's history
