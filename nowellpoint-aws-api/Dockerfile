#FROM java:openjdk-8-jdk
#FROM maven:3-jdk-8
#EXPOSE 9090

#WORKDIR /code

#ADD pom.xml /code/pom.xml

#RUN ["mvn", "dependency:resolve"]  
#RUN ["mvn", "verify"]

# Adding source, compile and package into a fat jar
#ADD src /code/src  
#RUN ["mvn", "package"]

#FROM openjdk:jre-alpine

FROM jboss/base-jdk:8

USER root
RUN yum install -y iproute

USER jboss
ADD target/nowellpoint-aws-api-swarm.jar /opt/wildfly-swarm.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/opt/wildfly-swarm.jar", "-Dswarm.project.stage=development"]
