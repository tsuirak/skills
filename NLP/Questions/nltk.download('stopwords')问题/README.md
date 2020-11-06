## 问题：

使用 NLTK 下载语料库的时候遇到如下问题：

```python
import nltk
nltk.download('stopwords')
```

```
[nltk_data] Error loading stopwords: <urlopen error [SSL:
[nltk_data]     CERTIFICATE_VERIFY_FAILED] certificate verify failed:
[nltk_data]     unable to get local issuer certificate (_ssl.c:1076)>

False
```



## 解决：

1. 方法一，添加如下代码

   若无法解决，只能手动下载数据集，参考方法二

   ```python
   import nltk
   import ssl
   
   try:
       _create_unverified_https_context = ssl._create_unverified_context
   except AttributeError:
       pass
   else:
       ssl._create_default_https_context = _create_unverified_https_context
       
   
   nltk.download('stopwords')
   ```

2. 方法二，手动下载数据

   - 首先，手动下载数据：[github](https://github.com/nltk/nltk_data) （注意，整个文件大小约500m，从github上下载可能会耗费很长时间）

   - 第二步，很关键！下载的数据文件名默认为 **nltk_data-gh-pages** ，现在你需要找到放置的位置。

     - 当你在执行 `nltk.downloads('stopwords')`  报错时，会出现以下提示

       ```cmd
       Please use the NLTK Downloader to obtain the resource: >>>
       nltk.download()
       Searched in:
       	- '/usr/local/lib/nltk_data'
       	- ...
       	...
       ```

       诸如此类路径。我们需要做的是，随便找一个你想要放置数据的文件目录，（注意，当前目录下并没有 nltk_data ），例如我们找到第一个目录 '/usr/local/lib/'，在当前目录下面创建 nltk_data。接下来，将下载完毕的数据集文件 **nltk_data-gh-pages** 解压至当前文件下。你需要进入解压后的文件找到 corpora 文件，（即，在相对路径下 ‘nltk_data-gh-pages/packages/’），将该文件复制到  '/usr/local/lib/nltk_data' 文件下，即可。

   

