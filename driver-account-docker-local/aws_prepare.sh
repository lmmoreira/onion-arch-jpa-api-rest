#!/bin/sh

#http://localhost:8055/#!/infra

export AWS_ACCESS_KEY_ID=foobar
export AWS_SECRET_ACCESS_KEY=foobar


echo ">>>>> prepare_aws execution started"

aws --endpoint-url=http://localhost:4576 --region=us-east-1 sqs create-queue --queue-name DEV_project_WORKER_TEST
aws --endpoint-url=http://localhost:4576 --region=us-east-1 sqs create-queue --queue-name DEV_project_TO_DRIVER_ACCOUNT_EVENTS

echo ">>>>> prepare_aws execution finished"