FROM openjdk:21

EXPOSE 8080

ARG JAR_FILE

COPY ${JAR_FILE} image-server-configuration.jar

ENTRYPOINT ["java","-jar","image-server-configuration.jar"]