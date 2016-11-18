import java.sql.Timestamp

import org.json4s._
import org.json4s.native.JsonMethods._
import scala.util.parsing.json.JSON
import java.text.SimpleDateFormat

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
                      			{"productId":"p1","quantity":"2","price":"10"},
                      			{"productId":"p2","quantity":"1","price":"12.4"},
                      			{"productId":"p3","quantity":"3","price":"20"}
                      		],
                      		"checkOutTime":"2016-11-11 11:10:10"	
                      	   }
                      }  """
    
    val interimMsg = JSON.parseFull(storeEvent).get.asInstanceOf[Map[String,String]]
    
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
                              val obj = parse(str).extract[CheckOutEvent]
                              println(obj.orgId)
                              println(obj.userId)
    }
    
     /*val Value =  parse(storeEvent).extract[LocationEvent]
     println(Value.userId)*/
  }
}