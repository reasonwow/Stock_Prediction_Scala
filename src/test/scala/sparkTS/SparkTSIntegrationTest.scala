package sparkTS

import org.apache.spark.sql.SparkSession
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

class SparkTSIntegrationTest extends FlatSpec with Matchers with BeforeAndAfter {
  implicit var spark: SparkSession = _

  before {
    spark = SparkSession
      .builder()
      .appName("SparkTS")
      .master("local[*]")
      .getOrCreate()
  }

  after {
    if (spark != null) {
      spark.stop()
    }
  }

  behavior of "loadSparkCsv"

  it should "match the size" in {
    val preDataFilePath = "testData/testDataForSparkTS.csv"
    val finalDf = SparkTS.loadSparkCsv(preDataFilePath)
    finalDf.count() shouldBe 3
  }

  it should "sort by timestamp" in {
    val preDataFilePath = "testData/testDataForSparkTS.csv"
    val finalDf = SparkTS.loadSparkCsv(preDataFilePath)
    finalDf.collect.map(_.toString) shouldBe {
      Array("[GOOG,2017-10-02 00:00:00.0,1.0]", "[GOOG,2017-10-03 00:00:00.0,2.0]", "[GOOG,2017-10-04 00:00:00.0,3.0]")
    }
  }

  behavior of "getTSRDD"

  it should "match the size" in {

    val preDataFilePath = "testData/testDataForSparkTS.csv"
    val finalDf = SparkTS.loadSparkCsv(preDataFilePath)
    finalDf.createOrReplaceTempView("preData")

    val tsRdd = SparkTS.getTSRDD(finalDf)
//    print(tsRdd.collectAsTimeSeries.head.toString())  //(GOOG,[2.2,2.2,2.2,2.2,2.2,2.2,2.2,2.2,2.2,2.2,2.2,2.2,2.2,2.2,2.2,2.2,2.2,2.2,2.2,2.2,2.2,2.2,2.2,2.2,2.2,2.2,2.2,2.2,2.2,2.2,2.2,2.2,2.2,2.2,2.2,2.2,2.2,2.2,2.2,2.2,2.2,2.2,2.2,3.3])
    tsRdd.count shouldBe 1
  }

  behavior of "getResultArray"

  it should "match the size" in {
    val preDataFilePath = "testData/testDataForSparkTS.csv"
    val finalDf = SparkTS.loadSparkCsv(preDataFilePath)
    finalDf.createOrReplaceTempView("preData")

    val tsRdd = SparkTS.getTSRDD(finalDf)
    val arrayResult = SparkTS.getResultArray(tsRdd, 1)

    arrayResult.size shouldBe 1
  }

  it should "predict correct" in {
    val preDataFilePath = "testData/testDataForSparkTS.csv"
    val finalDf = SparkTS.loadSparkCsv(preDataFilePath)
    finalDf.createOrReplaceTempView("preData")

    val tsRdd = SparkTS.getTSRDD(finalDf)
    val arrayResult = SparkTS.getResultArray(tsRdd, 5)

    arrayResult.head.toString shouldBe {
      "[GOOG,[3.0,4.0,5.0,6.0,7.0]]"
    }
  }

}
