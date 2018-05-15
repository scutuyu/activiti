package com.tuyu;

import org.activiti.engine.HistoryService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.junit.Test;

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
 * tuyu于5/13/18祈祷...
 *
 * @author tuyu
 * @date 5/13/18
 * Stay Hungry, Stay Foolish.
 */
public class HistoryQueryTest extends LeaveProcessTest {

    /**
     * 查询历史流程实例
     */
    @Test
    public void testHistoryProcessInstance() {
        String processInstanceId = "60001";
        HistoryService historyService = processEngine.getHistoryService();
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        printHistoryProcessInstance(historicProcessInstance);

    }

    protected void printHistoryProcessInstance(HistoricProcessInstance historicProcessInstance){
        System.out.println(signal + "\nprocess define id : " + historicProcessInstance.getProcessDefinitionId()
                + "\nprocess instance id : " + historicProcessInstance.getId()
                + "\nprocess instance start time : " + historicProcessInstance.getStartTime()
                + "\nprocess instance end time : " + historicProcessInstance.getEndTime()
                + "\nprocess instance duration : " + historicProcessInstance.getDurationInMillis());
    }

    /**
     * 查询所有历史流程实例
     */
    @Test
    public void testAllHistoryProcessInstance() {
        HistoryService historyService = processEngine.getHistoryService();
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery()
                .list();
        for (HistoricProcessInstance instance : list){
            printHistoryProcessInstance(instance);
        }
    }

    /**
     * 查询历史活动
     */
    @Test
    public void testHistoryActivity() {
        HistoryService historyService = processEngine.getHistoryService();
        String processInstanceId = "60001";
        List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery() // 创建历史活动实例的查询
                .processInstanceId(processInstanceId)
                .orderByHistoricActivityInstanceStartTime().asc()
                .list();
        for (HistoricActivityInstance instance : list){
            System.out.println(signal + "\nactivity instance id : " + instance.getId()
            + "\nprocess instancess id : " + instance.getProcessInstanceId()
            + "\nactivity type : " + instance.getActivityType()
            + "\nstart time : " + instance.getStartTime()
            + "\nend time : " + instance.getEndTime()
            + "\nduration : " + instance.getDurationInMillis());
        }

    }

    /**
     * 查询历史任务
     */
    @Test
    public void testQueryHistoryTask() {
        HistoryService historyService = processEngine.getHistoryService();
        String processInstanceId = "60001";
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId)
                .list();
        for (HistoricTaskInstance instance : list){
            printHistoryTask(instance);
        }
    }

    /**
     * 查询历史流程变量
     */
    @Test
    public void testHistoryProcessVariables() {
        HistoryService historyService = processEngine.getHistoryService();
        String processInstanceId = "60001";
        List<HistoricVariableInstance> list = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(processInstanceId)
                .list();
        for (HistoricVariableInstance instance : list){
            printHistoryVariableInstance(instance);
        }
    }
}
