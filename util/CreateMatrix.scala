import util.Random

object CreateMat {
  def main(args: Array[String]){
    val numRows = args(0).toInt
      val numCols = args(1).toInt
      for(r <- 1 to numRows){
        if(r % 1000 == 0){
          System.err.println(s"$r / $numRows")
        }
        var line = new StringBuilder()
          for(c  <- 1 to numCols){
            line.append(Random.nextDouble + ",")
          }
        println(line.toString.substring(0,line.toString.length-1))
      }
  }

}
