#!/bin/sh

echo 'helloworld...'
echo '*************'
export MINIMUM=$(aws ssm get-parameter --region ${AWS_REGION} --name ${MINIMUM_PARAM_NAME} | jq -r .Parameter.Value)
export MAXIMUM=$(aws ssm get-parameter --region ${AWS_REGION} --name ${MAXIMUM_PARAM_NAME} | jq -r .Parameter.Value)

echo $MINIMUM
echo $MAXIMUM
echo '*************'

java -jar application.jar