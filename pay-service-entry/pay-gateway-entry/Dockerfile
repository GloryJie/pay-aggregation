FROM openjdk:8-jdk-alpine
COPY target/pay-*.jar /app.jar
ENV PRE_PARAM=""
ENTRYPOINT ["sh","-c","java $PRE_PARAM -jar /app.jar"]