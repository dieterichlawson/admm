cd /Users/dlaw/school/spr_2014/ee364b/project/code
sbt "runMain admm.LassoExample
       --Afile ../data/smallA.csv \
       --maxiters 500 \
       --blocksize 10 \
       --abstol 1e-3 \
       --reltol 1e-3 \
       --lambda 0.05 \
       --rho 0.0032"
