import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD



class SCDaemon {
  
}

trait Callable[V] {
  def call(): V
}

/*class Daem(sc:SparkContext) extends Callable[Int]{
  def call():Int ={
   
  }
}
*/
class Daemon(ssc:SparkContext){
  
  def runJob(f: Int => Int):RDD[Int]={
   val rdd =  ssc.parallelize(Seq(1,2,3,4,5))
   rdd.map(x => f(x))
  }
}
object SCDaemon{
  
  
  def main(args:Array[String]) ={
    
    val conf = new SparkConf().setMaster("local").setAppName("Daemon")
    val sc = new SparkContext(conf)
    
    val dd = new Daemon(sc)
    //val dt = new Daem(sc)
   // dt.call()
    val rdd = sc.parallelize(Seq(1,2,3,4,5))
    
    rdd.collect().foreach { x => println(x) }
   // sc.submitJob(rdd, processPartition, partitions, resultHandler, resultFunc)
    
    val t = new Thread(new Runnable(){
      def run(){
        print("hello world")
      }
    })
   val rdd2 = dd.runJob { x => x *2 }
    rdd2.collect().foreach { println(_) }
    sc
   /*while(true){
     
     // rdd.collect().foreach { x => println(x) }
   }*/
    println("hello")
  }
}