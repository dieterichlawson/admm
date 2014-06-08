cd ../code
sbt "runMain admm.LassoExample
       --Afile ../data/smallA.csv \
       --spark_home $HOME/dev/spark \
       --scratch_dir $HOME/foo \
       --maxiters 500 \
       --blocksize 10 \
       --abstol 1e-3 \
       --reltol 1e-3 \
       --lambda 0.05 \
       --rho 0.01"
