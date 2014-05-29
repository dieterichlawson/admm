package admm.solver

import org.apache.spark.Logging

import breeze.linalg._
import breeze.linalg.{DenseVector => BDV, DenseMatrix => BDM}
import org.apache.spark.rdd.RDD
import org.apache.spark.SparkContext
import org.apache.spark.storage._
import org.apache.spark.broadcast._
import shapeless.syntax.std.tuple._
import admm.functions._

class ConsensusADMMSolver(val f: RDF[_],
                          val g: ProxableFunction,
                          val absTol: Double = 10e-3,
                          val relTol: Double = 10e-3,
                          @transient val sc: SparkContext) 
                          extends Serializable with Logging{

  f.splits.persist()

  var u_i: RDD[BDV[Double]] = f.splits.map(_ => BDV.zeros[Double](f.length))
  u_i.persist()
  var u: BDV[Double] = BDV.zeros[Double](f.length)

  var x_i: RDD[BDV[Double]] = f.splits.map(_ => BDV.zeros[Double](f.length))
  x_i.persist()
  var x: BDV[Double] = BDV.zeros[Double](f.length)

  var z: BDV[Double] = BDV.zeros[Double](f.length)
  var zb: Broadcast[BDV[Double]] = sc.broadcast(z)

  var iters: Int = 0

  def solve(rho: Double, maxIterations: Int = 300){
    do{
      iterate(rho)
    }while(!converged(rho) && iters < maxIterations)
  }
              
  def iterate(rho: Double){
    zb = sc.broadcast(z)
    u_i = u_i.zip(x_i).map(ux => {ux._1 + ux._2 - zb.value})
    x_i = f.prox(u_i.map(ui => {zb.value - ui}), rho)
    x = x_i.reduce(_+_) / f.numSplits.toDouble
    u = u_i.reduce(_+_) / f.numSplits.toDouble
    z = g.prox(x+u, f.numSplits*rho)
    iters += 1
    truncateRDDLineage
  }

  def truncateRDDLineage= {
    val dir = sc.getCheckpointDir getOrElse ""
    x_i.saveAsObjectFile(dir+"/x_i"+iters)
    x_i = sc.objectFile(dir+"/x_i"+iters)
    u_i.saveAsObjectFile(dir+"/u_i"+iters)
    u_i = sc.objectFile(dir+"/u_i"+iters)
  }

  def primalTolerance: Double = {
    Math.sqrt(f.length)*absTol + relTol*Math.max(norm(x),norm(z))
  }

  def primalResidual(rho: Double): Double = {
    rho*Math.sqrt(f.numSplits)*norm(z - zb.value)
  }

  def dualTolerance(rho: Double): Double = {
    Math.sqrt(f.length)*absTol + relTol*norm(u*rho)
  }

  def dualResidual: Double = {
     Math.sqrt(x_i.map(x_i => Math.pow(norm(x_i - z),2)).reduce(_+_))
  }

  def converged(rho: Double): Boolean = {
      val primRes = primalResidual(rho)
      val primTol = primalTolerance
      val dualRes = dualResidual
      val dualTol = dualTolerance(rho)
      val converged = (primRes <= primTol) && 
                      (dualRes <= dualTol)
      val iternum = iters + 1
      logInfo(f"Iteration: $iternum | $primRes%.6f / $primTol%.6f | $dualRes%.6f / $dualTol%.6f")
      if(converged){ logInfo("CONVERGED") }
      return converged
  }
}
