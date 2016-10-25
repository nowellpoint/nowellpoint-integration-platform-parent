#mvn clean package
rm distributions/nowellpoint-api.zip
zip distributions/nowellpoint-api.zip Procfile keystore.jks target/nowellpoint-aws-api-swarm.jar
eb deploy
