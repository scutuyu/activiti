package com.tuyu;

import com.tuyu.util.ResourceUtil;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
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
 * tuyu于5/16/18祈祷...
 * 测试Assignee，candidate，owner之间的区别
 * @author tuyu
 * @date 5/16/18
 * Stay Hungry, Stay Foolish.
 */
public class AssigneeTest extends BaseTest {

    /**
     * 删除流程定义
     */
    @Test
    public void testDeleteProcessDefine() {
        String deploymentId = "77501";
        super.deleteProcessDefine(deploymentId);
    }

    /**
     * 发布candidate的流程定义
     */
    @Test
    public void testDeployCandidate() {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        String resouce = "assignee.bpmn";
        Deployment deploy = repositoryService.createDeployment()
                .name("测试Assignee")
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
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("testAssignee");
        printProcessInstance(instance);
    }

    /**
     * bpmn文件有一个任务节点，该节点没有设置assignee，也没有设置candidate user和candidate group
     * <p>通过TaskService.setAssignee(taskId, assigneeName)来设置Assignee</p>
     * 调用setAssignee方法之后:
     * <ul>
     *     <li>act_hi_actinst表中对应活动记录的assignee字段被修改为设置的值</li>
     *     <li>act_hi_identitylink表新增了一条记录，type_字段是participant, user_字段是设置的值，pro_inst_id_是流程实例id</li>
     *     <li>act_ru_identitylink表新增了一条记录，type_字段是participant, user_字段是设置的值，pro_inst_id_是流程实例id</li>
     *     <li>act_hi_comment表新增了一条记录，type_字段是event,task_id_是任务id，action_是AddUserLink,message_是tuyu_|_assignee</li>
     *     <li>act_ru_task表中对应的任务记录的assignee字段被修改为设置的值</li>
     *     <li>act_hi_task表中对应的任务记录的assignee字段被修改为设置的值</li>
     * </ul>
     */
    @Test
    public void testSetAssignee() {
        String taskId = "97504";
//        String userId = "zhang";
//        String userId = "li";
//        String userId = "wang";
//        String userId = "wang2";
//        String userId = null;
        String userId = "wang3";
        TaskService taskService = processEngine.getTaskService();
//        taskService.setAssignee(taskId, userId);
//        taskService.claim(taskId, userId);
//        taskService.setAssignee(taskId, userId);
        taskService.claim(taskId, userId);
        System.out.println(signal + userId + " is assignee");
    }


}
