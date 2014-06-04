package admm.linalg

import org.apache.spark.{SparkConf, SparkContext, Partition, TaskContext,Dependency}
import breeze.linalg.{DenseVector => BDV, DenseMatrix => BDM}
import org.apache.spark.rdd.RDD

class RandomMatrix (sc: SparkContext,
                    val numRows: Long,
                    val numCols: Int,
                    val blockHeight: Int,
                    val maxBlocksPerPartition: Int)
                    extends RDD[BDM[Double]](sc, new Array[Dependency[BDM[Double]]](0)) {

  val numBlocks = (numRows / blockHeight + (if(numRows % blockHeight > 0) 1 else 0)).toInt

  val blocksPerPartition = (1 to numBlocks).grouped(maxBlocksPerPartition).toArray

  val numPartitions = blocksPerPartition.length

  val numBlocksPerPartition = (0 to numPartitions-1).map(blocksPerPartition(_).length)

  val partitionSizes = (0 to numPartitions-1).map(partind => {
    val blocks = numBlocksPerPartition(partind)
    (1 to blocks).map(b => {
      if((partind != numPartitions-1 || 
          b != numBlocksPerPartition(partind))
          || numRows % blockHeight == 0){
        blockHeight 
      }else{
        (numRows % blockHeight).toInt
      }
    })
  })

  def compute(split: Partition, context: TaskContext): Iterator[BDM[Double]] = {
    val sizes = partitionSizes(split.index) 
    new Iterator[BDM[Double]] {
      var blocksComputed = 0
      def hasNext = { blocksComputed < numBlocksPerPartition(split.index) }
      def next: BDM[Double] = {
        blocksComputed += 1
        BDM.rand[Double](sizes(blocksComputed-1),numCols)
      }
    }
  }

  def getPartitions: Array[Partition] = {
    ((0 to numPartitions-1).map(x => new Partition{ val index = x})).toArray
  }
}
