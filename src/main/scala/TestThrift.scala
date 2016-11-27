import org.apache.spark.sql.SparkSession


class TestThrift {

}

object TestThrift{


	def main(args:Array[String]) ={


		val warehouseLocation = "F:\\software\\spark-2.0.0-bin-hadoop2.7\\hadoop"
				val spark = SparkSession
				.builder()
				.appName("SparkSessionZipsExample")
				.config("spark.sql.warehouse.dir", warehouseLocation)
				.master("spark://192.168.99.1:7077")
				//.enableHiveSupport()
				.getOrCreate()

				val df = spark
				.read
				.option("url", "jdbc:hive2://localhost:20000") 
				.option("driver","org.apache.hive.jdbc.HiveDriver")
				.option("dbtable", "dbtest.test") 
				.format("jdbc")
				.load
				
				/*val categoryFootFallTopStatic = sqlContext.read.format("jdbc").option("url", "jdbc:mysql://localhost:3306/test")
                                          	.option("driver", "com.mysql.jdbc.Driver")
                                          	.option("dbtable", "DailyCategoryFootFallCount")
                                          	.option("user", "root")
                                          	.option("password", "")
                                          	.load()*/
				
				df.show()
				
				
	}
}