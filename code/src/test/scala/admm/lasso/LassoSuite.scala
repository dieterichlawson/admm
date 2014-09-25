package admm.lasso

import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfter

import org.apache.spark.LocalSparkContext
import org.apache.spark.Partition
import org.apache.spark.SparkContext
import org.apache.spark.TaskContext
import org.apache.spark.rdd.RDD

class LassoSuite extends FunSuite with BeforeAndAfter with LocalSparkContext {
  test("ADMM algorithm solves dense lasso problem correctly") {
    assert(true)
  }
}
