
import javax.jms._
import javax.naming._
import org.apache.log4j.BasicConfigurator
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Properties
import java.io.FileInputStream
import java.io.File
import scala.io.Source

//import DemoPublisherSubscriberModel._



object DemoPublisherSubscriberModel{

  
  def main(args:Array[String]) = {
       
    println("hello world")
    var demo:DemoPublisherSubscriberModel= null
    try {
      if (args.length != 3) println("Please Provide the topic name,username,password!")
      demo = new DemoPublisherSubscriberModel(args(0), args(1), args(2))
      val commandLine = new java.io.BufferedReader(new InputStreamReader(System.in))
      while (true) {
        val s = commandLine.readLine()
        if (s.equalsIgnoreCase("exit")) {
          demo.connection.close()
          System.exit(0)
        }
      }
    } catch {
      case e: Exception => e.printStackTrace()
    }
  }
  
  
}


class DemoPublisherSubscriberModel(topicName: String, username: String, password: String)
    extends javax.jms.MessageListener {

  

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
  
  private var connection: TopicConnection = conFactory.createTopicConnection(username, password)

  private var pubSession: TopicSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE)

  val chatTopic = jndi.lookup(topicName).asInstanceOf[Topic]
  
  private var publisher: TopicPublisher = pubSession.createPublisher(chatTopic)
  
  private var subSession: TopicSession = connection.createTopicSession(false,
    Session.AUTO_ACKNOWLEDGE);

  val subscriber = subSession.createSubscriber(chatTopic)

  subscriber.setMessageListener(this)

  connection.start()

  val message = pubSession.createTextMessage()

  message.setText(username + ": Howdy Friends!")

  publisher.publish(message)
  
  

  def onMessage(message: Message) {
    try {
      val textMessage = message.asInstanceOf[TextMessage]
      val text = textMessage.getText
      println(text)
    } catch {
      case jmse: JMSException => jmse.printStackTrace()
    }
  }
}