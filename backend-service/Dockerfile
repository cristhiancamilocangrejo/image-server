FROM openjdk:21

EXPOSE 8080

ARG JAR_FILE

COPY ${JAR_FILE} image-server-service.jar

ENTRYPOINT ["java","-jar","image-server-service"]