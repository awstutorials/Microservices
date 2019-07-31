FROM openjdk:8-jre-slim
VOLUME /tmp
COPY ./target/limits-service-new-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]