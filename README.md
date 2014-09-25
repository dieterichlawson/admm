### Apache Spark ADMM implementation

This project is an implementation of consensus form ADMM on Apache Spark.

Here is an example of solving a dense lasso problem:

```
val A = new BlockMatrix(sc.textFile(A_file), blockSize)
val f = L2NormSquared.fromMatrix(A, rho)
val g = new L1Norm(lambda)
var admm = new ConsensusADMMSolver(f, g, abstol, reltol, sc)
admm.solve(rho, maxiters)
```
For more information, see the [paper](http://di.eteri.ch/projects/admm_paper.pdf)
