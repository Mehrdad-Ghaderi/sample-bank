# 1. Build stage: use Maven + JDK 21 to compile and package
FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /app

# Copy only Maven wrapper + POM, set execution permission, and download dependencies
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw dependency:go-offline

# Copy source code and build the project
COPY src ./src
RUN ./mvnw clean package -DskipTests

# 2. Run stage: use a slim JRE image with Java 21
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the packaged fat JAR from build stage
COPY --from=build /app/target/sample-bank-0.0.1-SNAPSHOT.jar app.jar

# Bind the app to all interfaces and default to port 8080 inside the container.
ENV JAVA_OPTS="-Dserver.address=0.0.0.0"
ENV PORT="8080"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=$PORT -jar app.jar"]
