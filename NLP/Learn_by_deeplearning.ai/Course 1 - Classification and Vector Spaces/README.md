![45](Images/45.jpeg)
# 借助分类和词向量做自然语言处理

欢迎来到由 [DeepLearning.ai](http://deeplearning.ai/)提供的自然语言处理专项的第一门[课程](https://www.coursera.org/learn/classification-vector-spaces-in-nlp)。这门课由Younes Bensouda Mourri，Łukasz Kaiser和Eddy Shyu讲授。

## 目录

- [借助分类和词向量做自然语言处理](#借助分类和词向量做自然语言处理)
  - [目录](#目录)
- [课程简介](#课程简介)
  
  - [逻辑回归](#逻辑回归)
    - [有监督学习&情感分析](#有监督学习&情感分析)
    - [特征提取](#特征提取)
    - [预处理](#预处理)
    - [训练逻辑回归模型](#训练逻辑回归模型)
    - [测试逻辑回归模型](#测试逻辑回归模型)
    - [损失函数](#损失函数)
  
  - [朴素贝叶斯](#朴素贝叶斯)
  - [条件概率](#条件概率)
    - [贝叶斯法则](#贝叶斯法则)
    - [拉普拉斯平滑](#拉普拉斯平滑处理)
    - [概率比值](#概率比值)
    - [Likelihood times prior](#Likelihood times prior)
    - [Log Likelihood + log prior](#Log Likelihood + log prior)
    - [训练朴素贝叶斯模型](#训练朴素贝叶斯模型)
    - [测试朴素贝叶斯模型](#测试朴素贝叶斯模型)
    - [朴素贝叶斯模型的应用](#朴素贝叶斯模型的应用)
    - [朴素贝叶斯错误的来源](#朴素贝叶斯错误的来源)
  
- [词嵌入](#词嵌入)
  
    - [向量空间模型](#向量空间模型)
    - [统计单词](#统计单词)
    - [统计文本](#统计文本)
    - [欧式距离](#欧式距离)
    - [余弦相似度](#余弦相似度)
    - [向量空间中操作单词](#向量空间中操作单词)
    - [主成分分析](#主成分分析)
    
  - [单词翻译](#单词翻译)
  
    - [搜索文本](#搜索文本)
  
    - [转化单词向量](#转化单词向量)
    - [K-近邻算法](#k-近邻算法)
    - [哈希表和哈希函数](#哈希表和哈希函数)
    - [局部哈希敏感](#局部哈希敏感)
    - [接近最近邻搜索](#接近最近邻搜索)

## 课程简介

第一门课程的内容[简介](https://www.coursera.org/learn/classification-vector-spaces-in-nlp)

> In Course 1 of the Natural Language Processing Specialization, offered by deeplearning.ai, you will:
> a) Perform sentiment analysis of tweets using logistic regression and then naïve Bayes, 
> b) Use vector space models to discover relationships between words and use PCA to reduce the dimensionality of the vector space and visualize those relationships, and
c) Write a simple English to French translation algorithm using pre-computed word embeddings and locality sensitive hashing to relate words via approximate k-nearest neighbor search.

> Please make sure that you’re comfortable programming in Python and have a basic knowledge of machine learning, matrix multiplications, and conditional probability.

> By the end of this Specialization, you will have designed NLP applications that perform question-answering and sentiment analysis, created tools to translate languages and summarize text, and even built a chatbot!

> This Specialization is designed and taught by two experts in NLP, machine learning, and deep learning. Younes Bensouda Mourri is an Instructor of AI at Stanford University who also helped build the Deep Learning Specialization. Łukasz Kaiser is a Staff Research Scientist at Google Brain and the co-author of Tensorflow, the Tensor2Tensor and Trax libraries, and the Transformer paper.


## 逻辑回归
### 有监督学习 & 情感分析
- 在有监督机器学习中，你需要有输入特征$X$以及特征的标签$Y$
- 目标是尽可能地减小损失值
- 将特征$X$输入给$prediction\space function$，将实现特征$X$输出为预测标签$\hat{Y}$
- 当标签$Y$和预测值$\hat{Y}$差异值较小时，可以实现从特征到标签的最佳映射
- 损失函数$Cost$计算$\hat{Y}$和$Y$之间的差异
- 通过计算的损失值来更新参数，迭代重复至损失值为一个较理想的值
   - ![](Images/01.png)
- 逻辑回归的函数为$sigmoid$函数
  - ![](Images/08.png)
- 情感分析的有监督机器学习分类问题例子：
> 目的是辨别$tweet$文本中的语句是积极的还是消极的情感
>
>  - 建立逻辑回归分类器模型，我们分为3个步骤：提取特征， 训练，预测：
>     1. 处理原$tweet$文本为训练数据集并且提取有用的特征
>        - $tweet$文本中带有积极正面情感标记为1，带有消极负面情感标记为0
>     2. 训练逻辑回归分类器模型并减小损失值
>     3. 预测
>  - ![](Images/02.png)

### 特征提取
  1. 稀疏矩阵表示

- 为了将一个文本转化为向量表示，我们需要建立一个词汇表$(Vocabulary)$，然后能够将任何文本或则$tweet$转化为数组矩阵
- 词汇表$V$将会以列表形式存储$tweet$中的不同单词

- 利用稀疏矩阵存储，在$tweet$中出现的单词词汇表$V$将会赋予$1$，而未出现的单词赋予$0$

- ![](Images/03.png)

- 稀疏矩阵存在的问题:

- 逻辑回归模型将会学习$N+1$个参数，$N$是词汇表$V$的大小

- 耗费巨大的训练时间

- 耗费巨大的预测时间
- ![](Images/04.png)

  2. 分别统计消极负面和积极正面的频率

- 从$tweet$语料库中不同的单词建立词汇库$V$
- 建立两个类别，一个类别是消极负面情感，另一个类别是积极正面情感
- 在词汇表$V$中计算积极单词的频率，需要统计它在积极正面的$tweet$文本中出现的次数，计算消极单词的频率一致
   - ![05](Images/05.png)
- 实际上在编码时，此表是一个字典，将单词及其对应的类别映射到频率，例如单词$I$，$(I,PosFreq):3,(I,NegFreq):3$
- 使用字典提取有用的特征以进行情感分析，使用维度$=3$的向量表示$tweet$
   - 即：[偏置$=1$,$tweet$中不同单词的正频率总和, $tweet$中不同单词的负频率总和]
   - ![](Images/06.png)

### 预处理
- 使用词干化$(stemming)$以及停顿词$(stop\space words)$对文本进行预处理
- 首先，我们移除在$tweets$中不会带有重要含义的单词或则符号，例如停顿词，标点符号
- 在某些情况下，不必移除标点符号。因此，需要仔细考虑标点符号是否会为你的NLP任务添加重要信息
- NLP中的词干化只是简单的把单词转化为其基本的单词

  ![](Images/07.png)

### 训练逻辑回归模型
- 训练逻辑回归分类器，迭代至损失值小于阈值时得到的参数$\theta$
- 训练的算法叫梯度下降
  - ![](Images/09.png)
  
### 测试逻辑回归模型
- 你需要$X_{val}$和$Y_{val}$，即验证集数据
1. 首先，利用$\theta$和$X_{val}$，即$pred=h(X_{val},\theta)$,$h=sigmoid$函数
2. 其次，评估$pred$是否大于等于阈值，通常阈值设置为$0.5$
3. 最后在验证集上评估模型的准确率
   - ![](Images/10.png)

### 损失函数
- 变量$m$，代表了训练集中的训练样本数量，表示每次训练样本的总花费
- 该方程有两个相加的项：
  - 左侧$y^{(i)}*log h(x^{(i)},θ)$是逻辑回归函数$log(\hat{Y})$应用到每一次训练样本$y^{(i)}$
  - 如果 $y = 1$$==> $ $L(\hat{Y},1)$ $=$ $ -log(\hat{Y})$ $==>$ 我们想要$\hat{Y}$ 最大$ ==>$ $\hat{Y}$ 最大值为1
  - 如果 $y = 0$$==>$ $L(\hat{Y},0)$ $=$ $-log(1-\hat{Y})$ $==>$ 我们想要 $1-\hat{Y}$ 最大 $==>$ $\hat{Y}$ 尽可能小，因为它只能取$1$
    - ![](Images/11.png)

## 朴素贝叶斯
### 条件概率
- 在$tweet$语料库中的句子可以被标记为正面或负面的情感，其中有些单词有时被标记为正面的，有时别标记为负面的
- ![](Images/12.png)
- 假如$A\space tweet$被标记为正样本，$A$的概率为$P$，计算概率$P$的方法为正样本$tweet$的数目与语料库中所有的$tweet$样本的比值
- 将概率视为事件发生的频率
- $tweets$表达负面情绪的概率等于$1$减去正面情绪的概率
- ![](Images/13.png)
- 条件概率是指$B$发生条件下$A$发生的概率，即$P(A|B)$
### 贝叶斯法则
- 贝叶斯法则是指$$Y$$发生条件下$X$发生的概率，即$P(X|Y)$，等价于$X$发生条件下$Y$发生的概率乘以$$X$$和$$Y$$概率的比值，即$P(Y|X)\times \frac{P(X)}{P(Y)}$
- ![](Images/14.png)
-  朴素贝叶斯的第一步是计算每个单词的条件概率，即$P(w_i|class)$
-  ![](Images/15.png)
### 拉普拉斯平滑
- 拉普拉斯平滑处理，一种用于避免出现概率为$0$的技巧
- ![](Images/16.png)
- 在上表应用此公式，概率总仍然为$1$，并且不会出现概率为$0$的情况
- ![](Images/44.png)
### 概率比值
- 基于上一个表，概率比值被定义为正样本的条件概率除以负样本的条件概率，即$ratio(w_i)=\frac{P(w_i|Pos)}{P(w_i|Neg)}$
- ![](Images/17.png)
### Likelihood times prior
- Likelihood times prior 
- ![](Images/18.png)
### Log Likelihood + log prior
- 为了避免数值下溢（因为我们连乘了许多小数），我们转化为对数似然，即将乘法转化为加法
- ![](Images/19.png)
- 如果对数似然+对数先验大于$0$，那么$tweet$带有积极含义，否则带有消极含义
### 训练朴素贝叶斯模型
- 训练朴素贝叶斯模型，我们需要做以下几件事情：
    1. 得到带有标记的正面和负面样本的$tweet$训练集
    2. 预处理训练集
       1. 字母小写化
       2. 去除标点符号，网页链接，姓名
       3. 去除停顿词
       4. 词干化处理：将单词处理为词根形式
       5. 向量化语句：将每个句子拆分为不重复的单词
    3. 计算词汇表中每个单词和每个类别的频率：$frew(w,class)$
    4. 使用拉普拉斯平滑处理计算给定类别单词的频率，$ P(w|pos),P(w|neg)$
    5. Compute$\lambda(w)$, log of the ratio of your conditional probabilities
    6. 计算$$\lambda(w)$$，对数条件概率比值，即$log\frac{P(w_i|Pos)}{P(w_i|Neg)}$
    7. 计算$ logprior=log(\frac{P(Pos)}{P(Neg)})$
### 测试朴素贝叶斯模型
- 为了测试经过训练的贝叶斯模型，我们需要条件概率并且使用它们来预测未训练过的$tweets$的情感
- 为了评估模型，我们将使用标注情感的$tweets$的测试集
- 给出测试集$X_{val}$和$Y_{val}$，我们评估分数，即$X_{val},\lambda,logprior$的函数，预测为$pred=$分数
- 准确率为：
  - ![](Images/20.png)
- 在训练集中没有训练过的单词被认为是中性，因此在分数上加上0
### 朴素贝叶斯模型的应用
- 朴素贝叶斯有许多应用
  - 情感分析
    - ![](Images/21.png)
  - 作者识别
    - ![](Images/22.png)
  - 垃圾邮件过滤
    - ![](Images/23.png)
  - 信息检索
    - ![](Images/24.png)
  - 单词歧义
    - 例如，我们无法确定银行**(bank)**是指河流**(river)**还是金融机构**(financial institution)**，计算比率
    - ![](Images/25.png)
- 朴素贝叶斯的假设：
  - 条件独立 : 在$NLP$中不成立
  - 训练集类别的相对频率影响着模型并且不能代表现实中的分布
### 朴素贝叶斯错误的来源
- 朴素贝叶斯错误可能发生在:
  - 预处理
    - 移除标点符号（例如  ':(' ）
    - 移除停顿词
  - 单词顺序（不是句子中的单词顺序）
    - 例如 : I am happy because I did not go 和 I am not happy because I did go
  - 反义的话语 (容易被人理解但是算法不行...)
    - 讽刺，讽刺，委婉语等
    - 例如: This is a ridiculously powerful movie. The plot was gripping and I cried right through until the ending

## 词嵌入
### 向量空间模型
- Vector space models repesent words and documents as Vectors that captures relative meaning by identifying the context around
each word in the text
- 向量空间模型将单词和文本存储为向量，通过识别周围的单词来获取相对的含义
- 向量空间模型的主要用途是识别相似度和独立性
  - ![](Images/26.png)
- 基本概念： "You shall know a word by the company it keeps." Firth,1957
- 应用包括:
  - 信息提取
  - 机器翻译
  - 聊天机器人
### 统计单词
- 计算在一定距离$k$内两个单词出现的次数
  - ![](Images/27.png)
### 统计文本
- 计算单词在某种类别中出现的次数
  - ![](Images/28.png)
### 欧式距离
- 欧氏距离是点之间的直线长度
- 对于两个向量，公式在$Python$中是：
  - ![](Images/29.png)
- 当语料库大小不同时，欧氏距离可能会产生误差
### 余弦相似度
- 对于两个向量$v,w$，它们的夹角$\beta$的余弦由下面公式给出：
  - ![](Images/30.png)
- 余弦相似度的值介于0到1之间
### 向量空间中操作单词
- 如果向量空间代表某种含义，那么我们可以进行如下操作
- $USA$的首都是$Washington$，我们想要得到$Russia$的首都，即在向量空间中我们可以这样计算：
  - ![](Images/31.png) 
### 主成分分析
-  可视化单词向量空间，可以将向量空间（在较高维度中）转换为2或3维空间，以便可以查看单词之间的关系
- $PCA$在于将向量投影至低维度空间，并且尽可能的保留信息
-  $PCA$算法 :
  - 特征向量给出不相关特征的方向
  - 特征值给出每个特征保留的信息，即新特征的方差：
    1. 均值归一化数据，即每个特征进行归一化
    2. 计算协方差矩阵
    3. 执行奇异值分解，得到3个矩阵即$V、\sigma、U$
    4. 点积运算将不相关的特征数据，投影至$$k$$维度
       - ![](Images/32.png) 
    5. 计算保留方差的百分比：
       - ![33](Images/33.png)![](Images/33.png) 
  - 特征值应按降序排序

## 单词翻译
### 搜索文本
- 文本可以被表示为与单词具有相同维度的向量矩阵
- 单词向量矩阵相加由下图所示
  - ![43](Images/43.png)![](Images/43.png)
### 转化单词向量
- 为了将一种语言的单词向量$X$转化为另一种单词向量的$Y$，我们建立$R$矩阵，例如：
  - ![](Images/34.png) 
- 为了求解$R$，我们利用梯度下降：
  - ![](Images/35.png)
### K-近邻算法
- 使用$R$将$X$翻译为$Y$，$XR$不对应$Y$中的任何特别的向量
- $KNN$可以搜寻$XR$矩阵中最邻近的$K$个值
- 在整个空间中搜寻会十分缓慢，使用哈希表可以大幅度减少搜索的空间
- 哈希表可能会忽略一些邻近计算出的向量的值
### 哈希表和哈希函数
- 哈希函数将对象映射到哈希表中的桶中
  - ![](Images/36.png)
- 一个简单的哈希函数：哈希值 = 向量 % 哈希表中桶的个数
  - ![](Images/37.png)  
- 这个哈希函数不会将相似的对象存储于用一个桶中 ⇒ 局部哈希敏感
### 局部哈希敏感
- 使用超平面分割空间
  - ![](Images/38.png)
- 对于每个向量$v$，在超平面$h_i$上与法线向量进行$sign$运算
- $sign$运算决定了向量在哪一个方向
- 如果点积为正，$h_i=1$，否则$h_i=0$
  - ![](Images/39.png)
- 哈希值为：
  - ![](Images/40.png)
### 接近最近邻搜索
- 使用多组随机平面划分空间
  - ![](Images/41.png)
- 每个划分将在计算向量的相同存储桶中给出（可能）不同向量
- 那些不同的向量（来自多个划分）是成为$k$个最近邻居的良好候选者
  - ![](Images/42.png)
- 接近最近邻搜索不是最好的但是比单纯的搜索要快得多

