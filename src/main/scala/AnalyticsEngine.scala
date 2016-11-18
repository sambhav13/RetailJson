import org.apache.spark.sql.SparkSession
import org.apache.avro.ipc.specific.Person
import java.sql.Timestamp
import scala.util.parsing.json.JSON

import org.json4s._
import org.json4s.native.JsonMethods._

class AnalyticsEngine {
  
  
  
  
  
}



object AnalyticsEngine{
  
  
  
  case class ParsedPage(crawlDate: String, domain:String, url:String, text: String)


  
  case class Person(val id:Int,val name:String,val age:Int)
  
  /**
   * userId : the specific userID
   * cart : the items purchased by user with their quantity 
   */
  case class CheckOutEvent(val userId:String,
                           val cart:Map[String,Int],
                           val prodCat:Map[String,Int]  
                          )
  
                           
 
  
  case class LocationEvent(
                           val userId:String,
                           val orgId:String,
                           val rackId:String,
                           val time:Timestamp)
               
                           
                   /*    {
   "eventType":"LocationEvent",
   "Event":{
		"userId":"u1",
		"orgId":"org1",
		"storeid":"s1",
		"rackid":"r1",
		"time":"2016-11-11 11:10:10"	
	   }
}    */
                           
                           
     /*
       {
   "eventType":"CheckOutEvent",
   "Event":{
		"userId":"u1",
		"orgId":"org1",
		"storeid":"s1",
		"cart":[
			{"productId":"p1","quantity":2,"price":10},
			{"productId":"p2","quantity":1,"price":12.4},
			{"productId":"p3","quantity":3,"price":20}
		],
		"checkOuttime":"2016-11-11 11:10:10"	
	   }
}                      
      */
  
  def main(args:Array[String]):Unit={
    
   
    
    implicit val formats = DefaultFormats
    
     def parseString(msg:String)={
  val parsedMsg:Option[Any] = JSON.parseFull(msg)
  println(parsedMsg.getOrElse("nothing"))
   
  }
    
    parseString("""{"a":1}""")
    
    val js = """ {
                  "crawlDate": "20150226",
                  "domain": "0x20.be",
                  "url": "http://0x20.be/smw/index.php?title=99_Bottles_of_Beer&oldid=6214",
                  "text": "99 Bottles of Beer From Whitespace (Hackerspace Gent) Revision as of 14:43, 8 August 2012 by Hans ( Talk | contribs ) 99 Bottles of Beer Where: Loading map... Just me, with 99 bottles of beer and some friends. Subpages"
                  }"""


     val Value =  parse(js).extract[ParsedPage]
     println(Value.domain)
   	/*val spark = SparkSession
				.builder
				.master("local")
				.config("spark.sql.shuffle.partitions", "4")
				.appName("AnalyticsEngine")
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
						
						
						
						rdd_2.map (x => LocationEvent(
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
						* 
						*/
  }
  
  /*
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
String fechaStr = "2013-10-10 10:49:29.10000";  
Date fechaNueva = format.parse(fechaStr);

   * 
   */

}