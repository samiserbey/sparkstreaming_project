package com.scala_tasks.sparkstreaming

import Utilities.{setupLogging, checkDateAndGetNextDayDate}
import org.apache.spark.sql.SparkSession

/** Simple application to read parquet data and query them out */
object QueryParquet {
  /** Our main function where the action happens */
  def main(args: Array[String]): Unit = {
    val given_date = args(0)
    val given_date_limit = checkDateAndGetNextDayDate(given_date)
    val spark = SparkSession
      .builder()
      .appName("Query_Parquet")
      .master("local")
      .getOrCreate()
    setupLogging()

    val df = spark.read.parquet("/tmp/parquet")
    df.createOrReplaceTempView("data_iot")
    // maximum temperatures measured for every device
    val maxTempQuery = spark.sqlContext.sql("select deviceId, MAX(temperature) as maxTemp from data_iot group by deviceId")
    // amount of data points aggregated for every device
    val countQuery = spark.sqlContext.sql("select deviceId, count(*) as count from data_iot group by deviceId")
    // highest temperature measured on a given day for every device
    val maxTempOnGivenDay = spark.sqlContext.sql("select deviceId, MAX(temperature) as maxTemp from data_iot where time >= '"+given_date+"' and time < '"+given_date_limit+"' group by deviceId")
    println("MAX TEMPERATURE PER DEVICE:")
    maxTempQuery.show(5, false)
    println("AGGREGATED DATA COUNT PER DEVICE:")
    countQuery.show(5, false)
    println("MAX TEMPERATURE ON GIVEN DAY ("+given_date+") PER DEVICE:")
    maxTempOnGivenDay.show(5, false)
    spark.stop()
  }

}