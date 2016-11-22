import org.apache.spark.sql.SparkSession


class TestHive {
  
}

object TestHive{
  
  def main(args:Array[String])={
    
      val spark = SparkSession
				.builder
				//	.config("spark.sql.streaming.checkpointLocation", "./checkPointDir")
				//.master("local[2]")
				.master("spark://ip-172-31-21-112.ec2.internal:7077")
				.config("spark.sql.shuffle.partitions", "1")
				.config("spark.sql.warehouse.dir","/home/ec2-user/downloads/software/spark-2.0.2-bin-hadoop2.7/sbin/spark-warehouse")
				.config("hive.metastore.uris", "thrift://METASTORE:9083")
				.enableHiveSupport()
				.appName("StructuredNetworkWordCount")
				.getOrCreate()


				//spark.sql("select name as csvname,age from peopleCSVTable").show()
				
				import spark.implicits._

				
				val peopJson = spark.read.json("people.json")
				peopJson.createOrReplaceTempView("people")
				
				peopJson.registerTempTable("peopleTemp")
			  peopJson.write.format("csv").saveAsTable("peopleCSVTable")
			  
			  
			  val url = "jdbc:mysql://localhost:3306/test"
			  val table = "people";
      
        import java.util.Properties
        
        
        val prop = new Properties() 
		    prop.put("user", "root")
		    prop.put("password", "")
		    prop.put("driver", "com.mysql.jdbc.Driver")

      
			  peopJson.write.mode(SaveMode.Append).jdbc(url,"people",prop)
			  
			//	spark.read.format("jdbc").option("url",
			  
			  val data = spark.read.format("jdbc").option("url", "jdbc:mysql://localhost:3306/test")
			                .option("driver", "com.mysql.jdbc.Driver")
			                .option("dbtable", "people")
			                .option("user", "root")
			                .option("password", "")
			                .load()
			                
			   data.show()
			  
			  
			  val catalog = spark.catalog
				catalog.listDatabases().select("name").show()
      //  val df = sc.jsonFile("examples/src/main/resources/people.json")
      //  spark.sql("select * from people").show()
       while(true){
         
       }
      // spark.stop()
    
    
  }
}