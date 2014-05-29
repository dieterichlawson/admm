package admm.functions

import breeze.linalg._
import breeze.linalg.{DenseVector => BDV}

class GeqScalar(val threshold: Double, length: Int = 0) extends ProxableFunction(length){
  
  def prox(x: BDV[Double], rho: Double): BDV[Double] = {
    x.map(x_i => Math.max(x_i,threshold))
  }

  def apply(x: BDV[Double]): Double = {
    return 0.0
  }
}
