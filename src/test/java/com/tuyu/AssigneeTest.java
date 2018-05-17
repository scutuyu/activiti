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
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery()
                .taskId("52505")
                .singleResult();
        if (task != null){
            printTask(task);
        }else {
            System.out.println(signal + "task is null");
        }

        taskService.setAssignee(task.getId(), "wang");
        System.out.println(signal + "li is assignee");
//        task = taskService.createTaskQuery()
//                .singleResult();
//        printTask(task);
//        System.out.println(signal + " assignee : " + task.getAssignee());
    }

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
     *
     */
    @Test
    public void testCandidate() {
        TaskService taskService = processEngine.getTaskService();
        String userId = "zhang";
        Task task = taskService.createTaskQuery()
//                .processInstanceId("45001")
//                .taskCandidateUser("ty")
                .taskAssignee("ty")
//                .taskCandidateOrAssigned(userId)
                .singleResult();
        if (task != null){
            printTask(task);
        }else {
            System.out.println(signal + "task is null");
        }

        // claim与setAssignee区别在于claim领取之后别人不可以再领取不然会报错而setAssignee则不然

//        Map<String, Object> variables = taskService.getVariables(task.getId());
//        Map<String, Object> variablesLocal = taskService.getVariablesLocal(task.getId());
//        System.out.println(signal + variables);
//        System.out.println(signal + variablesLocal);
//        task = taskService.createTaskQuery()
//                .taskId("52505")
//                .singleResult();
//        taskService.claim(task.getId(), null);
//        taskService.setAssignee(task.getId(), null);
//        System.out.println(signal + userId +  " claim success");
    }
}
