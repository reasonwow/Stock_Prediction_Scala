package LSTM.predict

import java.io.File
import java.util.List
import javafx.util.Pair

import LSTM.model.RecurrentNets
import LSTM.representation.{PriceCategory, Result, StockDataSetIterator}
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.util.ModelSerializer
import org.nd4j.linalg.api.ndarray.INDArray
import org.slf4j.{Logger, LoggerFactory}

object StockPricePredictionLSTM {
  val exampleLength: Int = 22

  def predict(file: String, symbol: String, splitRatio: Double): Result = {
    val batchSize: Int = 64
    val epochs = 100 // training epochs

    println("Create dataSet iterator...")
    val category = PriceCategory.CLOSE
    // CLOSE: predict close price
    val iterator = new StockDataSetIterator(file, symbol, batchSize, exampleLength, splitRatio, category)
    println("Load test dataset...")
    val test = iterator.getTestDataSet

    println("Build lstm networks...")
    var net = RecurrentNets.buildLstmNetworks(iterator.inputColumns, iterator.totalOutcomes)

    println("Training...")
    (0 to epochs).foreach(i => {
      while (iterator.hasNext) net.fit(iterator.next) // fit model using mini-batch data
      iterator.reset() // reset iterator
      net.rnnClearPreviousState() // clear previous state
    })

    println("Saving model...")
    val locationToSave = new File("src/main/resources/StockPriceLSTM_".concat(String.valueOf(category)).concat(".zip"))
    // saveUpdater: i.e., the state for Momentum, RMSProp, Adagrad etc. Save this to train your network more in the future
    ModelSerializer.writeModel(net, locationToSave, true)

    println("Load model...")
    net = ModelSerializer.restoreMultiLayerNetwork(locationToSave)

    println("Testing...")

    val max: Double = iterator.getMaxNum(category)
    val min: Double = iterator.getMinNum(category)
    predictPriceOneAhead(net, test, max, min, category)
  }

  /** Predict one feature of a stock one-day ahead */
  private def predictPriceOneAhead(net: MultiLayerNetwork, testData: List[Pair[INDArray, INDArray]], max: Double, min: Double, category: PriceCategory) = {
    val predicts = new Array[Double](testData.size)
    val actuals = new Array[Double](testData.size)
    var i = 0
    while (i < testData.size) {
      predicts(i) = net.rnnTimeStep(testData.get(i).getKey).getDouble(exampleLength - 1) * (max - min) + min
      actuals(i) = testData.get(i).getValue.getDouble(0)

      {
        i += 1; i - 1
      }
    }
    new Result(predicts, actuals)
  }

}
