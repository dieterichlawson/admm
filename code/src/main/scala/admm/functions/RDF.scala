package admm.functions

import org.apache.spark.rdd.RDD
import org.apache.spark.Logging
import breeze.linalg.{DenseVector => BDV, DenseMatrix => BDM}

class RDF[F <: Function1[BDV[Double], Double] with Prox](val splits: RDD[F], var nSplits: Long)
   extends Function1[BDV[Double], Double] with Serializable with Logging{

  def prox(x: RDD[BDV[Double]], rho: Double): RDD[BDV[Double]] = {
    splits.zip(x).map({ case (fn, x_i) => fn.prox(x_i, rho) })
  }

  def numDeps(): Int = {
    numDeps(splits)
  }

  def numDeps(rdd: RDD[_]): Int = {
    var nDeps = 0
    for(dep <- rdd.dependencies){
      nDeps += 1
      nDeps += numDeps(dep.rdd)
    }
    nDeps
  }
  
  def apply(x: BDV[Double]): Double = {
    val broad_x = splits.context.broadcast(x)
    splits.map(fn => fn(broad_x.value)).reduce(_+_)
  }

  def numSplits(): Long = {
    if(nSplits == 0L){
      nSplits = splits.count
    }
    nSplits
  }
}
