FROM amazoncorretto

ADD target/www-app-thorntail.jar /opt/www-app-thorntail.jar

EXPOSE 8443

ENTRYPOINT ["java", "-jar", "/opt/www-app-thorntail.jar", "-Dswarm.project.stage=localhost", "-Djava.net.preferIPv4Stack=true"]