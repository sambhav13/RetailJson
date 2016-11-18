import java.sql.Timestamp

import org.json4s._
//import org.json4s.native.JsonMethods._
//import scala.util.parsing.json.JSON
import java.text.SimpleDateFormat

import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._

class JsonParsing {
  
}

object JsonParsing{
  
  case class LocationEvent(
                           val userId:String,
                           val orgId:String,
                           val storeId:String,
                           val rackId:String,
                           val time:Timestamp)
  
                           
  case class CheckOutEvent(val userId:String,
                           val orgId:String,
                           val storeId:String,
                           val cart:Seq[Map[String,Int]],
                           val checkOutTime:Timestamp  
                          )
                     
  case class Cart(val items:Seq[Any])
  
  
  def main(args:Array[String]) = {
    
    implicit val formats = new DefaultFormats {
    override def dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
  } 
   /* val storeEvent = 
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
    
    
    
    val storeEvent = 
                   """{
                         "eventType":"CheckOutEvent",
                         "Event":{
                      		"userId":"u1",
                      		"orgId":"org1",
                      		"storeId":"s1",
                      		"cart":[
                      		{"productId":"p1","quantity":"2","price":"10"}],
                      			
                      		"checkOutTime":"2016-11-11 11:10:10"	
                      	   }
                      }  """
    
    val interimMsg = scala.util.parsing.json.JSON.parseFull(storeEvent).get.asInstanceOf[Map[String,String]]
    
    val eventType = interimMsg.get("eventType").getOrElse("nothing")
    
    val event = interimMsg.get("Event").get.asInstanceOf[Map[String,String]]
    
    
    
   
    
   println(event)
    eventType match {
      case "LocationEvent" => 
                              val str = scala.util.parsing.json.JSONObject(event).toString()
                              val obj = parse(str).extract[LocationEvent]
                              println(obj.rackId)
                              println(obj.time)
        
      case "CheckOutEvent" =>
                              val str = scala.util.parsing.json.JSONObject(event).toString()
                             println(str)
                              val obj = parse(storeEvent)
                              println(obj \ "Event" \ "cart")
                              val cartList = (obj \ "Event" \ "cart")
                              println(cartList.toString())
                              
                              
                             val objCart =  cartList.extract[Cart]
                             println(objCart.items.length)
                              objCart.items.foreach(println(_))
                              println("hello")
                              ///JArray.unapply(cartList)
                             //val al =  cartList.as
                              
                              //.asInstanceOf[Cart]
                              //cartList.items.foreach(println(_))
                              //println(obj \ "Event" \ "cart")
                           //  val messagesIds = (obj \ "cart") \ "productId"
                              //println(messageIds.values)
                              
                              //extract[CheckOutEvent]
                              //println(obj.orgId)
                              //println(obj.userId)
    }
    
     /*val Value =  parse(storeEvent).extract[LocationEvent]
     println(Value.userId)*/
  }
}