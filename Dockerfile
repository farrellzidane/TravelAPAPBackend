# ===== Build stage =====
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle* settings.gradle* ./

RUN chmod +x gradlew && ./gradlew --no-daemon dependencies || true

COPY . .

RUN chmod +x gradlew && ./gradlew --no-daemon clean bootJar -x test

RUN JAR_FILE=$(ls build/libs/*.jar | grep -v plain | head -n 1) \
  && echo "Using jar: $JAR_FILE" \
  && cp "$JAR_FILE" /app/app.jar


# ===== Run stage =====
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/app.jar app.jar

EXPOSE 10000
CMD ["sh","-c","java -jar app.jar --server.port=${PORT:-10000}"]
