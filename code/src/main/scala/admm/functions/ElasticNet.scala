package admm.functions

import breeze.linalg._
import breeze.linalg.{DenseVector => BDV}

class ElasticNet(val lambda: Double, 
                 val gamma: Double,
                 length: Int = 0) extends ProxableFunction(length){
  
  def prox(x: BDV[Double], rho: Double): BDV[Double] = {
    x.map(x_i => (1/(1+lambda*gamma))*softThreshold(x_i,lambda/rho))
  }

  def softThreshold(x: Double, thresh: Double): Double = {
    Math.max(1.0-thresh/Math.abs(x), 0.0)*x
  }

  def apply(x: BDV[Double]): Double = {
    return lambda*norm(x,1) + (gamma/2)*Math.pow(norm(x),2)
  }
}
