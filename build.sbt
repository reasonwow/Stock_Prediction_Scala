name := "Stock_Prediction_Scala"

version := "0.1"

scalaVersion := "2.11.8"


dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-core" % "2.8.7"
dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % "2.8.7"
dependencyOverrides += "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.8.7"

// https://mvnrepository.com/artifact/org.apache.spark/spark-core
libraryDependencies += "org.apache.spark" % "spark-core_2.11" % "2.2.0"

libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.2.0"

// https://mvnrepository.com/artifact/com.yahoofinance-api/YahooFinanceAPI
libraryDependencies += "com.yahoofinance-api" % "YahooFinanceAPI" % "3.12.1"

// https://mvnrepository.com/artifact/com.cloudera.sparkts/sparkts
libraryDependencies += "com.cloudera.sparkts" % "sparkts" % "0.4.1"

// https://mvnrepository.com/artifact/org.apache.spark/spark-mllib
libraryDependencies += "org.apache.spark" % "spark-mllib_2.11" % "2.2.0"

// https://mvnrepository.com/artifact/org.nd4j/nd4j-api
libraryDependencies += "org.nd4j" % "nd4j-api" % "0.9.1"

libraryDependencies += "org.nd4j" % "nd4j-native" % "0.9.1"

classpathTypes += "maven-plugin"

libraryDependencies += "org.nd4j" % "nd4j-native-platform" % "0.9.1"

libraryDependencies += "org.deeplearning4j" % "deeplearning4j-core" % "0.9.1"

libraryDependencies += "org.datavec" % "datavec-api" % "0.9.1"

libraryDependencies += "org.datavec" % "datavec-dataframe" % "0.9.1"

libraryDependencies += "com.opencsv" % "opencsv" % "3.9"

libraryDependencies += "com.google.guava" % "guava" % "23.0"
