mvn clean package

aws s3 cp target/nowellpoint-aws-event-0.0.2-SNAPSHOT.jar \
s3://aws-microservices/nowellpoint-aws-event-0.0.2-SNAPSHOT.jar

aws lambda update-function-configuration \
--function-name EventHandler \
--role arn:aws:iam::600862814314:role/lambda_basic_execution \
--handler com.nowellpoint.aws.lambda.stream.EventHandler::handleEvent \
--runtime java8 \
--description "Event Handler: DynamoDB stream event handler on the Event table to process business logic for application events" \
--timeout 300 \
--memory-size 1536

aws lambda update-function-code \
--function-name EventHandler \
--s3-bucket aws-microservices \
--s3-key nowellpoint-aws-event-0.0.2-SNAPSHOT.jar \
--no-publish
