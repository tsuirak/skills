/**
 * 基于内容的推荐算法
 */

import org.apache.spark.SparkConf
import org.apache.spark.ml.feature.{HashingTF, IDF, Tokenizer}
import org.apache.spark.ml.linalg.SparseVector
import org.apache.spark.sql.SparkSession
import org.jblas.DoubleMatrix

case class Movie(mid: Int, name: String, descri: String, timelong: String, issue: String,
                 shoot: String,language: String, genres: String, actors: String, directors: String )
// 标准推荐对象，mid,score
case class Recommendation(mid: Int, score:Double)
// 用户推荐
case class ContentMovieRecs(uid: Int, recs: Seq[Recommendation])
case class MongoConfig(uri:String, db:String)

object ContentRecommend {

  // 定义常量
  val MONGODB_MOVIE_COLLECTION = "Movie"
  // 推荐表的名称
  val USER_RECS = "UserRecs"
  val CONTENT_MOVIE_RECS = "ContentMovieRecs"
  val USER_MAX_RECOMMENDATION = 20

  def main(args: Array[String]): Unit = {

    // 定义配置
    val config = Map(
      "spark.cores" -> "local[*]",
      "mongo.uri" -> "mongodb://10.0.0.11:27017/recommender",
      "mongo.db" -> "recommender"
    )
    // 创建 spark session
    val sparkConf = new
        SparkConf().setMaster(config("spark.cores")).setAppName("ContentRecommender")
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()
    implicit val mongoConfig = MongoConfig(config("mongo.uri"),config("mongo.db"))
    import spark.implicits._
    // 加载电影数据集
    val movieTagsDF = spark
      .read
      .option("uri", mongoConfig.uri)
      .option("collection", MONGODB_MOVIE_COLLECTION)
      .format("com.mongodb.spark.sql")
      .load()
      .as[Movie]
      .map(x => (x.mid, x.name, x.genres.map(c => if(c == '|') ' ' else c)))
      .toDF("mid", "name", "genres").cache()

    // 实例化一个分词器，默认按照空格分
    val tokenizer = new Tokenizer().setInputCol("genres").setOutputCol("words")

    val wordsData = tokenizer.transform(movieTagsDF)

    val hashingTF = new HashingTF().setInputCol("words").setOutputCol("rawFeatures").setNumFeatures(20)

    // 用 HashingTF 做处理
    val featurizedData = hashingTF.transform(wordsData)
    // 定义一个 IDF 工具
    val idf = new IDF().setInputCol("rawFeatures").setOutputCol("features")
    // 将词频数据传入，得到 idf 模型（统计文档）
    val idfModel = idf.fit(featurizedData)
    // 用 tf-idf 算法得到新的特征矩阵
    val rescaledData = idfModel.transform(featurizedData)
    // 从计算得到的 rescaledData 中提取特征向量
    val movieFeatures = rescaledData.map{
      case row => ( row.getAs[Int]("mid"), row.getAs[SparseVector]("features").toArray )
    }
      .rdd
      .map(x => {
        (x._1, new DoubleMatrix(x._2) )
      })

    // 2)自连接并且过滤
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
        case (mid, item) => ContentMovieRecs(mid, item.toList.map(x => Recommendation(x._1, x._2)))
      }.toDF()
    // 3) 写入数据库
    movieRecs
      .write
      .option("uri", mongoConfig.uri)
      .option("collection",CONTENT_MOVIE_RECS)
      .mode("overwrite")
      .format("com.mongodb.spark.sql")
      .save()

    spark.close()

  }

  def consinSim(x: DoubleMatrix, y: DoubleMatrix):Double = {
    x.dot(y) / (x.norm2() * y.norm2())
  }
}