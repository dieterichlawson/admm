#!/bin/bash
cd "$(dirname "$0")"
cd ../code
sbt "runMain admm.LassoExample
    --Afile hdfs://ec2-107-20-62-201.compute-1.amazonaws.com:9000/root/matrix/Big.csv \
    --scratch_dir hdfs://ec2-107-20-62-201.compute-1.amazonaws.com:9000/root/scratch \
    --master spark://ec2-107-20-62-201.compute-1.amazonaws.com:7077 \
    --maxiters 500 \
    --blocksize 1024 \
    --abstol 5e-3 \
    --reltol 5e-3 \
    --lambda 0.05 \
    --rho 0.01"
