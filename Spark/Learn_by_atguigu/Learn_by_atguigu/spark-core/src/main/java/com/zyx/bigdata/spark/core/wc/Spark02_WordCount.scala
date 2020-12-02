package com.zyx.bigdata.spark.core.wc

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object Spark02_WordCount {
  def main(args: Array[String]): Unit = {

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

    // 2.4 分组统计相同的单词
    var wordGroup : RDD[(String,Iterable[(String,Int)])] = wordToOne.groupBy(
      t => t._1
    ) // 按word进行分组 (word,(word,int))


    // 2.5 分组数据进行转换
    var wordToCount = wordGroup.map {
      case ( word , list ) => {
        list.reduce(
          (t1, t2) => {
            (t1._1, t1._2 + t2._2)
          }
        )
      }
    }

    // 2.6 结果展示
    var array : Array[(String,Int)] = wordToCount.collect()

    array.foreach(println)

    // 3.关闭连接
    sc.stop()
  }

}
