package SparkTS

import java.time.{ZoneId, ZonedDateTime}

import com.cloudera.sparkts.models.ARIMA
import com.cloudera.sparkts.{BusinessDayFrequency, DateTimeIndex, TimeSeriesRDD}
import org.apache.spark.mllib.linalg.DenseVector
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.to_timestamp
import org.apache.spark.sql.types.DoubleType

object SimpleApp {
  def main(args: Array[String]) {
    val spark = SparkSession.builder.appName("Simple Application").config("spark.master", "local").getOrCreate()
//    val symbols = Array[String]("INTC")
//    val stocks = YahooFinance.get(symbols) // single request
//    val stock = YahooFinance.get("INTC")
//    stock.print()

    val formattedData = spark
    .read
    .option("header", "true")
    .csv("/Users/kym1992/STUDY/NEU/CSYE7200/Dataset/nyse/prices-split-adjusted.csv")
      .drop("open", "low", "high", "volume")

    val finalDf = formattedData
      .withColumn("timestamp", to_timestamp(formattedData("date"), "yyyy-MM-dd"))
      .withColumn("price", formattedData("close").cast(DoubleType))
      .drop("date","close").sort("timestamp")
    finalDf.registerTempTable("preData")

    val minDate = finalDf.selectExpr("min(timestamp)").collect()(0).getTimestamp(0)
    val maxDate = finalDf.selectExpr("max(timestamp)").collect()(0).getTimestamp(0)
    val zone = ZoneId.systemDefault()
    val dtIndex = DateTimeIndex.uniformFromInterval(
      ZonedDateTime.of(minDate.toLocalDateTime, zone),
      ZonedDateTime.of(maxDate.toLocalDateTime, zone),
      new BusinessDayFrequency(1)
    )

    val tsRddToFill = TimeSeriesRDD.timeSeriesRDDFromObservations(dtIndex, finalDf, "timestamp", "symbol", "price")
    val tsRdd = tsRddToFill.fill("previous")

    import spark.implicits._

    val df = tsRdd.mapSeries
    {vector => {
      val newVec = new DenseVector(vector.toArray.map(x => if(x.equals(Double.NaN)) 0 else x))
      val arimaModel = ARIMA.fitModel(1, 0, 0, newVec)
      val forecasted = arimaModel.forecast(newVec, 5)
      new DenseVector(forecasted.toArray.slice(forecasted.size-(5+1), forecasted.size-1))
      }}.toDF("symbol","values")

    df.registerTempTable("data")

    df.collect.foreach(println)
  }
}