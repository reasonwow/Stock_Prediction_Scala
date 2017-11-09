import org.apache.spark.sql.SparkSession
import yahoofinance.Stock
import yahoofinance.YahooFinance
import java.util

object SimpleApp {
  def main(args: Array[String]) {
//    val logFile = "README.md" // Should be some file on your system
//    val spark = SparkSession.builder.appName("Simple Application").config("spark.master", "local").getOrCreate()
//    val logData = spark.read.textFile(logFile).cache()
//    val numAs = logData.filter(line => line.contains("a")).count()
//    val numBs = logData.filter(line => line.contains("b")).count()
//    println(s"Lines with a: $numAs, Lines with b: $numBs")
//    spark.stop()
    val symbols = Array[String]("INTC", "BABA", "TSLA", "AIR.PA", "YHOO")
    val stocks = YahooFinance.get(symbols) // single request


    val stock = YahooFinance.get("INTC")
    stock.print()
  }
}