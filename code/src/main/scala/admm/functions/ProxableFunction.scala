package admm.functions

import org.apache.spark.Logging
import breeze.linalg.{DenseVector => BDV}

trait Prox {
  
  def prox(x: BDV[Double], rho: Double): BDV[Double]

}
