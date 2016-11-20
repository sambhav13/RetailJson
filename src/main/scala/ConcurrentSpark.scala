import java.util.concurrent.ExecutorService
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import java.util.concurrent.Future
import org.apache.spark.SparkConf
import java.util.concurrent.Executors

class ConcurrentSpark {
  
}

object ConcurrentSpark{
  
  def main(args:Array[String])={
    
    val conf = new SparkConf().setMaster("local").setAppName("Concurrent")
    val sc = new SparkContext(conf)
    val executorService = Executors.newFixedThreadPool(2);
    
    // Start thread 1
    /*val future1 = executorService.submit(new Callable<Long> {
        @Override
        def call():Long ={
            JavaRDD<String> file1 = sc.textFile("/path/to/test_doc1");
            return file1.count();
        }
    });
    // Start thread 2
    val future2 = executorService.submit(new Callable<Long>() {
        @Override
        public Long call() throws Exception {
            JavaRDD<String> file2 = sc.textFile("/path/to/test_doc2");
            return file2.count();
        }
    });
    // Wait thread 1
    System.out.println("File1:"+future1.get());
    // Wait thread 2
    System.out.println("File2:"+future2.get());*/
  }
}