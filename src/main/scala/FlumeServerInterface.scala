
/*import org.apache.spark.streaming.flume._
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.Seconds
import org.apache.spark.storage.StorageLevel
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.dstream.ReceiverInputDStream
import org.apache.spark.streaming.dstream.DStream*/

//import org.apache.spark.input.

class FlumeServerInterface {
  
}

object flumeServerInterface{
  
  
  case class Person(val name:String,val age:Int)
  def main(args:Array[String]) = {
    
    /* val conf = new SparkConf().setMaster("Flume Reader").setAppName("FlumeSourceRetail")
    val spark = SparkSession
              .builder
              .config(conf)
              .config("spark.sql.shuffle.partitions", "4")
              .getOrCreate()
				
				

    // spark.readStream.format(source)
 
  
  val streamingContext = new StreamingContext(conf,Seconds(5))
  
   val hostIp = "35.154.2.153"
   val port = 8081
   val flumeStream:ReceiverInputDStream[SparkFlumeEvent] = FlumeUtils.createPollingStream(streamingContext,
                 hostIp,port,StorageLevel.MEMORY_AND_DISK)
        
              
   val splitData = flumeStream.map ( x => x.event.toString().split(",") )
   val personStream = splitData.map (x => Person(x(0),x(1).toInt))
   
   //val pairedDStream:DStream[    =
  val pairedDSt:DStream[(Int,String)] = personStream.map{ case Seq(name:String, age:Int) => age -> name} 
  //val paired:DStream[Int,String] =    personStream.map(x => x.age -> (x.age,x.name))
   
   flumeStream.count().map(cnt => "Received " + cnt + " flume events." ).print()
                 
   //flumeStream.map { x => x.readExternal() }
                 
    //flumeStream.    
   
   streamingContext.start()
   streamingContext.awaitTermination();*/
  }
}