package com.tuyu;

import com.tuyu.entity.Person;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricVariableInstance;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
 *
 * @author tuyu
 * @date 5/11/18
 * Stay Hungry, Stay Foolish.
 */
public class ProcessVariablesTest extends LeaveProcessTest {


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
        super.testStart(); // process instance id 60001
    }

    /**
     * 设置流程变量
     */
    @Test
    public void testSetProcessVariables() {
        TaskService taskService = processEngine.getTaskService();
//        String taskId = "50005";
//        String taskId = "55002";
        String taskId = "60005";
        // 基本类型
//        taskService.setVariableLocal(taskId, "leave days", 5); // setVariableLocal方法表示与当前任务绑定，下一个任务节点看不到该流程变量；如果下一个任务也设置该值，将会产生新的流程变量记录
//        taskService.setVariableLocal(taskId, "leave days", 3); // setVariableLocal方法表示与当前任务绑定，下一个任务节点看不到该流程变量；如果下一个任务也设置该值，将会产生新的流程变量记录
//        taskService.setVariable(taskId, "leave time", new Date());
//        taskService.setVariable(taskId, "leave reason", "go home to see family"); // 下一个任务如果也设置该值，那么会覆盖
//        taskService.setVariable(taskId, "leave reason", "go home to see family, and eat together"); // 下一个任务如果也设置该值，那么会覆盖
        // javaBean（序列化之后），要求javaBean的属性不能发生变化
        Person person = new Person();
        person.setId(10);
        person.setName("scutuyu");
        taskService.setVariable(taskId, "person info", person);
        System.out.println(signal +"set process variables success");
    }

    /**
     * 获取流程变量
     */
    @Test
    public void testGetProcessVariables() {
        TaskService taskService = processEngine.getTaskService();
//        String taskId = "50005";
//        String taskId = "55002";
        String taskId = "60005";
//        Object days = taskService.getVariable(taskId, "leave days");
//        Object reason = taskService.getVariable(taskId, "leave reason");
//        Object time = taskService.getVariable(taskId, "leave time");
//        System.out.println(signal + "\nleave day : " + days
//        + "\nleave time : " + time
//        + "\nleave reason : " + reason);
        Object person = taskService.getVariable(taskId, "person info");
        System.out.println(signal + person);
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
