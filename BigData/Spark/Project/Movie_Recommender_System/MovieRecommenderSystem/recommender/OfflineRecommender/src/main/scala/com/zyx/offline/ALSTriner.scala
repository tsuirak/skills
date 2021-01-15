/**
 * 模块作用:参数选择
 */
package com.zyx.offline

import breeze.numerics.sqrt
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.mllib.recommendation.{ALS, MatrixFactorizationModel, Rating}
import org.apache.spark.rdd.RDD


object ALSTriner {
  def main(args: Array[String]): Unit = {

    /**
     * 设置配置信息
     */
    val config = Map(
      "spark.cores" -> "local[*]",
      "mongo.uri" -> "mongodb://10.0.0.11:27017/recommender",
      "mongo.db" -> "recommender"
    )

    // 创建sparkConf配置
    val sparkConf = new SparkConf().setMaster(config.get("spark.cores").get).setAppName("DataLoader")
    // 创建sparkSession
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()

    val mongoConfig = MongoConfig(config("mongo.uri"),config("mongo.db"))
    import spark.implicits._

    /**
     * 加载评分数据
     */
    val ratingRDD = spark
      .read
      .option("uri",mongoConfig.uri)
      .option("collection",OfflineRecommender.MONGODB_RATING_COLLECTION)
      .format("com.mongodb.spark.sql")
      .load()
      .as[MovieRating]
      .rdd
      .map(rating => Rating(rating.uid,rating.mid,rating.score)).cache()


    /**
     * 输出最优参数
     */

    adjustALSParams(ratingRDD)

    spark.stop()
  }

  /**
   * 输出最终的最优参数
   * @param trainRDD
   */
  def adjustALSParams(trainRDD:RDD[Rating]){
    val result = for(rank <- Array(100, 200, 250); lambda <- Array(1, 0.1, 0.01, 0.001))
      yield {
        val model = ALS.train(trainRDD, rank, 5, lambda)
        val rmse = getRMSE(model, trainRDD)
        (rank, lambda, rmse)
      }
    println(result.minBy(_._3))
  }

  def getRMSE(model : MatrixFactorizationModel, testingRDD: RDD[Rating]): Double = {
    // 构造userProducts RDD[(Int,Int)]
    val userMovies = testingRDD.map(item => (item.user, item.product))
    val predictRating = model.predict(userMovies)
    val real = testingRDD.map(item => ((item.user, item.product), item.rating))
    val predict = predictRating.map(item => ((item.user, item.product), item.rating))

    // 计算RMSE
    sqrt(
      real.join(predict).map{
        case((uid, mid), (real, predict)) =>
          // 真实值与预测值之间的差值
          val err = real - predict
          err*err
      }.mean()
    )
  }


}
