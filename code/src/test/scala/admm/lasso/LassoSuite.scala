package admm.lasso

import org.scalatest.FunSuite
import org.scalatest.Matchers

import breeze.linalg.{DenseMatrix => BDM, DenseVector => BDV}

import org.apache.spark.mllib.util.LocalSparkContext
import org.apache.spark.util.Utils
import org.apache.spark.mllib.linalg.distributed._
import org.apache.spark.mllib.linalg.Vectors

import org.apache.spark.mllib.optimization.admm.functions._
import org.apache.spark.mllib.optimization.admm.linalg._
import org.apache.spark.mllib.optimization.admm.solver._

class LassoSuite extends FunSuite with LocalSparkContext with Matchers {
  test("ADMM algorithm solves dense lasso problem correctly") {
    val numblocks = 5 
    val blocksize = 5 
    val abstol = 1e-5
    val reltol = 1e-5
    val lambda = 0.1
    val rho = 0.1
    val maxiters = 20
    
    val A = arrayFromCoordinates(blocksize*numblocks, blocksize + 1, (r,c) => 1.0)
    
    val f = L2NormSquared.fromRowMatrix(A, rho, blocksize)

    val g = new L1Norm(lambda)
    var admm = new ConsensusADMMSolver(f, g, blocksize, abstol, reltol, sc)
    admm.solve(rho, maxiters)
    assert(true)
  }

  def arrayFromCoordinates(nRows: Int, nCols: Int, valfn: Function2[Int,Int,Double]): RowMatrix = {
    val rows = (1 to nRows).map(r => Vectors.dense((1 to nCols).map(c => valfn(r,c)).toArray))
    new RowMatrix(sc.parallelize(rows), nRows, nCols)
  }
}
