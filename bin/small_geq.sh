cd /Users/dlaw/school/spr_2014/ee364b/project/code
sbt "runMain admm.GeqExample \
       --Afile ../data/smallA.csv \
       --maxiters 500 \
       --blocksize 10 \
       --abstol 1e-3 \
       --reltol 1e-3 \
       --geq 0.5 \
       --rho 0.5"
