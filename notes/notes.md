## Functions to implement

* kl divergence
* bregman divergence
* entropy
* gaussian map operator??
* huber

## Notes on optimization step

if A is identity, then minimization step becomes prox operator

if f(x) is quadratic use matrix inversion lemma (slide 24)

f(x) smooth can use newton methods or quasi newton *use warm start*


## Possible Examples
* SVM w/different loss functions
* Lasso


## Benchmark versus

* Vowpal Wabbit on Hadoop
* Spark's built-in Logistic regression, etc...


## Pie in the sky

* Split matrices so that each worker has only what will fit into memory. If there are more workers
  than needed, only use a certain number (possible?).
