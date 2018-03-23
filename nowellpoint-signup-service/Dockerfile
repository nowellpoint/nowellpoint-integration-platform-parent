FROM fabric8/java-jboss-openjdk8-jdk:latest
ENV JAVA_APP_DIR=/deployments
ENV AB_ENABLED off
EXPOSE 8080 
COPY target/nowellpoint-signup-service-1.0.10-SNAPSHOT-swarm.jar /deployments/
CMD /bin/sh -c /deployments/run-java.sh -Dswarm.project.stage=production

#ENTRYPOINT ["java", "-jar", "java -jar run-java.jar", "-Dswarm.project.stage=production", "-Djava.net.preferIPv4Stack=true"]
