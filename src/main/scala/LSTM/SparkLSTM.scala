package LSTM

import LSTM.predict.StockPricePredictionLSTM
import LSTM.representation.Result
import org.apache.log4j.BasicConfigurator

object SparkLSTM {
  def main(args: Array[String])= {
    BasicConfigurator.configure()
    val result: Result = StockPricePredictionLSTM.predict("/Users/kym1992/STUDY/NEU/CSYE7200/Dataset/nyse/prices-split-adjusted.csv", "WLTW", 0.95)
    println("predicts, actual")
  }
}
