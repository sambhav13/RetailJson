import net.liftweb.json._
import org.apache.spark.sql.functions.udf
import net.liftweb.json.DefaultFormats
import java.text.SimpleDateFormat
import java.sql.Timestamp

class JsonSchemaTest {
  
}

case class LocEvent(
		val orgId:String,
		val storeId:String,
		val customerId:String,
		val rackId:String,
		val eventType:String,
		val createdStamp:Timestamp)
		
case class OrderItems(val productId:String,val quantity:String,val categoryId:String,val amount:String)
		
case class CheckEvent(
		val customerId:String,
		val orgId:String,
		val storeid:String,
		val orderId:String,
		val eventType:String,
		val orderItems:List[OrderItems],
		val createdStamp:Timestamp)

object JsonSchemaTest{
  implicit val formats = new DefaultFormats {
		override def dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
	} 
  def main(args:Array[String]) ={
    
    
   /*val x = """{
      	"orgId":"Walmart",
      	"storeId":"LA_ST_001",
      	"customerId":"1",
      	"rackId":"RCK1",
      	"eventType":0,
      	"createdStamp":"2016-11-11 11:10:10"
      } """*/
    
 val x = """{
                  "customerId":"c1",
                  "orgId":"org1",
                  "storeid":"s1",
                  "orderId":"o1"
				          "eventType":"3",
                  "orderItems":[
                            {"productId":"p1","categoryId":"c1","quantity":2,"amount":10},
                            {"productId":"p2","categoryId":"c1","quantity":1,"amount":12.4},
                            {"productId":"p3","categoryId":"c1","quantity":3,"amount":20}
                         ],
				  "createdStamp":"2016-11-11 11:10:10"
 
                }
          """

			val json = parse(x)
			val eventType = (json \ "eventType").extract[String]
			println(eventType)
			
			
			if(eventType =="0"){
			val obj =    json.extract[LocEvent]
			println(obj.orgId)
			  
			}
    
    if(eventType =="3"){
			val obj =    json.extract[CheckEvent]
			println(obj.orderItems.foreach { println })
			  
			}
    
  }
}