import javax.jms.JMSException
import javax.jms.TextMessage
import javax.jms.Message
import javax.jms.TopicSession
import javax.naming.InitialContext
import javax.jms.TopicConnectionFactory
import javax.jms.TopicConnection
import java.util.Properties
import javax.jms.Session
import javax.jms.Topic


class ActiveListener extends javax.jms.MessageListener{
  
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
  
  private var connection: TopicConnection = conFactory.createTopicConnection("sambhav","")

  private var pubSession: TopicSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE)
  
  val chatTopic = jndi.lookup("MyTopic").asInstanceOf[Topic]
  
   private var subSession: TopicSession = connection.createTopicSession(false,
    Session.AUTO_ACKNOWLEDGE);

  val subscriber = subSession.createSubscriber(chatTopic)

  subscriber.setMessageListener(this)

  connection.start()
  
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

object ActiveListener{
  
  
  def main(args:Array[String]) = {
    
    val listener =  new ActiveListener()
    
  }
}