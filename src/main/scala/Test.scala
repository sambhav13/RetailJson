import org.apache.spark.sql.SparkSession

object Test{

	def main(args:Array[String]) ={


		val spark = SparkSession
				.builder
			//	.config("spark.sql.streaming.checkpointLocation", "./checkPointDir")
			//	.master("local[*]")
				.master("spark://ip-172-31-21-112.ec2.internal:7077")
				.config("spark.sql.shuffle.partitions", "2")
				
				.appName("StructuredNetworkWordCount")
				.getOrCreate()


				import spark.implicits._

				// Create DataFrame representing the stream of input lines from connection to localhost:9999
				val lines = spark.readStream
				.format("socket")
				.option("host", "localhost")
				.option("port", 9999)
				.load()

				val rdd =   lines.as[String]
						val rdd_2 = rdd.map(x => x.split(","))

						//val rdd_person = rdd.as[Person]

						//df.as[UserTransaction,Encoders

						//  val dff:Dataset[Person] = rdd.as[Person]

						// val dff_2 = dff.withColumn("age",$"age".cast("int"))

						//	val ds = rdd_2.as[Person]
						//	val newDs = ds.map(x => (x.name,x.age))


						//val userDS = newDs.map(x => Person(x._1,x._2))
						val userDS = rdd_2.map(x => Person(x(0).toInt,x(1).toString(),x(2).toInt))
						//.asInstanceOf[Users]
						//userDS.show()

						//JDBC static datasource
						import java.util.Properties

					/*	val prop = new Properties() 
		prop.put("user", "root")
		prop.put("password", "") 

		val staticDf = spark.read.jdbc("jdbc:mysql://172.17.01:3306/test", "table1", prop)

		val joinedData = userDS.join(staticDf)*/
		val table = userDS.createOrReplaceTempView("person")
		//spark.sqlContext.cacheTable("person")
		val last = spark.sql("select id,max(age) as max from person group by id")

		val last2 = spark.sql("select id,min(age) as min from person group by id")

		val query = last.writeStream
		.outputMode("complete")
		.format("console")
		.start()
		
		//query.stop()

		/*val query2 = last2.writeStream
		.queryName("minaggregates")
		.outputMode("complete")
		.format("console")
		.start()*/
		
		 /*last2.writeStream
		 .format("parquet")
		 .option("path","./min")
     .start()*/
		val query2 = last2.writeStream
		.outputMode("complete")
		.format("console")
		.start()

		//spark.sql("select * from minaggregates limit 1").createOrReplaceTempView("limited")
		//.show()
		//spark.sql("select * from minaggregates").show()
		//write.partitionBy("id").csv("./minaggregates") 

		println(query2.exception)
	  println(spark.streams.active.foreach { println })
	  
	   println("query1 ---> "+query.sinkStatus.description)
	  println("query2 ---> "+query2.sinkStatus.description)
		query.awaitTermination()
	  query2.awaitTermination()
	//	query.awaitTermination()
		/*userDS.filter(_.age>20).show()


						//  """

						// Split the lines into words
						val words = lines.as[String].flatMap(_.split(" "))

						// words.map(func, encoder)

						// Generate running word count
						val wordCounts = words.groupBy("value").count()

						// Start running the query that prints the running counts to the console
						val query = wordCounts.writeStream
						.outputMode("complete")
						.format("console")
						.start()

						query.awaitTermination()*/
	}

case class Person(val id:Int,val name:String,val age:Int)
}