import org.scalatest.FunSuite

import com.holdenkarau.spark.testing.SharedSparkContext

class TestSpark extends FunSuite with SharedSparkContext {
  private val wordCount = new WordCount
  
  test("get worrd count rdd") {
    val result = wordcount.get("C:\\Users\\sambhav\\Desktop\\PythonClass\\ScalaDStreams\\RetailJson\\file.txt",sc)
    assert(result.take(10).length==10)
  }
}