FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY . .
RUN ./gradlew :apps:api-app:bootJar -x test

FROM eclipse-temurin:21-jre
WORKDIR /app

# Install Chromium and its system dependencies for Playwright
RUN apt-get update && apt-get install -y --no-install-recommends \
    chromium \
    libnss3 libatk1.0-0 libatk-bridge2.0-0 libcups2 \
    libdrm2 libxkbcommon0 libxcomposite1 libxdamage1 \
    libxfixes3 libxrandr2 libgbm1 libasound2 \
    && rm -rf /var/lib/apt/lists/*

# Tell Playwright to use the system Chromium instead of downloading its own
ENV PLAYWRIGHT_BROWSERS_PATH=/usr/bin
ENV PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD=1

COPY --from=builder /app/apps/api-app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
