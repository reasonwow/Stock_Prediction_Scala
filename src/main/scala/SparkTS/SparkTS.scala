package SparkTS

import java.time.{ZoneId, ZonedDateTime}

import com.cloudera.sparkts.models.ARIMA
import com.cloudera.sparkts.{BusinessDayFrequency, DateTimeIndex, TimeSeriesRDD}
import ingest.{ClosingPrice, Ingest}
import org.apache.spark.mllib.linalg.DenseVector
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.to_timestamp
import org.apache.spark.sql.types.DoubleType

import scala.io.{Codec, Source}

object SparkTS extends App {

  val ingester = new Ingest[ClosingPrice]()

  if (args.length == 2) {
    implicit val codec = Codec.UTF8
    val file = Source.fromFile(args(0))
    val symbolName = args(1)

    val priceSeqBySymbol = ClosingPrice.getSeqBySymbol(symbolName, ingester(file)).get.toSeq

    //    println(priceSeqBySymbol.toList)
    ClosingPrice.saveAsCsv(priceSeqBySymbol)

    val preDataFilePath = "src/main/resources/preData_TS.csv"

//    loadSparkCsv(preDataFilePath)


    val spark = SparkSession.builder.appName("Simple Application").config("spark.master", "local").getOrCreate()

    val formattedData = spark
      .read
      .option("header", "true")
      .csv(preDataFilePath)

    val finalDf = formattedData
      .withColumn("timestamp", to_timestamp(formattedData("date"), "yyyy-MM-dd"))
      .withColumn("price", formattedData("close").cast(DoubleType))
      .drop("date","close").sort("timestamp")

    finalDf.createOrReplaceTempView("preData")




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

//  def loadSparkCsv(path: String): Unit = {
//
//  }

}
