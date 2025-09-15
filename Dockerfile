FROM openjdk:17-jdk-slim
LABEL authors="Oussama"
ARG JAR_FILE=target/*.jar
COPY target/JWT-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar","/app.jar"]