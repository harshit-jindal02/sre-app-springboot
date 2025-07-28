# Stage 1: Build the application using a full Maven and JDK image
# Explicitly setting the platform to linux/amd64 to ensure compatibility on all systems (e.g., Apple Silicon)
FROM --platform=linux/amd64 maven:3.8.5-openjdk-17 AS build

# Set the working directory
WORKDIR /app

# Copy the pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the rest of the source code
COPY src ./src

# Package the application, skipping tests for a faster build
RUN mvn package -DskipTests

# Stage 2: Create the final, lightweight image using a reliable base image
# Explicitly setting the platform here as well for consistency.
FROM --platform=linux/amd64 eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the built JAR file from the 'build' stage
# The path is adjusted to where Spring Boot Maven plugin typically places the JAR
COPY --from=build /app/target/xmltojson-0.0.1-SNAPSHOT.jar app.jar

# Expose the port the application runs on
EXPOSE 6001

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
