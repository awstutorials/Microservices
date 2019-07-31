#!/bin/sh

echo 'helloworld...'
echo '*************'

export ENV_DB_PASSWORD=$(aws ssm get-parameter --region eu-west-1 --name ${DB_PASS_PARAM_NAME} --with-decryption | jq -r .Parameter.Value)

echo $ENV_DB_PASSWORD
echo '*************'

java -jar application.jar