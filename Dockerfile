FROM openjdk:19
ARG JAR_FILE=build/libs/BackendCart-0.0.1-SNAPSHOT.jar
ARG MONGODB_URI=mongodb://localhost:27017
ARG APPLICATION_PORT=8083
ARG KAFKA_URL=localhost:9092
ENV MONGODB_URI=$MONGODB_URI
ENV APPLICATION_PORT=$APPLICATION_PORT
ENV KAFKA_URL=$KAFKA_URL
EXPOSE 8083
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
