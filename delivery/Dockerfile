FROM sralloza/openjdk:11-jre as build

WORKDIR /home/gradle

COPY gradle/ /home/gradle/gradle/
COPY build.gradle settings.gradle gradlew /home/gradle/
COPY src/ /home/gradle/src/

ARG TARGETARCH

# XXX: Skip tests for ARMv7. Tests with MockWebServer fail on shutdown with "java.io.IOException: Gave up waiting for queue to shut down" only on ARMv7.
RUN if [ "${TARGETARCH}" != "arm" ]; then ./gradlew clean test --console verbose; else echo "Skipping tests for ARMv7"; fi
RUN ./gradlew jar

FROM sralloza/openjdk:11-jre

COPY delivery/wait-for-it.sh /app/wait-for-it.sh

COPY --from=build /home/gradle/build/libs/*.jar /app/chore-management-bot.jar

CMD [ "java", "-jar", "/app/chore-management-bot.jar" ]
