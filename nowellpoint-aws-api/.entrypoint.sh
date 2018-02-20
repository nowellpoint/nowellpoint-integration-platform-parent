#!/bin/bash


java -jar target/nowellpoint-aws-api-swarm.jar -Dswarm.project.stage=production -Djava.net.preferIPv4Stack=true -Daws.accessKeyId=$AWS_ACCESS_KEY -Daws.secretKey=$AWS_SECRET_ACCESS_KEY
#java -jar /opt/wildfly-swarm.jar -Dswarm.project.stage=production -Djava.net.preferIPv4Stack=true -Daws.accessKeyId=$AWS_ACCESS_KEY -Daws.secretKey=$AWS_SECRET_ACCESS_KEY
