Learn Activiti
===

请假流程示例 [`LeaveProcessTest`](https://github.com/scutuyu/activiti/blob/master/src/test/java/com/tuyu/LeaveProcessTest.java)
---

[bilibi视频地址](https://www.bilibili.com/video/av7670054/index_13.html#page=9)

# 工作流建立步骤

1. [获取流程引擎对象](https://github.com/scutuyu/activiti/wiki/ProcessEngine)
2. [部署流程定义](https://github.com/scutuyu/activiti/wiki/Deploy-ProcessDefine) 
3. [启动流程实例](https://github.com/scutuyu/activiti/wiki/Start-ProcessInstance)
4. [查询任务](https://github.com/scutuyu/activiti/wiki/Query-Tasks)
5. [完成任务](https://github.com/scutuyu/activiti/wiki/Complete-Task)

# 流程变量与任务变量
- 流程变量是是整个流程实例中所有节点都可以访问并修改的变量，修改会覆盖之前的值
- 任务变量是某个任务节点独有的变量，整个流程实例中其他任务节点不能访问该变量，如果多个任务节点都有相同变量名的变量，他们不会覆盖，相互独立

## [设置流程变量](https://github.com/scutuyu/activiti/wiki/Set-prcessVariables)
## [设置任务变量](https://github.com/scutuyu/activiti/wiki/Set-TaskVariables)
## [获取流程变量](https://github.com/scutuyu/activiti/wiki/Get-ProcessVariables)
## [获取任务变量](https://github.com/scutuyu/activiti/wiki/Get-TaskVariables)
## [assignee, candidate, owner的区别](https://github.com/scutuyu/activiti/wiki/assignee,-candidate,-owner%E7%9A%84%E5%8C%BA%E5%88%AB)

# [不使用网关实现任务回退](https://github.com/scutuyu/activiti/wiki/Retroversion)




