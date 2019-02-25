[![Build Status](https://travis-ci.org/nowellpoint/nowellpoint-integration-platform-parent.svg?branch=dev)](https://travis-ci.org/nowellpoint/nowellpoint-integration-platform-parent)

# nowellpoint-integration-platform-parent
Nowellpoint Small Business Integration Platform

Set the following environment variables:

AWS_REGION=<region>
AWS_ACCESS_KEY=<accessKey>
AWS_SECRET_ACCESS_KEY=<secretAccessKey>

Openshift command line tools: https://docs.openshift.com/online/cli_reference/get_started_cli.html
1) download command line tools from help menu
2) move archieve to /usr/local/bin
3) tar -xvf <archive>
4) copy login command from menu: oc login https://api.pro-us-east-1.openshift.com --token=<token>

nowellpoint-www-app

run local: run.sh
deploy to openshift: mvn fabric8:deploy -Popenshift -DskipTests

nowellpoint-streaming-event-listener

run local: run.sh
deploy to openshift: mvn fabric8:deploy -Popenshift -DskipTests