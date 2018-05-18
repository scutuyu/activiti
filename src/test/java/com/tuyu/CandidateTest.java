package com.tuyu;

import com.tuyu.util.ResourceUtil;
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
 *
 * @author tuyu
 * @date 5/18/18
 * Stay Hungry, Stay Foolish.
 */
public class CandidateTest extends BaseTest{

    /**
     * 发布candidate的流程定义
     */
    @Test
    public void testDeployCandidate() {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        String resouce = "candidate.bpmn";
        Deployment deploy = repositoryService.createDeployment()
                .name("测试Candidate")
                .addInputStream(resouce, ResourceUtil.getInputString(this.getClass(), resouce))
                .deploy();
        printDeployment(deploy);
    }

    /**
     * 启动流程定义
     */
    @Test
    public void testStartProcessInstance() {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        Map<String, Object> map = new HashMap<>(1);
        map.put("users", "tuyu,ty,scutuyu");
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("testCandidate", map);
        printProcessInstance(instance);
    }

    /**
     * 删除流程定义
     */
    @Test
    public void testDeleteProcessDefine() {
        String deploymentId = "";
        super.deleteProcessDefine(deploymentId);
    }

    /**
     * 先调用setAssignee，再调用setAssignee和claim能否成功
     */
    @Test
    public void testAssignee() {
        String taskId = "70005";
//        String userId = "zhang";
//        String userId = "li";
        String userId = "wang";
        TaskService taskService = processEngine.getTaskService();
//        taskService.setAssignee(taskId, userId);
        taskService.claim(taskId, userId);
        System.out.println(signal + userId + " is the assignee");
    }

    /**
     * 先调用claim设置候选人之外的人，再调用claim设置候选人之外的人，最后调用setAssignee设置候选人之外的人能能否成功
     */
    @Test
    public void testClaim() {
        String taskId = "80005";
//        String userId = "zhang";
//        String userId = "li";
//        String userId = null;
//        String userId = "li";
//        String userId = "wang";
//        String userId = "wang2";
        String userId = "wang3";
        TaskService taskService = processEngine.getTaskService();
//        taskService.claim(taskId, userId);
//        taskService.setAssignee(taskId, userId);
        taskService.claim(taskId, userId);
        System.out.println(signal + userId + " is the assignee");
    }

    /**
     * 添加组任务成员
     */
    @Test
    public void testAddCandidate() {
        TaskService taskService = processEngine.getTaskService();
        String taskId = "80005";
        String userId = "tuy";
        taskService.addCandidateUser(taskId, userId);
        System.out.println(signal + userId + " is the candidate of " + taskId);
    }
}
