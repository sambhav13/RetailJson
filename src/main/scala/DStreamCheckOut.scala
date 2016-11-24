import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.SQLContext
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.Seconds
import org.apache.spark.sql.SaveMode
import org.apache.spark.storage.StorageLevel
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.Time


import java.text.SimpleDateFormat
import java.sql.Timestamp
import net.liftweb.json._
import org.apache.spark.sql.Dataset
import java.util.Date



import org.apache.spark.sql.functions.udf
import org.apache.spark.sql.types.DataType
import java.util.Calendar


object DStreamCheckOut { 

	implicit val formats = new DefaultFormats {
		override def dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
	} 
case class CheckOutEvent(val userId:String,
		val orgId:String,
		val storeId:String,
		val cart:List[Cart],
		val checkOutTime:Timestamp  
		)                          
case class Cart(val productId:String,val quantity:String,val price:String)

case class Event1(val eventType:String,val Event:CheckOutEvent)
case class Event2(val eventType:String,val Event:LocationEvent)

case class LocationEvent(
		val userId:String,
		val orgId:String,
		val storeId:String,
		val rackId:String,
		val time:Timestamp)


case class Event(data:String)

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

	val day = t1.getDate();
	return day
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

def timeWeek(t1:Timestamp):Int ={

	val c = Calendar.getInstance();
	c.setTimeInMillis(t1.getTime())
	c.get(Calendar.WEEK_OF_MONTH);
}


def timeYear(t1:Timestamp):Int = {

	val year = t1.getYear();        
	return year
}



def main(args:Array[String])={


	val sparkConf = new SparkConf().setAppName("DStreamAgg")
			.setMaster("local[2]")
			.set("spark.sql.shuffle.partitions","1")
			//.setMaster("spark://ip-172-31-21-112.ec2.internal:7077")

			val ssc = new StreamingContext(sparkConf, Seconds(3))

	val sqlContext = new SQLContext(ssc.sparkContext)
	val lines = ssc.socketTextStream("localhost", 9999, StorageLevel.MEMORY_AND_DISK_SER)


 //DB connection
	val url = "jdbc:mysql://localhost:3306/test"
	val table = "people";
	import java.util.Properties
	val prop = new Properties() 
	prop.put("user", "root")
	prop.put("password", "")
	prop.put("driver", "com.mysql.jdbc.Driver")

	val dailyStoreData = sqlContext.read.format("jdbc").option("url", "jdbc:mysql://localhost:3306/test")
	.option("driver", "com.mysql.jdbc.Driver")
	.option("dbtable", "DailyStoreData")
	.option("user", "root")
	.option("password", "")
	.load()
	
	
	



	// val rdd =   lines.as[String]


	lines.foreachRDD( (rdd: RDD[String], time: Time) => {
		import sqlContext.implicits._

		val LocationEventData =  rdd.filter(x => {

			val json = parse(x)
					val eventType = getEventType(json)
					eventType=="LocationEvent"

		})

		val CheckOutEventData =  rdd.filter(x => {

			val json = parse(x)
					val eventType = getEventType(json)
					eventType=="CheckOutEvent"

		})

		val LocationDF =   LocationEventData.map ( x =>  {

			val json = parse(x)
					val event = (json.extract[Event2])
					println(event.Event.rackId)

					event.Event

		}).toDF()

		//LocationDF.show()


		val CheckOutDF =   CheckOutEventData.map ( x =>  {

			val json = parse(x)
					val event = (json.extract[Event1])
					println(event.Event.userId)
					val ev = event.Event
					CheckOutEvent(ev.userId,ev.orgId,ev.storeId,ev.cart,ev.checkOutTime)

		}).toDF()

		//CheckOutDF.show()
		//println(CheckOutDF.schema)

		//UDF registration 

		val dayUDF = udf(timeDay _ )		
		val weekUDF = udf(timeWeek _)
		val monthUDF = udf(timeMonth _ )
		val quarterUDF = udf(timeQuarter _ )
		val yearUDF = udf(timeYear _ )


		//*****Daily Store FootFall******//
		////////

		val d = new Date();
		val currentDay = d.getDate
				val func: (String => Boolean) = (arg: String) =>{

					val d = new Date()
					val currentDay = d.getDate
					println("input date day "+arg)
					currentDay == arg.toInt
				}

				val checkCurrentDayfunc = udf(func)


						////////



						//*****Monthly and Weekly Store FootFall******//
						////////

						val daySegregation_store  = LocationDF.withColumn("day", dayUDF(LocationDF.col("time")))
						.select("userId","orgId","storeId","day")
						//daySegregation_store.show() //to Test
						/*val weekSegregation_store  = LocationDF.withColumn("week", weekUDF(LocationDF.col("time")))
						                                      .select("userId","orgId","storeId","week")
						val monthlySegregation_store = LocationDF.withColumn("month", monthUDF(LocationDF.col("time")))
						                                      .select("userId","orgId","storeId","month")
						val quarterSegregation_store = LocationDF.withColumn("quarter", quarterUDF(LocationDF.col("time")))
						                                      .select("userId","orgId","storeId","quarter")*/



						/*weekSegregation_store.show()
            monthlySegregation_store.show()
            quarterSegregation_store.show()*/

						import org.apache.spark.sql.functions._

						//daySegregation_store.groupBy("orgId", "storeId").agg(count("day").alias("dayCount")).show()
						//weekSegregation_store.groupBy("orgId", "storeId").agg(count("week").alias("weekCount")).show()
						//monthlySegregation_store.groupBy("orgId", "storeId").agg(count("month").alias("monthCount")).show()
						//quarterSegregation_store.groupBy("orgId", "storeId").agg(count("quarter").alias("quarterCount")).show()




						val dailyFootFall_store = daySegregation_store.filter(checkCurrentDayfunc(daySegregation_store.col("day")))
						                                              .select("userId","orgId","storeId","day")
						                                              
			       //dailyFootFall_store.show()
						// dailyFootFall_store.show()
						//dailyFootFall_store.groupBy("userId","orgId","storeId").agg(count("day").alias("currentDayCount")).show()
						dailyFootFall_store.groupBy("orgId","storeId").agg(count("userId").alias("currentDayCount"))
						//.show()
						
						
						
						
						
						////////

						/*

					//*****Monthly and Weekly Rack level FootFall******//
						////////

						val daySegregation_rack  = LocationDF.withColumn("day", dayUDF(LocationDF.col("time")))
						val weekSegregation_rack  = LocationDF.withColumn("week", weekUDF(LocationDF.col("time")))
						val monthlySegregation_rack = LocationDF.withColumn("month", monthUDF(LocationDF.col("time")))
						val quarterSegregation_rack = LocationDF.withColumn("quarter", quarterUDF(LocationDF.col("time")))

						/*weekSegregation_rack.show()
            monthlySegregation_rack.show()
            quarterSegregation_rack.show()*/

            daySegregation_rack.groupBy("orgId", "storeId","rackId").agg(count("day").alias("dayCount")).show()
            weekSegregation_rack.groupBy("orgId", "storeId","rackId").agg(count("week").alias("weekCount")).show()
            monthlySegregation_rack.groupBy("orgId", "storeId","rackId").agg(count("month").alias("monthCount")).show()
            quarterSegregation_rack.groupBy("orgId", "storeId","rackId").agg(count("quarter").alias("quarterCount")).show()



            val dailyFootFall_rack = daySegregation_rack.filter(checkCurrentDayfunc(daySegregation_rack.col("day"))).select("userId","orgId","storeId","time")
						dailyFootFall_rack.groupBy("userId","orgId","storeId").count().show()
						 */
						////////


					 //TOP 3 Category by FootFall
						val categoryFootFall_Dynamic  = LocationDF.withColumn("day", dayUDF(LocationDF.col("time")))
						                                       .select("day","orgId","storeId","rackId")
						                                       .withColumnRenamed("rackId", "categoryId")
						
						val categoryFootFall_Static = sqlContext.read.format("jdbc").option("url", "jdbc:mysql://localhost:3306/test")
                                          	.option("driver", "com.mysql.jdbc.Driver")
                                          	.option("dbtable", "DailyCategoryFootFallData")
                                          	.option("user", "root")
                                          	.option("password", "")
                                          	.load()	
						
						
						
						val mergedCategoryFootFall = categoryFootFall_Dynamic.union(categoryFootFall_Static)
						
						categoryFootFall_Dynamic.write.mode(SaveMode.Append).jdbc(url,"DailyCategoryFootFallData",prop)
            mergedCategoryFootFall.show()
            
            
            
            val categoryFootFallTopDynamic  = mergedCategoryFootFall.groupBy("day","categoryId").agg(count("day").alias("currentDayCategoryCount"))
						
						val categoryFootFallTopStatic = sqlContext.read.format("jdbc").option("url", "jdbc:mysql://localhost:3306/test")
                                          	.option("driver", "com.mysql.jdbc.Driver")
                                          	.option("dbtable", "DailyCategoryFootFallCount")
                                          	.option("user", "root")
                                          	.option("password", "")
                                          	.load()
                                          	
                                          	
            val mergedCategoryFootFallCount  = categoryFootFallTopDynamic.union(categoryFootFallTopStatic)
            
            //DailyProductCount
						mergedCategoryFootFallCount.write.mode(SaveMode.Overwrite).jdbc(url,"DailyCategoryFootFallCount",prop)
						mergedCategoryFootFallCount.show()

						/********Sales************/
						/////////////////



						//*****Monthly and Weekly Store CheckOut******//
						////////
						
						
						//Top 3 Category by Sales
						val cart = CheckOutDF.select("cart")
						//val resDf = CheckOutDF.withColumn("Products", when(CheckOutDF("cart").isNotNull, explode(CheckOutDF("cart"))))
						
						
						val resDf = CheckOutDF.withColumn("Products", explode(CheckOutDF("cart")))
						                      .select("userId","orgId","storeId","checkOutTime","Products.productId","Products.quantity")
						
						/*val checkOutExplodedDF = resDf.withColumn("day", dayUDF(CheckOutDF.col("checkOutTime")))
						                                           .select("userId","orgId","storeId","day",
						                                               "productId","quantity")*/
						                      
					
						                                               
            val productDataDynamic = resDf.withColumn("day", dayUDF(CheckOutDF.col("checkOutTime")))
						                                           .select("day","productId","quantity")
						
						val productDataStatic = sqlContext.read.format("jdbc").option("url", "jdbc:mysql://localhost:3306/test")
                                          	.option("driver", "com.mysql.jdbc.Driver")
                                          	.option("dbtable", "DailyProductData")
                                          	.option("user", "root")
                                          	.option("password", "")
                                          	.load()		   	
                                          	
           val mergedProductData =  productDataDynamic.union(productDataStatic)
           
           productDataDynamic.write.mode(SaveMode.Append).jdbc(url,"DailyProductData",prop)
          // mergedProductData.show()
           
						
						val productTopDynamic  = mergedProductData.groupBy("day","productId").agg(count("quantity").alias("currentDayProductCount"))
						
						val productTopStatic = sqlContext.read.format("jdbc").option("url", "jdbc:mysql://localhost:3306/test")
                                          	.option("driver", "com.mysql.jdbc.Driver")
                                          	.option("dbtable", "DailyProductCount")
                                          	.option("user", "root")
                                          	.option("password", "")
                                          	.load()
                                          	
                                          	
            val mergedProductCount  = productTopDynamic.union(productTopStatic)
            
            //DailyProductCount
						mergedProductCount.write.mode(SaveMode.Overwrite).jdbc(url,"DailyProductCount",prop)
					//	mergedProductCount.show()
						
						 
						
						/*val daySegregation_store_sale  = CheckOutDF.withColumn("day", dayUDF(CheckOutDF.col("checkOutTime")))
						                                           .select("userId","orgId","storeId","day")
						                                           .withColumnRenamed("rackId", "categoryId")*/
					 //daySegregation_store_sale.show()
					/*	val weekSegregation_store_sale  = CheckOutDF.withColumn("week", weekUDF(CheckOutDF.col("checkOutTime")))
						                                            .select("userId","orgId","storeId","rackId","week")
						                                            .withColumnRenamed("rackId", "categoryId")
						val monthlySegregation_store_sale = CheckOutDF.withColumn("month", monthUDF(CheckOutDF.col("checkOutTime")))
						                                            .select("userId","orgId","storeId","rackId","month")
						                                            .withColumnRenamed("rackId", "categoryId")
						val quarterSegregation_store_sale = CheckOutDF.withColumn("quarter", quarterUDF(CheckOutDF.col("checkOutTime")))
						                                              .select("userId","orgId","storeId","rackId","quarter")
						                                              .withColumnRenamed("rackId", "categoryId")*/
						/*weekSegregation_store.show()
            monthlySegregation_store.show()
            quarterSegregation_store.show()*/

						import org.apache.spark.sql.functions._

						/*daySegregation_store_sale.groupBy("orgId", "storeId").agg(count("day").alias("dayCount")).show()
            weekSegregation_store_sale.groupBy("orgId", "storeId").agg(count("week").alias("weekCount")).show()
            monthlySegregation_store_sale.groupBy("orgId", "storeId").agg(count("month").alias("monthCount")).show()
            quarterSegregation_store_sale.groupBy("orgId", "storeId").agg(count("quarter").alias("quarterCount")).show()

*/


           // val dailyFootFall_store_sale = daySegregation_store_sale.filter(checkCurrentDayfunc(daySegregation_store_sale.col("day")))
                                     //                               .select("userId","orgId","storeId","categoryId","day")
             //  dailyFootFall_store_sale.show()
            //dailyFootFall_store_sale.groupBy("orgId","storeId","categoryId").agg(count("userId").alias("currentDayCount"))
            //.show()
						//dailyFootFall_store_sale.groupBy("userId","orgId","storeId").agg(count("day").alias("currentDayCount")).show()

						println("!!!!!!!!!!! Batch completed  !!!!!!!!!!!!")
						////////






						//
						///////////////////////////




						//          .select("")

						// dailyFootFall
						//LocationDF.groupBy("orgId", "storeId")




						////////

						// println(CheckOutDF.schema)

						/*val EventData = rdd.map(  x =>{

           println(x)
           val json = parse(x)
              val eventType = getEventType(json)

                eventType match {
								case "LocationEvent" => 
		  													val event = (json.extract[Event2])
																println(event.Event.rackId)




          			case "CheckOutEvent" =>

									              val storEve = json.extract[Event1] 
											          val cartEvent  = storEve.Event.cart
          											cartEvent.foreach { x =>  println("price"+x.price+","+"productId"+","+x.productId)}

        				}

             //x.foreach { println } 
             Event(x)


         }).toDF()

         EventData.show()

						 */
	});

	/*val parsedData = lines.map( x => {

              val json = parse(x)
              val eventType = getEventType(json)
              eventType match {
								case "LocationEvent" => 
		  													val event = (json.extract[Event2])
																println(event.Event.rackId)
          			case "CheckOutEvent" =>

									              val storEve = json.extract[Event1] 
											          val cartEvent  = storEve.Event.cart
          											cartEvent.foreach { x =>  println("price"+x.price+","+"productId"+x.productId)}

        				}

             x.foreach { println } 
             Event(x)


      }*/






	/*   */




	ssc.start()
	ssc.awaitTermination()

}




def getEventType(json: JValue):String ={

	implicit val formats = new DefaultFormats {
		override def dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
	}
	val childs = (json \ "eventType")
			val eventType = childs.extract[String]

					println(eventType)
					eventType

}
}