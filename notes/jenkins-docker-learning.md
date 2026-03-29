# Jenkins and Docker Learning Notes

## 1. What a Docker image is

A Docker image is a packaged blueprint for running an application.

It usually contains:
- the application code or built artifact
- the runtime it needs
- the startup command

Runtime means the software environment needed to execute the app after it has already been built.

For this project, the runtime mainly means:
- Linux inside the container
- Java Runtime Environment (`JRE` = Java Runtime Environment)
- the command that starts the Spring Boot jar

Important distinction:
- image = blueprint
- container = a running instance created from that blueprint

## 2. Where the image lives

A Docker image is not stored as a normal file inside this project folder.

It is stored inside Docker's internal image storage managed by the Docker engine.

Ways to see it:
- Docker Desktop -> `Images`
- terminal command: `docker images`
- terminal command: `docker image inspect sample-bank-app:latest`

Important:
- `sample-bank-app:latest` is the current local app image built from this project
- it stays on the local machine unless a `docker push` command is added later

Useful command:

```bash
docker image inspect sample-bank-app:latest
```

## 3. What Jenkins is doing

Jenkins is the automation server that runs your pipeline.

Current pipeline flow:
1. Jenkins checks out branch `infra/local-cicd` from Git
2. Jenkins verifies its workspace
3. Jenkins runs `TransactionServiceIT`
4. Jenkins builds Docker image `sample-bank-app:latest`

Important:
- branch selection happens in Jenkins job configuration
- the `Jenkinsfile` defines the steps, not the branch

Current Jenkins pipeline code:

```groovy
pipeline {
    agent any

    environment {
        SPRING_DATASOURCE_URL = 'jdbc:postgresql://host.docker.internal:5432/sample_bank'
        SPRING_DATASOURCE_USERNAME = 'sample_bank'
        SPRING_DATASOURCE_PASSWORD = 'sample_bank'
        APP_IMAGE_NAME = 'sample-bank-app'
    }

    stages {
        stage('Checkout Source') {
            steps {
                checkout scm
            }
        }

        stage('Verify Workspace') {
            steps {
                sh 'pwd'
                sh 'ls -la'
            }
        }

        stage('Run Transaction Integration Test') {
            steps {
                sh 'chmod +x mvnw'
                sh './mvnw clean -Dtest=TransactionServiceIT test'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t ${APP_IMAGE_NAME}:latest .'
            }
        }
    }
}
```

## 4. Why build the app image

Building the app image proves that:
- the code can be packaged successfully
- the packaged app can run in a container environment
- the same package can later be run on another machine or deployment platform

Right now the image is only built locally.

It is not automatically:
- pushed to Docker Hub
- pushed to GitHub Container Registry (`GHCR` = GitHub Container Registry)
- saved as a normal file in the repo

To upload an image later, the pipeline would need extra commands such as:
- `docker login`
- `docker tag`
- `docker push`

## 5. Port mapping basics

Port mapping format:

`host_port:container_port`

Examples:
- `9090:8080`
- `5432:5432`

Meaning:
- left side = port on Windows host machine
- right side = port inside the container

Examples in this project:
- Jenkins uses `9090:8080`
  - open `localhost:9090` on Windows
  - traffic is forwarded to port `8080` inside the Jenkins container
- PostgreSQL uses `5432:5432`
  - Windows port and container port are the same here
- App uses `8080:8080`
  - Windows port and container port are the same here

Important network rule:
- inside a container, `localhost` means that same container
- inside Jenkins container, `localhost` does not mean Windows host
- `host.docker.internal` is the special hostname that lets a container reach the host machine

That is why Jenkins uses:
- `jdbc:postgresql://host.docker.internal:5432/sample_bank`

## 6. Dockerfile basics for this project

This project uses a multi-stage Docker build.

Multi-stage means:
- one stage builds the app
- another stage creates the smaller final runtime image

Important lines:
- `FROM maven:... AS build`
  - build stage with Maven and Java
- `RUN ./mvnw clean package -DskipTests`
  - creates the jar
- `FROM eclipse-temurin:21-jre`
  - final runtime image with Java only
- `COPY --from=build ... app.jar`
  - copies built jar into final image
- `ENTRYPOINT [...]`
  - defines how the container starts

Current Dockerfile:

```dockerfile
FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw dependency:go-offline

COPY src ./src
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/sample-bank-0.0.1-SNAPSHOT.jar app.jar

ENV JAVA_OPTS="-Dserver.address=0.0.0.0"
ENV PORT="8080"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=$PORT -jar app.jar"]
```

## 7. Docker Compose basics in this project

`docker-compose.yml` defines:
- `postgres` service
- `app` service

`docker-compose.jenkins.yml` defines:
- `jenkins` service

Compose means Docker Compose:
- a tool for starting related containers together from a YAML file

Current app and database Compose file:

```yaml
services:
  postgres:
    image: postgres:15
    container_name: sample-bank-postgres
    environment:
      POSTGRES_DB: sample_bank
      POSTGRES_USER: sample_bank
      POSTGRES_PASSWORD: sample_bank
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  app:
    build:
      context: .
    container_name: sample-bank-app
    depends_on:
      - postgres
    environment:
      SPRING_PROFILES_ACTIVE: docker
    ports:
      - "8080:8080"

volumes:
  postgres_data:
```

## 8. Commands used and what they mean

### Jenkins and Docker status

`docker ps`
- shows running containers

`docker ps -a`
- shows all containers, including stopped ones

`docker logs sample-bank-jenkins`
- shows logs from Jenkins container

### Start services

`docker compose -f docker-compose.jenkins.yml up -d`
- start Jenkins service from the Jenkins Compose file
- `-f` means use a specific Compose file
- `up` means create/start services
- `-d` means detached mode, which runs in background

`docker compose up -d postgres`
- start only PostgreSQL service from main Compose file

`docker compose up -d app`
- start the app service from main Compose file
- Compose also ensures the related PostgreSQL container is available

`docker compose up -d --force-recreate app`
- recreate the app container using the latest built image
- `--force-recreate` means replace the existing container even if Docker thinks it could reuse it

`docker compose down`
- stop and remove containers from the current Compose file

`docker compose -f docker-compose.jenkins.yml down`
- stop and remove the Jenkins container from the Jenkins Compose file

### Image commands

`docker images`
- list local images

`docker image inspect sample-bank-app:latest`
- show detailed metadata about the app image

`docker build -t sample-bank-app:latest .`
- build an image from the current folder
- `-t` means tag
- `.` means use current folder as build context

`docker logs sample-bank-app`
- show logs from the app container

### Git commands

`git status --short`
- compact view of changed files

`git branch --all`
- show local and remote branches

`git add Jenkinsfile`
- stage file for commit

`git commit -m "message"`
- create a commit with a message

`git push origin infra/local-cicd`
- push local branch `infra/local-cicd` to remote `origin`

### Jenkins build expectations

Before Jenkins build:
- Jenkins container should be running
- PostgreSQL container should be running

If PostgreSQL is stopped, integration tests can fail even if the pipeline is correct.

## 9. Most important lessons so far

- Jenkins pipeline steps and Jenkins branch selection are two different things
- removing hard-coded `/workspace` was correct for SCM-driven Jenkins jobs
- integration tests depend on PostgreSQL being alive
- Docker image build is separate from Docker image push
- a successful local image build means the app is now packaged in a deployable container format
- a `401 Unauthorized` response means the app is running and reachable, but login is required
- if a container is up but the app does not answer on the expected port, container logs are the first place to inspect
