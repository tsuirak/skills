/**
 * 模块作用:构建离散推荐算法
 * 启动服务:mongodb
 */

/**
 * MongoDB:
 * UserRecs
 * MovieRecs
 */

package com.zyx.offline

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.mllib.recommendation.{ALS, Rating}
import org.jblas.DoubleMatrix


/**
 * movies.csv
 * @param mid 电影ID
 * @param name 电影名称
 * @param descri 电影详情描述
 * @param timelong 电影时长
 * @param issue 电影发行日期
 * @param shoot 电影拍摄日期
 * @param language 电影语言
 * @param genres 电影类型
 * @param actors 电影演员表
 * @param directors 电影导演
 */
case class Movie(mid: Int, name: String, descri: String, timelong: String, issue: String,
                 shoot: String, language: String, genres: String, actors: String,
                 directors: String)

/**
 * rating.csv
 * @param uid 用户ID
 * @param mid 电影ID
 * @param score 用户对于电影的评分
 * @param timestamp 用户对于电影的评分的时间
 */
case class MovieRating(uid: Int, mid: Int, score: Double, timestamp: Int)

/**
 * MongoDB的连接配置
 * @param uri MongoDB的连接
 * @param db MongoDB操作的数据库
 */
case class MongoConfig(uri:String, db:String)

/**
 * 标准推荐
 * @param mid
 * @param score
 */
case class Recommendation(mid: Int, score:Double)

/**
 * 用户推荐
 * @param uid
 * @param recs
 */
case class UserRecs(uid: Int, recs: Seq[Recommendation])

/**
 * 电影相似度（电影推荐）
 * @param mid
 * @param recs
 */
case class MovieRecs(mid: Int, recs: Seq[Recommendation])

object OfflineRecommender {

  // 数据集数据库
  val MONGODB_RATING_COLLECTION = "Rating"
  val MONGODB_MOVIE_COLLECTION = "Movie"

  // 推荐表的名称
  val USER_RECS = "UserRecs" //用于离线推荐
  val MOVIE_RECS = "MovieRecs" //用于实时推荐

  // 设定推荐数目
  val USER_MAX_RECOMMENDATION = 20


  def main(args: Array[String]): Unit = {

    /**
     * 设置配置信息
     */
    val config = Map(
      "spark.cores" -> "local[*]",
      "mongo.uri" -> "mongodb://10.0.0.11:27017/recommender",
      "mongo.db" -> "recommender"
    )


    /**
     * 创建spark配置
     */
    // 创建sparkConf配置
    val sparkConf = new SparkConf().setMaster(config.get("spark.cores").get).setAppName("Offline")
      .set("spark.executor.memory","4G").set("spark.driver.memory","2G")
    // 创建sparkSession
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()

    import spark.implicits._

    /**
     * 创建mongoconfig
     */
    val mongoConfig = MongoConfig(config("mongo.uri"),config("mongo.db"))

    /**
     * 读取mongoDB中的数据
     */

    // 评分数据集
    var ratingRDD = spark
      .read
      .option("uri",mongoConfig.uri)
      .option("collection",MONGODB_RATING_COLLECTION)
      .format("com.mongodb.spark.sql")
      .load()
      .as[MovieRating]
      .rdd
      .map(rating => (rating.uid, rating.mid, rating.score)).cache()

    var userRDD = ratingRDD.map(_._1).distinct()

    // 电影数据集
    var movieRDD = spark
      .read
      .option("uri",mongoConfig.uri)
      .option("collection",MONGODB_MOVIE_COLLECTION)
      .format("com.mongodb.spark.sql")
      .load()
      .as[Movie]
      .rdd
      .map(_.mid).cache()


    /**
     * 创建训练数据集和训练参数
     */
    val trainData = ratingRDD.map(x => Rating(x._1, x._2, x._3))
    val (rank, iterations, lambda) = (50, 5, 0.1)


    /**
     * 训练ALS模型
     */
    val model = ALS.train(trainData, rank, iterations, lambda)


    /**
     * 构建用户推荐矩阵
     */

    // 构建需要预测的矩阵
    val userMovies = userRDD.cartesian(movieRDD)

    // 预测值
    val preRatings = model.predict(userMovies)

    val userRecs = preRatings
      .filter(x => x.rating > 0.6)
      .map(rating => (rating.user, (rating.product, rating.rating)))
      .groupByKey()
      .map{
        case (uid, recs) => UserRecs(uid, recs.toList.sortWith(_._2 > _._2) // 降序排序
          .take(USER_MAX_RECOMMENDATION).map(x => Recommendation(x._1, x._2)))
      }.toDF()

    // 写入mongoDB数据库
    userRecs.write
      .option("uri",mongoConfig.uri)
      .option("collection",USER_RECS)
      .mode("overwrite")
      .format("com.mongodb.spark.sql")
      .save()

    /**
     * 计算电影的相似度矩阵以备实时推荐使用
     * 得到电影矩阵 (n x k)
     */

    // 计算电影的特征矩阵
    val movieFeatures = model.productFeatures.map{
      case (mid, features) => (mid, new DoubleMatrix(features))
    }
    // 过滤
    val movieRecs = movieFeatures.cartesian(movieFeatures)
      .filter{
        case (x, y) => x._1 != y._1
      }
      .map{
        case (x, y) =>
          val simScore = consinSim(x._2, y._2)
          (x._1, (y._1, simScore))
      }
      .filter(_._2._2 > 0.6)
      .groupByKey()
      .map{
        case (mid, item) => MovieRecs(mid, item.toList.map(x => Recommendation(x._1, x._2)))
      }.toDF()

    // 写入数据库
    movieRecs
      .write
      .option("uri", mongoConfig.uri)
      .option("collection",MOVIE_RECS)
      .mode("overwrite")
      .format("com.mongodb.spark.sql")
      .save()

    spark.stop()
  }

  def consinSim(x: DoubleMatrix, y: DoubleMatrix):Double = {
    x.dot(y) / (x.norm2() * y.norm2())
  }

}
