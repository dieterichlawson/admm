cd /Users/dlaw/school/spr_2014/ee364b/project/code
sbt "runMain admm.LassoExample
       --Afile ../data/BigA.csv \
       --maxiters 500 \
       --blocksize 1024 \
       --abstol 5e-3 \
       --reltol 5e-3 \
       --lambda 0.05 \
       --rho 0.01"
