mvn clean package -DskipTests
rm distributions/nowellpoint-api.zip
zip -j distributions/nowellpoint-api.zip Procfile target/nowellpoint-aws-api-swarm.jar
eb deploy
