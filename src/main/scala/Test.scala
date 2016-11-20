import org.apache.spark.sql.SparkSession

object Test{

	def main(args:Array[String]) ={


		val spark = SparkSession
				.builder
				.master("local")
				.config("spark.sql.shuffle.partitions", "4")
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

						val table = userDS.createOrReplaceTempView("person")
						//spark.sqlContext.cacheTable("person")
						val last = spark.sql("select id,max(age) from person group by id")
						
						val query = last.writeStream
						.outputMode("complete")
						.format("console")
						.start()

						query.awaitTermination()
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