# 1. Build stage: use Maven + JDK 17 to compile and package
FROM maven:3.8.6-openjdk-17 AS build
WORKDIR /app

# Copy only POM, download deps
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

# Copy source & build
COPY src ./src
RUN ./mvnw clean package -DskipTests

# 2. Run stage: use a slim JRE image
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the fat JAR from the build stage
COPY --from=build /app/target/sample-bank-0.0.1-SNAPSHOT.jar app.jar

# Let Spring Boot pick up the PORT env var, bind all interfaces
ENV JAVA_OPTS="-Dserver.address=0.0.0.0 -Dserver.port=\${PORT:8080}"
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]
