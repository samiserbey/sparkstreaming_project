package com.scala_tasks.sparkstreaming

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Seconds, StreamingContext, Time}
import Utilities.{getRecord, setupLogging}

import org.apache.spark.sql.{SaveMode, SparkSession}

import java.sql.Date
object WriteToParquet {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("Write_Parquet").setMaster("local[*]")
    val ssc = new StreamingContext(conf, Seconds(1));

    setupLogging()

    // store streaming data into RDD as serialized Java objects for efficiency in space (but it's more CPU-intensive to read)
    val lines = ssc.socketTextStream("localhost", 4999, StorageLevel.MEMORY_AND_DISK_SER);
    // Convert RDDs to Parquet file
    lines.foreachRDD((rdd: RDD[String], time: Time) => {
      val spark = SparkSession
        .builder()
        .appName("Write_Parquet")
        .getOrCreate()
      import spark.implicits._
      val device_data = rdd.map(jsonLine => Record(jsonLine)).filter(_ != null).toDF()
      device_data.write.mode(SaveMode.Append).parquet("/tmp/parquet");
    })

    lines.print()

    // Kick it off
    ssc.checkpoint("C:/checkpoint/")
    ssc.start()
    ssc.awaitTermination()

  }

  case class Record(deviceId: String, temperature: Int, location: Map[String, String], time: Date)

  object Record {
    def apply(jsonString: String): Record = {
      try {
        val r = getRecord(jsonString)
        Record(r._1, r._2, r._3, r._4)
      } catch {
        case _: com.fasterxml.jackson.core.JsonParseException => {
          println("Invalid record entered: "+jsonString)
          null
        }
      }
    }
  }
}
