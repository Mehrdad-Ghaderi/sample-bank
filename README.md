# 💰 Sample Bank

A backend-focused banking system built with **Spring Boot** that supports key operations, CRUD operations, account management, multi-currency accounts, transaction history, and balance tracking.

---

## 🚀 Tech Stack

- **Backend:** Java, Spring Boot, Spring Data JPA
- **Database:** PostgreSQL (hosted on [Supabase](https://supabase.io/) ☁️)
- **Tools:** GitHub, Maven, IntelliJ
- **Tests:** JUnit
- **Deployment:** Local (can be containerized or deployed to the cloud)

## 🌍 Cloud Database

The PostgreSQL database is hosted on **Supabase**, making this project ready for cloud deployments and scalable integrations. No local DB install needed — the app interacts with a cloud-native PostgreSQL database over REST.

---

## 📦 Features

- ➕ Add a new client
- 💱 Multi-currency support (USD, EUR, CAD) under one account
- 📞 Update client phone number
- 💸 Deposit, Withdraw, and Transfer money
- 🧾 View account balance per currency
- 🕓 Show latest transactions (with timestamp)
- ❌ Remove (deactivate) a client
- 🧊 Freeze an account
- 🏦 View overall bank balance
- 🔄 Abort any operation during its process

---

## 📌 Business Constraints

- Every client must have exactly **one** account
- Supported currencies: **USD, EUR, GBP, CAD**
- Removing a client does **not** delete historical records

---

## 🚧 Project Status

This project is under active development and is being evolved step by step toward
a production-grade, enterprise-style banking system.

Upcoming improvements include:
- CI/CD pipelines
- Docker & Kubernetes deployment
- Improved testing strategy
- Observability and logging
- Database migration and versioning

---

## 🛠️ Setup Instructions

### Prerequisites

- Java 21
- Maven
- Git
- Postman (Optional)

### Clone & Run

```bash
git clone https://github.com/Mehrdad-Ghaderi/sample-bank.git
cd sample-bank
./mvnw spring-boot:run
```

## Registry-Based Deployment With Compose

The CI pipeline publishes immutable app images to GitHub Container Registry (`ghcr.io`).

That changes the delivery model:

- Jenkins builds and tests the app
- Jenkins pushes a traceable Docker image tag
- Compose runs that pushed image instead of rebuilding from source

### Why this matters

This keeps deployment tied to the exact artifact that CI produced. In professional delivery systems, environments should consume a built artifact, not rebuild the application independently.

### Start PostgreSQL

```bash
docker compose up -d postgres
```

### Run the latest registry image through Compose

```bash
docker login ghcr.io
docker compose -f docker-compose.yml -f docker-compose.ghcr.yml up -d app
```

This uses `ghcr.io/mehrdad-ghaderi/sample-bank:latest`.

### Run a specific immutable image tag through Compose

```bash
docker login ghcr.io
APP_IMAGE=ghcr.io/mehrdad-ghaderi/sample-bank:develop-12-a1b2c3d docker compose -f docker-compose.yml -f docker-compose.ghcr.yml up -d app
```

Using an immutable tag is the stronger verification path because it proves the environment is running the exact image Jenkins produced for a specific branch, build, and commit.

### Verify the deployed container

```bash
docker compose ps
curl http://localhost:8080/actuator
```

If `/actuator` responds with `401 Unauthorized`, the container is running and Spring Security is protecting the endpoint as expected.

### What the override file does

`docker-compose.ghcr.yml` changes only the app image source:

- default app image becomes `ghcr.io/mehrdad-ghaderi/sample-bank:latest`
- `pull_policy: always` tells Compose to fetch the registry image instead of relying on whatever is cached locally

This keeps local development and deployment concerns separate:

- `docker-compose.yml` stays the base runtime definition
- `docker-compose.ghcr.yml` expresses deployment from a remote registry artifact
