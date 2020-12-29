/**
* 模块作用:统计推荐算法
* 启动服务:mongodb
*/

/**
 * MongoDB:
 * RateMoreMovies
 * RateMoreRecentlyMovies
 * AverageMovies
 * GenresTopMovies
 */
package com.zyx.statistics

import java.text.SimpleDateFormat
import java.util.Date

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession


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
 * MongoDB的连接配置
 * @param uri MongoDB的连接
 * @param db MongoDB操作的数据库
 */
case class MongoConfig(uri:String, db:String)

/**
 * rating.csv
 * @param uid 用户ID
 * @param mid 电影ID
 * @param score 用户对于电影的评分
 * @param timestamp 用户对于电影的评分的时间
 */
case class Rating(uid: Int, mid: Int, score: Double, timestamp: Int)

/**
 * 推荐电影
 * @param mid 电影推荐的id
 * @param score 电影推荐的评分
 */
case class Recommendation(mid:Int, score:Double)

/**
 * 电影类别推荐
 * @param genres 电影类别
 * @param recs top10的电影集合
 */
case class GenresRecommendation(genres:String, recs:Seq[Recommendation])


object StatisticsRecommender {
  // 数据集的绝对路径
  val MOVIE_DATA_PATH = "/Users/nanase/Documents/GitHub/skills/Spark/Project/Movie_Recommender_System/MovieRecommenderSystem/recommender/DataLoader/src/main/resources/movies.csv"
  val RATING_DATA_PATH = "/Users/nanase/Documents/GitHub/skills/Spark/Project/Movie_Recommender_System/MovieRecommenderSystem/recommender/DataLoader/src/main/resources/ratings.csv"

  // 设置topk中的k值
  val MOST_SCORE_OF_NUMBER = 10
  // MongoDB中的表名
  val MONGODB_RATING_COLLECTION = "Rating"
  val MONGODB_MOVIE_COLLECTION = "Movie"

  // 统计表的名称
  val RATE_MORE_MOVIES = "RateMoreMovies"
  val RATE_MORE_RECENTLY_MOVIES = "RateMoreRecentlyMovies"
  val AVERAGE_MOVIES = "AverageMovies"
  val GENRES_TOP_MOVIES = "GenresTopMovies"

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
    val sparkConf = new SparkConf().setMaster(config.get("spark.cores").get).setAppName("StatisticsRecommender")
    // 创建sparkSession
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()

    /**
     * 加载数据
     */

    var mongoConfig = MongoConfig(config("mongo.uri"),config("mongo.db"))

    import spark.implicits._

    val ratingDF = spark
      .read
      .option("uri", mongoConfig.uri)
      .option("collection", MONGODB_RATING_COLLECTION)
      .format("com.mongodb.spark.sql")
      .load()
      .as[Rating]
      .toDF()

    val movieDF = spark
      .read
      .option("uri", mongoConfig.uri)
      .option("collection", MONGODB_MOVIE_COLLECTION)
      .format("com.mongodb.spark.sql")
      .load()
      .as[Movie]
      .toDF()

    // 创建ratings表
    ratingDF.createOrReplaceTempView("ratings")

    /**
     * 统计推荐算法
     */

    /**
     * 统计历史热门电影
     */
    val rateMoreMoviesDF = spark.sql("select mid,count(mid) as count from ratings group by mid")

    rateMoreMoviesDF
      .write
      .option("uri", mongoConfig.uri)
      .option("collection", RATE_MORE_MOVIES)
      .mode("overwrite")
      .format("com.mongodb.spark.sql")
      .save()

    /**
     * 统计最近热门电影
     */
    val simpleDateFormat = new SimpleDateFormat("yyyyMM")
    // 用于将rating数据中的timestamp转换成年月格式
    spark.udf.register("changeDate", (x:Int) => simpleDateFormat.format(new Date(x * 1000L)).toInt)

    val ratingOfYearMonth = spark.sql("select mid, score, changeDate(timestamp) as yearmonth from ratings")

    // 创建ratingOfMonth表
    ratingOfYearMonth.createOrReplaceTempView("ratingOfMonth")

    // 先以yearmonth分组再以mid分组
    val rateMoreRecentlyMovies = spark.sql("select mid, count(mid) as count, yearmonth from ratingOfMonth group by yearmonth, mid")

    rateMoreRecentlyMovies
      .write
      .option("uri", mongoConfig.uri)
      .option("collection", RATE_MORE_RECENTLY_MOVIES)
      .mode("overwrite")
      .format("com.mongodb.spark.sql")
      .save()

    /**
     * 统计电影的平均评分
     */
    val averageMoviesDF = spark.sql("select mid, avg(score) as avg from ratings group by mid")

    averageMoviesDF
      .write
      .option("uri", mongoConfig.uri)
      .option("collection", AVERAGE_MOVIES)
      .mode("overwrite")
      .format("com.mongodb.spark.sql")
      .save()

    /**
     * 统计每种类别top10
     */

    // join数据集averageMoviesDF和movieWithScore
    val movieWithScore = movieDF.join(averageMoviesDF, Seq("mid"))

    // 电影种类
    val genres =
      List("Action","Adventure","Animation","Comedy","Crime","Documentary","Drama","Family","Fantasy","Foreign",
        "History","Horror","Music","Mystery"
        ,"Romance","Science","Tv","Thriller","War","Western")

    // genres转换为RDD
    val genresRDD = spark.sparkContext.makeRDD(genres)

    // movieWithScore与genres进行笛卡尔积
    val genresTopMovies  = genresRDD.cartesian(movieWithScore.rdd)
      .filter{
        // 过滤电影类别不匹配的电影
        case(genres, row) =>
          row.getAs[String]("genres").toLowerCase.contains(genres.toLowerCase)
      }
      .map{
        // 减少数据集的数据量
        case(genres, row) =>
          (genres, ((row.getAs[Int]("mid")), row.getAs[Double]("avg")))
      }.groupByKey() // 将数据集中的类别相同的电影进行聚合
      .map{
        // 通过评分大小进行降序排序
        case (genres, items) => GenresRecommendation(genres, items.toList.sortWith(_._2 > _._2)
          .take(MOST_SCORE_OF_NUMBER).map(item => Recommendation(item._1, item._2)))
      }.toDF()

    genresTopMovies
      .write
      .option("uri", mongoConfig.uri)
      .option("collection", GENRES_TOP_MOVIES)
      .mode("overwrite")
      .format("com.mongodb.spark.sql")
      .save()

    spark.stop()
  }
}
