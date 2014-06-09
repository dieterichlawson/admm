name := "ADMM"

version := "1.0"

scalaVersion := "2.10.4"

libraryDependencies += "org.apache.spark" %% "spark-core" % "1.0.0"

libraryDependencies += "org.apache.spark" %% "spark-mllib" % "1.0.0"

libraryDependencies += "org.apache.hadoop" % "hadoop-client" % "1.0.4"

libraryDependencies += "com.github.scopt" %% "scopt" % "3.2.0"

libraryDependencies += "com.github.fommil.netlib" % "all" % "1.1.2"

libraryDependencies += "org.scalanlp" % "breeze_2.10" % "0.8"

libraryDependencies += "org.scalanlp" % "breeze-natives_2.10" % "0.8"

resolvers += Resolver.sonatypeRepo("public")

resolvers += "Akka Repository" at "http://repo.akka.io/releases/"

resolvers ++= Seq(
  "Sonatype OSS Releases"  at "http://oss.sonatype.org/content/repositories/releases/",
  "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"
)

javaOptions in (Test,run) += "-Xmx6G"

