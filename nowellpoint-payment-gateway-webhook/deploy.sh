mvn clean package -Pdeploy -DskipTests

aws s3 cp target/nowellpoint-payment-gateway-webhook.jar \
s3://aws-microservices/nowellpoint-payment-gateway-webhook.jar

#aws lambda create-function \
#--region us-east-1 \
#--function-name PaymentGatewayWebhook \
#--code S3Bucket=aws-microservices,S3Key=nowellpoint-payment-gateway-webhook.jar \
#--role arn:aws:iam::600862814314:role/lambda_basic_execution \
#--handler com.nowellpoint.braintree.webhook.PaymentGatewayHandler::handleRequest \
#--runtime java8 \
#--description "Payment Gateway: Handle Payment Gateway Events" \
#--timeout 30 \
#--memory-size 256

aws lambda update-function-code \
--function-name arn:aws:lambda:us-east-1:600862814314:function:streaming-event-handler \
--s3-bucket aws-microservices \
--s3-key nowellpoint-payment-gateway-webhook.jar \
--no-publish