package admm.solver

import org.apache.spark.Logging

import breeze.linalg._
import breeze.linalg.{DenseVector => BDV, DenseMatrix => BDM}
import org.apache.spark.rdd.RDD
import org.apache.spark.SparkContext
import org.apache.spark.storage._
import org.apache.spark.broadcast._
import admm.functions._

class ConsensusADMMSolver(val f: RDF[_],
                          val g: Function1[BDV[Double], Double] with Prox with Serializable,
                          val n: Int,
                          val absTol: Double = 10e-3,
                          val relTol: Double = 10e-3,
                          @transient val sc: SparkContext)
                          extends Serializable with Logging{

  f.splits.cache()

  var u_i: RDD[BDV[Double]] = f.splits.map(_ => BDV.zeros[Double](n))
  u_i.cache()
  var u: BDV[Double] = BDV.zeros[Double](n)

  var x_i: RDD[BDV[Double]] = f.splits.map(_ => BDV.zeros[Double](n))
  x_i.cache()
  var x: BDV[Double] = BDV.zeros[Double](n)

  var r_i: RDD[Double] = f.splits.map(_ => 0)
  r_i.cache()
  var r: Double = 0

  var z: BDV[Double] = BDV.zeros[Double](n)
  var zb: Broadcast[BDV[Double]] = sc.broadcast(z)

  var iter: Int = 0

  def solve(rho: Double, maxIterations: Int = 300, evalFn: Boolean = false){
    solve(x => rho, maxIterations, evalFn)    
  }

  def solve(rho: Int => Double, maxIterations:Int, evalFn: Boolean){
    var done = false
    while(!done){
      iter += 1
      val iter_rho = rho(iter)
      iterate(iter_rho)
      done = converged(iter_rho, evalFn) || iter >= maxIterations
    }
  }

  def iterate(rho: Double){
    zb = sc.broadcast(z)
    r_i = x_i.map(x => Math.pow(norm(x - zb.value), 2))
    r_i.checkpoint()
    u_i = u_i.zip(x_i).map(ux => ux._1 + ux._2 - zb.value)
    u_i.checkpoint()
    x_i = f.prox(u_i.map(ui => {zb.value - ui}), rho)
    x_i.checkpoint()
    x = x_i.reduce(_+_) / f.numSplits.toDouble
    u = u_i.reduce(_+_) / f.numSplits.toDouble
    z = g.prox(x+u, f.numSplits*rho)
    r = Math.sqrt(r_i.reduce(_+_))
  }

  def primalTolerance: Double = {
    Math.sqrt(n)*absTol + relTol*Math.max(norm(x),norm(z))
  }

  // previous z is stored in zb.value
  def primalResidual(rho: Double): Double = {
    rho*Math.sqrt(f.numSplits)*norm(z - zb.value)
  }

  def dualTolerance(rho: Double): Double = {
    Math.sqrt(n)*absTol + relTol*norm(u*rho)
  }

  def dualResidual: Double = {
    r
  }

  def converged(rho: Double, evalFn: Boolean): Boolean = {
      val primRes = primalResidual(rho)
      val primTol = primalTolerance
      val dualRes = dualResidual
      val dualTol = dualTolerance(rho)
      val converged = (primRes <= primTol) && 
                      (dualRes <= dualTol)
      if(evalFn){
        logInfo(f"Iteration: $iter | $fnVal | $primRes%.6f / $primTol%.6f | $dualRes%.6f / $dualTol%.6f")
      }else{
        logInfo(f"Iteration: $iter | $primRes%.6f / $primTol%.6f | $dualRes%.6f / $dualTol%.6f")
      }
      if(converged){ logInfo("CONVERGED") }
      return converged
  }
  
  def fnVal: Double = {
    f(z) + g(z)
  }
}
