FROM amazoncorretto:21-alpine-jdk
EXPOSE 8080
ARG JAR_FILE=target/*.jar
COPY ./target/canopusApi-0.0.1.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]