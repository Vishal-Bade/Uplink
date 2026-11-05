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

# Add curl, create uploads directory, and configure permissions for non-root user
RUN apk add --no-cache curl && \
    addgroup -S spring && adduser -S spring -G spring && \
    mkdir -p /app/uploads && \
    chown -R spring:spring /app

USER spring:spring

# Expose default web port
EXPOSE 8080

# Copy executable jar from build stage
COPY --from=build --chown=spring:spring /app/target/*.jar app.jar

# JVM Container Ergonomics optimized for cloud environments (512MB RAM)
ENV JAVA_OPTS="-XX:+UseSerialGC -Xmx384m -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
