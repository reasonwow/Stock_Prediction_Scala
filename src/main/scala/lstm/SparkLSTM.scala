package lstm

import java.io.{BufferedWriter, FileWriter}

import lstm.predict.StockPricePredictionLSTM
import lstm.representation.Result
import au.com.bytecode.opencsv.CSVWriter

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import org.apache.log4j.BasicConfigurator

import scala.collection.mutable.ListBuffer

object SparkLSTM {
  def main(args: Array[String])= {
    BasicConfigurator.configure()
    val prepared = StockPricePredictionLSTM.prepare("/Users/kym1992/STUDY/NEU/CSYE7200/Dataset/nyse/prices-split-adjusted.csv", args(0), 0.90)
    val result = StockPricePredictionLSTM.predictPriceOneAhead(prepared._1, prepared._2, prepared._3, prepared._4, prepared._5)
    println("predicts, actual")
    (result.predicts, result.actuals).zipped.foreach((x, y) => println(x + ", " + y))
    saveAsCsv(result, args(0))
  }

  def saveAsCsv(result: Result, stockCode: String): Unit =  {
    //https://blog.knoldus.com/2017/01/01/csv-file-writer-using-scala/
    val outputFile = new BufferedWriter(new FileWriter
    ("src/main/resources/" + stockCode + ".csv")) //replace the path with the
    // desired path and filename with the desired filename
    val csvWriter = new CSVWriter(outputFile)
    val csvFields = Array("predicts", "actual")
    var listOfRecords = new ListBuffer[Array[String]]()
    listOfRecords += csvFields

    (result.predicts, result.actuals).zipped.foreach((p, a) => {
      listOfRecords += Array(p.toString, a.toString)
    })
    csvWriter.writeAll(listOfRecords.toList)
    outputFile.close()
  }
}
