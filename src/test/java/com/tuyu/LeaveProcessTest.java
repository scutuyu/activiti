package com.tuyu;

import com.tuyu.util.ResourceUtil;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricIdentityLink;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.persistence.entity.IdentityLinkEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

/**
 * 请假流程测试类
 * <p>测试步骤</p>
 * <ul>
 *     <li>构建流程引擎（测试基类中已经完成）</li>
 *     <li>发布流程定义（测试基类中提供了3中发布方式）</li>
 *     <li>启动流程实例</li>
 *     <li>查询任务（可以按人查，也可以按流程实例查）</li>
 *     <li>完成任务</li>
 *     <li>该流程实例运行完成</li>
 * </ul>
 * @author tuyu
 * @date 5/11/18
 * Stay Hungry, Stay Foolish.
 */
public class LeaveProcessTest extends BaseTest{

    /**
     * 部署流程定义
     * <p>部署之后会存在数据库中，下次就可以不用部署了</p>
     */
    @Test
    public void testDeploy() {
        // 通过资源文件部署
        deploy("请假流程5.18.0");// 6.0.0.4
        // 通过zip压缩包加载，压缩包中包含了bpmn文件和png图片
//        deployZip("zip流程部署");
        // 通过inputStream流夹部署
//        deployInputStream("inputStream流部署");
    }

    /**
     * 启动流程实例
     * <p>启动流程实例之后会存在数据库中，下次就可以不用启动了，知道该流程实例结束</p>
     */
    @Test
    public void testStart() {
        RuntimeService runtimeService = processEngine.getRuntimeService(); // 与正在执行的流程实例和执行对象相关的service
        String processDefineKey = "leaveProcess"; // 流程定义文件中的id属性值
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefineKey);// 使用流程定义的key启动流程实例，默认按照最新版本的流程定义启动
        printProcessInstance(processInstance); // process instance id 77501

    }

    /**
     * 查询所有正在运行的流程实例
     */
    @Test
    public void testAllProcessInstall() {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery()
                .list();
        for (ProcessInstance instance : list){
            printProcessInstance(instance);
        }
    }

    /**
     * 查询当前人的个人任务
     */
    @Test
    public void testQueyTask() {
        TaskService taskService = processEngine.getTaskService(); // 与正在执行的任务管理相关的service
        String assignee = "张三";
//        String assignee = "李四";
//        String assignee = "王五";
        List<Task> list = taskService.createTaskQuery() // 创建任务查询对象
//                .taskCandidateOrAssigned(assignee) // 指定个人任务查询，指定办理人
//                .taskAssignee(assignee)
                .list();
        if (list != null && list.size() > 0){
            for (Task task : list){
                printTask(task);
            }
        }
    }

    /**
     * 根据流程实例查询任务
     */
    @Test
    public void testQueryCurrentTask() {
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery()
                .processInstanceId("5001") // 2501  5001
                .singleResult();
        printTask(task);
        RepositoryService repositoryService = processEngine.getRepositoryService();
        // 查询ProcessDefinitionEntiy对象
        ProcessDefinition definition = repositoryService.getProcessDefinition(task.getProcessDefinitionId());
        ProcessDefinitionEntity entity = (ProcessDefinitionEntity) definition;
        // 获取当前的活动
        ActivityImpl activity = entity.findActivity(task.getTaskDefinitionKey());
        System.out.println(signal + activity);
    }

    /**
     * 完成我的任务
     * <p>任务完成时间duration的值是任务结束时间和开始时间的差值，单位毫秒</p>
     * <pre>
     *     select DURATION_, TIMESTAMPDIFF(MICROSECOND, START_TIME_, END_TIME_)/1000 as num from act_hi_taskinst;
     * </pre>
     * <p>参考链接</p>
     * <pre>
     *     https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html
     * </pre>
     */
    @Test
    public void testCompleteTask() {
        TaskService taskService = processEngine.getTaskService();
//        String taskId = "10005";
//        String taskId = "12502";
//        String taskId = "37505";
//        String taskId = "40002";
        String taskId = "15002";
        taskService.complete(taskId);
        System.out.println(signal + "\ncomplete task, id is " + taskId);
    }

    /**
     * 查询所有正在执行的流程实例
     */
    @Test
    public void testAllRunningProcessInstance() {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery()
                .list();
        for (ProcessInstance instance : list){
            printProcessInstance(instance);
        }
    }

    /**
     * 判断流程是否结束
     */
    @Test
    public void testJudgeProcessInstanceClose() {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        String processInstanceId = "1";
        Execution execution = runtimeService.createExecutionQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        if (execution == null){
            System.out.println(signal + "process has closed");
        }else {
            System.out.println(signal + "process has not closed");
        }
    }


}
