FROM openjdk:17 as builder
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY src src
RUN chmod +x ./gradlew
RUN ./gradlew bootjar


FROM openjdk:17
COPY --from=builder build/libs/*.jar app.jar
EXPOSE 50001
ENTRYPOINT ["java","-Dspring.profiles.active=product","-jar","/app.jar"]
