
FROM gradle:8.7.0-jdk23 AS build

WORKDIR /app

COPY . .

RUN gradle clean build --no-daemon


FROM openjdk:23-jdk-slim

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
