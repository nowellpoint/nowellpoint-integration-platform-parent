FROM java:openjdk-8-jdk
CMD mvn clean package deploy
ADD target/nowellpoint-aws-app-0.0.2-SNAPSHOT-swarm.jar /opt/nowellpoint-aws-app-swarm.jar
CMD curl -O https://oss.sonatype.org/content/repositories/snapshots/com/nowellpoint/nowellpoint-aws-app/0.0.2-SNAPSHOT/nowellpoint-aws-app-0.0.2-20151202.145829-11.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "/opt/nowellpoint-aws-app-swarm.jar"]
