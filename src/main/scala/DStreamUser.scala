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
import java.sql.Timestamp
import java.text.SimpleDateFormat
import net.liftweb.json.DefaultFormats
import java.text.DateFormat
import java.util.Date

import org.apache.spark.sql.functions.udf
import org.apache.spark.sql.types.DataType





object DStreamUser {
  
  case class User(userId:String, rack:String, time:Timestamp)
  
  def time_delta( t1:Timestamp, t2:Timestamp):Long = { 
    //from datetime import datetime
    val delta = t1.getTime() - t2.getTime()
    return delta
  }
  
   def timeAdd( t1:Timestamp):Timestamp = { 
    //from datetime import datetime
    val delta = t1.getTime + 24 * 60 * 60 * 1000
    return new Timestamp(delta)
  }
   
   
   def timeMonth(t1:Timestamp):Int = {
     
     val month = t1.getMonth();
     return month
   }
   
   def timeDay(t1:Timestamp):Int = {
     
     val month = t1.getDate();
     return month
   }
   
   
   def timeQuarter(t1:Timestamp):Int = {
     
     val month = t1.getMonth();
     var quarter:Int = 0
     if(month<3){
       quarter = 1
     } else if(month >3 & month<6) {
              quarter = 2
     } else if(month >6 & month<9){
       quarter = 3
     }else{
       quarter = 4
     }
    
     return quarter
   }


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
			                .option("dbtable", "user")
			                .option("user", "root")
			                .option("password", "")
			                .load()
			                
			   readData.show()
     
    lines.foreachRDD( (rdd: RDD[String], time: Time) => {
      import sqlContext.implicits._
     /* implicit val formats = new DefaultFormats {
    override def dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
  } */

      // Convert RDD[String] to DataFrame
      val data = rdd.map(w => {
        val words = w.split(",")
       val  df:DateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        val date:Date = df.parse(words(2))
        
        val time = new Timestamp(date.getTime())
        User(words(0), words(1), time)
        }).toDF()

        
        val myUDF = udf(time_delta _ )
        val oneDay = udf(timeAdd _ )
        val monthUDF = udf(timeMonth _ )
        val dayUDF = udf(timeDay _ )
        val quarterUDF = udf(timeQuarter _ )
        
        val dailyData = data.union(readData).withColumn("day", dayUDF(data.col("time")))
        val monthlyData = data.union(readData).withColumn("month", monthUDF(data.col("time")))
        val quarterlyData = data.union(readData).withColumn("quarter", quarterUDF(data.col("time")))
        
        
        val unionedData = data.union(readData).withColumn("timespent",oneDay(data.col("time")))
        //.withColumn("timespent", myUDF(data.col("time"),readData.col("time")) )
        //unionedData.show()
        
       
        
        
        import org.apache.spark.sql.functions._ 
        
        val footFalldata = data.union(readData).groupBy("userId").agg(count("userId").alias("Footfall"))
        footFalldata.show()
        
        footFalldata.write.mode(SaveMode.Overwrite).jdbc(url,"userVisits",prop);
      
        val aggregatedData = data.union(readData).groupBy("userId").agg(max("time").alias("maxTime"),min("time").alias("minTime"
                            ))
       aggregatedData.withColumn("timespent",
                                myUDF(aggregatedData.col("maxTime"),aggregatedData.col("minTime"))).show()
       // data.union(readData).groupBy("userId").max("time").show()
        //val joinedData = data.join(readData,data.col("userId")===readData.col("id"),joinType="inner")
        
        //joinedData.show()
                                
       val timeSpentData_perUser = aggregatedData.withColumn("timespent",
                                myUDF(aggregatedData.col("maxTime"),aggregatedData.col("minTime")))
       
       
        val aggregatedData_perUserperRack = data.union(readData).groupBy("userId").agg(max("time").alias("maxTime"),min("time").alias("minTime"
                            ))
                                
        val sortedData = data.union(readData).sort($"time")
       val userRack = sortedData.select("userId", "rack").limit(1)
       
       
       userRack
                                
        data.write.mode(SaveMode.Append).jdbc(url,"user",prop)
     })
         
     ssc.start()
     ssc.awaitTermination()
        
    }
}