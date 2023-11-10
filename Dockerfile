FROM amazoncorretto:21-alpine-jdk
EXPOSE 8080
ARG JAR_FILE=target/*.jar
COPY ./keystore.p12 keystore.p12
COPY ./target/canopusApi-0.0.1.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]