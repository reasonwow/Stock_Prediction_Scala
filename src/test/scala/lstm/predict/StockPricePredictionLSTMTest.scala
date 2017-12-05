package lstm.predict

import lstm.representation.PriceCategory
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

class StockPricePredictionLSTMTest extends FlatSpec with Matchers with BeforeAndAfter {
  behavior of "Test Prepare data in Stock Price Prediction LSTM"

  it should "match the pattern as String, String, Double" in {
    val result = StockPricePredictionLSTM.prepare("testData/testDataForSparkTS.csv", "GOOG", 1.0)
    result._3 shouldBe 4.9E-324
    result._4 shouldBe 1.7976931348623157E308
    result._5 shouldBe PriceCategory.CLOSE
  }

  it should "match the size" in {
    val preData = StockPricePredictionLSTM.prepare("testData/testDataForLSTM.csv", "WLTW", 0.90)
    val testResult = StockPricePredictionLSTM.predictPriceOneAhead(preData._1, preData._2, preData._3, preData._4, preData._5)
    testResult.actuals.size shouldBe 2
    testResult.predicts.size shouldBe 2
  }
}
