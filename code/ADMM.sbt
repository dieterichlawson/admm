name := "Lasso"

version := "1.0"

scalaVersion := "2.10.4"

libraryDependencies += "org.apache.spark" %% "spark-core" % "1.0.0-SNAPSHOT"

libraryDependencies += "org.apache.spark" %% "spark-mllib" % "1.0.0-SNAPSHOT"

libraryDependencies += "com.github.scopt" %% "scopt" % "3.2.0"

libraryDependencies += "com.chuusai" % "shapeless_2.10.4" % "2.0.0"

libraryDependencies += "com.github.fommil.netlib" % "all" % "1.1.2"

resolvers += Resolver.sonatypeRepo("public")

resolvers += "Akka Repository" at "http://repo.akka.io/releases/"

resolvers ++= Seq(
    "Sonatype OSS Releases"  at "http://oss.sonatype.org/content/repositories/releases/",
    "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"
)
