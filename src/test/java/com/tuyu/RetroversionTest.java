package com.tuyu;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;

import java.util.HashMap;
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
 * tuyu于5/18/18祈祷...
 * 不使用网关实现任务的回退功能
 * @author tuyu
 * @date 5/18/18
 * Stay Hungry, Stay Foolish.
 */
public class RetroversionTest extends BaseTest{

    /**
     * 删除流程定义
     */
    @Test
    public void testDeleteProcessDefine() {
        String deploymentId = "130001";
        super.deleteProcessDefine(deploymentId);
    }

    /**
     * 发布流程定义
     */
    @Test
    public void testDeploy() {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource("retroversion.bpmn")
                .name("回退测试部署")
                .deploy();
        printDeployment(deploy);
    }

    /**
     * 启动流程实例
     */
    @Test
    public void testStartProcessInstance() {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        String key = "testRetroversion";
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(key);
        printProcessInstance(instance); // 142501
    }

    /**
     * 完成第一个任务
     */
    @Test
    public void testCompleteTask1() {
        String taskId = "142504";
        TaskService taskService = processEngine.getTaskService();
        taskService.complete(taskId);
        printCompleteTask(taskId);
    }

    /**
     * 完成第二个任务，status设为no
     */
    @Test
    public void testCompleteTask2() {
        Map<String, Object> map = new HashMap<>(1);
        map.put("status", "no");
        String taskId = "145002";
        TaskService taskService = processEngine.getTaskService();
        taskService.complete(taskId, map);
        printCompleteTask(taskId);
    }

    /**
     * 完成第二个任务，status设为yes
     */
    @Test
    public void testCompleteTask3() {
        Map<String, Object> map = new HashMap<>(1);
        map.put("status", "yes");
        String taskId = "150002";
        TaskService taskService = processEngine.getTaskService();
        taskService.complete(taskId, map);
        printCompleteTask(taskId);
    }

    /**
     * 判断流程实例是否结束
     */
    @Test
    public void testProcessInstanceClosed() {
        String instanceId = "142501";
        boolean b = super.processInstanceClosed(instanceId);
        printProcessInstanceClosed(b, instanceId);
    }
}
