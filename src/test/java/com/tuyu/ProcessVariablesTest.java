package com.tuyu;

import com.tuyu.entity.Person;
import com.tuyu.util.ResourceUtil;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * tuyu于5/11/18祈祷...
 *
 * <p>流程变量测试类</p>
 * 设置流程变量和获取流程变量
 * @author tuyu
 * @date 5/11/18
 * Stay Hungry, Stay Foolish.
 */
public class ProcessVariablesTest extends BaseTest {


    @Override
    protected void deployInputStream(String name) {
        // 可以不用设置图片，只设置bpmn文件，程序会自动根据bpmn文件生成对应的png图片存放在数据库
        String resource = "assignee.bpmn";
        if (name == null || "".equals(name)){
            name = "测试Assignee,candidate,owner";
        }
        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deploy = repositoryService.createDeployment()
                .name(name)
                .addInputStream(resource, ResourceUtil.getInputString(getClass(), resource))
                .deploy();
        printDeployment(deploy);
    }

    /**
     * 部署流程定义
     */
    @Test
    public void testProcessDefine() {
        deployInputStream("inputStream部署流程定义"); // process define id = 47501
    }

    /**
     * 启动流程实例
     */
    @Test
    public void testStart() {
        Map<String, Object> map = new HashMap<>();
        map.put("sourceId", "1002");
        RuntimeService runtimeService = processEngine.getRuntimeService();
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("testAssignee", map);
        printProcessInstance(instance);
    }

    /**
     * 设置流程变量 TaskService
     * <p>设置流程变量有多种方式：</p>
     * <ul>
     *     <li>RunTimeService.setVariables(executionId, map)</li>
     *     <li>RunTimeService.startProcessInstanceByKey(processDefineKey,map)</li>
     *     <li>TaskService.setVariables(taskId, map)</li>
     *     <li>TaskService.complete(taskId, map)</li>
     * </ul>
     */
    @Test
    public void testSetProcessVariables1() {
        Person person = new Person();
        person.setId(10);
        person.setName("scutuyu");
        Map<String, Object> map = new HashMap<>();
        map.put("person info", person); // 数据类型可以是基本类型，也可以是对象，对象需要实现Serializable接口

        TaskService taskService = processEngine.getTaskService();
        String taskId = "25005";
        taskService.setVariables(taskId,  map);
        System.out.println(signal +"set process variables success"
        + "\n" + map);
    }

    /**
     * 设置流程变量 RunTimeService
     * <p>设置流程变量有多种方式：</p>
     * <ul>
     *     <li>RunTimeService.setVariables(executionId, map)</li>
     *     <li>RunTimeService.startProcessInstanceByKey(processDefineKey,map)</li>
     *     <li>TaskService.setVariables(taskId, map)</li>
     *     <li>TaskService.complete(taskId, map)</li>
     * </ul>
     */
    @Test
    public void testSetProcessVariables2() {
        Person person = new Person();
        person.setId(10);
        person.setName("scutuyu");
        Map<String, Object> map = new HashMap<>();
        map.put("person info", person); // 数据类型可以是基本类型，也可以是对象，对象需要实现Serializable接口
        RuntimeService runtimeService = processEngine.getRuntimeService();
        String executionId = "25001";
        runtimeService.setVariables(executionId,  map);
        System.out.println(signal +"set process variables success"
                + "\n" + map);
    }

    /**
     * 设置任务变量
     * <ul>
     *     <li>TaskService.setVariablesLocal(taskId, map)</li>
     * </ul>
     */
    @Test
    public void testSetTaskVariables() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "keen");
        TaskService taskService = processEngine.getTaskService();
        String taskId = "25005";
        taskService.setVariablesLocal(taskId, map); // setVariableLocalsLocal方法表示与当前任务绑定，下一个任务节点看不到该流程变量；如果下一个任务也设置该值，将会产生新的变量
        System.out.println(signal +"set process variables success"
                + "\n" + map);
    }

    /**
     * RunTimeService不能设置任务遍历
     */
    @Test
    public void testSetProcessVariablesLocal() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "keen");
        String executionId = "25001";
        RuntimeService runtimeService = processEngine.getRuntimeService();
        runtimeService.setVariables(executionId, map); // 本来是设置流程变量
        System.out.println(signal +"set process variables success"
        + "\n" + map);
        map.clear();
        map.put("age", "12");
        runtimeService.setVariablesLocal(executionId, map); // 本来是设置任务变量，但其实是设置流程变量，所有节点都可以访问
        System.out.println(signal +"set process variables success"
                + "\n" + map);
    }

    /**
     * 根据任务id获取流程变量 TaskService
     * <p>获取流程变量也有多种方式：</p>
     * <ul>
     *     <li>TaskService.getVariables(taskId)</li>
     * </ul>
     */
    @Test
    public void testGetProcessVariables1() {
        TaskService taskService = processEngine.getTaskService();
        String taskId = "25005";
        Map<String, Object> variablesLocal = taskService.getVariablesLocal(taskId);
        System.out.println(signal + variablesLocal);

    }

    /**
     * RuntimeService不能获取任务变量
     */
    @Test
    public void testGetProcessVariables2() {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        String executionId = "25001";
        Map<String, Object> map = runtimeService.getVariables(executionId);
        System.out.println(signal + map);
    }

    /**
     * RuntimeService不能获取任务变量
     */
    @Test
    public void testGetVariables() {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        String executionId = "25001";
        Map<String, Object> variables = runtimeService.getVariables(executionId); // 获取流程变量
        Map<String, Object> variablesLocal = runtimeService.getVariablesLocal(executionId); // 本来是获取任务变量，但是返回的结果和流程变量一样，并非任务变量
        System.out.println(signal + variables);
        System.out.println(signal + variablesLocal);
    }

    /**
     * 完成任务
     */
    @Test
    public void testComplete() {
        TaskService taskService = processEngine.getTaskService();
        String taskId = "72502";
        taskService.complete(taskId);
        System.out.println(signal + "task complete , task id : " + taskId);
    }

    /**
     * 查询流程变量历史表
     */
    @Test
    public void testQueryHistoryProcessVariables() {
        HistoryService historyService = processEngine.getHistoryService();
        List<HistoricVariableInstance> list = historyService.createHistoricVariableInstanceQuery()
                .variableName("leave days")
                .list();
        for (HistoricVariableInstance instance : list){
            printHistoryVariableInstance(instance);
        }
    }
}
