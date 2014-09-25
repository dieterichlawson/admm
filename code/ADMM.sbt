name := "ADMM"

version := "1.0"

scalaVersion := "2.10.3"

libraryDependencies += "org.apache.spark" %% "spark-core" % "1.1.0-SNAPSHOT" 

libraryDependencies += "org.apache.spark" %% "spark-mllib" % "1.1.0-SNAPSHOT" 

libraryDependencies += "org.apache.hadoop" % "hadoop-client" % "1.0.4" 

libraryDependencies += "com.github.scopt" %% "scopt" % "3.2.0"

libraryDependencies  ++= Seq(
    "org.scalanlp" %% "breeze" % "0.7",
    "org.scalanlp" %% "breeze-natives" % "0.7"
)

libraryDependencies += "com.github.scopt" %% "scopt" % "3.2.0"

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.0" % "test"

libraryDependencies += "com.novocode" % "junit-interface" % "0.10" % "test"

libraryDependencies += "org.scalacheck" % "scalacheck_2.10" % "1.10.0"

resolvers += Resolver.mavenLocal

resolvers += Resolver.sonatypeRepo("public")

resolvers += "Akka Repository" at "http://repo.akka.io/releases/"

resolvers ++= Seq(
  "Sonatype OSS Releases"  at "http://oss.sonatype.org/content/repositories/releases/",
  "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"
)

javaOptions in (Test,run) += "-Xmx6G"

