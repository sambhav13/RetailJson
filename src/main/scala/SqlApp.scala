import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.SQLContext
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.Seconds
import org.apache.spark.sql.SaveMode
import org.apache.spark.storage.StorageLevel
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.Time



case class Record(id:Int, status:String, source:String)

object SqlApp {
  def main(args: Array[String]) {
    val sparkConf = new SparkConf().setAppName("SqlApp2").setMaster("local[2]")
    val sc = new SparkContext(sparkConf)
    val sqlContext = new SQLContext(sc)
    // Create the streaming context with a 10 second batch size
    val ssc = new StreamingContext(sc, Seconds(10))

    val lines = ssc.socketTextStream("localhost", 9999, StorageLevel.MEMORY_AND_DISK_SER)

    var alldata=sqlContext.emptyDataFrame
    alldata.createOrReplaceTempView("alldata")

    lines.foreachRDD( (rdd: RDD[String], time: Time) => {
      import sqlContext.implicits._

      // Convert RDD[String] to DataFrame
      val data = rdd.map(w => {
        val words = w.split(",")
        Record(words(0).toInt, words(1), words(2))
        }).toDF()

      // Register as table
      data.createOrReplaceTempView("alldata")
      //data.save("./DataDstream_Inside"+System.currentTimeMillis(), "json", SaveMode.ErrorIfExists)  // this data is written properly
      data.write.json("./DataDstream_Inside"+System.currentTimeMillis())
    }
    )

    val dataOutside = sqlContext.sql("select * from alldata")
   // dataOutside.save("DataDstream_Outside"+System.currentTimeMillis(), "json", SaveMode.ErrorIfExists) // this data is empty, how to make the SQL table registered inside the forEachRDD loop visible for rest of application
    dataOutside.write.json("./DataDstream_Outside"+System.currentTimeMillis())
    ssc.start()
    ssc.awaitTermination()
  }
}