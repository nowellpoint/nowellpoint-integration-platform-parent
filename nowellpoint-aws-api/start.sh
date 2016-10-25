#mvn -f /Users/jherson/workspace/nowellpoint-integration-platform-parent/nowellpoint-aws-core/pom.xml clean deploy -DskipTests
#mvn -f /Users/jherson/workspace/nowellpoint-integration-platform-parent/nowellpoint-aws-data/pom.xml clean deploy -DskipTests
#mvn -f /Users/jherson/workspace/nowellpoint-integration-platform-parent/nowellpoint-aws-sforce/pom.xml clean deploy -DskipTests
mvn -f /Users/jherson/workspace/nowellpoint-integration-platform-parent/nowellpoint-aws-api/pom.xml clean package -DskipTests
cp /Users/jherson/Dropbox/STAR.NOWELLPOINT.COM/keystore.jks target/
java -jar target/nowellpoint-aws-api-swarm.jar
