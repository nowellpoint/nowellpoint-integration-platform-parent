mvn clean package -DskipTests
java -jar target/nowellpoint-www-app-thorntail.jar -Dswarm.project.stage=localhost -Djava.net.preferIPv4Stack=true
#docker build --tag=nowellpoint/console .
#docker run --name=console -it \
#-e AWS_REGION=$AWS_REGION \
#-e AWS_ACCESS_KEY=$AWS_ACCESS_KEY \
#-e AWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY \
#-e AWS_SECRET_NAME=$AWS_SECRET_NAME \
#-p 8443:8443 nowellpoint/console:latest
