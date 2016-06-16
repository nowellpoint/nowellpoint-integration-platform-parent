mvn -f /Users/jherson/workspace/nowellpoint-integration-platform-parent/nowellpoint-aws-core/pom.xml clean deploy -DskipTests
mvn -f /Users/jherson/workspace/nowellpoint-integration-platform-parent/nowellpoint-aws-data/pom.xml clean deploy -DskipTests
mvn -f /Users/jherson/workspace/nowellpoint-integration-platform-parent/nowellpoint-aws-sforce/pom.xml clean deploy -DskipTests
mvn -f /Users/jherson/workspace/nowellpoint-integration-platform-parent/nowellpoint-aws-api/pom.xml clean package -DskipTests
java -jar target/nowellpoint-aws-api-0.0.2-SNAPSHOT-swarm.jar
