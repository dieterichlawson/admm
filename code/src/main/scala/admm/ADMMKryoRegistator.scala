package admm

import com.esotericsoftware.kryo.Kryo
import org.apache.spark.serializer.KryoRegistrator
import breeze.linalg.DenseVector
import breeze.linalg.DenseMatrix
import admm.functions._

class ADMMKryoRegistrator extends KryoRegistrator {
  override def registerClasses(kryo: Kryo) {
    kryo.register(classOf[DenseVector[Double]])
    kryo.register(classOf[DenseMatrix[Double]])
    kryo.register(classOf[L1Norm])
    kryo.register(classOf[ElasticNet])
    kryo.register(classOf[GeqScalar])
    kryo.register(classOf[L2NormSquared])
    kryo.register(classOf[Prox])
    kryo.register(classOf[Zero])
  }
}
