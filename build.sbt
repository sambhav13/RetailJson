name := "Retail HSC"

version := "1.0-SNAPSHOT"

scalaVersion := "2.10.6"


libraryDependencies += "org.apache.spark" % "spark-core_2.10" % "2.0.1" 
libraryDependencies += "org.apache.spark" % "spark-streaming_2.10" % "2.0.1"
libraryDependencies += "org.apache.spark" % "spark-sql_2.10" % "2.0.1"
libraryDependencies += "org.apache.spark" % "spark-streaming-flume_2.10" % "2.0.1"
libraryDependencies += "org.json4s" % "json4s-native_2.10" % "3.5.0"