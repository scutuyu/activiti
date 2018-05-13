package com.tuyu;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author tuyu
 * @date 5/10/18
 * Stay Hungry, Stay Foolish.
 */
public class LeaveProcess {

    private static final Logger logger = LoggerFactory.getLogger(LeaveProcess.class);
    private static final String signal = "------------> ";

    public static void main(String[] args) {
        // 构造流程引擎
        ProcessEngine processEngine = ProcessEngineConfiguration
                .createStandaloneProcessEngineConfiguration()
                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE)
                .setJdbcUrl("jdbc:mysql://localhost:3306/activiti?useUnicode=true&characterEncoding=UTF-8")
                .setJdbcDriver("com.mysql.jdbc.Driver")
                .setJdbcUsername("root")
                .setJdbcPassword("123456")
                .buildProcessEngine();
                System.out.println(signal + "\nprocess engine : " + processEngine);

    }


}