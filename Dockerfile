# Etapa 1: Build cu Gradle + JDK 17 (merge ok)
FROM gradle:8.7.0-jdk17 AS build

WORKDIR /app

COPY . .

RUN gradle clean build --no-daemon

# Etapa 2: Runtime cu JDK 23
FROM openjdk:23-jdk-slim

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
