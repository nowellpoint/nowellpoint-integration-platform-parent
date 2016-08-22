mvn clean install

aws s3 cp target/nowellpoint-sforce-metadata-backup-service-0.0.5-SNAPSHOT.jar \
s3://aws-microservices/nowellpoint-sforce-metadata-backup-service-0.0.5-SNAPSHOT.jar

aws lambda update-function-configuration \
--function-name SalesforceMetadataBackupRequest \
--role arn:aws:iam::600862814314:role/lambda_basic_execution \
--handler com.nowellpoint.sforce.MetadataBackupRequestHandler::handleEvent \
--runtime java8 \
--description "Salesforce: Metadata Backup Request Handler" \
--timeout 180 \
--memory-size 256

aws lambda update-function-code \
--function-name SalesforceMetadataBackupRequest \
--s3-bucket aws-microservices \
--s3-key nowellpoint-sforce-metadata-backup-service-0.0.5-SNAPSHOT.jar \
--no-publish