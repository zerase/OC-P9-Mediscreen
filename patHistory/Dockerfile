FROM openjdk:11
VOLUME /tmp
EXPOSE 8082
ARG JAR_FILE=target/patHistory-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]