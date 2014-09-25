package admm.solver

import collection.mutable
import scala.collection.mutable.MutableList

trait Instrumentation extends ConsensusADMMSolver {
  var iterData: MutableList[mutable.Map[String,AnyVal]] = MutableList()
  var currData: mutable.Map[String,AnyVal] = mutable.Map[String,AnyVal]()

  override def iterate(rho:Double){
    currData("rho") = rho
    currData("iter") = iter
    time("iterTime", { super.iterate(rho) }) 
  }

  override def converged(rho: Double, evalFn: Boolean):Boolean = {
    val res = super.converged(rho, evalFn)
    iterData :+= currData
    currData = mutable.Map[String,AnyVal]()
    res
  }

  override def primalTolerance: Double = {
    store("primalTolerance",{ super.primalTolerance })
  }

  override def primalResidual(rho: Double): Double = {
    store("primalResidual",{ super.primalResidual(rho) })
  }

  override def dualTolerance(rho: Double): Double = {
    store("dualTolerance",{ super.dualTolerance(rho) })
  }

  override def dualResidual: Double = {
     store("dualResidual",{ super.dualResidual }) 
  }

  override def fnVal: Double = {
    store("fnVal",{ time("evalFnTime",{ super.fnVal }) })
  }
  
  def time[R](key: String, block: => R): R = {
    val t0 = System.nanoTime()
    val result = block 
    val t1 = System.nanoTime()
    currData(key) = t1 - t0
    result
  }

  def store[R <: AnyVal](key: String, block: => R): R = {
    val result = block
    currData(key) = result
    result
  }

  def printData = {
    println("iteration,dualResidual,dualTolerance,primalResidual,primalTolerance,iterTime")
    iterData.map(printIterData) 
  }

  def printIterData(data: mutable.Map[String,AnyVal]) = {
    val str = data("iter") +"," +
              data("dualResidual") + "," + data("dualTolerance") + "," +
              data("primalResidual") + "," + data("primalTolerance") + "," +
              data("iterTime") 
  println(str)
  }
}
