FROM gradle:8.7-jdk21 AS build
USER gradle
WORKDIR /home/gradle/project

COPY --chown=gradle:gradle build.gradle settings.gradle ./
COPY --chown=gradle:gradle gradle gradle
COPY --chown=gradle:gradle gradlew gradlew
COPY --chown=gradle:gradle gradlew.bat gradlew.bat

RUN gradle --no-daemon dependencies || true

COPY --chown=gradle:gradle . .

RUN gradle --no-daemon bootJar -x test

FROM eclipse-temurin:21-jre
ENV APP_HOME=/app
WORKDIR ${APP_HOME}

ARG PORT=8080
ENV PORT=${PORT}

COPY --from=build /home/gradle/project/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT}"]