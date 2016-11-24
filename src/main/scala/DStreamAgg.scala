import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.SQLContext
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.Seconds
import org.apache.spark.sql.SaveMode
import org.apache.spark.storage.StorageLevel
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.Time

import org.apache.spark.sql.types.{
    StructType, StructField, StringType, IntegerType}
import org.apache.spark.sql.Row



object DStreamAgg {
  
  case class Record(id:Int, status:String, source:String)
  def main(args:Array[String])={
    
    val sparkConf = new SparkConf().setAppName("DStreamAgg")
                                   .setMaster("local[2]")
                                   
                                   //.setMaster("spark://ip-172-31-21-112.ec2.internal:7077")
   // val sc = new SparkContext(sparkConf)
   
    // Create the streaming context with a 3 second batch size
    val ssc = new StreamingContext(sparkConf, Seconds(3))
    
    val sqlContext = new SQLContext(ssc.sparkContext)
    val lines = ssc.socketTextStream("localhost", 9999, StorageLevel.MEMORY_AND_DISK_SER)

    var alldata=sqlContext.emptyDataFrame
    alldata.createOrReplaceTempView("alldata")
    
    
    val schema = StructType(
    StructField("id", IntegerType, true) ::
    StructField("status", StringType, false) ::
    StructField("source", StringType, false) ::
    Nil)
    
   /*  val dataSet = sqlContext.read.schema(schema).json("./Data/DataDstream_Inside_partition")
   println(dataSet.rdd.partitioner)
     
    val newDstr =  lines.map(w => {
        val words = w.split(",")
        Record(words(0).toInt, words(1), words(2))
        }
    )*/
     // Join each batch in stream with the dataset
  //val joinedstr = newDstr.transform(x => dataSet.join(x,x$"id".equalTo(dataSet.col("id")))
  
   val url = "jdbc:mysql://localhost:3306/test"
			  val table = "people";
      
        import java.util.Properties
        
        
        val prop = new Properties() 
		    prop.put("user", "root")
		    prop.put("password", "")
		    prop.put("driver", "com.mysql.jdbc.Driver")

      
			  val readData = sqlContext.read.format("jdbc").option("url", "jdbc:mysql://localhost:3306/test")
			                .option("driver", "com.mysql.jdbc.Driver")
			                .option("dbtable", "event")
			                .option("user", "root")
			                .option("password", "")
			                .load()
			                
			   readData.show()
     
    lines.foreachRDD( (rdd: RDD[String], time: Time) => {
      import sqlContext.implicits._

      // Convert RDD[String] to DataFrame
      val data = rdd.map(w => {
        val words = w.split(",")
        Record(words(0).toInt, words(1), words(2))
        }).toDF()

        val joinedData = data.join(readData,data.col("id")===readData.col("id"),joinType="inner")
        joinedData.show()
        data.write.mode(SaveMode.Append).jdbc(url,"event",prop)
     })
         
     ssc.start()
     ssc.awaitTermination()
        
    }
}