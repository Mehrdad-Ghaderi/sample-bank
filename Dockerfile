# 1. Build stage
FROM maven:3.8.6-eclipse-temurin-17-alpine AS build
WORKDIR /app

COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

COPY src ./src
RUN ./mvnw clean package -DskipTests

# 2. Run stage
FROM eclipse-temurin:17-jre
WORKDIR /app
EXPOSE 10000

COPY --from=build /app/target/sample-bank-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["sh", "-c", "java -Dserver.address=0.0.0.0 -Dserver.port=${PORT:-10000} -jar app.jar"]
