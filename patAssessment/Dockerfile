FROM openjdk:11
VOLUME /tmp
EXPOSE 8083
ARG JAR_FILE=target/patAssessment-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]