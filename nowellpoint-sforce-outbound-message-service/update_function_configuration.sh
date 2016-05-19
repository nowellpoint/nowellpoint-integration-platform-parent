aws lambda update-function-configuration \
--function-name SalesforceOutboundMessageConsumer \
--role arn:aws:iam::600862814314:role/lambda_basic_execution \
--handler com.nowellpoint.sforce.OutboundMessageConsumer::handleRequest \
--runtime java8 \
--description "Salesforce: Outbound Message Consumer" \
--timeout 15 \
--memory-size 512
