mvn clean package -DskipTests
cp $JBOSS_HOME/standalone/configuration/my.jks target/keystore.jks
rm distributions/nowellpoint-api.zip
zip -j distributions/nowellpoint-api.zip Procfile target/nowellpoint-aws-api-swarm.jar
eb deploy
