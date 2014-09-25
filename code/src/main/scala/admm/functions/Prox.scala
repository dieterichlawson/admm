package admm.functions

import breeze.linalg.{DenseVector => BDV}

trait Prox {
  
  def prox(x: BDV[Double], rho: Double): BDV[Double]

}
