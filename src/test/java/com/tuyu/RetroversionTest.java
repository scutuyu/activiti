package com.tuyu;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.junit.Test;

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
        String deploymentId = "140001";
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
        printProcessInstance(instance); // 165001
    }

    /**
     * 添加批注
     * <p>没有userId_字段</p>
     */
    @Test
    public void testAddComment() {
        TaskService taskService = processEngine.getTaskService();
        Comment comment = taskService.addComment("165004", "165001", "first comment");
        System.out.println(comment);
    }

    /**
     * 添加批注
     * <p>设置userId_字段</p>
     */
    @Test
    public void testAddComment2() {
        Authentication.setAuthenticatedUserId("scutuyu");
        TaskService taskService = processEngine.getTaskService();
        Comment comment = taskService.addComment("165004", "165001", "second comment");
        System.out.println(comment);
    }

    /**
     * 查询批注
     */
    @Test
    public void testQueryComment() {
        TaskService taskService = processEngine.getTaskService();
        List<Comment> list = taskService.getProcessInstanceComments("165001");
        for (Comment comment : list){
            System.out.println(comment);
        }
    }

    /**
     * 完成第一个任务
     */
    @Test
    public void testCompleteTask1() {
        String taskId = "180004";
        Map<String, Object> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");
        map.put("key3", "value3");
        map.put("key4", "value4");
        TaskService taskService = processEngine.getTaskService();
        taskService.setVariables(taskId, map);
        taskService.complete(taskId);
        printCompleteTask(taskId);
    }

    /**
     * 完成第二个任务，status设为no
     * <p>当重复设置已经存在的变量时，变量没有变化</p>
     * <pre>
     *     第一个任务设置了四个变量key1-key4,值分别是value1-value4
     *     第二个任务将上面这4个变量都查出来，原封不动地保存，act_ru_variable结果没有改变，变量的版本号也没有变化
     *     但是act_hi_varinst表中的变量的版本都改变了，保存了几次，版本号就加了几次
     * </pre>
     */
    @Test
    public void testCompleteTask2() {
        TaskService taskService = processEngine.getTaskService();
        String taskId = "182506";
//        Map<String, Object> map = new HashMap<>(1);
        Map<String, Object> map = taskService.getVariables(taskId);
        map.put("status", "no");
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
        String taskId = "187502";
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
