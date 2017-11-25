package ingest

import spray.json._
import scala.util.Try

/**
  * Team 9
  * This class represents closing price of a stock from the "prices-split-adjusted.csv" file on Kaggle
  * Created by Qichu Zhao on Nov. 23
  */

/**
  * Closing Price:
  * Qichu, Nov23: To take fundamental factors into consideration later on,
  *               generate Company case class when cleaning FundData.
  *               Consider about one example later on: one company can have multiple stockSymbol
  *               like Google have GOOG and GOOGL
  *               Both can share same company info (may be discarded)
  *
  * @param date: yyyy-mm-dd
  * @param stockSymbol
  * @param close
  * @param company
  */
case class ClosingPrice(date: String, stockSymbol: String, close: Double, company: Option[Company])

object ClosingPriceProtocol extends DefaultJsonProtocol {
  implicit val closingPriceFormat = jsonFormat4(ClosingPrice.apply)
}

object ClosingPrice {
  import ClosingPriceProtocol._

  trait IngestibleClosingPrice extends Ingestible[ClosingPrice] {
    def fromString(w: String): Try[ClosingPrice] = ClosingPrice.parse(w.split(",").toSeq)
//    def fromString(w: String): Try[ClosingPrice] = {
//      //println("w="+w.parseJson.prettyPrint)
//
//      Try(w.parseJson.convertTo[ClosingPrice])
//    }
  }

  implicit object IngestibleClosingPrice extends IngestibleClosingPrice

  /**
    * Clean Data: choose only date, symbol and close from data sequence
    *             sort by date ?????????
    * @param ws
    * @return a closing price
    */
  def parse(ws: Seq[String]): Try[ClosingPrice] = {
    val date = ws(0)
    val stockSymbol = ws(1)
    val close = ws(3)
//    val company = Company.parse(elements(ws, ???, ???))
    // COMPANY
    import Function._
//    val fy = lift(uncurried((apply _).curried))
//    for (f <- fy(company)) yield f(date)(stockSymbol)(close)

  }

}