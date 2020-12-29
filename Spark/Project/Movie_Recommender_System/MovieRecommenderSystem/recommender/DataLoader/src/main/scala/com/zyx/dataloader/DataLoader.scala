/**
 * 模块作用:数据加载服务
 * 启动服务:mongodb elasticsearch
 */

/**
 * MongoDB:
 * Movie
 * Rating
 * Tag
 */

/**
 * Elasticsearch:
 * Movie
 */

package com.zyx.dataloader
import java.net.InetAddress
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.{MongoClient, MongoClientURI}
import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.transport.client.PreBuiltTransportClient


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
case class Rating(uid: Int, mid: Int, score: Double, timestamp: Int)

/**
 *
 * @param uid 用户ID
 * @param mid 电影ID
 * @param tag 用户对于电影的标签
 * @param timestamp 用户对于电影的标签的时间
 */
case class Tag(uid: Int, mid: Int, tag: String, timestamp: Int)

/**
 * MongoDB的连接配置
 * @param uri MongoDB的连接
 * @param db MongoDB操作的数据库
 */
case class MongoConfig(uri:String, db:String)

/**
 * ElasticSearch的连接配置
 * @param httpHosts http的主机列表
 * @param transportHosts transport的主机列表
 * @param index 需要操作的索引
 * @param clustername ES集群的名称
 */
case class ESConfig(httpHosts:String, transportHosts:String, index:String,
                    clustername:String)

/**
 * 加载数据
 */
object DataLoader {

  // 数据集的绝对路径
  val MOVIE_DATA_PATH = "/Users/nanase/Documents/GitHub/skills/Spark/Project/Movie_Recommender_System/MovieRecommenderSystem/recommender/DataLoader/src/main/resources/movies.csv"
  val RATING_DATA_PATH = "/Users/nanase/Documents/GitHub/skills/Spark/Project/Movie_Recommender_System/MovieRecommenderSystem/recommender/DataLoader/src/main/resources/ratings.csv"
  val TAG_DATA_PATH = "/Users/nanase/Documents/GitHub/skills/Spark/Project/Movie_Recommender_System/MovieRecommenderSystem/recommender/DataLoader/src/main/resources/tags.csv"

  // MongoDB中的表名
  val MONGODB_MOVIE_COLLECTION = "Movie"
  val MONGODB_RATING_COLLECTION = "Rating"
  val MONGODB_TAG_COLLECTION = "Tag"
  //  ES中的表名
  val ES_MOVIE_INDEX = "Movie"

  def main(args: Array[String]): Unit = {

    /**
     * 设置配置信息
     */
    val config = Map(
      "spark.cores" -> "local[*]",
      "mongo.uri" -> "mongodb://10.0.0.11:27017/recommender",
      "mongo.db" -> "recommender",
      "es.httpHosts" -> "10.0.0.11:9200",
      "es.transportHosts" -> "10.0.0.11:9300",
      "es.index" -> "recommender",
      "es.cluster.name" -> "es-cluster"
    )

    /**
     * 创建spark配置
     */
    // 创建sparkConf配置
    val sparkConf = new SparkConf().setMaster(config.get("spark.cores").get).setAppName("DataLoader")
    // 创建sparkSession
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()

    // 隐式转化
    import spark.implicits._

    /**
     * 加载数据集
     */
    val movieRDD = spark.sparkContext.textFile(MOVIE_DATA_PATH)
    val ratingsRDD = spark.sparkContext.textFile(RATING_DATA_PATH)
    val tagRDD = spark.sparkContext.textFile(TAG_DATA_PATH)

    /**
     * 将RDD转换为DataFrame
     */

    // 将movieRDD转化为DataFrame
    val movieDF = movieRDD.map(item => {
      // 通过^分割数据
      val attr = item.split("\\^")
      Movie(attr(0).toInt, attr(1).trim, attr(2).trim, attr(3).trim,
        attr(4).trim, attr(5).trim, attr(6).trim, attr(7).trim, attr(8).trim, attr(9).trim)
    }).toDF()

    // 将ratingsRDD转化为DataFrame
    val ratingsDF = ratingsRDD.map(item => {
      val attr = item.split(",")
      Rating(attr(0).toInt, attr(1).toInt, attr(2).toDouble, attr(3).toInt)
    }).toDF()

    // 将tagRDD转化为DataFrame
    val tagDF = tagRDD.map(item => {
      val attr = item.split(",")
      Tag(attr(0).toInt, attr(1).toInt, attr(2).trim, attr(3).toInt)
    }).toDF()

    implicit val mongoConfig = MongoConfig(config.get("mongo.uri").get, config.get("mongo.db").get)

    // 将DF数据存入MongoDB
    storeDataInMongoDB(movieDF, ratingsDF, tagDF)

    import org.apache.spark.sql.functions._
    /**
     * 构建以mid聚合后新的tags表
     * MID
     * Tags
     */
    var newTag = tagDF.groupBy($"mid")
      .agg(concat_ws("|",collect_set($"tag")).as("tags"))
        .select("mid","tags")

    /**
     * 构建新的movieTags表
     */
    // movieDF和newTag的以mid进行左连接
    var movieWithTagsDF = movieDF.join(newTag,Seq("mid","mid"),"left")

    /**
     * 声明ES配置的隐式参数
     */
    implicit val esConfig = ESConfig(
      config.get("es.httpHosts").get,
      config.get("es.transportHosts").get,
      config.get("es.index").get,
      config.get("es.cluster.name").get)

    // 将处理后的新数据存入ES
    storeDataInES(movieWithTagsDF)(esConfig)

    // 关闭spark连接
    spark.stop()

  }

  /**
   * 将数据写入MongoDB
   * @param movieDF
   * @param ratingDF
   * @param tagDF
   * @param mongoConfig
   */
  def storeDataInMongoDB(movieDF: DataFrame, ratingDF: DataFrame, tagDF: DataFrame)
                        (implicit mongoConfig: MongoConfig): Unit = {
    /**
     * 新建MongoDB连接
     */
    val mongoClient = MongoClient(MongoClientURI(mongoConfig.uri))

    /**
     * 如果Mongodb中表已经存在则删除否则创建
     */
    mongoClient(mongoConfig.db)(MONGODB_MOVIE_COLLECTION).dropCollection()
    mongoClient(mongoConfig.db)(MONGODB_RATING_COLLECTION).dropCollection()
    mongoClient(mongoConfig.db)(MONGODB_TAG_COLLECTION).dropCollection()

    /**
     * 将DF数据写入Mongodb数据库
     */
    movieDF
      .write
      .option("uri", mongoConfig.uri)
      .option("collection", MONGODB_MOVIE_COLLECTION)
      .mode("overwrite")
      .format("com.mongodb.spark.sql")
      .save()

    ratingDF
      .write
      .option("uri", mongoConfig.uri)
      .option("collection", MONGODB_RATING_COLLECTION)
      .mode("overwrite")
      .format("com.mongodb.spark.sql")
      .save()

    tagDF
      .write
      .option("uri", mongoConfig.uri)
      .option("collection", MONGODB_TAG_COLLECTION)
      .mode("overwrite")
      .format("com.mongodb.spark.sql")
      .save()

    /**
     * 对数据库建立索引
     */
    mongoClient(mongoConfig.db)(MONGODB_MOVIE_COLLECTION).createIndex(MongoDBObject("mid" -> 1))
    mongoClient(mongoConfig.db)(MONGODB_RATING_COLLECTION).createIndex(MongoDBObject("mid" -> 1))
    mongoClient(mongoConfig.db)(MONGODB_RATING_COLLECTION).createIndex(MongoDBObject("uid" -> 1))
    mongoClient(mongoConfig.db)(MONGODB_TAG_COLLECTION).createIndex(MongoDBObject("mid" -> 1))
    mongoClient(mongoConfig.db)(MONGODB_TAG_COLLECTION).createIndex(MongoDBObject("uid" -> 1))

    /**
     * MongoDB关闭连接
     */
    mongoClient.close()
  }

  /**
   * 将数据存入Elasticsearch
   * @param movieWithTagsDF
   * @param esConfig
   */
  def storeDataInES(movieWithTagsDF: DataFrame)(implicit esConfig: ESConfig): Unit = {
    /**
     * 新建ES配置
     */
    val settings: Settings = Settings.builder()
      .put("cluster.name", esConfig.clustername).build()

    /**
     * 新建ES客户端
     */
    val esClient = new PreBuiltTransportClient(settings)
    // 将TransportHosts添加到esClient中
    val REGEX_HOST_PORT = "(.+):(\\d+)".r
    esConfig.transportHosts.split(",").foreach {
      case REGEX_HOST_PORT(host: String, port: String) => {
        esClient.addTransportAddress(new
            InetSocketTransportAddress(InetAddress.getByName(host), port.toInt))
      }
    }

    /**
     * 清除ES中遗留的数据
     */
    if (esClient.admin().indices().exists(new IndicesExistsRequest(esConfig.index)).actionGet().isExists) {
      esClient.admin().indices().delete(new DeleteIndexRequest(esConfig.index))
    }

    esClient.admin().indices().create(new CreateIndexRequest(esConfig.index))

    /**
     * 将数据写入ES
     */
    movieWithTagsDF
      .write
      .option("es.nodes", esConfig.httpHosts)
      .option("es.http.timeout", "100m")
      .option("es.mapping.id", "mid")
      .mode("overwrite")
      .format("org.elasticsearch.spark.sql")
      .save(esConfig.index + "/" + ES_MOVIE_INDEX)
  }

}