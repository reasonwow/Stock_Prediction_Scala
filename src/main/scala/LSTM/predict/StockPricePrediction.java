package LSTM.predict;

import LSTM.model.RecurrentNets;
import LSTM.representation.PriceCategory;
import LSTM.representation.Result;
import LSTM.representation.StockDataSetIterator;
import javafx.util.Pair;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class StockPricePrediction {

    private static final Logger log = LoggerFactory.getLogger(StockPricePrediction.class);

    private static int exampleLength = 22; // time series length, assume 22 working days per month

    public static Result predict (String file, String symbol, double splitRatio) throws IOException {

        int batchSize = 64; // mini-batch size
//        double splitRatio = 0.9; // 90% for training, 10% for testing
        int epochs = 100; // training epochs

        System.out.println("Create dataSet iterator...");
        PriceCategory category = PriceCategory.CLOSE; // CLOSE: predict close price
        StockDataSetIterator iterator = new StockDataSetIterator(file, symbol, batchSize, exampleLength, splitRatio, category);
        System.out.println("Load test dataset...");
        List<Map.Entry<INDArray, INDArray>> test = iterator.getTestDataSet();

        System.out.println("Build lstm networks...");
        MultiLayerNetwork net = RecurrentNets.buildLstmNetworks(iterator.inputColumns(), iterator.totalOutcomes());

        System.out.println("Training...");
        for (int i = 0; i < epochs; i++) {
            while (iterator.hasNext()) net.fit(iterator.next()); // fit model using mini-batch data
            iterator.reset(); // reset iterator
            net.rnnClearPreviousState(); // clear previous state
        }

        System.out.println("Saving model...");
        File locationToSave = new File("src/main/resources/StockPriceLSTM_".concat(String.valueOf(category)).concat(".zip"));
        // saveUpdater: i.e., the state for Momentum, RMSProp, Adagrad etc. Save this to train your network more in the future
        ModelSerializer.writeModel(net, locationToSave, true);

        System.out.println("Load model...");
        net = ModelSerializer.restoreMultiLayerNetwork(locationToSave);

        System.out.println("Testing...");

        double max = iterator.getMaxNum(category);
        double min = iterator.getMinNum(category);
        return predictPriceOneAhead(net, test, max, min, category);

    }

    /** Predict one feature of a stock one-day ahead */
    private static Result predictPriceOneAhead (MultiLayerNetwork net, List<Map.Entry<INDArray, INDArray>> testData, double max, double min, PriceCategory category) {
        double[] predicts = new double[testData.size()];
        double[] actuals = new double[testData.size()];
        for (int i = 0; i < testData.size(); i++) {
            predicts[i] = net.rnnTimeStep(testData.get(i).getKey()).getDouble(exampleLength - 1) * (max - min) + min;
            actuals[i] = testData.get(i).getValue().getDouble(0);
        }

        return new Result(predicts, actuals);
    }

}
