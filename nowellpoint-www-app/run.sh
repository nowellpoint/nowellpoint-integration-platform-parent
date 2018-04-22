mvn clean package -Popenshift
docker build --tag=nowellpoint/console .
docker run -e AWS_REGION=$AWS_REGION \
-e AWS_ACCESS_KEY=$AWS_ACCESS_KEY -e AWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY -e MONGO_CLIENT_URI=$MONGO_CLIENT_URI -e OKTA_API_KEY=$OKTA_API_KEY -e OKTA_ORG_URL=$OKTA_ORG_URL -e OKTA_CLIENT_ID=$OKTA_CLIENT_ID -e OKTA_CLIENT_SECRET=$OKTA_CLIENT_SECRET -e OKTA_AUTHORIZATION_SERVER=$OKTA_AUTHORIZATION_SERVER -p 8443:8443 nowellpoint/console:latest
