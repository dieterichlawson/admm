package admm.functions

import org.apache.spark.Logging
import breeze.linalg.{DenseVector => BDV}

abstract class ProxableFunction(val length: Int) 
           extends Function1[BDV[Double], Double] with Serializable with Logging{
 
  def apply(x: BDV[Double]): Double 

  def prox(x: BDV[Double], rho: Double): BDV[Double]

}
