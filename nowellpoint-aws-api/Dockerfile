FROM java:openjdk-8-jdk
EXPOSE 9090

WORKDIR /code

# Install maven
RUN apt-get update  
RUN apt-get install -y maven
ADD pom.xml /code/pom.xml

RUN ["mvn", "dependency:resolve"]  
RUN ["mvn", "verify"]

# Adding source, compile and package into a fat jar
ADD src /code/src  
RUN ["mvn", "package"]

ENTRYPOINT ["java","-jar","nowellpoint-aws-api-swarm.jar"]
