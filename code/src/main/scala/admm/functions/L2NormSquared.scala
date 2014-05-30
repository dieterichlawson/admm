package admm.functions

import org.apache.spark.rdd.RDD
import admm.linalg.BlockMatrix
import breeze.linalg._
import breeze.linalg.{DenseVector => BDV, DenseMatrix => BDM}

class L2NormSquared(val A: BDM[Double],
                    val b: BDV[Double]) extends ProxableFunction(A.cols) {

  val I = BDM.eye[Double](A.cols)

  def prox(x: BDV[Double], rho: Double): BDV[Double] = {
    ((A.t*A + I*rho) \ (A.t*b + x*rho)).toDenseVector
  }

  def apply(x: BDV[Double]): Double = {
    Math.pow(norm(A*x - b),2.0)
  }
}

object L2NormSquared {
  def fromTextFile(file: RDD[String], blockHeight: Int = 1024): RDF[L2NormSquared] = {
    val fns = new BlockMatrix(file, blockHeight).blocks.
      map(X => new L2NormSquared(X(::, 0 to -2), X(::,-1).toDenseVector))
    new RDF[L2NormSquared](fns, 0L, fns.first.length) 
  }

  def fromMatrix(A: BlockMatrix): RDF[L2NormSquared] = {
    val x = BDV.rand[Double](A.numCols.toInt)
    val fns = A.blocks.map(X => new L2NormSquared(X, X*x))
    new RDF[L2NormSquared](fns, 0L, fns.first.length) 
  }
}
