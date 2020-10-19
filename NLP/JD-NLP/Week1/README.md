# Lecture

## 目录

- [NLP领域顶会](#NLP领域顶会)
- [自然语言处理中的基础任务](#自然语言处理中的基础任务)
  - [分词$(Word\space Segmentation)$](#分词)
  - [词性标注$(POS\space Tagging)$](#词性标注)
- 



## NLP领域顶会

- 自然语言处理领域
  - [ACL](https://acl2020.org/)  ⭐
  - [EMNLP](https://2020.emnlp.org/) ⭐
  - [NAACL](https://naacl.org/) ⭐
  - [COLING]()
- 机器学习领域
  - [ICML](https://icml.cc/) ⭐
  - [NIPS](https://neu rips.cc/) ⭐
  - [UAI](http://www.auai.org/uai2020/) ⭐
  - [AISTATS](https://www.myhuiban.com/conference/2655) ⭐
- 深度学习
  - [ICLR](https://iclr.cc/Conferences/2021/CallForPapers) ⭐
- 数据挖掘领域
  - [KDD](https://www.kdd.org/kdd2020/)  ⭐
  - [WSDM](http://www.wsdm-conference.org/2021/) ⭐
  - [SDM](https://www.baidu.com/link?url=Lf7PFGzKR3U5LVcY-dRtoSqeLh3qPoTdx9du6Jwx5sr2qIqehbewshZW50vC08di&wd=&eqid=dcf33c710004df90000000035f8d410c)⭐
- 人工智能领域
  - [IJCAI](https://www.ijcai.org/) ⭐
  - [AAAI](https://aaai.org/Conferences/AAAI-20/) ⭐



## 自然语言处理中的基础任务

- ##### 分词

  - 最大匹配分词算法

    - $demo:$ 前向最大匹配 $(forward-max\space matching)$

    - ```
      例子：我们经常有意见分歧
      词典：["我们","经常","有","有意见","意见","分歧"]
      
      假设：max_len=5
      
      1.
      我们经常有	×
      我们经常	×
      我们经		×
      我们		✔
      我们|经常有意见分歧
      2.
      经常有意见	×
      经常有意	×
      经常有		×
      经常		✔
      我们|经常|有意见分歧
      3.
      有意见分歧	×
      有意见分	×
      有意见		✔
      我们|经常|有意见|分歧
      4.
      分歧	✔
      我们|经常|有意见|分歧|
      
      最终分词的结果：我们|经常|有意见|分歧|
      ```

    - $demo：$ 后向最大匹配 $(backward-max\space matching)$

    - 问题：

      1. 贪心策略 $->$ 局部最优解
      2. $OOV(Out-of-Vocabulary)$
      3. $Semantic$ ×

  - 分词算法

    - [$Jieba$](https://github.com/fxsjy/jieba)
    - [$SnowNLP$](https://github.com/isnowfy/snownlp)
    - [$LTP$](http://www.ltp-cloud.com/)
    - [$HanNLP$](https://github.com/hankcs/HanLP)

- ##### 词性标注

  - $Sequence\space Labeling$
    - 每个单词独立地去做分类
    - 对于当前单词以及上下文单词 $(sliding\space window)$ 提取特征，并用这些特征去做分类

- ##### 命名实体识别 $(NER)$

- ##### 句法分析 $(Syntatic\space Analysis)$

- ##### 语义分析 $(Semantic\space Analysis)$

