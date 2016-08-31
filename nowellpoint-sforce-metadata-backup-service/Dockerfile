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
RUN ["mvn", "package", "-DskipTests"] 

ENTRYPOINT ["java","-jar","target/nowellpoint-sforce-metadata-backup-service-0.0.7-SNAPSHOT.jar"]
