mvn clean package
zip distributions/nowellpoint-api.zip Procfile target/nowellpoint-aws-api-0.0.2-SNAPSHOT-swarm.jar
eb deploy
