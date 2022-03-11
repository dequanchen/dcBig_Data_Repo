package OS_Hardening;

import java.util.Arrays;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;
/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 5/19/2016
*/ 

public class dcSparkJavaTrial {
	//https://spark.apache.org/docs/0.9.1/java-programming-guide.html
	//http://spark.apache.org/examples.html
	//https://spark.apache.org/docs/latest/configuration.html
	//https://blog.cloudera.com/blog/2014/04/how-to-run-a-simple-apache-spark-app-in-cdh-5/

	public static void main(String[] args) {
		String hdfsFilePathAndName = "hdfs://hdpr03mn01.mayo.edu:8020/data/test/HBase/dcHBaseTestData_employee.txt";
		System.out.print("\n\n*-1-* Spark Java Input Data File - " + hdfsFilePathAndName + "\n\n");
		
		SparkConf conf = new SparkConf().setAppName("Spark Count");
		     //.setMaster("spark:/hdpr03en02.mayo.edu:10015"); //7077..18080..10015

		JavaSparkContext sc = new JavaSparkContext(conf);				
		
		JavaRDD<String> textFile  = sc.textFile(hdfsFilePathAndName);
		JavaRDD<String> words = textFile.flatMap(
		  new FlatMapFunction<String, String>() {
		    public Iterable<String> call(String s) {
		      return Arrays.asList(s.split(" "));
		    }
		  }
		);
		
		JavaPairRDD<String, Integer> pairs = words.mapToPair(
			new PairFunction<String, String, Integer>() {
			  public Tuple2<String, Integer> call(String s) { 
				  return new Tuple2<String, Integer>(s, 1); 
			  }
			}
		);
		
		JavaPairRDD<String, Integer> counts = pairs.reduceByKey(
			new Function2<Integer, Integer, Integer>() {
			  public Integer call(Integer a, Integer b) { 
				  return a + b; 
			  }
			}
		);
				
		//counts.saveAsTextFile("hdfs://...");
		
		System.out.print("\n\n*-1-* Spark counts.collect(): " + counts.collect() + "\n\n");
		

	}

}
