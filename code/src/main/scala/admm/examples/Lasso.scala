/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package admm.examples

import org.apache.spark.{SparkConf, SparkContext}
import breeze.linalg.{DenseMatrix => BDM, DenseVector => BDV}
import breeze.linalg._
import admm.functions._
import admm.linalg._
import admm.solver._


object Lasso {
  def main(args: Array[String]){
    val conf = new SparkConf().setAppName("Lasso")
    val sc = new SparkContext(conf)
    sc.setCheckpointDir("hdfs://ec2-54-82-182-191.compute-1.amazonaws.com:9000/root/scratch")
    sc.addJar("/root/admm/code/target/scala-2.10/ADMM-assembly-1.0.jar") 
    val numWorkers = 8
    val coresPerWorker = 4
    val tasksPerCore = 2
    val parallelism = numWorkers* coresPerWorker*tasksPerCore

    val A = sc.parallelize(1 to 200, parallelism).map(x => BDM.rand[Double](500,500))
    A.checkpoint
    A.cache
    A.count

    val rho = 0.01
    val lambda = 0.5

    val sol = sc.broadcast(BDV.rand[Double](A.first.cols))
    val fns = A.map(X => new L2NormSquared(X, X*sol.value, rho))
    val f = new RDF[L2NormSquared](fns, 0L, fns.first.length) 
    f.splits.cache
    f.splits.count

    val g = new L1Norm(lambda)
    var admm = new ConsensusADMMSolver(f, g, 1e-3, 1e-3, sc) with Instrumentation
    admm.solve(rho,500)
  }
}
