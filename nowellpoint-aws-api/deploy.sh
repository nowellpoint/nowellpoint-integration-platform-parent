mvn clean package
rm distributions/nowellpoint-api.zip
zip distributions/nowellpoint-api.zip Procfile target/nowellpoint-aws-api-swarm.jar
eb deploy
