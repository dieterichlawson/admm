cd /Users/dlaw/school/spr_2014/ee364b/project/code
sbt "runMain admm.GeqExample \
       --Afile ../data/BigA.csv \
       --maxiters 500 \
       --blocksize 1024 \
       --abstol 1e-3 \
       --reltol 1e-3 \
       --geq 0.5 \
       --rho 5"
