# Multi-stage build for Java 17 microservice
FROM eclipse-temurin:17-jdk-alpine as build

WORKDIR /app

# Copy Maven wrapper and pom files
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY */pom.xml ./

# Replace with directory names
RUN mkdir -p domain/src common-utils/src store-common/src microservice/src

# Copy source code
COPY domain/src domain/src
COPY common-utils/src common-utils/src
COPY store-common/src store-common/src
COPY microservice/src microservice/src

# Build the application
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the built artifact from the build stage
COPY --from=build /app/microservice/target/*.jar app.jar

# Environment variables
ENV MONGODB_CONNECTION_STRING=mongodb://mongo:27017
ENV MONGODB_DATABASE_NAME=alphaentropy
ENV SPRING_PROFILES_ACTIVE=prod

# Expose the application port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
  CMD wget -q --spider http://localhost:8080/api/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
