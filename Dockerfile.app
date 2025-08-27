FROM gradle:8.12

WORKDIR /app

COPY ./src/ /app/src/
COPY ./gradle/ /app/gradle/
COPY ./.env /app
COPY ./build.gradle.kts /app
COPY ./gradlew /app
COPY ./settings.gradle.kts /app

CMD ["sh", "-c", "./gradlew bootRun"]
