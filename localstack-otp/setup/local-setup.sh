#! /bin/bash
echo "############################################## WARNING ###########################################################"
echo "This is using the default setup sample file (https://github.com/piso19/localstack-infra/local-setup.sh)....."
echo "Please make sure you are including the actual setup file for the service you want to run locally in the container definitions, either the docker-compose file or Dockerfile."
echo "############################################## PROVISIONING INFRA ###########################################################"
## Provision your service's infrastructure here

awslocal sqs create-queue --queue-name notifications --attributes file:///setup/sqs-config.json

# awslocal dynamodb create-table --table-name Blacklist \
#    --attribute-definitions \
#        AttributeName=idCard,AttributeType=S AttributeName=idType,AttributeType=S \
#    --key-schema AttributeName=idCard,KeyType=HASH AttributeName=idType,KeyType=RANGE \
#    --provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1

