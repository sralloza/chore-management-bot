FROM sralloza/openjdk:11-jre as build

WORKDIR /home/gradle

COPY gradle/ /home/gradle/gradle/
COPY build.gradle settings.gradle gradlew /home/gradle/
COPY src/ /home/gradle/src/

# RUN ./gradlew build
# RUN ./gradlew test --scan
RUN ./gradlew fat -i

FROM sralloza/openjdk:11-jre

COPY utils/wait-for-it.sh /app/wait-for-it.sh

COPY --from=build /home/gradle/build/libs/*.jar /app/chore-management-bot.jar

CMD [ "java", "-jar", "/app/chore-management-bot.jar" ]
