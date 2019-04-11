FROM openjdk:8-jdk-alpine
COPY target/pay-*.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 10010