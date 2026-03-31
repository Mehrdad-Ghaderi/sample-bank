# CI/CD Progress

Current branch:
- `infra/local-cicd`

Latest relevant commit:
- `2f87cdf` `ci: build app image in Jenkins`

Current working state:
- local app runs with profile `local`
- Docker Postgres runs on `5432`
- Jenkins runs in Docker on `http://localhost:9090`
- Jenkins job is configured as `Pipeline script from SCM`
- Jenkins builds branch `infra/local-cicd`
- Jenkins green pipeline checks out repo code, runs `TransactionServiceIT`, and builds Docker image `sample-bank-app:latest`
- local Docker image `sample-bank-app:latest` builds successfully
- app container starts successfully with profile `docker` and responds on container port `8080`

Current local tooling:
- Docker Desktop
- Docker Desktop Kubernetes enabled (`kind`)
- Jenkins in Docker
- PostgreSQL in Docker

Important repo/runtime decisions:
- `application.properties` holds shared defaults
- `application-local.properties` is for IntelliJ/host runs
- `application-docker.properties` is for app-in-container runs
- `application-test.properties` is for integration tests
- `application-prod.properties` is env-driven
- `.gitignore` ignores `jenkins_home/` and `facenet/`
- `Jenkinsfile` uses Jenkins SCM workspace instead of hard-coded `/workspace`
- `Dockerfile` defaults app container startup to port `8080`

Known local environment notes:
- Apache/PEM service `PEMHTTPD-x64` was stopped and set to `Manual` so port `8080` is free
- IntelliJ app runs should use active profile `local`
- Jenkins integration tests require PostgreSQL container to be running first
- app endpoints are protected by Spring Security default credentials from `application.properties`
- `http://localhost:8080/actuator` returning `401 Unauthorized` means the app is running but requires authentication

Next step:
1. Tag Docker images with something better than only `latest` such as Jenkins build number or Git commit
2. Decide whether the next delivery target is:
   - local `docker run`
   - registry push
   - deployment with Docker Compose
   - deployment with Kubernetes
3. If registry push is chosen later, add explicit `docker login`, `docker tag`, and `docker push` stages
