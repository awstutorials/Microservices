# Microservices

$(aws ecr get-login --region $AWS_DEFAULT_REGION --no-include-email)

docker build --tag 182388080935.dkr.ecr.eu-west-1.amazonaws.com/limitdemoecr:3 --build-arg JAR_FILE=./target/limits-service-new-0.0.1-SNAPSHOT.jar .

docker push 182388080935.dkr.ecr.eu-west-1.amazonaws.com/limitdemoecr:3




docker build --tag 182388080935.dkr.ecr.eu-west-1.amazonaws.com/currencyexchangedemoecr:3 --build-arg JAR_FILE=./target/currency-exchange-service-new-0.0.1-SNAPSHOT.jar .

docker push 182388080935.dkr.ecr.eu-west-1.amazonaws.com/currencyexchangedemoecr:3


docker build --tag 182388080935.dkr.ecr.eu-west-1.amazonaws.com/currencyconversiondemoecr:1 --build-arg JAR_FILE=./target/currency-conversion-service-new-0.0.1-SNAPSHOT.jar .

docker push 182388080935.dkr.ecr.eu-west-1.amazonaws.com/currencyconversiondemoecr:1



docker build --tag 182388080935.dkr.ecr.eu-west-1.amazonaws.com/currencyexchangedemoecr:12 --build-arg JAR_FILE=./target/currency-exchange-service-new-0.0.1-SNAPSHOT.jar .

docker push 182388080935.dkr.ecr.eu-west-1.amazonaws.com/currencyexchangedemoecr:12

docker build --tag 182388080935.dkr.ecr.eu-west-1.amazonaws.com/currencyconversiondemoecr:12 --build-arg JAR_FILE=./target/currency-conversion-service-new-0.0.1-SNAPSHOT.jar .

docker push 182388080935.dkr.ecr.eu-west-1.amazonaws.com/currencyconversiondemoecr:12


xraysidecar
amazon/aws-xray-daemon:1

AWS_Region
eu-west-1




http://microserviceslb-1623179640.eu-west-1.elb.amazonaws.com/api/currency-exchange/currency-exchange/from/EUR/to/INR
http://microserviceslb-1623179640.eu-west-1.elb.amazonaws.com/api/currency-conversion/currency-converter/from/EUR/to/INR/quantity/100
