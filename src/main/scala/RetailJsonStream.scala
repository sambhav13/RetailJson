import org.apache.spark.sql.SparkSession
import net.liftweb.json._
import java.text.SimpleDateFormat
import java.sql.Timestamp
import org.apache.spark.sql.SQLImplicits
 class RetailJsonStream {

  
}


case class LocationEvent(
		val userId:String,
		val orgId:String,
		val storeId:String,
		val rackId:String,
		val time:Timestamp)



case class CheckOutEvent(val userId:String,
		val orgId:String,
		val storeId:String,
		val cart:List[Cart],
		val checkOutTime:Timestamp  
		)                          
case class Cart(val productId:String,val quantity:String,val price:String)

case class Event1(val eventType:String,val Event:CheckOutEvent)
case class Event2(val eventType:String,val Event:LocationEvent)
case class CheckObj(val orgId:String,val storeId:String,val userId:String)
object RetailJsonStream{
 implicit val formats = new DefaultFormats {
		override def dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
	} 

	def main(args:Array[String])={


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
//rdd.map(x => x.)
				//val parsedRdd  =   parse(rdd)
		val locationRDD = rdd.filter(x => {
		  
		    val pd = parseJs(x)
		    val eType = pd.extract[String]
		    (eType.equals("LocationEvent"))
		  }
		)
		
		val checkOutRDD = rdd.filter(x => {
		  
		    val pd = parseJs(x)
		    val eType = pd.extract[String]
		    (eType.equals("CheckOutEvent"))
		  }
		)
		
		import spark.implicits._
		val locData = locationRDD.map(x =>  { val data = parseLocEvent(x)	;data.Event})
		
		//locData.map { x => x. }
		
		val table = locData.createOrReplaceTempView("LocationEvent")
						//spark.sqlContext.cacheTable("person")
		val last = spark.sql("select userId,orgId,count(rackId) from LocationEvent group by userId,orgId")
		
		
		
		val checkOutData = checkOutRDD.map(x => { 
		  val data = parseCheckOutEvent(x) 
		  val eve = data.Event;
		  CheckObj(eve.orgId, eve.storeId, eve.userId)
		 }
		)
		
	checkOutData.createOrReplaceTempView("CheckOutEvent")
	val last2 = spark.sql("select userId,orgId,count(storeId) from CheckOutEvent group by userId,orgId")
		
		/*import org.apache.spark.sql.Row
    checkOutData.explode($"orgId", $"cart") {case row: Row =>
    val id = row(0).asInstanceOf[String]
    val words = row(1).asInstanceOf[String].split(" ")
    words.map(word => (id, word))
    }*/
		
		// This may be replaced by explodeArray() someday val explodedDepartmentWithEmployeesDF = departmentWithEmployeesDF.explode(departmentWithEmployeesDF("employees")) { case Row(employee: Seq[Row]) => employee.map(employee => Employee(employee(0).asInstanceOf[String], employee(1).asInstanceOf[String], employee(2).asInstanceOf[String]) ) }
		
		//checkOutData.createOrReplaceTempView("CheckOutEvent")
						//spark.sqlContext.cacheTable("person")
		//val last2 = spark.sql("select userId,orgId,count(rackId) from CheckOutEvent group by userId,orgId")
		
/*	val df = locData.as[LocationEvent]
		
		//val orgID = df.select($"orgId")
		//val deviceEventsDS = ds.select($"device_name", $"cca3", $"c02_level").where($"c02_level" > 1300)

		//
		
		
		locData.foreach { x => println(x.Event.storeId)}
					
		//checkOutData.foreach { x => println(x.Event.time) }
		
		val table = locData.createOrReplaceTempView("Event2")
		//spark.sqlContext.cacheTable("person")

		val last = spark.sql("select id,max(age) from person group by id")*/
						
		val query = last.writeStream
						.outputMode("complete")
						.format("console")
						.start()
						
						/*val query2 = last2.writeStream
						.outputMode("complete")
						.format("console")
						.start()*/
						
						last2.writeStream.outputMode("complete").format("csv")
						
		query.awaitTermination()
		//query2.awaitTermination()
		
	}

	def parseJs(eventMsg:String):JValue={
		
	  
	      val json = parse(eventMsg)

				val childs = (json \\ "eventType")
				return childs
	}
	
	
	def parseLocEvent(msg:String):Event2={
	      val storeEvent = parse(msg)
				val event2 = (storeEvent.extract[Event2])
	  	  return event2
	}
	
	def parseCheckOutEvent(msg:String):Event1={
	      val storeEvent = parse(msg)
				val event1 = (storeEvent.extract[Event1])
	  	  return event1
	}
	
	//def parseEvent(childs:JsonAST.JValue) ={
	 /* def parseEvent(msg:String) ={
	  
	      val childs = parse(msg)
				val eventType = childs.extract[String]

						println(eventType)
						eventType match {
						case "LocationEvent" => 


						val event = (msg.extract[Event2])

						println(event.Event.rackId)
						 
						return event;
						

						case "CheckOutEvent" =>


						val json = parse(eventMsg)

						val storEve =   json.extract[Event1] 

								val cartEvent  = storEve.Event.cart

								cartEvent.foreach { x =>  println("price"+x.price+","+"productId"+x.productId)}



		}
		
	        return null;

						//////////////////



		}  */
	}
