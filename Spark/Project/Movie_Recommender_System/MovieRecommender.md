# 项目

- 基于Spark的电影推荐系统
- **Course supported by：** [gulixueyuan](http://www.gulixueyuan.com/goods/show/208?targetId=314&preview=0)

# 搭建流程

- 由于 github 阅读性太差 ，以下步骤如果需要参考均转移到本人的 [语雀]() 进行阅读

## 阶段1：项目体系架构概述

## 阶段2：基础环境搭建

- 虚拟机： virtualbox
- 系统：centos7
- 工具：Finalshell
- 主机：macOS Big Sur

配置Mongodb

配置redis

配置ElasticSearch

配置Azkaban 

配置Zookeeper

配置Spark

配置Kafka

配置Flume

## 阶段3：数据加载服务

- 将数据加载到MongoDB
- 将数据加载到Elasticsearch

## 阶段4：推荐系统建设

- ### 离线统计推荐算法

  - 根据已经存储与 MongoDB 中的 movie 和 rating 数据集，统计历史热门电影、最近热门电影、电影的平均评分、电影每种类别中的 top10 电影

  - ```
    数据集格式:
    
    movies 数据集:
    mid 电影ID
    name 电影名称
    descri 电影详情描述
    timelong 电影时长
    issue 电影发行日期
    shoot 电影拍摄日期
    language 电影语言
    genres 电影类型
    actors 电影演员表
    directors 电影导演
    
    rating 数据集:
    uid 用户ID
    mid 电影ID
    score 用户对于电影的评分
    timestamp 用户对于电影的评分的时间
    ```

  - 实现：spark-sql (参考 OfflineRecommender 模块)

  #### 构建的目标：

  - 历史热门电影

    - 定义：根据所有历史评分数据，计算历史评分次数最多的电影

  - 最近热门电影

    - 定义：根据评分，按月为单位计算最近时间的月份里面评分数最多的电影集合

  - 电影的平均评分

    - 定义：根据历史数据中所有用户对电影的评分，周期性的计算每个电影的平均得分

  - Top10：

    - 定义：根据提供的所有电影类别，分别计算每种类型的电影集合中评分最高的 10 个电

      影 

- ### 离散推荐算法构建

  - 协同过滤 ALS 算法
  
- ### 实时推荐算法构建

  - 



## 阶段5：实时联调







