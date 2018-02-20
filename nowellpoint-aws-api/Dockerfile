FROM java:openjdk-8-jdk

ADD target/nowellpoint-aws-api-swarm.jar /opt/wildfly-swarm.jar

EXPOSE 8443

ENTRYPOINT ["java", "-jar", "/opt/wildfly-swarm.jar", "-Dswarm.project.stage=production", "-Djava.net.preferIPv4Stack=true"]
