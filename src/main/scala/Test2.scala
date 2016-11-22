
import org.apache.spark.sql.SparkSession

object Test2{

	def main(args:Array[String]) ={


		val spark = SparkSession
				.builder
				//	.config("spark.sql.streaming.checkpointLocation", "./checkPointDir")
				//.master("local[2]")
				.master("spark://ip-172-31-21-112.ec2.internal:7077")
				.config("spark.sql.shuffle.partitions", "1")

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


						val userDS = rdd_2.map(x => Person(x(0).toInt,x(1).toString(),x(2).toInt))

						val table = userDS.createOrReplaceTempView("person")
						//spark.sqlContext.cacheTable("person")
						val last = spark.sql("select id,max(age) as max from person group by id")

						/*val query = last.writeStream
						.outputMode("complete")
						.format("console")
						.start()*/
						
						val query = last.writeStream
						.queryName("aggregates")
						.outputMode("complete")
						.format("memory")
						.start()

						val tbl = spark.sql("select * from aggregates")
						tbl.createOrReplaceTempView("agg")
						spark.table("agg").cache()
						
          query.awaitTermination()
						
	}

case class Person(val id:Int,val name:String,val age:Int)
}