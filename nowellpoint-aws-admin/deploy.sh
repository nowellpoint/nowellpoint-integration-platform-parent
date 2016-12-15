mvn clean package -Pdeploy -DskipTests

aws s3 cp target/nowellpoint-aws-admin.jar \
s3://aws-microservices/nowellpoint-aws-admin.jar

#aws lambda create-function \
#--region us-east-1 \
#--function-name PropertyServiceHandler \
#--code S3Bucket=aws-microservices,S3Key=nowellpoint-aws-admin.jar \
#--role arn:aws:iam::600862814314:role/lambda_basic_execution \
#--handler com.nowellpoint.aws.admin.PropertyServiceHandler::handleRequest \
#--runtime java8 \
#--description "Properties: Get Application Properties Service" \
#--timeout 15 \
#--memory-size 256

aws lambda update-function-code \
--function-name PropertyServiceHandler \
--s3-bucket aws-microservices \
--s3-key nowellpoint-aws-admin.jar \
--no-publish