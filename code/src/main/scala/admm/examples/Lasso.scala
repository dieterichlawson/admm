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
import breeze.linalg.operators._
import admm.functions._
import admm.linalg._
import admm.solver._
import scopt.OptionParser

object Lasso {

  case class Params(
    numblocks: Int = 100,
    blockheight: Int = 500,
    maxiters: Int = 300,
    lambda: Double = 0.5,
    rho: Double = 0.1,
    abstol: Double = 1e-3,
    reltol: Double = 1e-3
  )

  val defaultParams = Params()

  val parser = new OptionParser[Params]("Lasso") {
    head("Lasso: An example app for ADMM Lasso.")
    opt[Int]("numblocks")
    .text(s"number of blockheightx500 blocks, default: ${defaultParams.numblocks}")
    .action((x,c) => c.copy(numblocks= x))
    opt[Int]("blockheight")
    .text(s"height of each block, default: ${defaultParams.blockheight}")
    .action((x,c) => c.copy(blockheight= x))
    opt[Int]("maxiters")
    .text(s"maximum iterations for solver, default: ${defaultParams.maxiters}")
    .action((x,c) => c.copy(maxiters= x))
    opt[Double]("lambda")
    .text(s"the l1 regularization weight, default: ${defaultParams.lambda}")
    .action((x,c) => c.copy(lambda= x))
    opt[Double]("rho")
    .text(s"step size, default: ${defaultParams.rho}")
    .action((x,c) => c.copy(rho= x))
    opt[Double]("abstol")
    .text(s"absolute tolerance, default: ${defaultParams.abstol}")
    .action((x,c) => c.copy(abstol= x))
    opt[Double]("reltol")
    .text(s"relative tolerance, default: ${defaultParams.reltol}")
    .action((x,c) => c.copy(reltol= x))
  }
 
  def main(args: Array[String]){
    parser.parse(args, defaultParams).map { params =>
      run(params)
    } getOrElse {
      sys.exit(1)
    }
  }

  def run(params: Params){
    val conf = new SparkConf().setAppName("Lasso")
    val sc = new SparkContext(conf)
    if(sc.master.equals("local")){
      sc.setCheckpointDir("/Users/dlaw/foo")
    }else{
       sc.setCheckpointDir(s"hdfs://${sc.master.substring(8,sc.master.length-5)}:9000/root/scratch")
    }
    val numWorkers = 8
    val coresPerWorker = 4
    val tasksPerCore = 3 
    val parallelism = numWorkers* coresPerWorker*tasksPerCore

    val A = sc.parallelize(1 to params.numblocks,params.numblocks).map(x => BDM.rand[Double](params.blockheight,500))
    A.checkpoint

    val f = L2NormSquared.fromMatrix(A,params.rho)
    f.splits.cache
    f.splits.count

    val g = new L1Norm(params.lambda)
    var admm = new ConsensusADMMSolver(f, g, 500, params.abstol, params.reltol, sc) with Instrumentation
    val t0 = System.nanoTime()
    admm.solve(params.rho,params.maxiters)
    val t1 = System.nanoTime()
    println(s"Total solve took ${(t1-t0)/1e9} seconds")
    admm.printData
    sc.stop
  }
}
