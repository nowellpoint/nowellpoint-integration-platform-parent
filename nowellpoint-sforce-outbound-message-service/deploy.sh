mvn clean install

aws s3 cp target/nowellpoint-sforce-outbound-message-service-0.0.5-SNAPSHOT.jar \
s3://aws-microservices/nowellpoint-sforce-outbound-message-service-0.0.5-SNAPSHOT.jar

aws lambda update-function-configuration \
--function-name SalesforceOutboundMessageConsumer \
--role arn:aws:iam::600862814314:role/lambda_basic_execution \
--handler com.nowellpoint.sforce.OutboundMessageConsumer::handleRequest \
--runtime java8 \
--description "Salesforce: Outbound Message Consumer" \
--timeout 15 \
--memory-size 256

aws lambda update-function-code \
--function-name SalesforceOutboundMessageConsumer \
--s3-bucket aws-microservices \
--s3-key nowellpoint-sforce-outbound-message-service-0.0.2-SNAPSHOT.jar \
--no-publish

aws lambda update-function-configuration \
--function-name SalesforceOutboundMessageHandler \
--role arn:aws:iam::600862814314:role/lambda_basic_execution \
--handler com.nowellpoint.sforce.OutboundMessageHandler::handleEvent \
--runtime java8 \
--description "Salesforce: Outbound Message Handler" \
--timeout 15 \
--memory-size 256

aws lambda update-function-code \
--function-name SalesforceOutboundMessageHandler \
--s3-bucket aws-microservices \
--s3-key nowellpoint-sforce-outbound-message-service-0.0.5-SNAPSHOT.jar \
--no-publish
