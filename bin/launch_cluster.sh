#!/bin/bash
cd ~/dev/spark/ec2 
./spark-ec2 -k spark -i ~/Downloads/spark.pem \
                --instance-type=m3.xlarge \
                --slaves=5 \
                --spark-version=61fe0d09d6a7873647820565065e066e544d55c9 \
                --spark-git-repo=https://github.com/darabos/spark.git \
                launch lassotest
