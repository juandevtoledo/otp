#!/bin/bash

curl -X POST -H "content-type: application/json" -H "accept: */*" -d '{"withdrawal": {"amount": 1000.25, "documentId": "68942241"}}' 'http://localhost:8083/otp/operations/withdrawal' && echo 'done1' &
curl -X POST -H "content-type: application/json" -H "accept: */*" -d '{"withdrawal": {"amount": 1000.25, "documentId": "68942242"}}' 'http://localhost:8083/otp/operations/withdrawal' && echo 'done2' &
curl -X POST -H "content-type: application/json" -H "accept: */*" -d '{"withdrawal": {"amount": 1000.25, "documentId": "68942243"}}' 'http://localhost:8083/otp/operations/withdrawal' && echo 'done3'

