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
```
RunTimeService.setVariables(executionId, map)
RunTimeService.startProcessInstanceByKey(processDefineKey,map)
TaskService.setVariables(taskId, map)
TaskService.complete(taskId, map)
```
setVariables方法可以替换为setVariable方法，区别是：前者保存单个变量，后者通过传入一个Map保存多个变量

## 设置任务变量
```
RunTimeService.setVariablesLocal(executionId, map)
TaskService.setVariablesLocal(taskId, map)
```
setVariables方法可以替换为setVariable方法，区别是：前者保存单个变量，后者通过传入一个Map保存多个变量

**注意**
我用的Activiti版本是5.18.0
测试了`RunTimeService.setVariablesLocal(executionId, map)`和`RunTimeService.setVariables(executionId, map)`
发现他们的表现是一样的都是设置流程变量，在整个流程执行过程中都可以访问到
下面是我的测试过程：
```java
public class VariableTest{
    @Test
    public void testSetVariables(){
        Map<String, Object> map = new HashMap<>();
        map.put("name", "keen");
        String executionId = "25001";
        RuntimeService runtimeService = processEngine.getRuntimeService(); // 先设置流程变量
        runtimeService.setVariables(executionId, map);
        
        map.clear();
        map.put("age", "12");
        runtimeService.setVariablesLocal(executionId, map); // 在设置任务变量
    }
}
```
观察控制台输出的sql语句
```log
03:30:06,090 [main] DEBUG org.activiti.engine.impl.persistence.entity.HistoricVariableInstanceEntity.insertHistoricVariableInstance  - ooo Using Connection [com.mysql.jdbc.JDBC4Connection@536dbea0]
03:30:06,090 [main] DEBUG org.activiti.engine.impl.persistence.entity.HistoricVariableInstanceEntity.insertHistoricVariableInstance  - ==>  Preparing: insert into ACT_HI_VARINST (ID_, PROC_INST_ID_, EXECUTION_ID_, TASK_ID_, NAME_, REV_, VAR_TYPE_, BYTEARRAY_ID_, DOUBLE_, LONG_ , TEXT_, TEXT2_, CREATE_TIME_, LAST_UPDATED_TIME_) values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) 
03:30:06,092 [main] DEBUG org.activiti.engine.impl.persistence.entity.HistoricVariableInstanceEntity.insertHistoricVariableInstance  - ==> Parameters: 35001(String), 25001(String), 25001(String), null, name(String), 0(Integer), string(String), null, null, null, keen(String), null, 2018-05-16 15:30:06.087(Timestamp), 2018-05-16 15:30:06.087(Timestamp)
03:30:06,093 [main] DEBUG org.activiti.engine.impl.persistence.entity.HistoricVariableInstanceEntity.insertHistoricVariableInstance  - <==    Updates: 1

03:30:06,093 [main] DEBUG org.activiti.engine.impl.persistence.entity.VariableInstanceEntity.insertVariableInstance  - ==>  Preparing: insert into ACT_RU_VARIABLE (ID_, REV_, TYPE_, NAME_, PROC_INST_ID_, EXECUTION_ID_, TASK_ID_, BYTEARRAY_ID_, DOUBLE_, LONG_ , TEXT_, TEXT2_) values ( ?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) 
03:30:06,094 [main] DEBUG org.activiti.engine.impl.persistence.entity.VariableInstanceEntity.insertVariableInstance  - ==> Parameters: 35001(String), string(String), name(String), 25001(String), 25001(String), null, null, null, null, keen(String), null
03:30:06,095 [main] DEBUG org.activiti.engine.impl.persistence.entity.VariableInstanceEntity.insertVariableInstance  - <==    Updates: 1

03:30:06,103 [main] DEBUG org.activiti.engine.impl.persistence.entity.HistoricVariableInstanceEntity.insertHistoricVariableInstance  - ==>  Preparing: insert into ACT_HI_VARINST (ID_, PROC_INST_ID_, EXECUTION_ID_, TASK_ID_, NAME_, REV_, VAR_TYPE_, BYTEARRAY_ID_, DOUBLE_, LONG_ , TEXT_, TEXT2_, CREATE_TIME_, LAST_UPDATED_TIME_) values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) 
03:30:06,104 [main] DEBUG org.activiti.engine.impl.persistence.entity.HistoricVariableInstanceEntity.insertHistoricVariableInstance  - ==> Parameters: 35002(String), 25001(String), 25001(String), null, age(String), 0(Integer), string(String), null, null, null, 12(String), null, 2018-05-16 15:30:06.102(Timestamp), 2018-05-16 15:30:06.102(Timestamp)
03:30:06,104 [main] DEBUG org.activiti.engine.impl.persistence.entity.HistoricVariableInstanceEntity.insertHistoricVariableInstance  - <==    Updates: 1

03:30:06,105 [main] DEBUG org.activiti.engine.impl.persistence.entity.VariableInstanceEntity.insertVariableInstance  - ==>  Preparing: insert into ACT_RU_VARIABLE (ID_, REV_, TYPE_, NAME_, PROC_INST_ID_, EXECUTION_ID_, TASK_ID_, BYTEARRAY_ID_, DOUBLE_, LONG_ , TEXT_, TEXT2_) values ( ?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) 
03:30:06,105 [main] DEBUG org.activiti.engine.impl.persistence.entity.VariableInstanceEntity.insertVariableInstance  - ==> Parameters: 35002(String), string(String), age(String), 25001(String), 25001(String), null, null, null, null, 12(String), null
03:30:06,106 [main] DEBUG org.activiti.engine.impl.persistence.entity.VariableInstanceEntity.insertVariableInstance  - <==    Updates: 1
```
从日志中的6条sql日志可以看出，前两条sql是将name=keen变量分别保存到act_hi_varinst表和act_ru_variable表，后两条sql是将age=12变量保存到act_hi_varinst表和act_ru_variable表，从他们传递的参数来看，task_id字段的值都是`null`,
这说明不过调用`RunTimeService.setVariablesLocal(executionId, map)`方法还是调用`RunTimeService.setVariables(executionId, map)`方法，都是一样的处理，这一点可以通过查询接口API得到验证：
```java
public class VariableTest{
    @Test
    public void testGetVariables(){
        RuntimeService runtimeService = processEngine.getRuntimeService();
        String executionId = "25001";
        Map<String, Object> variables = runtimeService.getVariables(executionId);
        Map<String, Object> variablesLocal = runtimeService.getVariablesLocal(executionId);
        System.out.println("" + variables);
        System.out.println(signal + variablesLocal);
    }
}
```
在控制台打印sql日志：
```log
03:39:04,222 [main] DEBUG org.activiti.engine.impl.persistence.entity.VariableInstanceEntity.selectVariablesByExecutionId  - ==>  Preparing: select * from ACT_RU_VARIABLE where EXECUTION_ID_ = ? and TASK_ID_ is null 
03:39:04,222 [main] DEBUG org.activiti.engine.impl.persistence.entity.VariableInstanceEntity.selectVariablesByExecutionId  - ==> Parameters: 25001(String)
03:39:04,225 [main] DEBUG org.activiti.engine.impl.persistence.entity.VariableInstanceEntity.selectVariablesByExecutionId  - <==      Total: 6

03:39:04,239 [main] DEBUG org.activiti.engine.impl.persistence.entity.VariableInstanceEntity.selectVariablesByExecutionId  - ==>  Preparing: select * from ACT_RU_VARIABLE where EXECUTION_ID_ = ? and TASK_ID_ is null 
03:39:04,240 [main] DEBUG org.activiti.engine.impl.persistence.entity.VariableInstanceEntity.selectVariablesByExecutionId  - ==> Parameters: 25001(String)
03:39:04,243 [main] DEBUG org.activiti.engine.impl.persistence.entity.VariableInstanceEntity.selectVariablesByExecutionId  - <==      Total: 6

------------> {sourceId=1002, leave reason=想睡觉, leave days=20, name=keen, person info=Person{id=10, name='scutuyu'}, age=12}
------------> {sourceId=1002, leave reason=想睡觉, leave days=20, name=keen, person info=Person{id=10, name='scutuyu'}, age=12}
```
从日志可以看出，不管是通过`runtimeService.getVariables(executionId)`还是`runtimeService.getVariablesLocal(executionId)`去查询变量，得到的都是一样的
按道理前者应该能查出age变量，后者能查出name变量和age变量，因为在在保存时，age变量调用的是`RunTimeService.setVariablesLocal(executionId, map)`,name变量调用的是`RunTimeService.setVariables(executionId, map)`

## 获取流程变量
```
TaskService.getVariables(taskId)
RuntimeService.getVariables(executionId)
```

## 获取任务变量

```
TaskService.getVariablesLocal(taskId)
```

# assignee, candidate, owner的区别

## `TaskService.claim(taskId, userId)`
**前提** 假设某个任务节点的candidateUser属性被正确设置了候选人，假设有三个人用逗号分隔，如：`tuyu,ty,scutuyu`
当流程走到该任务节点时，会在`act_ru_identitylink`表和`act_hi_identitylink`表分别新增6条数据，如下：
```log
+-------+------+-----------+-------------+----------+----------+---------------+--------------+
| ID_   | REV_ | GROUP_ID_ | TYPE_       | USER_ID_ | TASK_ID_ | PROC_INST_ID_ | PROC_DEF_ID_ |
+-------+------+-----------+-------------+----------+----------+---------------+--------------+
| 52506 |    1 | NULL      | candidate   | scutuyu  | 52505    | NULL          | NULL         |
| 52507 |    1 | NULL      | participant | scutuyu  | NULL     | 52501         | NULL         |
| 52508 |    1 | NULL      | candidate   | tuyu     | 52505    | NULL          | NULL         |
| 52509 |    1 | NULL      | participant | tuyu     | NULL     | 52501         | NULL         |
| 52510 |    1 | NULL      | candidate   | ty       | 52505    | NULL          | NULL         |
| 52511 |    1 | NULL      | participant | ty       | NULL     | 52501         | NULL         |
+-------+------+-----------+-------------+----------+----------+---------------+--------------+
```

当调用`TaskService.claim(taskId, userId)`方法去认领一个任务时，会修改三个表对应数据行的`assignee_`字段，这三个表分别是
- ACT_HI_ACTINST
- ACT_HI_TASKINST
- ACT_RU_TASK

如果是调用`claim(taskId, userId)`方法认领任务，那么这个认领是可以撤销的，即调用`claim(taskId, null)`,此时会将上述三张表的`assignee_`字段更新为null

当然认领任务的`userId`也可以不是`(ty, tuyu, scutuyu)`中的其中一个，也可以是别的，如`'zhang'`
此时，会在`act_ru_identitylink`表和`act_hi_identitylink`表分别新增一条数据，如下：
```log
+-------+------+-----------+-------------+----------+----------+---------------+--------------+
| ID_   | REV_ | GROUP_ID_ | TYPE_       | USER_ID_ | TASK_ID_ | PROC_INST_ID_ | PROC_DEF_ID_ |
+-------+------+-----------+-------------+----------+----------+---------------+--------------+
| 57501 |    1 | NULL      | participant | zhang    | NULL     | 52501         | NULL         |
+-------+------+-----------+-------------+----------+----------+---------------+--------------+
```
此外，以下三张表对应数据行的`assignee_`字段会被修改为`'zhang''`，这三个表分别是
- ACT_HI_ACTINST
- ACT_HI_TASKINST
- ACT_RU_TASK
 
 **总之** 只要是通过`claim`方法认领的任务，都是可以撤回的
 
 ## `TaskService.setAssignee(taskId, userId)`
 **前提** 某个任务节点没有设置candidateUsers属性，也没有设置candidateGroups属性，也没有设置assignee属性，
 此时该任务的assignee_字段是null，如果调用了`TaskService.setAssignee(taskId, userId)`方法，
 就会在`act_hi_comment`表中新增一条数据，入下：
 ```log
 +-------+-------+-------------------------+----------+----------+---------------+-------------+-----------------+-----------+
 | ID_   | TYPE_ | TIME_                   | USER_ID_ | TASK_ID_ | PROC_INST_ID_ | ACTION_     | MESSAGE_        | FULL_MSG_ |
 +-------+-------+-------------------------+----------+----------+---------------+-------------+-----------------+-----------+
 | 60002 | event | 2018-05-17 11:14:08.003 | NULL     | 52505    | NULL          | AddUserLink | li_|_assignee   | NULL      |
 +-------+-------+-------------------------+----------+----------+---------------+-------------+-----------------+-----------+
 ```
 同时也会在`act_ru_identitylink`表和`act_hi_identitylink`表分别新增一条数据，如下：
 ```log
 +-------+------+-----------+-------------+----------+----------+---------------+--------------+
 | ID_   | REV_ | GROUP_ID_ | TYPE_       | USER_ID_ | TASK_ID_ | PROC_INST_ID_ | PROC_DEF_ID_ |
 +-------+------+-----------+-------------+----------+----------+---------------+--------------+
 | 60001 |    1 | NULL      | participant | li       | NULL     | 52501         | NULL         |
 +-------+------+-----------+-------------+----------+----------+---------------+--------------+
 ```
 此外，以下三张表对应数据行的`assignee_`字段会被修改为`'li''`，这三个表分别是
- ACT_HI_ACTINST
- ACT_HI_TASKINST
- ACT_RU_TASK

如果想把该任务的assignee_字段设置为其他人，比如`wang`,就会报错，下面两种方式都会报错
```
TaskService.claim(taskId, 'wang')

or

TaskService.setAssignee(taskId, 'wang')
```
错误信息如下：
```log

```