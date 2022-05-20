FROM amazoncorretto:11-alpine-jdk
MAINTAINER baeldung.com
COPY target/retailstore-0.0.1-SNAPSHOT.jar retailstore-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/retailstore-0.0.1-SNAPSHOT.jar"]