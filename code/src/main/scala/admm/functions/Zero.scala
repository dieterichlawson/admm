package admm.functions

import breeze.linalg._
import breeze.linalg.{DenseVector => BDV}

class Zero(length: Int = 0) extends ProxableFunction(length){
  
  def prox(x: BDV[Double], rho: Double): BDV[Double] = {
    BDV.zeros[Double](length) 
  }

  def apply(x: BDV[Double]): Double = {
    0
  }
}
