package LSTM

import java.io.{BufferedWriter, FileWriter}

import LSTM.predict.StockPricePredictionLSTM
import LSTM.representation.Result
import au.com.bytecode.opencsv.CSVWriter

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import org.apache.log4j.BasicConfigurator

import scala.collection.mutable.ListBuffer

object SparkLSTM {
  def main(args: Array[String])= {
    BasicConfigurator.configure()
    val result: Result = StockPricePredictionLSTM.predict("/Users/kym1992/STUDY/NEU/CSYE7200/Dataset/nyse/prices-split-adjusted.csv", "DAL", 0.90)
    println("predicts, actual")
    (result.predicts, result.actuals).zipped.foreach((x, y) => println(x + ", " + y))
    saveAsCsv(result)
  }

  def saveAsCsv(result: Result): Unit =  {
    //https://blog.knoldus.com/2017/01/01/csv-file-writer-using-scala/
    val outputFile = new BufferedWriter(new FileWriter
    ("src/main/resources/DAL.csv")) //replace the path with the
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
