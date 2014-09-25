package admm.functions

import org.apache.spark.rdd.RDD
import admm.linalg.BlockMatrix
import breeze.linalg._
import breeze.linalg.{DenseVector => BDV, DenseMatrix => BDM}
import org.apache.spark.SparkContext
import org.apache.spark.storage._
import org.apache.spark.broadcast._

class L2NormSquared(val A: BDM[Double],
                    val b: BDV[Double],
                    val rho: Double) extends Function1[BDV[Double],Double] with Prox with Serializable{

  lazy val factor = {
    inv(A.t*A + BDM.eye[Double](A.cols)*rho)
  }
  
  def prox(x: BDV[Double], rho: Double): BDV[Double] = {
    (factor * (A.t*b + x*rho)).toDenseVector
  }

  def apply(x: BDV[Double]): Double = {
    Math.pow(norm(A*x - b),2.0)
  }
}

object L2NormSquared {
  def fromTextFile(file: RDD[String], rho: Double, blockHeight: Int = 1024): RDF[L2NormSquared] = {
    val fns = new BlockMatrix(file, blockHeight).blocks.
      map(X => new L2NormSquared(X(::, 0 to -2), X(::,-1).toDenseVector, rho))
    new RDF[L2NormSquared](fns, 0L)
  }

  def fromMatrix(A: RDD[BDM[Double]], rho: Double): RDF[L2NormSquared] = {
    val localx = BDV.rand[Double](A.first.cols)
    val x = A.context.broadcast(localx)
    val fns = A.map(X => new L2NormSquared(X, X*x.value, rho))
    new RDF[L2NormSquared](fns, 0L)
  }
}
