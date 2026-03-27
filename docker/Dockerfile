FROM eclipse-temurin:25-jdk AS builder
WORKDIR /app
COPY . .
RUN ./gradlew bootJar -p apps/api-app

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=builder /app/apps/api-app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

