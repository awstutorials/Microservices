FROM openjdk:8-jre-slim

# Create app directory
WORKDIR /opt/app

ARG JAR_FILE

RUN apt-get update && apt-get install -y curl python python-pip jq && pip install awscli

COPY entrypoint.sh .
COPY $JAR_FILE application.jar

EXPOSE 8080

ENTRYPOINT ["sh", "entrypoint.sh"]