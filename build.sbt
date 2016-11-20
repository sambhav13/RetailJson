name := "Retail HSC JSON"

version := "1.0-SNAPSHOT"

scalaVersion := "2.10.6"



libraryDependencies += "org.apache.spark" % "spark-core_2.10" % "2.0.1" 
libraryDependencies += "org.apache.spark" % "spark-streaming_2.10" % "2.0.1"
libraryDependencies += "org.apache.spark" % "spark-sql_2.10" % "2.0.1"
libraryDependencies += "org.apache.spark" % "spark-streaming-flume_2.10" % "2.0.1"
libraryDependencies += "org.json4s" % "json4s-native_2.10" % "3.5.0"
libraryDependencies += "org.json4s" % "json4s-jackson_2.10" % "3.5.0"
libraryDependencies += "net.liftweb" %% "lift-json" % "2.5.1"
libraryDependencies += "org.scalatra" % "scalatra_2.10" % "2.4.1"
