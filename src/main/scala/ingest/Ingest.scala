package ingest

import scala.util.Try
import scala.io.{Codec, Source}

import java.io.{BufferedWriter, FileWriter}
import au.com.bytecode.opencsv.CSVWriter

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer

class Ingest[T: Ingestible] extends (Source => Iterator[Try[T]]) {
  def apply(source: Source): Iterator[Try[T]] = source.getLines.toSeq.drop(1).map(e => implicitly[Ingestible[T]].fromString(e)).iterator
}

trait Ingestible[X] {
  def fromString(w: String): Try[X]
}

/**
  * Closing Price:
  *
  * @param date: yyyy-mm-dd
  * @param stockSymbol
  * @param close
  */
case class ClosingPrice(date: String, stockSymbol: String, close: Double)

object ClosingPrice extends App {

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

  val ingester = new Ingest[ClosingPrice]()

  if (args.length == 2) {
    implicit val codec = Codec.UTF8
    val file = Source.fromFile(args(0))
    val symbolName = args(1)

    val priceSeqBySymbol = getSeqBySymbol(symbolName, ingester(file)).get.toSeq

//    println(priceSeqBySymbol.toList)
    saveAsCsv(priceSeqBySymbol)
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


