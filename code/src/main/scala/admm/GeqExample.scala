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

object GeqExample extends App {

  case class Params(
      Afile: String = "/Users/dlaw/class/ee364b/project/spark-testing/lasso/A.csv",
      geq: Double = 0.5,
      maxiters: Int = 300,
      blocksize: Int = 1024,
      rho: Double = 0.1,
      abstol: Double = 1e-3,
      reltol: Double = 1e-3)

  val defaultParams = Params()

  val parser = new OptionParser[Params]("GeqExample") {
    head("GeqExample: an example app for ADMM.")
    opt[Int]("maxiters")
      .text("max number of iterations")
      .action((x, c) => c.copy(maxiters = x))
    opt[Int]("blocksize")
      .text("Size of block matrices")
      .action((x, c) => c.copy(blocksize = x))
    opt[Double]("rho")
      .text(s"rho (projection parameter), default: ${defaultParams.rho}")
      .action((x, c) => c.copy(rho = x))
    opt[Double]("abstol")
      .text(s"absolute tolerance, default: ${defaultParams.abstol}")
      .action((x, c) => c.copy(abstol = x))
    opt[Double]("reltol")
      .text(s"relative tolerance, default: ${defaultParams.reltol}")
      .action((x, c) => c.copy(reltol= x))
    opt[String]("Afile")
      .text(s"input matrix, default: ${defaultParams.Afile}")
      .action((x, c) => c.copy(Afile = x))
    opt[Double]("geq")
      .text(s"constraint, default: ${defaultParams.geq}")
      .action((x, c) => c.copy(geq= x))

  }

  parser.parse(args, defaultParams).map { params =>
    run(params)
  } getOrElse {
    sys.exit(1)
  }

  def run(params: Params) {
    val conf = new SparkConf().setAppName("ADMM w/ >= constraint with $params").
                               setMaster("local")
    val sc = new SparkContext(conf)
    sc.setCheckpointDir("/Users/dlaw/school/spr_2014/ee364b/project/code/scratch")
    val A = sc.textFile(params.Afile)
    val f = L2NormSquared.fromTextFile(A)
    val g = new GeqScalar(params.geq)
    var admm = new ConsensusADMMSolver(f, g, params.abstol, params.reltol, sc)
    admm.solve(params.rho, params.maxiters)
    println("Solution: " + admm.z)
    val optval = f(admm.z) + g(admm.z)
    println("**********")
    println("  Optval: " + optval)
    println("**********")
    sc.stop()
  }
}
