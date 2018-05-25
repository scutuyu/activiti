package com.tuyu;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;

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
 * tuyu于5/21/18祈祷...
 * 监听器测试类
 * @author tuyu
 * @date 5/21/18
 * Stay Hungry, Stay Foolish.
 */
public class ListenerTest extends BaseTest{

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
                .addClasspathResource("listener.bpmn")
                .name("监听器测试部署")
                .deploy();
        printDeployment(deploy);
    }

    /**
     * 启动流程实例
     */
    @Test
    public void testStartProcessInstance() {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        String key = "testListener";
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(key);
        printProcessInstance(instance); // 142501
    }

    @Test
    public void test() {

    }
}
