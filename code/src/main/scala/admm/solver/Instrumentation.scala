package admm.solver

import collection.mutable

trait Instrumentation extends ConsensusADMMSolver {
  var iterData: Array[mutable.Map[String,AnyVal]] = Array()
  var currData: mutable.Map[String,AnyVal] = mutable.Map[String,AnyVal]()

  override def iterate(rho:Double){
    currData("rho") = rho
    currData("iter") = iter
    time("iterTime", { super.iterate(rho) }) 
    }

  override def primalTolerance: Double = {
    val res = super.primalTolerance
    currData("primalTolerance") = res
    res
  }

  override def primalResidual(rho: Double): Double = {
    val res = super.primalResidual(rho)
    currData("primalResidual") = res
    res

  }

  override def dualTolerance(rho: Double): Double = {
    val res = super.dualTolerance(rho)
    currData("dualTolerance") = res
    res
  }

  override def dualResidual: Double = {
    val res = time("calcDualResidualTime",{ super.dualResidual })
    currData("dualResidual") = res
    res
    }

  def time[R](key: String, block: => R): R = {
    val t0 = System.nanoTime()
    val result = block    // call-by-name
    val t1 = System.nanoTime()
    currData(key) = t1 - t0
    result
  }
  }
