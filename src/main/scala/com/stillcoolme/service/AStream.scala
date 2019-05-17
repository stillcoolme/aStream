package com.stillcoolme.service

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}

/**
  * 参数填入 kafakBroker地址端口 消费组名 topic名
  * 参数填入 172.16.21.189:9092 astream monitor
  * @author stillcoolme
  * @date 2019/2/28 19:31
  */
object AStream {
  def main(args: Array[String]): Unit = {
    if(args.length != 3){
      println("Usage:StatStreamingApp <brokers> <group> <topic>")
      System.exit(1)
    }
    Logger.getRootLogger.setLevel(Level.WARN)
/*    val properties = new Properties()
    val path = Thread.currentThread().getContextClassLoader.getResource("stream.properties").getPath
    properties.load(new FileInputStream(path))
    properties.getProperty("")*/

    val Array(brokers, groupId, topic) = args
//    val sparkConf = new SparkConf().setAppName("aStream").setMaster("local[*]");
    val sparkConf = new SparkConf().setAppName("aStream");
    val ssc = new StreamingContext(sparkConf, Seconds(2))

    val topics = topic.split(",").toSet
    val kafkaParmas = Map[String, Object](
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> brokers,
      ConsumerConfig.GROUP_ID_CONFIG -> groupId,
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
      ConsumerConfig.AUTO_OFFSET_RESET_CONFIG -> "latest",
      ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG -> (false: java.lang.Boolean))
    val message = KafkaUtils.createDirectStream[String, String](
      ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](topics, kafkaParmas)
    )

    message.map(_.value().split("\t"))


    message.map(_.value()).count().print()
    message.map(_.value()).print()


    ssc.start()
    ssc.awaitTermination();

  }

}
