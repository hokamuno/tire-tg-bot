FROM gradle:8.12

WORKDIR /app

COPY ./gradle/ /app/gradle/
COPY ./settings.gradle.kts /app
COPY ./build.gradle.kts /app
COPY ./gradlew /app

COPY ./src/ /app/src/
COPY ./.env /app

CMD ["sh", "-c", "./gradlew bootRun"]