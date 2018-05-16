package com.tuyu;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
 * @author tuyu
 * @date 5/11/18
 * Stay Hungry, Stay Foolish.
 */
public class ProcessDefineTest extends LeaveProcessTest {

    private static final Logger logger = LoggerFactory.getLogger(ProcessDefineTest.class);

    /**
     * 部署流程定义
     */
    @Test
    public void testProcessDefine() {
//        deploy("流程定义"); // process define id = 20001
        deployZip("zip部署流程定义"); // process define id = 27501
    }

    /**
     * 查询流程定义
     */
    @Test
    public void testQueryProcessDefine() {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        String key = "leaveProcess";
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(key)
                // 指定查询条件，where条件
//                .deploymentId("")
//                .processDefinitionId("")

                // 排序
                .orderByProcessDefinitionVersion().asc() // 按照发布版本升序排序

                // 返回结果集
                .list(); // 结果集是list
//                .singleResult();// 结果集是单条记录
//                .count();// 结果是记录数
//                .listPage(startIndext, rows); // 分页查询，第几条开始，一共查询多少条
        if (list != null && list.size() > 0){
            for (ProcessDefinition definition : list){
                printProcessDefine(definition);
            }
        }
    }


    public void printProcessDefine(ProcessDefinition definition){
        System.out.println(signal + "\nprocess define id : " + definition.getId() // 规则是 key:version:randomNumber
                + "\nprocess name : " + definition.getName() // 对应bpmn文件中name属性
                + "\nprocess define key : " + definition.getKey() // 对应bpmn文件中id属性
                + "\nprocess define version : " + definition.getVersion() // 当bpmn文件的id相同时，版本升级，默认是版本是1
                + "\nasset file bpmn : " + definition.getResourceName()
                + "\nasset file diagram : " + definition.getDiagramResourceName()
                + "\ndeploy id : " + definition.getDeploymentId());
    }


    /**
     * 删除流程定义
     */
    @Test
    public void testDeleteProcessDefine() {
        RepositoryService repositoryService = processEngine.getRepositoryService();
//        String deploymentId = "1";
        String deploymentId = "27501";
//        repositoryService.deleteDeployment(deploymentId); // 不带级联删除，只能删除没有启动的流程，如果流程启动，删除时会抛异常
        repositoryService.deleteDeployment(deploymentId, true); // 级联删除，不管流程是否启动，都可以删除
        System.out.println(signal + "delete success");
    }

    /**
     * 查看流程图
     */
    @Test
    public void testQueryDiagram() {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        // 将图片放到文件夹下
        String deploymentId = "22501";
        String fileName = "";
        List<String> names = repositoryService.getDeploymentResourceNames(deploymentId);
        for (String name : names){
            if (name.indexOf(".png") >= 0){
                fileName = name;
            }
        }
        InputStream inputStream = repositoryService.getResourceAsStream(deploymentId, fileName);
        // 将输入流写到本地
        File file = new File("picture/" + fileName);
        try {
            FileUtils.copyInputStreamToFile(inputStream, file);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        System.out.println(signal + "file has been wrote at " + file.getName());
    }

    /**
     * 查询最新版本流程定义
     */
    @Test
    public void testQueryLastProcessDefine() {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()
                .orderByProcessDefinitionVersion().asc()
                .list();
        Map<String, ProcessDefinition> map = new LinkedHashMap<>();
        if (list != null && list.size() > 0){
            for (ProcessDefinition definition : list){
                map.put(definition.getKey(), definition);
            }
        }
        list = new ArrayList<>(map.values());
        for (ProcessDefinition definition : list){
            printProcessDefine(definition);
        }
    }

    /**
     * 根据流程定义的key删除所有流程定义
     */
    @Test
    public void testDeleteProcessDefineByKey() {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        String key = "leaveProcess";
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(key)
                .list();
        for (ProcessDefinition definition : list){
            repositoryService.deleteDeployment(definition.getDeploymentId(), true);
        }
        System.out.println(signal + "delete all process define by key : " + key);
    }
}
