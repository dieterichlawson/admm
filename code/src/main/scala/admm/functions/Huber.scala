package admm.functions

import org.apache.spark.rdd.RDD
import admm.linalg.BlockMatrix
import breeze.linalg._
import breeze.linalg.{DenseVector => BDV, DenseMatrix => BDM}
import org.apache.spark.SparkContext
import org.apache.spark.storage._
import org.apache.spark.broadcast._

class Huber(val A: BDM[Double],
            val b: BDV[Double],
            val rho: Double)
  extends Function1[BDV[Double], Double] with Prox with Serializable {

  def prox(x: BDV[Double], rho: Double): BDV[Double] = {
    x.map(x_i => rho/(1+rho)*x_i + softThreshold(x_i, 1 + 1/rho)/(1+rho))
  }

  def softThreshold(x: Double, thresh: Double): Double = {
    Math.max(1.0-thresh/Math.abs(x), 0.0)*x
  }

  private def huber(x: Double, delta: Double = 1.0): Double = {
    if (Math.abs(x) <= delta) x*x/2
    else Math.abs(x)-0.5
  }

  def apply(x: BDV[Double]): Double = {
    val s = (A*x-b).map(huber(_)).reduce(_+_)
    s(0)
  }
}

object Huber {
  def fromTextFile(file: RDD[String], rho: Double, blockHeight: Int = 1024): RDF[Huber] = {
    val fns = new BlockMatrix(file, blockHeight).blocks.
      map(X => new Huber(X(::, 0 to -2), X(::,-1).toDenseVector, rho))
    new RDF[Huber](fns, 0L)
  }

  def fromMatrix(A: RDD[BDM[Double]], rho: Double): RDF[Huber] = {
    val localx = BDV.rand[Double](A.first.cols)
    val x = A.context.broadcast(localx)
    val fns = A.map(X => new Huber(X, X*x.value, rho))
    new RDF[Huber](fns, 0L)
  }
}
