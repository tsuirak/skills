

# 简介

- sql语句50道练习题

- 文件目录结构

  ```
  ├─SQL_Exercise_50
    ├─fig # 运行结果图片
  ```

  

# 题目

#### 1.查询课程编号为"01"的课程比"02"的课程成绩高的所有学生的学号(重点)

```mysql
-- 思路
-- 1.子查询 先查询"01"课程的学生id,score记为a表 再查询"02"课程的学生的id,score记为b表
-- 2.内联结a,b表

select a.s_id "学号" from 
(
select s_id,s_score from Score
where c_id = '01') as a
INNER JOIN
(
select s_id,s_score from Score
where c_id = '02') as b 
on a.s_id = b.s_id
where a.s_score > b.s_score
```

#### 2.查询平均成绩大于60分的学生的学号和平均成绩

```mysql
-- 思路
-- 1.groupby学生id
-- 2.avg统计平均成绩

select s_id"学号",avg(s_score)"平均成绩" from Score
GROUP BY s_id
HAVING avg(s_score) > 60
```

#### 3.查询所有学生的学号、姓名、选课数、总成绩

```mysql
-- 思路
-- 1.左联结
-- 2.选课数目使用count统计
-- 3.总成绩需要使用case when,再使用sum求和

select st.s_id"学号",st.s_name"姓名",COUNT(s.c_id)"课程数",sum(case when s.s_score is null then 0 else s.s_score end)"总成绩" from Student as st
LEFT JOIN Score as s
on st.s_id = s.s_id
GROUP BY st.s_id
```

#### 4.查询姓"猴"老师的个数

```mysql
-- 思路
-- 1.老师->教师表 
-- 2.count函数

SELECT COUNT(t_id)"姓张老师的个数" from Teacher
where t_name like "张%"
```

#### 5.查询没学过张三老师课的学生的学号和姓名 (重点)

```mysql
-- 思路
-- 1.先选张三老师教过的学生的id
-- 2.从学生表中选出不包含在张三老师教过的学生的id

select s_id,s_name from Student
where s_id not in
(
select s.s_id from Score as s 
INNER JOIN Course as c on s.c_id = c.c_id
INNER JOIN Teacher as t on c.t_id = t.t_id
where t_name = '张三')
```

#### 6.查询学过张三老师所教过的所有课的同学的学号和姓名 (重点)

```mysql
-- 思路
-- 1.内联结

select st.s_id"学号",st.s_name"姓名" from Course as c 
INNER JOIN Score as s on c.c_id = s.c_id
INNER JOIN Teacher as t on c.t_id = t.t_id
INNER JOIN student as st on st.s_id = s.s_id
where t.t_name = '张三'
order by st.s_id 
```

#### 7.查询学过编号01课程也学过编号02课程的学生的学号和姓名 (重点)

```mysql
-- 思路
-- 1.子查询 先查询01课程作为表a 再查询02课程作为表b
-- 2.内联结a,b 

select s_id"学号",s_name"姓名" from Student
WHERE s_id in(
select a.s_id from 
(select s_id from Score
WHERE c_id = '01') as a 
INNER JOIN(
select s_id from Score
WHERE c_id = '02') as b on a.s_id = b.s_id)
```

#### 8.查询课程编号为02的总成绩

```mysql
-- 思路
-- 1.sum函数

select sum(s_score) from Score
where c_id = '02'
```

#### 9.查询所有课程成绩小于60分的学生的学号和姓名

```mysql
-- 思路
-- 1.两个子查询
-- 2.第一个子查询先筛选出成绩小于60分并且以学号进行groupy by的表a
-- 3.第二个子查询筛选出以学号进行groupy by的表b
-- 4.两个表进行比较课程的数目 

SELECT a.s_id"学号",st.s_name"姓名" from 
(select s_id,count(c_id) as cnt 
from Score 
where s_score < 60
GROUP BY s_id)as a
INNER JOIN(
SELECT s_id,count(c_id) as cnt 
FROM Score
GROUP BY s_id) as b on a.s_id = b.s_id
INNER JOIN Student as st on st.s_id = a.s_id
where a.cnt = b.cnt
```

#### 10.查询没有学全所有课的学生的学号、姓名(重点)

```mysql
-- 思路
-- 1.学生表左联结成绩表
-- 2.groupby学号 统计每个组的课程数目是否小于总的课程数目

select st.s_id"学号",st.s_name"姓名" from Student as st
left join Score as s on st.s_id = s.s_id
GROUP BY st.s_id
HAVING count(distinct s.c_id) < (select count(DISTINCT c_id) from Course)
```

#### 11.查询至少有一门课与学号为01的学生所学的课程相同的学生的学号和姓名(重点)

```mysql
-- 思路
-- 1.子查询 先查询学号01学生所学的课程号x
-- 2.再查询成绩表中存在x的学号

select s_id"学号",s_name"姓名" from Student
where s_id in(
select DISTINCT s_id from Score
WHERE c_id in(
select c_id from Score 
where s_id = '01') and s_id != '01')
```

#### 12.查询和01号同学所学课程完全相同的其他同学的学号(重点)

```mysql
-- 思路
-- 1.两个子查询
-- 2.第一个子查询 筛选出与01号学生课程数目相同的学生
-- 3.第二个子查询 查询01号学生上过的课程并且筛选出未上过一样课程的学生

select s_id"学号",s_name"姓名" from Student
where s_id in(
select s_id from Score
WHERE s_id != '01'
GROUP BY s_id 
HAVING count(DISTINCT c_id) =(SELECT count(DISTINCT c_id) from Score where s_id = '01'))
and s_id not in(
select s_id from Score
where c_id not in(
select c_id from Score
where s_id = '01'))
```

#### 15.查询两门及其以上不及格课程的同学的学号，姓名以及平均成绩(重点)

```mysql
-- 思路
-- 1.先查询成绩表 先筛选出成绩不及格的学生的学号 再进行分组 筛选出分组后课程数目大于2的学生学号
-- 2.子查询选出的学生学号id

select st.s_id"学号",st.s_name"姓名",avg(s_score)"平均成绩" from Student as st 
INNER JOIN Score as s on st.s_id = s.s_id
WHERE st.s_id in(
select s_id from Score
where s_score < 60
GROUP BY s_id 
HAVING count(s_id) >= 2)
GROUP BY st.s_id,st.s_name
```

#### 16.检索01号课程分数小于60,按照分数降序排列学生信息

```mysql
-- 思路
-- 1.内联结成绩表和学生表
-- 2.使用order by 

select st.s_id"学号",st.s_name"姓名",s.s_score"成绩" from Student as st
INNER JOIN Score as s on st.s_id = s.s_id
where s.c_id = '01' and s.s_score < 60
ORDER BY s.s_score DESC
```

#### 17.按照平均成绩从高到低显示所有学生的所有课程的成绩以及平均成绩(重点)

```mysql
-- 思路
-- 1.按照学号进行group by
-- 2.case when 统计每门课程的成绩
-- 3.avg 统计平均成绩
-- 4.order by 排序

select s_id "学号",
max(case when c_id='01' then s_score else NULL end)"语文",
max(case when c_id='02' then s_score else NULL end)"数学",
max(case when c_id='03' then s_score else NULL end)"英文",
avg(s_score) "平均成绩"
from Score
GROUP BY s_id
ORDER BY avg(s_score) DESC
```

#### 18.查询各科成绩最高分、最低分和平均分(超级重点)

#### 以如下形式显示：课程ID，课程name，最高分，最低分，平均分，及格率，中等率，优良率，优秀率

#### 及格为>=60，中等为：70-80，优良为：80-90，优秀为：>=90 (超级重点)

```mysql
-- 思路
-- 1.case when的用法

SELECT s.c_id"课程号",c.c_name"课程",
max(s.s_score)"最大值",
min(s.s_score)"最小值",
avg(s.s_score)"平均值",
sum(case when s.s_score>=60 then 1 else 0 end)/count(s.s_id) "及格率",
sum(case when s.s_score>=70 and s.s_score<80 then 1 else 0 end)/count(s.s_id) "中等率",
sum(case when s.s_score>=80 and s.s_score<90 then 1 else 0 end)/count(s.s_id) "优良率",
sum(case when s.s_score>=90 then 1 else 0 end)/count(s.s_id) "优秀率"
from Score as s 
INNER JOIN Course as c on s.c_id = c.c_id
GROUP BY s.c_id
```

##### 19.按各科成绩进行排序,并显示排名(重点)

```mysql

```

#### 20.查询学生的总成绩并进行排名

```mysql
-- 思路
-- 1.使用RANK函数

select s_id"学号",sum(s_score)"总成绩",RANK() over(order by sum(s_score) DESC)"排名"  from Score
GROUP BY s_id
```

#### 21.查询不同老师所教不同课程的平均分从高到低显示

```mysql
-- 思路
-- 1.成绩表和课程表内联结
-- 2.以课程号进行分组 avg
-- 3.order by

SELECT s.c_id"课程号",c.c_name"课程名称",avg(s.s_score) as avg_score from Score as s 
INNER JOIN Course as c on s.c_id = c.c_id
GROUP BY s.c_id
ORDER BY avg_score DESC
```

#### 22.查询所有课程的成绩第2名到第3名的学生信息及该课程成绩

#### 23.使用分段[100-85) [85-70) [70-60) [<60]来统计各科成绩,分别统计各分数段人数:课程ID和课程名称 (重点)

```mysql
-- 思路
-- 1.成绩表和课程表内联结
-- 2.在以课程id进行groupy by后使用case when统计个数  

select c.c_id"课程号",c.c_name"课程名称"
,sum(case when s.s_score <= 100 and s.s_score > 85 then 1 else 0 end) as '[100,85)'
,sum(case when s.s_score <= 85 and s.s_score > 70 then 1 else 0 end) as '[85,70)'
,sum(case when s.s_score <= 70 and s.s_score > 60 then 1 else 0 end) as '[70,60)'
,sum(case when s.s_score <= 60 then 1 else 0 end) as '[<60]'
from Score as s 
INNER JOIN Course as c on s.c_id = c.c_id
GROUP BY c.c_id,c.c_name
```

#### 24.查询学生平均成绩及其名次 (重点)

```mysql
-- 思路
-- 1.ROW_NUMBER函数

select s_id"学号",avg(s_score)"平均成绩",ROW_NUMBER() over(ORDER BY avg(s_score) DESC) as 'rank'
from Score
GROUP BY s_id
```

#### 25.同22题

#### 26.查询每门课程被选修的学生数

```mysql
-- 思路
-- 1.成绩表和课程表内联结
-- 2.group by 课程id 使用count统计个数

select c.c_id"课程号",c.c_name"课程名称",count(DISTINCT s.s_id)"个数" from Score as s
INNER JOIN Course as c on s.c_Id = c.c_id 
GROUP BY c.c_id,c.c_name
```



#### 27.查询出只有两门课程的全部学生的学号和姓名

```mysql
-- 思路
-- 1.学生表和成绩表内联结
-- 2.以学生id进行group by 筛选课程数目=2的学生

SELECT st.s_id"学号",st.s_name"姓名" from Score as s 
INNER JOIN Student as st on s.s_id = st.s_id 
GROUP BY st.s_id 
HAVING count(DISTINCT s.c_id) = 2
```

#### 28.查询男生女生人数

```mysql
-- 思路
-- 1.group by性别

select s_sex"性别",count(s_id)"个数" from Student
GROUP BY s_sexs_sex

-- 思路
-- 1.case when统计男女个数

select 
sum(case when s_sex = '男' then 1 else 0 end) '男生人数',
sum(case when s_sex = '女' then 1 else 0 end) '女生人数'
FROM Student
```

#### 29.查询名字中带有'风'字的学生信息

```mysql
-- 思路
-- 1.通配符%的使用

select s_id"学号",s_name"姓名",s_birth"生日",s_sex"性别" from Student
where s_name like "%风%"
```

#### 31.查询1990年出生的学生名单(重点)

```mysql
-- 思路
-- 1.year的使用

select s_id"学号",s_name"姓名",s_birth"生日",s_sex"性别" from Student
where year(s_birth) = 1990
```



#### 32.查询平均成绩大于等于85的所有学生的学号 姓名 平均成绩

```mysql
-- 思路
-- 1.查询出平均分大于85学生的信息作为表1
-- 2.表1与成绩表内联结

select st.s_id"学号",st.s_name"姓名",temp.avg"平均分" from Student as st
INNER JOIN(
select s_id,avg(s_score) as 'avg'
from Score
GROUP BY s_id 
HAVING avg >= 85) as temp 
on st.s_id = temp.s_id
```



#### 33.查询每门课的平均成绩,结果按照平均成绩升序排列,平均成绩相同时候,按照课程号降序排列

```mysql
-- 思路
-- 1.排序时候order by的用法

select c_id"课程号",avg(s_score)"平均分" from Score
GROUP BY c_id 
ORDER BY avg(s_score) asc,c_id DESC
```



#### 34.查询课称号名为"数学",并且分数低于60的学生姓名和分数

```mysql
-- 思路
-- 1.内联结成绩表,学生表,课程表

select st.s_id"学号",st.s_name"姓名",c.c_name"课程",s.s_score"分数" from Score as s 
INNER JOIN Course as c on s.c_id = c.c_id
INNER JOIN Student as st on st.s_id = s.s_id
where c.c_name = '数学' and s.s_score < 60
```



#### 35.查询所有学生的课程及分数情况(重点)

```mysql
-- 思路
-- 1.学生表、成绩表、课程表内联结
-- 2.以学号、姓名进行group by
-- 3.因为GROUP UP 要与select 列一致，所以case when 加修饰max

select st.s_id"学号",st.s_name"姓名",
max(case when c.c_name = "数学" then s.s_score else null end) "数学",
max(case when c.c_name = "语文" then s.s_score else null end) "语文",
max(case when c.c_name = "英语" then s.s_score else null end) "英语"
from Student as st 
INNER JOIN Score as s on st.s_id = s.s_id
INNER JOIN Course as c on s.c_id = c.c_id
GROUP BY st.s_id,st.s_name
```



#### 36.查询课程成绩在70分以上课程的名称，分数和学生名称

```mysql
-- 思路
-- 1.学生表、成绩表、课程表内联结

select c.c_name"课程名称",s.s_score"分数",st.s_name"姓名" from Score as s 
INNER JOIN Course as c on s.c_id = c.c_id
INNER JOIN Student as st on s.s_id = st.s_id
WHERE s.s_score > 70
```



#### 37.查询不及格的课程并且按照课程号从大到小排列

```mysql
-- 思路
-- 1.成绩表和课程表内联结
-- 2.筛选并使用Order by

select c.c_id"课程号",c.c_name"课程",s.s_score"成绩" from Score as s 
INNER JOIN Course as c on s.c_id = c.c_id
where s.s_score < 60
ORDER BY s.c_id DESC
```



#### 38.查询课程编号为03且课程成绩在80分以上的学生的学号和姓名

```mysql
-- 思路
-- 1.学生表、成绩表、课程表内联结
-- 2.筛选

select st.s_id"学号",st.s_name"姓名" from Score as s 
INNER JOIN Student as st on st.s_id = s.s_id
INNER JOIN Course as c on c.c_id = s.c_id
where c.c_id = '03' and s.s_score > 80
```



#### 39.求每门课程的学生人数

```mysql
-- 思路
-- 1.以课程号进行group by再count统计人数

SELECT c_id"课程号",count(DISTINCT s_id)"人数" from Score
GROUP BY c_id
```



#### 40.查询选修"张三"老师所授课程的学生中成绩最高的学生及其成绩

```mysql
-- 思路
-- 1.4张表内联结
-- 2.筛选老师=张三的所有学生的成绩,并且排序后取第一行

select st.s_name"姓名",s.s_score"成绩" from Score as s 
INNER JOIN Course as c on s.c_id = c.c_id
INNER JOIN Student as st on st.s_id = s.s_id
INNER JOIN Teacher as t on t.t_id = c.t_id
where t.t_name = '张三'
ORDER BY s.s_score DESC limit 0,1
```



##### 41.查询不同课程成绩相同的学生的学生编号、课程编号、学生成绩(重点)

#### 43.统计每门课程的学生选修人数(超过5人的课程才统计)

```mysql
-- 思路
-- 1.成绩表中以课程号进行group by 
-- 2.筛选统计count>5

SELECT c_id"课程号",count(s_id)"人数" from Score
GROUP BY c_id
HAVING count(DISTINCT s_id) > 5
```

####  

#### 44.检索至少选修两门课程的学生学号

```mysql
-- 思路
-- 1.成绩表中以学号进行group by
-- 2.筛选统计conut>2

select s_id"学号",count(DISTINCT c_id)"课程数目" from Score
GROUP BY s_id
HAVING count(DISTINCT c_id) >=2
```



#### 45.查询选修了全部课程的学生信息

```mysql
-- 思路
-- 1.成绩表和学生表内联结
-- 2.以学号进行group by
-- 3.筛选出课程号的数目等于总的课程数目

select st.s_id"学号",st.s_name"姓名",st.s_birth"生日",st.s_sex"性别" from Score as s
INNER JOIN Student as st on st.s_id = s.s_id
GROUP BY st.s_id
HAVING count(c_id) = (SELECT count(c_id) from Course) 
```



#### 46.查询各学生的年龄

```mysql
-- 思路
-- 1.DAREDIFF(expr1,expr2)的用法

SELECT s_id"学号",s_birth"生日",floor(DATEDIFF('2020-8-22',s_birth)/365)"年龄" from Student 
```



##### 47.查询没学过“张三”老师讲授的任一门课程的学生姓名

##### 48.查询下周过生日的同学

#### 49.查询本月过生日的同学

```mysql
-- 思路
-- 1.MONTH的用法

SELECT s_id"学号",s_name"姓名",s_birth"生日",s_sex"性别" from Student
where MONTH(s_birth) = MONTH(Now())
```

##### 50.查询下一个月过生日的同学

