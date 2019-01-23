FROM amazoncorretto

ADD target/nowellpoint-streaming-event-listener-thorntail.jar /opt/nowellpoint-streaming-event-listener-thorntail.jar

EXPOSE 8443

ENTRYPOINT ["java", "-jar", "/opt/nowellpoint-streaming-event-listener-thorntail.jar", "-Dswarm.project.stage=production", "-Djava.net.preferIPv4Stack=true"]