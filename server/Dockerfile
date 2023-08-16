FROM amazoncorretto:11-alpine-jdk
LABEL authors="marilu34"
ARG JAR_FILE=target/*.jar
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:9091
COPY ${JAR_FILE} share-it-gateway.jar
ENTRYPOINT ["java","-jar","/share-it-gateway.jar"]