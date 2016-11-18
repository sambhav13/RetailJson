import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession
import java.sql.Timestamp
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Encoders
import org.apache.spark.sql.Encoder



           
class TimestampCheck {
  
}
/*object UserEncoders {
  implicit def barEncoder: org.apache.spark.sql.Encoder[UserTransaction] = 
  org.apache.spark.sql.Encoders.kryo[UserTransaction]
}*/

object TimestampCheck{
   
  
  case class UserTransaction(val userid:String,val rackid:String,storeid:String,val beaconid:String,
           eventid:String,productid:String,time:Timestamp)
           
   case class Users(val userid:String,val beaconid:String,time:Timestamp)
   def main(args:Array[String]){
     
     val conf = new SparkConf().setMaster("TimestampCheck").setMaster("local[2]")
     
    /* val sc = new SparkContext(conf)*/
     
     
     val spark = SparkSession
                      .builder                    
                      .appName("TimestampCheck")
                      .config(conf)
                      //.master("local")
                      .getOrCreate()
     
     val df = spark.read.option("header",true).option("inferSchema",true).csv("UserData.txt")
     
     df.printSchema()
     
     df.show()
     
     import spark.implicits._
     import org.apache.spark.sql.functions.year
     //df.as[UserTransaction,Encoders
     
     val ds:Dataset[UserTransaction] = df.as[UserTransaction]
     
     val sqlContext = spark.sqlContext
    //import sqlContext.implicits._
    //import UserEncoders._
    
     //val encoder = Encoders.bean(UserTransaction.class);
   //  val ds_fromEncoder = spark.sqlContext.createDataset(df)
     //Encoders.bean(MyClass.class);
     
     
     val df2 = df.withColumn("time",$"time".cast("timestamp"))
     //df.map(x=> UserTransaction(x(0),x(1),x(2),x(3),x(4),x(5),x(6).t)
     
    // df.as
     ds.printSchema()
    
    df2.printSchema() 
    
    df2.show()
    //tuple match { case (name, age) => Person(name, age) }
    val newDs = ds.map(x => (x.userid,x.beaconid,x.time))
    val userDS = newDs.map(x => Users(x._1,x._2,x._3))
    //.asInstanceOf[Users]
    userDS.show()
    
    val table = userDS.createOrReplaceTempView("users")
    sqlContext.cacheTable("users")
    
     sqlContext.sql("select userid,max(time) as max,min(time) as min,datediff(max(time),min(time)) as diff from users group by userid ").show();
     
     sqlContext.sql("select userid,max(time) as max,min(time) as min,( cast(max(time) as long) - cast(min(time) as long) ) as difference from users group by userid ").show();
    //sqlContext.sql("select userid,max(time) as max,min(time) as min,(max(time).cast(long) - min(time).cast(long)) as diff from users group by userid ").show();
   
}
  

}