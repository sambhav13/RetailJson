import org.apache.spark.sql.SparkSession
import org.apache.avro.ipc.specific.Person
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.Row
import org.apache.spark.sql.types.StringType

import org.apache.spark.sql.types._
//https://www.generalmills.com/en/News/Issues/food-allergy-statement

object SparkJDBC {
	def main(args:Array[String]) = {

		val spark = SparkSession
				.builder
				.config("spark.sql.warehouse.dir","/home/sambhav/Downloads/spark-2.0.0-bin-hadoop2.7/sbin/spark-warehouse" )
				.appName("SparkJDBC")
				.master("spark://sambhav-virtual-machine:7077")
				.getOrCreate()


				//import spark.implicits._

				val sc = spark.sparkContext
				//val rdd = sc.parallelize(Seq(1,2,3,4,5))

				//val peopleRDD  = spark.read.format("csv").load("data.txt")
				
				val peopleRDD  = sc.textFile("data.txt")

				//rdd.createOrReplaceTempView("person")

			 
				// val rdd_person = rdd.as[Person]

				//val df =  rdd_person.toDF()

				//df.
				// rdd_person.createOrReplaceTempView("person")

				// The schema is encoded in a string
				val schemaString = "name age"

				// Generate the schema based on the string of schema
				val fields = schemaString.split(" ")
				.map(fieldName => StructField(fieldName, StringType, nullable = true))
				val schema = StructType(fields)

				// Convert records of the RDD (people) to Rows
				val rowRDD = peopleRDD
				.map(_.split(","))
				.map(attributes => Row(attributes(0), attributes(1).trim))

				// Apply the schema to the RDD
				val peopleDF = spark.createDataFrame(rowRDD, schema)

				// Creates a temporary view using the DataFrame
				//peopleDF.createOrReplaceTempView("people")
				
				
				peopleDF.write.saveAsTable("people")
				
				
					println("created temp table")
					
					val person = spark.sql("select * from person")
					person.show()

				// SQL can be run over a temporary view created using DataFrames
				val results = spark.sql("SELECT name FROM people")

				results.show()
				// The results of SQL queries are DataFrames and support all the normal RDD operations
				// The columns of a row in the result can be accessed by field index or by field name
				//results.map(attributes => "Name: " + attributes(0)).show()
				// +-------------+
				Thread.sleep(12000)

	}
case class Person(val name:String,val age:Int)
}