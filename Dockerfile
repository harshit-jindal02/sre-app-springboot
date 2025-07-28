# Stage 1: Build the application using a full Maven and JDK image
# This image has multi-platform support, resolving the architecture error.
FROM maven:3.8.5-openjdk-17 AS build

# Set the working directory
WORKDIR /app

# Copy the pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the rest of the source code
COPY src ./src

# Package the application, skipping tests for a faster build
RUN mvn package -DskipTests

# Stage 2: Create the final, lightweight image
# Use a JRE image which is smaller than a full JDK
FROM openjdk:17-jre-slim

WORKDIR /app

# Copy the built JAR file from the 'build' stage
# The path is adjusted to where Spring Boot Maven plugin typically places the JAR
COPY --from=build /app/target/xmltojson-0.0.1-SNAPSHOT.jar app.jar

# Expose the port the application runs on
EXPOSE 6001

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
