import com.isaac.stock.predict.StockPricePrediction
import com.isaac.stock.representation.Result
import org.apache.log4j.BasicConfigurator

object PredictLSTM {
  def main(args: Array[String]) {
    BasicConfigurator.configure()
    val result: Result = StockPricePrediction.predict("/Users/kym1992/STUDY/NEU/CSYE7200/Dataset/nyse/prices-split-adjusted.csv", "WLTW", 0.95)
    println("predicts, actual")
//    result.getPredicts.zip(result.getActuals).foreach()
    (result.getPredicts, result.getActuals).zipped.foreach((x, y) => println(x + ", " + y))
  }
}