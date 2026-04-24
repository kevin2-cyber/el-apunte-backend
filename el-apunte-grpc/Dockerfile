# --- Stage 1: The Builder (Compiles the Code) ---
FROM eclipse-temurin:17-jdk-jammy AS builder
WORKDIR /app

# 1. Copy Gradle wrapper & settings first (Cached Layer)
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 2. Download dependencies (This layer rarely changes)
RUN ./gradlew dependencies --no-daemon

# 3. Copy source code & build the JAR
COPY src src
# "bootJar" creates the executable Spring Boot JAR
RUN ./gradlew bootJar -x test --no-daemon

# --- Stage 2: The Runtime (Runs the App) ---
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Create a non-root user (Best practice for security)
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

# Copy the built JAR from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose ports: 9090 (gRPC) and 8080 (HTTP)
EXPOSE 9090 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]