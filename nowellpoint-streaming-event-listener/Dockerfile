FROM amazoncorretto

ADD target/streaming-event-listener-thorntail.jar /opt/streaming-event-listener-thorntail.jar

EXPOSE 8443

ENTRYPOINT ["java", "-jar", "/opt/streaming-event-listener-thorntail.jar", "-Dswarm.project.stage=localhost", "-Djava.net.preferIPv4Stack=true"]
