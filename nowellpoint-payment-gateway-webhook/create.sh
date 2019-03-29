aws lambda create-function \
--region us-east-1 \
--function-name streaming-event-handler \
--code S3Bucket=aws-microservices,S3Key=nowellpoint-payment-gateway-webhook.jar \
--role arn:aws:iam::600862814314:role/lambda_basic_execution \
--handler com.nowellpoint.listener.handler.StreamingEventHandler::handleRequest \
--runtime java8 \
--description "Streaming Events: Process Streaming Events" \
--timeout 120 \
--memory-size 256