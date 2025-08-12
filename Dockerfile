# 1. Build stage: use Maven + JDK 21 to compile and package
FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /app

# Copy only POM, download dependencies offline
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

# Copy source code and build the project
COPY src ./src
RUN ./mvnw clean package -DskipTests

# 2. Run stage: use a slim JRE image with Java 21
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the packaged fat JAR from build stage
COPY --from=build /app/target/sample-bank-0.0.1-SNAPSHOT.jar app.jar

# Pass environment variables and bind all interfaces
ENV JAVA_OPTS="-Dserver.address=0.0.0.0 -Dserver.port=\${PORT:-8080}"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
