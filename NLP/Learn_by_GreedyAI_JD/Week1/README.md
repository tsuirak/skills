# Lecture

## 目录

- [NLP领域顶会](#NLP领域顶会)
- [自然语言处理中的基础任务](#自然语言处理中的基础任务)
  - [分词$(Word\space Segmentation)$](#分词)
  - [词性标注$(POS\space Tagging)$](#词性标注)
  - [命名实体识别$(NER)$](#命名实体识别 $(NER)$)
  - [句法分析 $(Syntatic\space Analysis)$](#句法分析 $(Syntatic\space Analysis)$)
  - [语义分析 $(Semantic\space Analysis)$](#语义分析 $(Semantic\space Analysis)$)
- [常见的应用](#常见的应用)
- [如何成为优秀的NLP人才](#如何成为优秀的NLP人才)



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

- ##### 分词$(word\space segmentation)$

  - **DEMO**：最大匹配分词算法

    - 前向最大匹配 $(forward-max\space matching)$

    - ```
      例子：我们经常有意见分歧
      词典：["我们","经常","有","有意见","意见","分歧"]
      
      假设：max_len=5
      
      步骤：
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

    - 后向最大匹配 $(backward-max\space matching)$

    - 问题：

      1. 贪心策略 $\Rightarrow$ 局部最优解
      2. $OOV(Out-of-Vocabulary)$
      3. $Semantic$ ×

  - 分词算法

    - [$Jieba$](https://github.com/fxsjy/jieba)
    - [$SnowNLP$](https://github.com/isnowfy/snownlp)
    - [$LTP$](http://www.ltp-cloud.com/)
    - [$HanNLP$](https://github.com/hankcs/HanLP)

  - $Solved\space Problem$

- ##### 词性标注$(POS\space Tagging)$

  - $ Sequence\space Labeling$ 当作分类问题
    - 每个单词独立地去做分类
    - 对于当前单词以及上下文单词 $(sliding\space window)$ 提取特征，并用这些特征去做分类
  - $ Sequence\space Labeling$ 当作分类问题
    - 利用概率来表示序列
    - 考虑单词之间的前后依赖关系
    - 常见的算法：
      - 隐马尔可夫模型$(Hidden\space Markov\space Model)$
      - 条件随机场$(Conditional\space Random\space Fields)$
  - $Solved\space Problem$

- ##### 命名实体识别 $(NER)$

  - 类似词性标注，也可以看作是序列标注的问题

- ##### 句法分析 $(Syntatic\space Analysis)$

  - 对于一个句子的语法做分词，比如主谓宾

  - ```
    i.e.,他喜欢读书
    
    				  S
    			/				\
    		/						VP
    	/					/ \			\
    他/P			喜欢/V 读/V	书/N
    ```

    

- ##### 语义分析 $(Semantic\space Analysis)$

  - 主要有两个问题
    - 如何理解一个单词的意思？
    - 如何理解一个文本的意思？
  - 主要技术：
    - SkipGram，CBOW，Glove，ELMo，BERT，ALBERT
    - XLNet，GPT-2，GPT-3，Tiny-BERT

## 常见的应用

- 写作助手$(Spell\space Correction)$
- 文本分类
  - 情感分析$(Sentiment\space Analysis)$
  - 情绪分析$(Emotion\space Analysis)$
  - 主题分类$(Topic\space Classification)$
- 信息检索$(Information\space Retrieval)$
- 问答系统$(QA)$
- 自动生成文本摘要$(Test\space Summary)$
  - $Extractive\space Method$
  - $Abstractive\space Method$
- 机器翻译
  - $Relu-based\space Method$
  - $Statistical\space Method$
- 信息抽取

## 如何成为优秀的NLP人才

1. 扎实的数学基础、统计基础、数据结构与算法
2. 重视机器学习，理解核心的细节
3. 自然语言相关技术
4. 编程
5. 读论文、复现论文！
6. 搜索能力、检索能力



# Paper

##  目录

- [论文检索](#论文检索)
- [选择文献](#选择文献)
- [走进一个领域](#走进一个领域)
- [阅读文献](#阅读文献)



## 论文检索

### 英文

- Google学术(跟踪作者和论文)
- DBLP(整理会议、作者的全部工作)
- 微软学术(引用趋势)

### 中文

- 知网
- 万方
- 维普

## 选择文献

- 面向特定主题的文献选择：Google Scholar
- 面向某个领域的学习：
  - CNKI中搜索"课题名称+综述"或在Google Scholar中搜索"课程名称+survey/review/tutorial"
  - 相关领域会议/期刊tutorial
- 面向知识更新的文献选择
  - arXiv.org上定期发布的论文(较新)
  - 相关国际顶级会议每年的论文集(ACL、EMNLP、COLING、NAACL)
  - 相关国际顶级期刊定期发表的论文
    - TACL
    - Computational Linguistics
  - 国际顶尖高校研究组或企业研究机构发布的新闻或学术报告
  - 科技媒体和社交媒体集中报道或讨论的学术成果
    - 机器之心、雷锋网、AI科技评论、PaperWeekly、DeepTech、新智元
- 引用次数
- 会议或期刊排名：参考CCF列表
- 大牛作者或顶尖研究机构

## 走进一个领域

关键词、关键技术、重要论文列表、领域划分、领域大牛

- (相对成熟的领域)综述和优秀的学位论文
- 领域扩充
  - 关键词关联
  - 参考文献关联
- 会议扫墙，保持对领域的清晰认知
  - 技巧：abstract和introduction

## 阅读文献

### 建议的阅读顺序

- 题目
- 摘要(abstract)
- 导论(introduction)
- 实验结果(results)
- 本文工作(method)
- 相关的工作(related work)
- 结论(conclusion)
- 附录(appendix)

### 快速阅读

- 摘要(Abstrct)
- 导论(Introduction)
- Problem-Driven(对前人工作的优化)
- 图文浏览

### 精读

- 理解论文基本原理：motivation，所用的理论
- 理解论文详细内容：深入理解论文细节，包括定义、假设和相关的公式等
- 重现实验：是否能够重现实验，得到相同的结果
- 组织讨论：与其他人进行讨论，是否存在疏忽或者理解不彻底的地方
- 设计更好的方案：是否能更进一步，设计更好的方法
- 多次阅读
- 批判创造
- Experiment is Important & Code is Everything
- 实验部分很重要(作者对于网络结构、参数的设置为复现论文提供了重要的素材)

