FROM openjdk:8-jdk-alpine
VOLUME ["/tmp"]
ADD api-0.0.1.jar app.jar
ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,address=5005,server=y,suspend=n", "-Dspring.data.mongodb.uri=mongodb://mongo/taskmgr", "-Dspring.redis.host=redis", "-Dspring.profiles.active=prod", "-Djava.security.egd=file:/dev/./urandom", "-jar","/app.jar"]
MAINTAINER Peng Wang "wpcfan@gmail.com"
