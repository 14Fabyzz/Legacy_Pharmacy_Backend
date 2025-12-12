# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the built jar from build stage
COPY --from=build /app/target/usuarios-*.jar app.jar

# Expose port (Render will assign dynamically)
EXPOSE 8080

# Run with PORT environment variable
# This is CRITICAL for Render to detect the service
CMD ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar app.jar"]