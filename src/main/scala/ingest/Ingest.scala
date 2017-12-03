package ingest

import scala.util.Try
import scala.io.Source

class Ingest[T: Ingestible] extends (Source => Iterator[Try[T]]) {
  def apply(source: Source): Iterator[Try[T]] = source.getLines.toSeq.drop(1).map(e => implicitly[Ingestible[T]].fromString(e)).iterator
}

trait Ingestible[X] {
  def fromString(w: String): Try[X]
}


//object ClosingPrice extends App {



//  val ingester = new Ingest[ClosingPrice]()
//
//  if (args.length == 2) {
//    implicit val codec = Codec.UTF8
//    val file = Source.fromFile(args(0))
//    val symbolName = args(1)
//
//    val priceSeqBySymbol = getSeqBySymbol(symbolName, ingester(file)).get.toSeq
//
////    println(priceSeqBySymbol.toList)
//    saveAsCsv(priceSeqBySymbol)
//  }



//}


