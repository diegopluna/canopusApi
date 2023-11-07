FROM ubuntu:latest AS build

RUN apt-get update
RUN wget -O - https://apt.corretto.aws/corretto.key | gpg --dearmor -o /usr/share/keyrings/corretto-keyring.gpg && \ echo "deb [signed-by=/usr/share/keyrings/corretto-keyring.gpg] https://apt.corretto.aws stable main" | tee /etc/apt/sources.list.d/corretto.list
RUN apt-get update; apt-get install -y java-21-amazon-corretto-jdk
COPY . .

RUN apt-get install maven -y

FROM amazoncorretto:21-alpine-jdk

EXPOSE 8080

COPY --from=build /target/canopusApi-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT [ "java", "-jar", "app.jar" ]