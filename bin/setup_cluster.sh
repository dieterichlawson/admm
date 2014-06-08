#!/bin/bash

cd "$(dirname "$0")"
echo "Installing SBT"
wget http://repo.scala-sbt.org/scalasbt/sbt-native-packages/org/scala-sbt/sbt//0.12.4/sbt.rpm
yum install sbt.rpm

echo "Assembling code"
cd ../code
sbt assembly

echo "Creating HDFS Dirs"
../../ephemeral-hdfs/bin/hadoop fs -mkdir /root/scratch
../../ephemeral-hdfs/bin/hadoop fs -mkdir /root/matrix
