FROM eclipse-temurin:21-jre
WORKDIR /app
COPY build/libs/readygsm-server-*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]