# Use official OpenJDK base image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy JAR file to the container
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar

# Expose port (dacă aplicația pornește pe 8080)
EXPOSE 8080

# Command to run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]
