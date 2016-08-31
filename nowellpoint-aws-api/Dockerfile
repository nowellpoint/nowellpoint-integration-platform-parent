FROM java:openjdk-8-jdk
FROM maven:3-jdk-8
EXPOSE 9090

WORKDIR /code

# Install maven
ADD pom.xml /code/pom.xml

RUN ["mvn", "dependency:resolve"]  
RUN ["mvn", "verify"]

# Adding source, compile and package into a fat jar
ADD src /code/src  
RUN ["mvn", "package"]

ENTRYPOINT ["java","-jar","target/nowellpoint-aws-api-0.0.6-SNAPSHOT-swarm.jar"]
