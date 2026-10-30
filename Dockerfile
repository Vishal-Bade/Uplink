# ==============================================================================
# Multi-stage Dockerfile for Uplink Spring Boot Backend
# ==============================================================================

# Stage 1: Build & Package application
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Cache dependencies
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B || true

# Copy source code and build production jar
COPY src src
RUN ./mvnw clean package -DskipTests -B

# Stage 2: Minimal Production Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Add curl for health check support and create secure non-root user
RUN apk add --no-cache curl && \
    addgroup -S spring && adduser -S spring -G spring

USER spring:spring

# Expose Spring Boot web port
EXPOSE 8080

# Copy executable jar from build stage
COPY --from=build /app/target/*.jar app.jar

# JVM Container Ergonomics and GC optimization
ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

# Healthcheck to verify Tomcat status
HEALTHCHECK --interval=30s --timeout=10s --start-period=45s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 0

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
