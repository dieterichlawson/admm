package admm.functions

import breeze.linalg._
import breeze.linalg.{DenseVector => BDV}

class Zero(length: Int = 0) extends Function1[BDV[Double],Double] with Prox {
  
  def prox(x: BDV[Double], rho: Double): BDV[Double] = {
    BDV.zeros[Double](length) 
  }

  def apply(x: BDV[Double]): Double = {
    0
  }
}
