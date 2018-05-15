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
 * @author tuyu
 * @date 5/11/18
 * Stay Hungry, Stay Foolish.
 */
public class LeaveProcessTest {

    ProcessEngine processEngine;

    protected static final String signal = "------------> ";

    @Before
    public void before(){
        testBuildProcessEngine();
    }

    @Test
    public void testBuildProcessEngine() {
        processEngine = ProcessEngineConfiguration
                .createStandaloneProcessEngineConfiguration()
                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE)
                .setJdbcUrl("jdbc:mysql://localhost:3306/activiti?useUnicode=true&characterEncoding=UTF-8")
                .setJdbcDriver("com.mysql.jdbc.Driver")
                .setJdbcUsername("root")
                .setJdbcPassword("123456")
                .buildProcessEngine();
        System.out.println(signal + "process engine : " + processEngine);
    }

    /**
     * 部署流程定义
     * <p>部署之后会存在数据库中，下次就可以不用部署了</p>
     */
    @Test
    public void testDeploy() {
        deploy("请假流程5.18.0");// 6.0.0.4
    }

    // 资源文件
    protected void deploy(String name){
        RepositoryService repositoryService = processEngine.getRepositoryService(); // repositoryService 与流程定义和部署相关的service
        if (name == null || "".equals(name)){
            name = "请假流程";
        }
        Deployment deploy = repositoryService.createDeployment() // 创建部署对象
                .name(name) // 添加部署对象
                .addClasspathResource("leave.bpmn") // 加载资源， 当流程定义的key值相同，则版本升级
                .addClasspathResource("leave.png") // 加载资源，如果没有加载图片，那么将会自动生成图片存在数据库
                .deploy(); // 完成部署
        System.out.println(signal + "\nprocess define id : " + deploy.getId()
                + "\nprocess define name : " + deploy.getName());
    }

    // zip格式文件
    protected void deployZip(String name){
        RepositoryService repositoryService = processEngine.getRepositoryService(); // repositoryService 与流程定义和部署相关的service
        if (name == null || "".equals(name)){
            name = "请假流程";
        }
        InputStream inputStream = ResourceUtil.getInputString(this.getClass(), "leave.zip");
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        Deployment deploy = repositoryService.createDeployment() // 创建部署对象
                .name(name) // 添加部署对象
                .addZipInputStream(zipInputStream)
                .deploy(); // 完成部署
        System.out.println(signal + "\nprocess define id : " + deploy.getId()
                + "\nprocess define name : " + deploy.getName());
    }

    // inputStream资源文件
    protected void deployInputStream(String name){
        RepositoryService repositoryService = processEngine.getRepositoryService(); // repositoryService 与流程定义和部署相关的service
        if (name == null || "".equals(name)){
            name = "请假流程";
        }
        String bpmn = "leave.bpmn";
        String png = "leave.png";
        InputStream inputStreamBpmn = ResourceUtil.getInputString(this.getClass(), bpmn);
        InputStream inputStreamPng = ResourceUtil.getInputString(this.getClass(), png);
        Deployment deploy = repositoryService.createDeployment() // 创建部署对象
                .name(name) // 添加部署对象
                .addInputStream(bpmn, inputStreamBpmn) // 使用资源文件名称（要求与资源文件的名称一致）和输入流完成部署
                .addInputStream(png, inputStreamPng) // 使用资源文件名称和输入流完成部署
                .deploy(); // 完成部署
        System.out.println(signal + "\nprocess define id : " + deploy.getId()
                + "\nprocess define name : " + deploy.getName());
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

    protected void printProcessInstance(ProcessInstance processInstance){
        System.out.println(signal
                + "\nprocess instance id " + processInstance.getId() // process instance id = 30001
                + "\nprocess define id : " + processInstance.getProcessDefinitionId()
                + "\nprocess define name : " + processInstance.getProcessDefinitionName());
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
                // 查询条件，where条件
//                .taskCandidateOrAssigned(assignee) // 指定个人任务查询，指定办理人
//                .taskAssignee(assignee)
                // 返回结果集
                .list();
        if (list != null && list.size() > 0){
            for (Task task : list){
                printTask(task);
            }
        }
    }

    protected void printTask(Task task){
        System.out.println(signal + "\ntask id : " + task.getId()
                + "\ntask name : " + task.getName()
                + "\ntask create time : " + task.getCreateTime()
                + "\ntask assignee : " + task.getAssignee()
                + "\nprocess instance id : " + task.getProcessInstanceId()
                + "\nexecution id : " + task.getExecutionId()
                + "\ninstance define id : " + task.getProcessDefinitionId());
    }

    /**
     * 根据流程实例查询任务
     */
    @Test
    public void testQueryCurrentTask() {
        TaskService taskService = processEngine.getTaskService();
        Task task1 = taskService.createTaskQuery()
                .processInstanceId("5001") // 2501  5001
                .singleResult();
        printTask(task1);
//        RuntimeService runtimeService = processEngine.getRuntimeService();
//        ProcessInstance instance = runtimeService.createProcessInstanceQuery()
//                .processInstanceId(task1.getProcessInstanceId())
//                .singleResult();
//        printProcessInstance(instance);
//        String activityId = instance.getActivityId();
//        System.out.println(signal + activityId);
        RepositoryService repositoryService = processEngine.getRepositoryService();
        ProcessDefinition definition = repositoryService.getProcessDefinition(task1.getProcessDefinitionId());
        ProcessDefinitionEntity entity = (ProcessDefinitionEntity) definition;
        ActivityImpl activity = entity.findActivity(task1.getTaskDefinitionKey());
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

    /**
     * 查询历史任务
     */
    @Test
    public void testQueryHistoryTask() {
        HistoryService historyService = processEngine.getHistoryService(); // 与历史相关的数据
        String assignee = "张三";
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
//                .taskAssignee(assignee)
//                .taskOwner(assignee)
//                .taskCandidateUser(assignee)
                .list();
//        if (list != null && list.size() > 0){
//            for(HistoricTaskInstance instance : list){
//                printHistoryTask(instance);
//            }
//        }

        String processInstanceId = "2501";
        List<HistoricIdentityLink> list1 = historyService.getHistoricIdentityLinksForProcessInstance(processInstanceId);
        for (HistoricIdentityLink  link : list1){
            System.out.println(signal + "\nuserId : " + link.getUserId()
            + "\ntask type : " + link.getType()
            + "\ntask id : " + link.getTaskId()
            + "\nprocess instance id : " + link.getProcessInstanceId());
        }

    }

    protected void printHistoryTask(HistoricTaskInstance instance){
        System.out.println(signal + "\ntask id : " + instance.getId()
                + "\ntask name : " + instance.getName()
                + "\nprocess instance id : " + instance.getProcessInstanceId()
                + "\ntask start time : " + instance.getStartTime()
                + "\ntask end time : " + instance.getEndTime()
                + "\ntask duration : " + instance.getDurationInMillis());
    }

    /**
     * 查询历史流程实例
     */
    @Test
    public void testQueryHistProcessInstance() {
        HistoryService historyService = processEngine.getHistoryService();
        String processInstanceId = "37501";
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        if (historicProcessInstance != null){
            System.out.println(signal + "\nprocess instance id : " + historicProcessInstance.getId()
            + "\nprocess start time : " + historicProcessInstance.getStartTime()
            + "\nprocess end time : " + historicProcessInstance.getEndTime()
            + "\nprocess duration : " + historicProcessInstance.getDurationInMillis());
        }
    }

    protected void printHistoryVariableInstance(HistoricVariableInstance instance){
        System.out.println(signal + "\nvariable id : "  + instance.getId()
                + "\nprocess instance id : " + instance.getProcessInstanceId()
                + "\nvariable name : " + instance.getVariableName()
                + "\nvariable value: " + instance.getValue());
    }
}
