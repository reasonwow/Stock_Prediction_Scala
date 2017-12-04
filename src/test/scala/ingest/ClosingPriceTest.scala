package ingest

import SparkTS.SparkTS.args
import org.scalatest.{FlatSpec, Matchers}

import scala.io.{Codec, Source}
import scala.util.Success

class ClosingPriceTest extends FlatSpec with Matchers {
  behavior of "Test Data For Closing Price"

  it should "match the pattern as String, String, Double" in {
    val x = ClosingPrice.parse(List("StringA", "StringB", "StringC", "0.22", "StringD"))
    x should matchPattern {
      case Success(ClosingPrice("StringA", "StringB", 0.22)) =>
    }
  }

  it should "match size for getting symbol" in {
    val ingester = new Ingest[ClosingPrice]()
    implicit val codec = Codec.UTF8
    val file = Source.fromFile("testData/testDataForClosingPrice.csv")
    val seq = ClosingPrice.getSeqBySymbol("WLTW", ingester(file))
    seq.get.size shouldBe 1
    file.close()
  }


  it should "work for simple file" in {
    val ingester = new Ingest[ClosingPrice]()
    implicit val codec = Codec.UTF8
    val file = Source.fromFile("testData/testDataForClosingPrice.csv")
    val seq = ClosingPrice.getSeqBySymbol("WLTW", ingester(file)).get.toSeq
    val outputPath = "testData/testResult1.csv"
    ClosingPrice.saveAsCsv(seq, outputPath)
    file.close()

    val rst = Source.fromFile(outputPath)
    rst.getLines.size shouldBe 2
  }
}
