![62](Images/62.jpeg)

# 借助概率模型做自然语言处理

欢迎来到由 [DeepLearning.ai](http://deeplearning.ai/)提供的自然语言处理专项的第二门[课程](https://www.coursera.org/learn/probabilistic-models-in-nlp)。这门课由Younes Bensouda Mourri，Łukasz Kaiser和Eddy Shyu讲授。

## 目录
- [借助概率模型做自然语言处理](#借助概率模型做自然语言处理)
  - [目录](#目录)
  - [课程简介](#课程简介)
  - [自修正和动态规划算法](#自修正和动态规划算法)
    - [自修正](#自修正)
    - [建立模型](#建立模型)
    - [最小编辑距离](#最小编辑距离)
    - [最小编辑距离算法](#最小编辑距离算法)
  - [词性标注和隐马尔可夫模型](#词性标注和隐马尔可夫模型)
    - [词性标注](#词性标注)
    - [马尔可夫链](#马尔可夫链)
    - [马尔可夫链和词性](#马尔可夫链和词性)
    - [隐马尔可夫模型](#隐马尔可夫模型)
    - [转移矩阵](#转移矩阵)
    - [发射矩阵](#发射矩阵)
    - [维特比算法](#维特比算法)
      - [初始化](#初始化)
      - [前向传播](#前向传播)
      - [反向传播](#反向传播)
  - [自修正和语言模型](#自修正和语言模型)
    - [N-Grams](#n-grams)
    - [N-grams和概率](#N-grams和概率)
    - [序列概率](#序列概率)
    - [句子开始和结束的符号](#句子开始和结束的符号)
    - [N-gram语言模型](#N-gram语言模型)
    - [语言模型的评估](#语言模型的评估)
    - [处理词汇表外的单词](#处理词汇表外的单词)
    - [平滑处理](#平滑处理)
  - [神经网络中的词嵌入](#神经网络中的词嵌入)
    - [单词表示的基本方法](#单词表示的基本方法)
    - [词嵌入](#词嵌入)
    - [词嵌入方法](#词嵌入方法)
    - [CBOW模型](#CBOW模型)
    - [文本预处理](#文本预处理)
    - [将单词转化为向量](#将单词转化为向量)
    - [CBOW模型的结构](#CBOW模型的结构)
    - [CBOW模型的维度](#CBOW模型的维度)
    - [激活函数](#激活函数)
    - [损失函数](#损失函数)
    - [前向传播](#前向传播)
      - [反向传播和梯度下降](#反向传播和梯度下降)
      - [词嵌入向量的特征提取](#词嵌入向量的特征提取)
    - [词嵌入模型的评估](#词嵌入模型的评估)
      - [内在评估](#内在评估)
      - [外在评估](#外在评估)

## 课程简介
第二门课程的内容[简介](https://www.coursera.org/learn/probabilistic-models-in-nlp)

> In Course 2 of the Natural Language Processing Specialization, offered by deeplearning.ai, you will:
>
> Create a simple auto-correct algorithm using minimum edit distance and dynamic programming,
> Apply the Viterbi Algorithm for part-of-speech (POS) tagging, which is important for computational linguistics,
> Write a better auto-complete algorithm using an N-gram language model, and 
> Write your own Word2Vec model that uses a neural network to compute word embeddings using a continuous bag-of-words model.

> Please make sure that you’re comfortable programming in Python and have a basic knowledge of machine learning, matrix multiplications, and conditional probability.

> By the end of this Specialization, you will have designed NLP applications that perform question-answering and sentiment analysis, created tools to translate languages and summarize text, and even built a chatbot!

> This Specialization is designed and taught by two experts in NLP, machine learning, and deep learning. Younes Bensouda Mourri is an Instructor of AI at Stanford University who also helped build the Deep Learning Specialization. Łukasz Kaiser is a Staff Research Scientist at Google Brain and the co-author of Tensorflow, the Tensor2Tensor and Trax libraries, and the Transformer paper.

## 自修正和动态规划算法
### 自修正
- 自修正指一种将拼写错误的单词纠正为正确形式的应用。
  - 例如：Happy birthday *deah* friend! ==> dear
- 工作流程:
  1. 确定错误拼写的单词
  2. 编辑距离算法：计算单词$1$转化为单词$2$的最小编辑距离
  3. 筛选编辑候选列表
  4. 计算单词的概率
### 建立模型
- 确定错误拼写的单词
  - 如果该单词未被存储在词汇表$V$中即视为错误拼写
- 计算单词$1$转化为单词$2$的最小编辑距离
  - 编辑距离
    - 计算字符串$a$转换为字符串$b$的最少单字符编辑次数
      - 插入 (增加一个字母)
         - 在当前单词任意部分增加一个字母: to ==> top,two,...
      -  删除 (删除一个字母)
         - 在当前单词任意部分删除一个字母 : hat ==> ha, at, ht
      -  交换 (交换两个邻近的字母)
         - 例如: eta=> eat,tea
      -  替换 (将一个字母转换为另外一个任意的字母)
         - 例如: jaw ==> jar,paw,saw,...
 -  通过组合这$4$种编辑操作，我们获得了所有可能的编辑列表
    - ![](Images/1.png)
- 筛选编辑候选列表: 
  - 从编辑列表中，仅考虑真实和正确拼写的单词
  - 如果编辑列表中的单词不存在于词汇表$V$中==>将其从候选编辑列表中删除
    - ![](Images/2.png)
- 计算单词概率：候选单词是概率最高的单词
  - 语料库中的单词概率为：单词出现的次数除以单词总数。
    - ![](Images/3.png) 
### 最小编辑距离
- 评估两个单词之间的相似度
- 计算一个字符转换为另一个字符所需的最少编辑次数
- 该算法最小化编辑成本
  - ![](Images/4.png)
- 应用:
  -  拼写校正
  -  文本相似度
  -  机器翻译
  -  DNA测序
  -  ...
### 最小编辑距离算法
- 源单词位于矩阵的列
- 目标单词位于矩阵的行
- 每个单词开头的空字符设为$0$
- $D [i,j]$是指源单词空字符到$i$与目标单词空字符到$j$之间的最小编辑距离
  - ![](Images/5.png)
- 要填写表格的其余部分，我们可以使用以下公式化的方法：
  - ![](Images/6.png)
## 词性标注和隐马尔可夫模型
### 词性标注
- 词性标注指的是单词或词汇术语的类别
  - 标签: 名次, 动词, 形容词, 介词, 副词,...
  - 例句示例: why not learn something ?
    - ![](Images/7.png)
- 应用:
  - 命名实体识别
  - 指代消歧
  - 语音识别
### 马尔可夫链
- 马尔可夫链可以被描述为有向图
  - 图形是一种数据结构，由一组由线连接的圆可视化表示
- 图的圆圈代表模型的状态
- 从状态$s1$到$s2$的箭头表示转移概率，即从s1到s2的可能性
  - ![](Images/8.png) 
### 马尔代夫链和词性

- 想象这样一个句子，由一组单词序列组成，并且标注有词性
  -  我们可以用图来表示这样一个序列
  - 其中某个词性定义为一个事件，可以通过模型图的状态来判断事件发生的可能性
  - 状态之间箭头上的权重定义了从一个状态到另一状态的概率
  - 即词性转化的概率由下图可以得知
    - ![](Images/9.png)  
- 下一个事件的概率仅取决于当前事件
- 模型图可以定义为形状为$(n + 1 \times n)$的转移矩阵
  - 当没有先前状态时，我们引入初始状态$\pi$。
  - 一个状态的所有转移概率的总和应始终为$1$。
    - ![](Images/10.png)   
### 隐马尔可夫模型
- 隐马尔可夫模型意味着状态是隐藏的或无法直接观察到的
- 隐马尔可夫模型具有维度$(N + 1,N)$转移概率矩阵A，其中N是隐藏状态的数量
- 隐马尔可夫模型具有发射概率矩阵$B$，描述了从隐藏状态到可观察值的转换（语料库的单词）
  - 隐藏状态的发射概率行总和为1
  - ![](Images/11.png)
### 转移矩阵
- 转移矩阵是存储隐马尔可夫模型状态之间的所有转移概率
- $C(t_{i-1},t_i)$是计算训练语料库中所有词性的出现次数
- $C(t_{i-1},t_j)$是计算词性$t_{i-1}$的出现次数
  - ![](Images/12.png)
- ![](Images/13.png)
- 为了避免被零除并且转换矩阵中的很多实例为$0$，我们对概率公式应用平滑
  - ![](Images/14.png)
### 发射矩阵
- 计算特定单词和它对应的词性的次数
  - ![](Images/15.png)
### 维特比算法
- 维特比算法实际上是图算法
- 目的是找到隐藏单元的序列或词性标注中最高概率的序列
  - ![](Images/16.png)
- 该算法可以分为三个主要步骤：初始化，前向传播和反向传播
- 辅助矩阵$C$和$D$
  - 矩阵$C$拥有中间最优概率
  - matrix$D$ holds the indices of the visited states as we are traversing the model graph to find the most likely sequence of parts of speech tags for the given sequence of words, $W_1$all the way to $W_k$
  - $C$和$D$矩阵有$n$行（词性标注的数量）和$k$个列（序列中的单词数量）
#### 初始化
- 矩阵$C$的初始化表明每个单词属于某个词性的概率
  - ![](Images/17.png)
- in D matrix, we store the labels that represent the different states we are traversing when finding the most likely sequence of parts of speech tags for the given sequence of words W<sub>1</sub> all the way to W<sub>k</sub>.
- 在矩阵$D$中，我们存储能够代表不同单元
#### 前向传播
- 对于矩阵$C$，每个单元通过以下公式计算：
  - ![](Images/18.png)
- 对于矩阵$D$，保存$k$，这将最大化$c_{i,j}$中的单元
  - ![](Images/19.png)
####  反向传播
- 反向传播将提取出序列中的单词最有可能表达的词性
- 首先，计算矩阵$C$中最后一列单元中最高概率$C_{i,k}$的索引
  - 表示当我们观察单词$w_i$时所经过的最后一个隐藏状态
- 使用此索引回溯到矩阵$D$以重构词性标注的序列
- 将许多非常小的数字相乘，例如概率，会导致数值溢出问题
   - 使用对数概率代替，转化成数字相加而不是相乘。
   - ![](Images/20.png)
## 自修正和语言模型
### N-Grams
- 语言模型是一种计算句子概率的工具。
- 语言模型可以根据给定历史的单词来估计下一个单词的概率
- 应用语言模型对给定句子自修正后，然后输出对句子的建议
- 应用:
  - 语音识别
  - 拼写校正
### N-grams和概率
- N-gram是一个单词序列。 N-gram也可以是字符或其他元素
- ![](Images/21.png)
- 序列符号
  - $m$代表语料库的长度
  - $W_i^j$ 是指文本语料库中从索引$i$到$j$的单词序列
- $Uni-Gram$ 概率
  - ![](Images/22.png)
- $Bi-gram$ 概率
  - ![](Images/23.png)
- $N-gram$ probability
  - ![](Images/24.png)
### 序列概率
- giving a sentence the Teacher drinks tea, the sentence probablity can be represented as based on conditional probability and chain rule: 
- 给定一个句子['Teacher drinks tea']，根据条件概率和连锁规则将句子的概率表示为：
  - ![](Images/25.png)
- 这种直接的方法对序列概率有其局限性，句子的较长部分不太可能出现在训练语料库中
  - $P(tea|the\space teacher\space drinks)$
  - 由于它们都不可能出现在训练语料库中，因此它们的计数为0
  - 在这种情况下，整个句子的概率公式无法给出概率估计
- 序列概率的近似
  - 马尔可夫假设：只有最后$N$个单词重要
  - $Bigram$ $P(w_n| w_{1}^{n-1}) ≈ P(w_n| w_{n-1}) $
  - $Ngram$ $ P(w_n| w_{1}^{n-1}) ≈ P(w_n| w_{n-N+1}^n-1) $
  - 用$Bigram$建模整个句子：
      - ![](Images/26.png) 
### 句子开始和结束的符号
- 句子开头符号：<s>
- 句子结尾符号：</s>
### N-gram语言模型
- 计数矩阵存储$n-grams$的出现次数
  
- - ![](Images/27.png)
- 计数矩阵转换为概率矩阵，存储$n-grams$条件概率
  - ![](Images/28.png)
- 将概率矩阵与语言模型关联
  - ![](Images/29.png)
- 将许多概率相乘会带来数字下溢的风险，请使用乘积的对数代替将项的乘积转化为项的总和
### 语言模型的评估
- 为了评估语言模型，将语料库分为训练$(80％)$，验证$(10％)$和测试$(10％)$集。
- 划分方式可以是：连续文本划分或随机文本划分
  - ![](Images/30.png)
- Evaluate the language models using the perplexity metric
- 使用困惑度指标评估语言模型
  - ![](Images/31.png)
  - 困惑度越小，模型越好
  - 字符级别模型$PP$低于基于单词的模型$PP$
  - $bi-gram$模型的困惑度
    - ![](Images/32.png)
  - 对数$PP$
    - ![](Images/33.png)
### 词汇表外的单词
- 未知单词是词汇表$V$中不存在的单词
  - 词汇表$V$外的单词用特殊字符**UNK**代替
  - 在语料库中但不在词汇表中的单词将由**UNK**代替
### 平滑处理
- 当我们在有限的语料库上训练$n-gram$时，某些单词的概率可能会出现问题
  - 当训练语料库中缺少由已知单词组成的$N-gram$时，会出现这种情况
  - 它们的数量不能用于概率估计
  - ![](Images/34.png)
- 拉普拉斯平滑或$Add-1$平滑
  - ![](Images/35.png)
- $Add-k$平滑
  - ![](Images/36.png)
## 神经网络中的词嵌入
### 单词表示的基本方法
- 将单词转化为数字的最简单方法是给词汇表$V$中的每个单词分配一个唯一的整数
  - ![](Images/38.png) 
  - 尽管它是简单的表示形式，但却没有什么具体意义
- 独热编码表示
  - ![](Images/39.png)
  - 尽管它是简单的表示形式并且和顺序无关，但对于计算而言可能是巨大的，并且每个单词之间没有潜入的含义
### 词嵌入
- 词嵌入在相对较小维度中带有含义的向量
  - ![](Images/40.png) 
- 为了建立词嵌入，需要语料库和嵌入的方法
  - ![](Images/41.png)
### 词嵌入方法

- $Word2vec$：最初普及了机器学习的使用，以生成词嵌入
  - Word2vec使用浅层神经网络来学习单词嵌入
  - 它提出了两种模型架构
    2. $CBOW$模型根据周围的单词预测缺失的单词
    2. $skip-gram$模型和$CBOW$模型相反，是通过学习输入单词周围的单词
- $GloVe$：涉及分解语料库单词共现矩阵的对数，类似于计数器矩阵
- $fastText$：基于$skip-gram$模型，并通过将单词表示为$n-gram$考虑单词的结构
- 生成单词嵌入的高级模型的其他示例：$BERT,GPT-2,ELMo$
### CBOW模型
- $CBOW$是基于机器学习的嵌入方法，根据周围的单词来预测缺失的单词
- 两个单词出现在各种句子中时，经常都被一组相似的词所包围--->这两个词往往在语义上相关
-  要为预测任务创建训练数据，我们需要上下文的单词和目标中心单词，示例
   - ![](Images/42.png)
   - 通过滑动窗口，您可以创建下一个训练示例和目标中心词
   - ![](Images/43.png)
### 文本预处理
- 我们应该认为语料库的单词不区分大小写 比如：$The==THE==the$
- 标点：？ 。 ，！ 和其他字符作为词汇表$V$中的一个特殊字符
- 数字：如果数字在用例中不重要，我们可以删除或保留它们（爷可以用特殊令牌<NUMBER>替换它们）
- 特殊字符：数学符号/货币符号，段落符号
- 特殊字词：表情符号，标签
### 将单词转化为向量
- 通过将中心词和上下文词转换为独热向量
- 最终准备好的训练集是：
  - ![](Images/44.png)
### CBOW模型的结构
- $CBOW$模型基于具有输入层，隐藏层和输出层的浅层密集神经网络
- ![](Images/45.png)
### CBOW模型维度
- ![](Images/46.png)
- ![](Images/47.png)
### 激活函数
- ![](Images/48.png)
- ![](Images/49.png)
### 损失函数
- 学习过程的目标是使用交叉熵损失找到在给定训练数据集的情况下将损失最小化的参数
- ![](Images/50.png)
- ![](Images/51.png)
### 前向传播
- ![](Images/52.png)
- ![](Images/53.png)

#### 反向传播和梯度下降
- 反向传播计算权重和偏差的偏导数
  - ![](Images/54.png)
- 梯度下降更新权重和偏差
  - ![](Images/55.png)
#### 词嵌入向量的特征提取

- 训练完神经网络后，我们可以提取出三种替代的词嵌入表示
  
  1. consider each column of W_1 as the column vector embedding vector of a word of the vocabulary
  2. 将W_1的每一列视为词汇表单词的列向量嵌入向量
    - ![](Images/56.png)
  2. use each row of W_2 as the word embedding row vector for the corresponding word. 
  3. 使用W_2的每一行作为相应单词的单词嵌入行向量。
    - ![](Images/57.png) 
  3. average W_1 and the transpose of W_2 to obtain W_3, a new n by v matrix. 
    - ![](Images/58.png) 
### 词嵌入模型的评估
#### 内在评估
- 内部评估方法评估词嵌入在本质上如何捕获单词之间的语义（含义）或句法（语法）关系
- 在语义类上测试
  - ![](Images/59.png)
- 使用聚类算法，在词向量空间中将相似的单词归为一类
  - ![](Images/60.png) 
  - ![](Images/61.png)
#### 外在评估
- 即命名实体识别，词性标注
- 使用一些选定的评估指标（例如准确性或F1分数）在测试集中评估此分类器
- 评估将比内部评估更加耗时，并且更难以排除故障
