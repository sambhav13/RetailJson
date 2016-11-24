
import java.sql.Timestamp

import net.liftweb.json._


import java.text.SimpleDateFormat
import java.util.Date

class LiftJsonParse {

}

object LiftJsonParse{


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


def main(args:Array[String]) = {

	implicit val formats = new DefaultFormats {
		override def dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
	} 
	/*val storeEvent = 
			"""{
			"eventType":"LocationEvent",
			"Event":{
			"userId":"u1",
			"orgId":"org1",
			"storeId":"s1",
			"rackId":"r1",
			"time":"2016-11-11 11:10:10"	
			}
			}"""*/

	val dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	val date = new Date();
	val currentDate = dateFormat.format(date)
	println(date.getDate)
	System.out.println(dateFormat.format(date)); 

	
	val storeEvent = 
	"""{
	"eventType":"CheckOutEvent",
	"Event":{
	"userId":"u1",
	"orgId":"org1",
	"storeId":"s1",
	"cart":[
	{"productId":"p1","quantity":"2","price":"10"},
	{"productId":"p2","quantity":"20","price":"20"}
	],

	"checkOutTime":"2016-11-11 11:10:10"	
	}
	}  """

	/*val interimMsg = scala.util.parsing.json.JSON.parseFull(storeEvent).get.asInstanceOf[Map[String,String]]

		  val eventType = interimMsg.get("eventType").getOrElse("nothing")

			val event = interimMsg.get("Event").get.asInstanceOf[Map[String,String]]*/

	val json = parse(storeEvent)

	val child  = json.children

	//child.foreach { x => println(x.toString()) }
	val childs = (json \ "eventType")
	//println(childs.extract[String])
	//println(childs.childrentoString())

	val sample = (json \\ "even")
	//println(sample.asInstanceOf[String])
	val eventType = childs.extract[String]

			println(eventType)
			eventType match {
			case "LocationEvent" => 


			val event = (json.extract[Event2])

			println(event.Event.rackId)



			// val storEve =   json.extract[Event] 

			//println(storEve.Event.orgId)

			case "CheckOutEvent" =>



			//////////

			val json = parse(storeEvent)

			val storEve =   json.extract[Event1] 

					val cartEvent  = storEve.Event.cart

					cartEvent.foreach { x =>  println("price"+x.price+","+"productId"+x.productId)}
			println(storEve.Event.checkOutTime)





			//////////////////



	}  



}
}