Learn Activiti
===

请假流程示例
---

观看active

# 工作流代码实现步骤

1. 获取流程引擎对象`ProcessEngine`
2. 部署流程定义，部署之后的流程定义时不能修改的，那如何控制修改呢？使用流程定义的key相同的情况下，实现版本升级。
    1. 获取`RepositoryService`
    2. 加载`.bpmn`流程定义文件
```sql
-- 流程定义部署成功后，数据库中有3张表被修改
-- act_re_deployment（部署对象表）, act_re_procdef（流程定义表）, act_ge_bytearray（资源文件表）

select * from act_re_deployment;
+-------+--------------+-----------+------+------------+-------------------------+-----------------+
| ID_   | NAME_        | CATEGORY_ | KEY_ | TENANT_ID_ | DEPLOY_TIME_            | ENGINE_VERSION_ |
+-------+--------------+-----------+------+------------+-------------------------+-----------------+
| 20001 | 流程定义 | NULL      | NULL |            | 2018-05-11 14:55:52.798 | NULL            |
+-------+--------------+-----------+------+------------+-------------------------+-----------------+

select * from act_re_procdef;
+----------------------+------+------------------------------+--------------+--------------+----------+----------------+----------------+---------------------+--------------+---------------------+-------------------------+-------------------+------------+-----------------+
| ID_                  | REV_ | CATEGORY_                    | NAME_        | KEY_         | VERSION_ | DEPLOYMENT_ID_ | RESOURCE_NAME_ | DGRM_RESOURCE_NAME_ | DESCRIPTION_ | HAS_START_FORM_KEY_ | HAS_GRAPHICAL_NOTATION_ | SUSPENSION_STATE_ | TENANT_ID_ | ENGINE_VERSION_ |
+----------------------+------+------------------------------+--------------+--------------+----------+----------------+----------------+---------------------+--------------+---------------------+-------------------------+-------------------+------------+-----------------+
| leaveProcess:2:20004 |    1 | http://www.activiti.org/test | 请假流程 | leaveProcess |        2 | 20001          | leave.bpmn     | leave.png           | NULL         |                   0 |                       1 |                 1 |            | NULL            |
+----------------------+------+------------------------------+--------------+--------------+----------+----------------+----------------+---------------------+--------------+---------------------+-------------------------+-------------------+------------+-----------------+
select * from act_ge_bytearray;
7502	1	leave.png	7501	(BLOB) 25.62 KB	0
7503	1	leave.bpmn	7501	(BLOB) 4.35 KB	0
```
3. 启动流程实例`ProcessInstance`
    1. 获取`RunTimeService`
    2. 通过流程定义的key来启动
```sql
-- 启动流程实例成功后，数据库中有5张表被修改了，那什么是流程实例呢？就是代表流程定义的执行实例，包括了所有的运行节点
-- act_ru_execution(正在执行的执行对象表），什么是执行对象呢？流程安装流程定义的规则执行一次，就可以表示执行对象
-- act_hi_procinst(流程实例表），
-- act_ru_task（正在执行的任务表, 只有userTaskd节点才会在该表中存在记录），
-- act_hi_taskinst（任务历史表，只有userTaskd节点才会在该表中存在记录）
-- act_hi_actinst（所有活动节点的历史表）
-- 如果是单例流程（没有分支和聚合），执行对象id和流程实例id相等
-- 一个流程中流程实例只有一个，执行对象可以存在多个（如果存在分支和聚合）
select * from act_ru_execution;

select * from act_hi_procinst;

select * from act_ru_task;

select * from act_hi_taskinst;

select * from act_hi_actinst;
```
4. 查询任务
5. 完成任务

# 流程变量

```sql
-- 流程变量相关的表有两个,分别是act_ru_variable和act_hi_varinst
select * from act_ru_variable;

select * from act_hi_varinst;
```

## 流程变量的作用
1. 用来传递业务参数
2. 指定连线完成任务（同意还是拒绝）
3. 动态指定任务的办理人

## 设置流程变量的方式
1. 调用`RuntimeService`的`setVariable`方法或者`setVariables`方法
2. 调用`TaskService`的`setVariable`方法或者`setVariables`方法
3. 调用`RunTimeServie`的`startProcessInstanceByKey`方法在启动流程实例时设置流程变量
4. 调用`TaskService`的`complete`方法设置流程变量

## 获取流程变量
1. 调用`RuntimeService`的`getVariable`方法或者`getVariables`方法
2. 调用`RunTimeServie`的`getVariable`方法或者`getVariables`方法
