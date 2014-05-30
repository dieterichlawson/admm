#!/bin/bash

cd "$(dirname "$0")"
echo "Installing SBT"
wget http://dl.bintray.com/sbt/rpm/sbt-0.13.2.rpm
yum install sbt-0.13.2.rpm

echo "Assembling code"
cd ../code
sbt assembly

echo "Creating HDFS Dirs"
../../ephemeral-hdfs/bin/hadoop fs -mkdir /root/scratch
../../ephemeral-hdfs/bin/hadoop fs -mkdir /root/matrix

echo "Creating 1e5 x 5e4 Matrix"
cd ../util
scalac CreateMatrix.scala
scala CreateMat 100000 5000 | ../../ephemeral-hdfs/bin/hadoop fs -put - /root/scratch/matrix/A.csv
