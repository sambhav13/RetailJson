
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

class WordCount {
  def get(url:String,sc:SparkContext):RDD[(String,Int)] = {
    
    val lines =sc.textFile(url)
    lines.flatMap(_.split(" ")).map((_,1)).reduceByKey(_ + _)
  }
}