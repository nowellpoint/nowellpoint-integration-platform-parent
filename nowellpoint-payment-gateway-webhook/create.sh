aws lambda create-function \
--region us-east-1 \
--function-name SubscriptionProcessingService \
--code S3Bucket=aws-microservices,S3Key=nowellpoint-payment-gateway-webhook.jar \
--role arn:aws:iam::600862814314:role/lambda_basic_execution \
--handler com.nowellpoint.braintree.webhook.SubscriptionProcessingService::handleEvent \
--runtime java8 \
--description "Payment Gateway: Process Subscription Events" \
--timeout 120 \
--memory-size 256
