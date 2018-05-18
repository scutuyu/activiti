package com.tuyu;

import com.tuyu.util.ResourceUtil;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.zip.ZipInputStream;

/**
 * <pre>
 * ////////////////////////////////////////////////////////////////////
 * //                          _ooOoo_                               //
 * //                         o8888888o                              //
 * //                         88" . "88                              //
 * //                         (| ^_^ |)                              //
 * //                         O\  =  /O                              //
 * //                      ____/`---'\____                           //
 * //                    .'  \\|     |//  `.                         //
 * //                   /  \\|||  :  |||//  \                        //
 * //                  /  _||||| -:- |||||-  \                       //
 * //                  |   | \\\  -  /// |   |                       //
 * //                  | \_|  ''\---/''  |   |                       //
 * //                  \  .-\__  `-`  ___/-. /                       //
 * //                ___`. .'  /--.--\  `. . ___                     //
 * //              ."" '<  `.___\_<|>_/___.'  >'"".                  //
 * //            | | :  `- \`.;`\ _ /`;.`/ - ` : | |                 //
 * //            \  \ `-.   \_ __\ /__ _/   .-` /  /                 //
 * //      ========`-.____`-.___\_____/___.-`____.-'========         //
 * //                           `=---='                              //
 * //      ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^        //
 * //             佛祖保佑       永无BUG     永不修改                   //
 * ////////////////////////////////////////////////////////////////////
 * </pre>
 * tuyu于5/16/18祈祷...
 * 工作流测试类的基类
 * <p>提供了ProcessEngine类的实例，以及常见的打印输出的方法</p>
 * @author tuyu
 */
public class BaseTest {

    protected ProcessEngine processEngine;

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
     * 删除流程定义
     * @param deploymentId 流程定义发布id
     */
    protected void deleteProcessDefine(String deploymentId){
        RepositoryService repositoryService = processEngine.getRepositoryService();
        repositoryService.deleteDeployment(deploymentId, true);
        System.out.println(signal + "has delete deployment, id is : " + deploymentId);
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
        printDeployment(deploy);
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
        printDeployment(deploy);
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
        printDeployment(deploy);
    }

    /** 判断流程实例是否结束
     * @param processInstanceId 流程实例id
     * @return true，已经结束， false，还未结束
     */
    protected boolean processInstanceClosed(String processInstanceId){
        RuntimeService runtimeService = processEngine.getRuntimeService();
        Execution execution = runtimeService.createExecutionQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        if (execution == null){
            return true;
        }else {
            return false;
        }
    }

    protected void printProcessInstanceClosed(boolean b, String processInstanceId){
        if (b){
            System.out.println(signal + processInstanceId + " has closed");
        }else {
            System.out.println(signal + processInstanceId + " has not closed");
        }
    }

    protected void printDeployment(Deployment deploy){
        System.out.println(signal + "\nprocess define id : " + deploy.getId()
                + "\nprocess define name : " + deploy.getName());
    }

    /**
     * 打印流程实例对象
     * @param processInstance
     */
    protected void printProcessInstance(ProcessInstance processInstance){
        System.out.println(signal
                + "\nprocess instance id " + processInstance.getId() // process instance id = 30001
                + "\nprocess define id : " + processInstance.getProcessDefinitionId()
                + "\nprocess define name : " + processInstance.getProcessDefinitionName());
    }

    /**
     * 打印任务对象
     * @param task
     */
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
     * 打印历史任务对象
     * @param instance
     */
    protected void printHistoryTask(HistoricTaskInstance instance){
        System.out.println(signal + "\ntask id : " + instance.getId()
                + "\ntask name : " + instance.getName()
                + "\nprocess instance id : " + instance.getProcessInstanceId()
                + "\ntask start time : " + instance.getStartTime()
                + "\ntask end time : " + instance.getEndTime()
                + "\ntask duration : " + instance.getDurationInMillis());
    }

    /**
     * 打印历史变量对象
     * @param instance
     */
    protected void printHistoryVariableInstance(HistoricVariableInstance instance){
        System.out.println(signal + "\nvariable id : "  + instance.getId()
                + "\nprocess instance id : " + instance.getProcessInstanceId()
                + "\nvariable name : " + instance.getVariableName()
                + "\nvariable value: " + instance.getValue());
    }

    protected void printHistoryProcessInstance(HistoricProcessInstance historicProcessInstance){
        System.out.println(signal + "\nprocess define id : " + historicProcessInstance.getProcessDefinitionId()
                + "\nprocess instance id : " + historicProcessInstance.getId()
                + "\nprocess instance start time : " + historicProcessInstance.getStartTime()
                + "\nprocess instance end time : " + historicProcessInstance.getEndTime()
                + "\nprocess instance duration : " + historicProcessInstance.getDurationInMillis());
    }

    protected void printCompleteTask(String taskId){
        System.out.println(signal + " complete task id : " + taskId);
    }
}
