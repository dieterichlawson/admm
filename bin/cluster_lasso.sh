#!/bin/bash
AWS_HOSTNAME="$(curl -s http://169.254.169.254/latest/meta-data/public-hostname)"
cd "$(dirname "$0")"
cd ../code
echo "Removing scratch directory"
../../ephemeral-hdfs/bin/hadoop fs -rmr /root/scratch
../../ephemeral-hdfs/bin/hadoop fs -mkdir /root/scratch
sbt "runMain admm.LassoExample
    --Afile hdfs://$AWS_HOSTNAME:9000/root/matrix/Big.csv \
    --scratch_dir hdfs://$AWS_HOSTNAME:9000/root/scratch \
    --master spark://$AWS_HOSTNAME:7077 \
    --maxiters 500 \
    --blocksize 1024 \
    --abstol 5e-3 \
    --reltol 5e-3 \
    --lambda 0.05 \
    --rho 0.01"
