cd ../code
sbt "runMain admm.LassoExample
       --Afile ../data/smallA.csv \
       --spark_home $HOME/dev/spark \
       --scratch_dir $HOME/foo \
       --maxiters 500 \
       --blocksize 10 \
       --abstol 1e-5 \
       --reltol 1e-5 \
       --lambda 0.05 \
       --rho 0.032"
