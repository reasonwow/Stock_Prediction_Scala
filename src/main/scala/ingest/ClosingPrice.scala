package ingest

import scala.util.Try

import java.io.{BufferedWriter, FileWriter}
import au.com.bytecode.opencsv.CSVWriter

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer


/**
  * Closing Price:
  *
  * @param date: yyyy-MM-dd
  * @param stockSymbol
  * @param close
  */
case class ClosingPrice(date: String, stockSymbol: String, close: Double)


object ClosingPrice {

  trait IngestibleClosingPrice extends Ingestible[ClosingPrice] {
    def fromString(w: String): Try[ClosingPrice] = ClosingPrice.parse(w.split(",").toSeq)
  }

  implicit object IngestibleClosingPrice extends IngestibleClosingPrice


  /**
    * Clean Data
    * @param ws
    * @return a closing price
    */
  def parse(ws: Seq[String]): Try[ClosingPrice] = {
    val date = ws(0)
    val stockSymbol = ws(1)
    val close = ws(3)
    Try(ClosingPrice(date, stockSymbol, close.toDouble))
  }

  def saveAsCsv(priceSeqBySymbol: Seq[ClosingPrice]): Unit =  {
    //https://blog.knoldus.com/2017/01/01/csv-file-writer-using-scala/
    val outputFile = new BufferedWriter(new FileWriter
    ("src/main/resources/preData_TS.csv")) //replace the path with the
    // desired path and filename with the desired filename
    val csvWriter = new CSVWriter(outputFile)
    val csvFields = Array("date", "symbol", "close")
    var listOfRecords = new ListBuffer[Array[String]]()
    listOfRecords += csvFields
    for (i <- priceSeqBySymbol) {
      listOfRecords += Array(i.date, i.stockSymbol, i.close.toString)
    }
    csvWriter.writeAll(listOfRecords.toList)
    outputFile.close()
  }

  def getSeqBySymbol(symbol: String, rawData: Iterator[Try[ClosingPrice]]): Try[Seq[ClosingPrice]] = {
    val mys = for (my <- rawData.toSeq) yield
      for (m <- my; if m.stockSymbol == symbol) yield m
    sequence(for (my <- mys; if my.isSuccess) yield my)
  }

  def sequence[X](xys: Seq[Try[X]]): Try[Seq[X]] = (Try(Seq[X]()) /: xys) {
    (xsy, xy) => for (xs <- xsy; x <- xy) yield xs :+ x
  }
}








