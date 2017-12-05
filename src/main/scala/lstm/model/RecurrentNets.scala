package lstm.model

import org.deeplearning4j.nn.api.OptimizationAlgorithm
import org.deeplearning4j.nn.conf.layers.{DenseLayer, GravesLSTM, RnnOutputLayer}
import org.deeplearning4j.nn.conf.{BackpropType, MultiLayerConfiguration, NeuralNetConfiguration, Updater}
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.nn.weights.WeightInit
import org.deeplearning4j.optimize.listeners.ScoreIterationListener
import org.nd4j.linalg.activations.Activation
import org.nd4j.linalg.lossfunctions.LossFunctions

object RecurrentNets {
  private val learningRate = 0.05
  private val iterations = 1
  private val seed = 12345

  private val lstmLayer1Size = 256
  private val lstmLayer2Size = 256
  private val denseLayerSize = 32
  private val dropoutRatio = 0.2
  private val truncatedBPTTLength = 22

  def buildLstmNetworks(nIn: Int, nOut: Int): MultiLayerNetwork = {
    val conf = new NeuralNetConfiguration.Builder().seed(seed).iterations(iterations).learningRate(learningRate).optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT).weightInit(WeightInit.XAVIER).updater(Updater.RMSPROP).regularization(true).l2(1e-4).list.layer(0, new GravesLSTM.Builder().nIn(nIn).nOut(lstmLayer1Size).activation(Activation.TANH).gateActivationFunction(Activation.HARDSIGMOID).dropOut(dropoutRatio).build).layer(1, new GravesLSTM.Builder().nIn(lstmLayer1Size).nOut(lstmLayer2Size).activation(Activation.TANH).gateActivationFunction(Activation.HARDSIGMOID).dropOut(dropoutRatio).build).layer(2, new DenseLayer.Builder().nIn(lstmLayer2Size).nOut(denseLayerSize).activation(Activation.RELU).build).layer(3, new RnnOutputLayer.Builder().nIn(denseLayerSize).nOut(nOut).activation(Activation.IDENTITY).lossFunction(LossFunctions.LossFunction.MSE).build).backpropType(BackpropType.TruncatedBPTT).tBPTTForwardLength(truncatedBPTTLength).tBPTTBackwardLength(truncatedBPTTLength).pretrain(false).backprop(true).build
    val net = new MultiLayerNetwork(conf)
    net.init()
    net.setListeners(new ScoreIterationListener(100))
    net
  }

}
