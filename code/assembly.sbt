import AssemblyKeys._ // put this at the top of the file

assemblySettings

mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>  {
  case PathList("javax", "servlet", xs @ _*)         => MergeStrategy.first
  case PathList("org", "apache", "commons", xs @ _*) => MergeStrategy.first
  case PathList("com", "esotericsoftware", "minlog", xs @ _*) => MergeStrategy.first
  case PathList("scala", "reflect", "api", xs @ _*) => MergeStrategy.first
  case PathList("META-INF", "ECLIPSEF.RSA", xs @ _*) => MergeStrategy.first
  case PathList("META-INF", "mailcap", xs @ _*) => MergeStrategy.first
  case "plugin.properties" => MergeStrategy.first
  case "about.html"                                  => MergeStrategy.rename
  case "log4j.properties" => MergeStrategy.first
  case x => old(x)
}}

