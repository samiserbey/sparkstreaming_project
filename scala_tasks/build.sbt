name := "sparkstreaming_scala_tasks"

version := "0.1"

scalaVersion := "2.12.14"

idePackagePrefix := Some("com.scala_tasks.sparkstreaming")

// https://mvnrepository.com/artifact/log4j/log4j
libraryDependencies += "log4j" % "log4j" % "1.2.17"

libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.0.3" % "compile"
libraryDependencies += "org.apache.spark" %% "spark-core" % "3.0.3" % "compile"
libraryDependencies += "org.apache.spark" %% "spark-streaming" % "3.0.3" % "compile"

