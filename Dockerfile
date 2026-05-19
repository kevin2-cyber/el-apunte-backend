# --- Stage 1: The Builder (Compiles the Code) ---
FROM eclipse-temurin:25-jdk-jammy AS builder
WORKDIR /app

# 1. Copy Gradle wrapper & settings first (Cached Layer)
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

# 2. Download dependencies (This layer rarely changes)
RUN ./gradlew dependencies --no-daemon --configuration-cache

# 3. Copy source code & build the JAR
COPY src src
RUN ./gradlew bootJar -x test --no-daemon --configuration-cache

# --- Stage 2: The Runtime (Runs the App) ---
FROM eclipse-temurin:25-jre-jammy
WORKDIR /app

# Create a non-root user (Best practice for security)
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

# Copy the built JAR from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
