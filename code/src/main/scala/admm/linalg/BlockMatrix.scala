package admm.linalg

import breeze.linalg.{DenseVector => BDV, DenseMatrix => BDM}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

class BlockMatrix(val blocks: RDD[BDM[Double]],
                  var nRows: Long,
                  var nCols: Int,
                  val blockHeight: Int) extends Serializable{


  def this(file: RDD[String], blockHeight: Int = 1024) = 
    this(file.map(_.split(',').map(_.toDouble)). 
              mapPartitions(_.sliding(blockHeight, blockHeight)).
              map(x => BDM.tabulate(x.length, x.head.length)((r,c) => x(r)(c))),
              0L, 0, blockHeight)

    def numCols(): Long = {
      if (nCols <= 0){
        nCols = blocks.first().cols
      }
      nCols
    }

    def numRows(): Long = {
      if(nRows <= 0L){
        nRows = blocks.aggregate[Long](0L)((count,M) => count + M.rows, _+_)
      }
      nRows
    }

    def *(that: BDM[Double]) = 
      new BlockMatrix(blocks.map[BDM[Double]](_*that), nRows, that.cols, blockHeight)
}
object BlockMatrix {
  
  def rand(nRows: Int, nCols: Int, blockHeight: Int, blocksPerPartition: Int, sc: SparkContext): BlockMatrix= {
    new BlockMatrix(new RandomMatrix(sc, nRows, nCols, blockHeight, blocksPerPartition)
                    , nRows, nCols, blockHeight)
  }
}
