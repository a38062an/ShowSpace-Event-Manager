# Multi-stage Dockerfile for ShowSpace Spring Boot app
# Stage 1: Build
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /build

# Copy Maven files
COPY pom.xml .
COPY src ./src

# Install Maven
RUN apt-get update && apt-get install -y maven && \
    mvn clean package -DskipTests && \
    mv target/showspace-1.0.0.jar app.jar

# Stage 2: Runtime
FROM eclipse-temurin:17-jre
WORKDIR /app

# Create directory for H2 database persistence
RUN mkdir -p /app/data && chmod 777 /app/data

# Copy built JAR from builder stage
COPY --from=builder /build/app.jar app.jar

# Expose application port
EXPOSE 8080

# Set JVM options for container environment
ENV JAVA_OPTS="-Xmx256m -Xms128m -XX:+UseSerialGC"

# Run the application with JVM options
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
