#!/bin/bash
cd ~/dev/spark/ec2 
./spark-ec2 -k spark -i ~/Downloads/spark.pem \
                --instance-type=m3.xlarge \
                --slaves=8 \
                --spark-version=1.0.0 \
                launch lassotest
