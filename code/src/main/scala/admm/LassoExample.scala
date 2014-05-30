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

package admm

import org.apache.log4j.{Level, Logger}
import scopt.OptionParser

import org.apache.spark.{SparkConf, SparkContext}
import breeze.linalg._

import admm.functions._
import admm.solver.ConsensusADMMSolver
import admm.linalg.BlockMatrix
import org.apache.spark.Logging

/**
 * An example app for lasso. Run with
 * {{{
 * ./bin/run-example org.apache.spark.examples.mllib.Lasso
 * }}}
 * If you use it as a template to create your own app, please use `spark-submit` to submit your app.
 */
object LassoExample extends App with Logging{

  case class Params(
    Afile: String = "/Users/dlaw/class/ee364b/project/spark-testing/lasso/A.csv",
    master: String = "local",
    spark_home: String = "/root/spark",
    scratch_dir: String = "hdfs://ec2-107-20-62-201.compute-1.amazonaws.com:9000/root/scratch",
    maxiters: Int = 300,
    blocksize: Int = 1024,
    lambda: Double = 0.5,
    rho: Double = 0.1,
    abstol: Double = 1e-3,
    reltol: Double = 1e-3)
  
  val defaultParams = Params()

  val parser = new OptionParser[Params]("Lasso") {
    head("Lasso: an example app for ADMM Lasso.")
    opt[Int]("maxiters")
      .text("max number of iterations")
      .action((x, c) => c.copy(maxiters = x))
    opt[String]("master")
      .text(s"master node URL, default: ${defaultParams.master}")
      .action((x, c) => c.copy(master= x))
    opt[String]("spark_home")
      .text(s"spark_home, default: ${defaultParams.spark_home}")
      .action((x, c) => c.copy(spark_home= x))
    opt[String]("scratch_dir")
      .text(s"scratch_dir, default: ${defaultParams.scratch_dir}")
      .action((x, c) => c.copy(scratch_dir= x))
    opt[Int]("blocksize")
      .text("Size of block matrices")
      .action((x, c) => c.copy(blocksize = x))
    opt[Double]("lambda")
      .text(s"lambda (regularization parameter), default: ${defaultParams.lambda}")
      .action((x, c) => c.copy(lambda = x))
    opt[Double]("rho")
      .text(s"rho (projection parameter), default: ${defaultParams.rho}")
      .action((x, c) => c.copy(rho = x))
    opt[Double]("abstol")
      .text(s"absolute tolerance, default: ${defaultParams.abstol}")
      .action((x, c) => c.copy(abstol = x))
    opt[Double]("reltol")
      .text(s"relative tolerance, default: ${defaultParams.reltol}")
      .action((x, c) => c.copy(abstol = x))
    opt[String]("Afile")
      .text(s"input matrix, default: ${defaultParams.Afile}")
      .action((x, c) => c.copy(Afile = x))
   }

  parser.parse(args, defaultParams).map { params =>
    run(params)
  } getOrElse {
    sys.exit(1)
  }

  def run(params: Params) {
    val conf = new SparkConf()
      .setMaster(params.master)
      .setSparkHome(params.spark_home)
      .setJars(List("/root/admm/code/target/scala-2.10/ADMM-assembly-1.0.jar"))
      .setAppName("ADMM Lasso")
      .set("spark.executor.memory", "6g")
      .set("spark.default.parallelism", "8")
    val sc = new SparkContext(conf)
    logInfo("Loading textfile")
    val t = sc.textFile(params.Afile)
    logInfo("Creating matrix")
    val A = new BlockMatrix(t, params.blocksize)
    val f = L2NormSquared.fromMatrix(A)
    val g = new L1Norm(params.lambda)
    var admm = new ConsensusADMMSolver(f, g, params.abstol, params.reltol, sc, params.scratch_dir)
    admm.solve(params.rho, params.maxiters)
    println("Solution: " + admm.z)
    val optval = f(admm.z) + g(admm.z)
    println("**********")
    println("  Optval: " + optval)
    println("**********")
    sc.stop()
  }
}
