import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.sql.SQLContext
import org.apache.spark.SparkConf
import org.apache.spark.storage.StorageLevel
import javax.jms.TopicSession
import javax.naming.InitialContext
import javax.jms.TopicConnectionFactory
import javax.jms.TopicConnection
import javax.jms.TopicPublisher
import java.util.Properties
import javax.jms.Topic
import javax.jms.Session


class AllergenCode {

	val props = new Properties();

	//val source = Source.fromURL(getClass.getResource("/jndi.properties"))

	props.setProperty("java.naming.factory.initial","org.apache.activemq.jndi.ActiveMQInitialContextFactory")
	props.setProperty("java.naming.provider.url","tcp://SambhavPC:61616")
	props.setProperty("connectionFactoryNames","connectionFactory, queueConnectionFactory, topicConnectionFactry")
	props.setProperty("queue.testQueue","testQueue")
	props.setProperty("topic.MyTopic","example.MyTopic")
	//props.load(getClass.getResourceAsStream("/jndi.properties")) 

	val jndi = new InitialContext(props)

	val conFactory = jndi.lookup("topicConnectionFactry").asInstanceOf[TopicConnectionFactory]

			//username,password
			var connection: TopicConnection = conFactory.createTopicConnection("sambhav", "")

			var pubSession: TopicSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE)

			val chatTopic = jndi.lookup("MyTopic").asInstanceOf[Topic]

					var publisher: TopicPublisher = pubSession.createPublisher(chatTopic)


}

object AllergenCode {



case class UserCart(userId:String,productId:String,productName:String,categoryId:String);
def main(args:Array[String]) = {

	println("hello")
	val warehouseLocation = "F:\\software\\spark-2.0.0-bin-hadoop2.7\\hadoop"
	val activeMqHandle:AllergenCode = new AllergenCode()

	val sparkConf = new SparkConf().setAppName("DStreamAgg")
	.setMaster("local[2]")
	.set("spark.sql.shuffle.partitions","1")
	.set("spark.sql.warehouse.dir", warehouseLocation)
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

	val allerGenData = sqlContext.read.format("jdbc").option("url", "jdbc:mysql://localhost:3306/test")
	.option("driver", "com.mysql.jdbc.Driver")
	.option("dbtable", "Allergen")
	.option("user", "root")
	.option("password", "")
	.load()

	import sqlContext.implicits._
	//val stringInput  = lines.asInstanceOf[String]
	lines.foreachRDD(x => {

		val words =  x.map { x => x.split(",")}
		val userCart =   words.map({ obj =>  


		UserCart(obj(0),obj(1),obj(2),obj(3))  
		}).toDF();

		// userCart.show()  


		val userProfile =  userCart.select("productId","productName","categoryId")
				val userProf =    userProfile.alias("userProf")
				val joinedProfile = userProf.join(allerGenData,userProfile("productId")===allerGenData("productId"),"inner")
				.select("userProf.*")


				//println(joinedProfile.count())
				// joinedProfile.select(userProfile("productId", cols)
				if(joinedProfile.count()>0)
				{
					println("!!!!!!!!!!!!!ALLERGEN!!!!!!!!!!!!!!!!!!")
					
					val message = activeMqHandle.pubSession.createTextMessage()
          message.setText("u1,Allergen,p10")
          
					activeMqHandle.publisher.publish(message)

				}
		//joinedProfile.show()

	}
			)



			allerGenData.show()

			ssc.start()
			ssc.awaitTermination()

}
}