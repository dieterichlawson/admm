import AssemblyKeys._ // put this at the top of the file

assemblySettings

mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>  {
  case PathList("javax", "servlet", xs @ _*)         => MergeStrategy.first
  case PathList("org", "apache", "commons", xs @ _*) => MergeStrategy.first // commons-beanutils-core-1.8.0.jar vs commons-beanutils-1.7.0.jar
  case PathList("com", "esotericsoftware", "minlog", xs @ _*) => MergeStrategy.first // kryo-2.21.jar vs minlog-1.2.jar
  case "about.html"                                  => MergeStrategy.rename
  case "log4j.properties" => MergeStrategy.first
  case x => old(x)
}}
