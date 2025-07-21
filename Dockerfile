# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS buildgit checkout -b fix-docker-base-image
git add Dockerfile
git commit -m "fix: update base image to maven:3.9.6-eclipse-temurin-17"
git push origin fix-docker-base-image

WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Package stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/backend-0.0.3-SNAPSHOT.jar app.jar
ENV PORT=8080
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]