import java.io.InputStream
import java.util.Properties
import java.io.FileInputStream
import java.io.File
import java.util.Map.Entry


class Prop {
  
  def getFilePath():InputStream= {
//   var input =  new FileInputStream("application.conf");
    
    val stream : InputStream = getClass.getResourceAsStream("/application.conf")
val lines = scala.io.Source.fromInputStream( stream ).getLines
 //println(lines)
   //val filePath =  getClass().getClassLoader().getResource("/application.conf")
   //println(filePath)
   //.getFile()
   //print
   //filePath.toString()
   
   stream
  }
  
}

object Prop{
  
  def main(args:Array[String]) ={
    
    val prop = new Properties()
	  
    val p = new Prop()
    //val filePath  = p.getFilePath()
  //val file = new File(filePath);
   
    //println(file.exists())
   /*try (Scanner scanner = new Scanner(file)) {

		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			result.append(line).append("\n");
		}

		scanner.close();

	} catch (IOException e) {
		e.printStackTrace();
	}*/
		// load a properties file
		prop.load(p.getFilePath())

		
		val set = prop.entrySet()
		val block : Entry[Object,Object] => Unit = x => { println(x) }
		
		//val out = in.asScala

		val iter = set.iterator()
		
		while (iter.hasNext()) {
    System.out.println(iter.next());
}
			// get the property value and print it out
		println(prop.getProperty("host"))
		///System.out.println(prop.getProperty("dbuser"));
		//System.out.println(prop.getProperty("dbpassword"));

  }
}