package com.zyx.bigdata.spark.core.wc

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

object Spark03_WordCount {
  def main(args: Array[String]): Unit = {
    // Spark框架可以将分组和聚合使用一个方法实现
    // reduceByKey()

    // 1. 建立和Spark框架的连接
    var sparkConf = new SparkConf().setMaster("local").setAppName("WordCount") // local环境
    var sc = new SparkContext(sparkConf)
    // 2.执行业务操作

    // 2.1 读取文件 按行获取数据
    var lines : RDD[String] = sc.textFile(path = "datasets")

    // 2.2 拆分单词
    var words : RDD[String] = lines.flatMap(_.split(" ")) // 扁平化操作

    // 2.3 单词映射 (word,1)
    val wordToOne = words.map {
      word => (word, 1)
    }

    // 2.4 reduceByKey()
    var wordToCount = wordToOne.reduceByKey(_ + _)

    // 2.5 结果展示
    var array : Array[(String,Int)] = wordToCount.collect()

    array.foreach(println)

    // 3.关闭连接
    sc.stop()

  }
}
