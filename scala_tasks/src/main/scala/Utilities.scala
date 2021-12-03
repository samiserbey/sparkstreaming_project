package com.scala_tasks.sparkstreaming
import java.sql.{Timestamp, Date}
import java.util.Calendar
import org.json4s.jackson.JsonMethods.{parse, compact}

object Utilities {
  /** Makes sure only ERROR messages get logged to avoid log spam. */
  def setupLogging() = {
    import org.apache.log4j.{Level, Logger}
    val rootLogger = Logger.getRootLogger()
    rootLogger.setLevel(Level.ERROR)
  }

  def checkDateAndGetNextDayDate(date_str:String): String = {
    try {
      val date = Timestamp.valueOf(date_str + " 00:00:00");
      val c = Calendar.getInstance()
      c.setTime(date)
      if (new Date(c.getTimeInMillis()).toString() != date_str) {
        throw new Exception("Invalid date")
      }
      c.add(Calendar.DATE, 1)
      new Date(c.getTimeInMillis()).toString()
    } catch {
      case e: Exception => throw new Exception("Invalid date")
    }
  }

  def getRecord(jsonString:String): (String, Int, Map[String, String], Date) = {
    val jsonObject = parse(jsonString)
    val data = jsonObject \ "data"
    val deviceId = compact(data\"deviceId")
    val temperature = compact(data\"temperature").toInt
    val location = Map(
      "longitude" -> compact(data\"location"\"longitude"),
      "latitude" -> compact(data\"location"\"latitude")
    )
    // quick fix because for some reason double quote on time (from each side) appear after parsing
    val time_str:String = compact(data\"time").replaceAll("[^\\d.]", "")
    val time = new Date(time_str.toLong*1000)
    (deviceId, temperature, location, time)
  }
}
