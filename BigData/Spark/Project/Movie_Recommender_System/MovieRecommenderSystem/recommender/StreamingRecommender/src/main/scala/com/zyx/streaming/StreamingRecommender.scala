/**
 * 模块作用:实时推荐算法
 * 启动服务:mongodb kafka-zookeeper redis
 */

/**
 * MongoDB:
 * StreamRecs
 * Rating
 * MovieRecs
 */

package com.zyx.streaming

import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.{MongoClient, MongoClientURI}
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import redis.clients.jedis.Jedis

import scala.collection.JavaConversions._


// 连接助手对象
object ConnHelper extends Serializable{
  // 连接Redis集群
  lazy val jedis = new Jedis("10.0.0.11")
  // 连接MongoDB服务
  lazy val mongoClient =
    MongoClient(MongoClientURI("mongodb://10.0.0.11:27017/recommender"))
}

case class MongoConfig(uri:String,db:String)
// 定义标准推荐对象
case class Recommendation(mid:Int, score:Double)
// 定义基于预测评分d1用户的推荐
case class StreamRecs(uid:Int, recs:Seq[Recommendation])
// 定义基于LFMd电影特征向量的电影的相似度列表
case class MovieRecs(mid:Int, recs:Seq[Recommendation])


object StreamingRecommender {
  val MAX_USER_RATINGS_NUM = 20
  val MAX_SIM_MOVIES_NUM = 20
  val MONGODB_STREAM_RECS_COLLECTION = "StreamRecs"
  val MONGODB_RATING_COLLECTION = "Rating"
  val MONGODB_MOVIE_RECS_COLLECTION = "MovieRecs"

  def main(args: Array[String]): Unit = {
    /**
     * 设置配置信息
     */
    val config = Map(
      "spark.cores" -> "local[*]",
      "mongo.uri" -> "mongodb://10.0.0.11:27017/recommender",
      "mongo.db" -> "recommender",
      "kafka.topic" -> "recommender"
    )

    /**
     * 创建spark配置
     */
    // 创建sparkConf配置
    val sparkConf = new SparkConf().setMaster(config.get("spark.cores").get).setAppName("StreamingRecommender")
    // 创建sparkSession
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()
    var sc = spark.sparkContext
    var ssc = new StreamingContext(sc, Seconds(2))

    import spark.implicits._

    /**
     * 广播电影相似度矩阵
     */

    implicit var mongoConfig = MongoConfig(config("mongo.uri"),config("mongo.db"))

    val simMoviesMatrix = spark
      .read
      .option("uri", config("mongo.uri"))
      .option("collection", MONGODB_MOVIE_RECS_COLLECTION)
      .format("com.mongodb.spark.sql")
      .load()
      .as[MovieRecs]
      .rdd
      .map{
        recs => (recs.mid, recs.recs.map(x => (x.mid, x.score)).toMap)
      }.collectAsMap()

    // 加载电影相似度矩阵 进行广播
    val simMoviesMatrixBroadcast = sc.broadcast(simMoviesMatrix)

    /**
     * 创建kafka的连接配置
     */
    val kafkaPara = Map(
      "bootstrap.servers" -> "10.0.0.11:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "recommender",
      "auto.offset.reset" -> "latest"
    )

    // 创建kafka连接
    val kafkaStream = KafkaUtils.createDirectStream[String, String](ssc, LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](Array(config("kafka.topic")), kafkaPara))

    // 格式:UID|MID|SCORE|TIMESTAMP|
    val ratingStream = kafkaStream.map{
      case msg =>
        var attr = msg.value().split("\\|")
        (attr(0).toInt, attr(1).toInt, attr(2).toDouble, attr(3).split("#")(0).toInt)
    }

    ratingStream.foreachRDD{
      rdd => rdd.map{
        case (uid, mid, score, timestame) =>
          println(">>>>>>>>>>>>>>>>>>")
          // 从redis中获取用户最近M次电影评分
          val userRecentlyRatings = getUserRecentlyRating(MAX_USER_RATINGS_NUM, uid, ConnHelper.jedis)
//
          // 从获取流中获取电影P最相似的K个电影
          val simMovies = getTopSimMovies(MAX_SIM_MOVIES_NUM, mid, uid, simMoviesMatrixBroadcast.value)
//
          // 计算候选电影的推荐优先级
          val streamRecs = computeMovieScores(simMoviesMatrixBroadcast.value, userRecentlyRatings, simMovies)
//
          // 将数据保存到MongoDB
          saveRecsToMongoDB(uid, streamRecs)
      }.count()
    }

    /**
     * 启动Streaming程序
     */
    ssc.start()
    ssc.awaitTermination()
  }


  /**
   * 获取用户最近M次电影评分
   * @param num 评分个数
   * @param uid 用户
   * @param jedis redis端口
   * @return
   */
  def getUserRecentlyRating(num: Int, uid: Int, jedis: Jedis):Array[(Int, Double)] = {
    // 从用户队列中获取num个评分
    jedis.lrange("uid:" + uid.toString, 0, num - 1).map{
      item =>
        val attr = item.split("\\:")
        (attr(0).trim.toInt, attr(1).trim.toDouble)
    }.toArray
  }

  /**
   * 获取电影P最相似的K个电影
   * @param num 相似电影数量
   * @param mid 电影ID
   * @param uid 用户ID
   * @param simMovies 电影相似度广播矩阵
   * @param mongConfig MongoDB配置
   * @return
   */
  def getTopSimMovies(num: Int, mid: Int, uid: Int,
                      simMovies: scala.collection.Map[Int, scala.collection.immutable.Map[Int, Double]])
                     (implicit mongConfig: MongoConfig):Array[Int] = {

    // 从广播变量的电影相似度矩阵获取当前电影最相似的电影
    val allSimMovies = simMovies.get(mid).get.toArray
    // 先获取用户已经观看过的电影
    val ratingExist = ConnHelper.mongoClient(mongConfig.db)(MONGODB_RATING_COLLECTION)
      .find(MongoDBObject("uid" -> uid)).toArray.map{
      item => item.get("mid").toString.toInt
    }
    // 过滤用户观看过的电影并进行排序
    allSimMovies.filter(x => !ratingExist.contains(x._1)).sortWith(_._2 > _._2)
      .take(num).map(x => x._1)
  }

  /**
   * 计算候选电影的推荐分数
   * @param simMovies 电影相似度矩阵
   * @param userRecentlyRatings 电影最近的k次评分
   * @param topSimMovies 当前电影的最相似的k个电影
   * @return
   */
  def computeMovieScores(simMovies: scala.collection.Map[Int, scala.collection.immutable.Map[Int, Double]],
                         userRecentlyRatings: Array[(Int, Double)], topSimMovies: Array[Int]):Array[(Int, Double)] = {

    // 用于保存最后的每一个待选电影和他的评分
    val score = scala.collection.mutable.ArrayBuffer[(Int, Double)]()

    // 用于保存每一个电影的增强因子
    val increMap = scala.collection.mutable.HashMap[Int, Int]()

    // 用于保存每一个电影的衰减因子
    val decreMap = scala.collection.mutable.HashMap[Int, Int]()

    for(topSimMovie <- topSimMovies; userRecentlyRating <- userRecentlyRatings){
      val simScore = getMoviesSimScore(simMovies, userRecentlyRating._1, topSimMovie)

      if(simScore > 0.6){
        score += ((topSimMovie, simScore * userRecentlyRating._2))
        if(userRecentlyRating._2 > 3){
          increMap(topSimMovie) = increMap.getOrDefault(topSimMovie, 0) + 1
        }else {
          decreMap(topSimMovie) = decreMap.getOrDefault(topSimMovie, 0) + 1
        }
      }

    }

    score.groupBy(_._1).map{
      case (mid, sims) =>
        (mid, sims.map(_._2).sum / sims.length +
          math.log10(increMap.getOrDefault(mid, 1)) - math.log10(decreMap.getOrDefault(mid, 1)))

    }.toArray.sortWith(_._2 > _._2)


  }

  /**
   * 用于找出用户评分的电影和候选电影的相似度
   * @param simMovies 相似度矩阵
   * @param userRatingMovie 用户评分的电影
   * @param topSimMovie 候选电影
   * @return
   */
  def getMoviesSimScore(simMovies:scala.collection.Map[Int,scala.collection.immutable.Map[Int,Double]],
                        userRatingMovie: Int, topSimMovie: Int): Double ={
    simMovies.get(topSimMovie) match {
      case Some(sim) => sim.get(userRatingMovie) match {
        case Some(score) => score
        case None => 0.0
      }
      case None => 0.0
    }
  }

  /**
   * 数据保存MongoDB
   * @param uid
   * @param streamRecs 流式推荐
   * @param mongConfig MongoDB配置
   */
  def saveRecsToMongoDB(uid: Int, streamRecs: Array[(Int, Double)])
                       (implicit mongConfig: MongoConfig): Unit ={
    val streaRecsCollection = ConnHelper.mongoClient(mongConfig.db)(MONGODB_STREAM_RECS_COLLECTION)

    streaRecsCollection.findAndRemove(MongoDBObject("uid" -> uid))
    streaRecsCollection.insert(MongoDBObject("uid" -> uid, "recs" ->
      streamRecs.map(x => MongoDBObject("mid" -> x._1, "score" -> x._2))))
  }

}
