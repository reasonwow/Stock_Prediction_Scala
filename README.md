# Stock_Prediction_Scala

[![CircleCI](https://circleci.com/gh/reasonwow/Stock_Prediction_Scala.svg?style=svg)](https://circleci.com/gh/reasonwow/Stock_Prediction_Scala)

This is the CSYE7200 FinalProject for Team9 Fall2017

Team member:

Bowei Wang - wang.bowe@husky.neu.edu

Qichu Zhao - zhao.qic@husky.neu.edu

You Li - li.you1@husky.neu.edu

# Stock Price Prediction based on Hitorical Data

# Abstract

The goal of this project is to predict stock price movement based on historical data points and visualize the difference between the prediction and the actual stock prices. If time permits, we would also like to apply this algorithm to the real-time stock quotes. 

# Methodology

Spark Timeseries:

LSTM:
1. LSTMs is one kind of Recurrent Neural Networks
2. Data for RNNs are time series
3. Input data has shape [numExamples,inputSize,timeSeriesLength]
4. Output data has shape [numExamples,outputSize,timeSeriesLength]
5. Implements the standard DataSetIterator from Deeplearning4J
6. Input and target INDArrays from N-Dimensional Arrays for Java
7. GravesLSTM -> GraveLSTM -> DenseLayer -> RNNOutputLayer


# Input and Output

The arguments that can be passed while running the jar are:

Spark Timeseries:
1. Source File Path
2. Output File Path
3. Symbol Name
4. Number of dates to predict

eg: /Users/{username}/{SourceFileDir}/prices-split-adjusted.csv src/main/resources/preData_AAPL_TS.csv AAPL 5

Output:
Array of Symbol Name and predict results rendered in terminal.

LSTM:
1. Source File Path
2. Symbol Name to predict

eg: /Users/{username}/{SourceFileDir}/prices-split-adjusted.csv AAPL

Output: 
Saved as csv file in src/main/resources/{symbolName}.csv
