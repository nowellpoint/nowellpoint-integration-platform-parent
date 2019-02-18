FROM amazoncorretto

ADD target/nowellpoint-www-app-thorntail.jar /opt/nowellpoint-www-app-thorntail.jar

EXPOSE 8443

ENTRYPOINT ["java", "-jar", "/opt/nowellpoint-www-app-thorntail.jar", "-Dswarm.project.stage=localhost", "-Djava.net.preferIPv4Stack=true"]
