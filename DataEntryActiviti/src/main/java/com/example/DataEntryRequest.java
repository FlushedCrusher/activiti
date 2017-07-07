package com.example;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;

public class DataEntryRequest {

	
	
	public static void main(String[] args) {
		/*
		 * Load JDBC Properties file
		 */
		Properties jdbc = new Properties();
		InputStream jdbcInput = null;
		
		try {
			jdbcInput = ClassLoader.getSystemResourceAsStream("jdbc.properties");
			jdbc.load(jdbcInput);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (jdbcInput != null) {
				try {
					jdbcInput.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		/*
		 * Set up process configuration & build process engine
		 */
		ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration()
				.setJdbcUrl(jdbc.getProperty("URL"))
				.setJdbcUsername(jdbc.getProperty("USER"))
				.setJdbcPassword(jdbc.getProperty("PASSWORD"))
				.setJdbcDriver(jdbc.getProperty("DRIVER"))
				.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
		ProcessEngine processEngine = cfg.buildProcessEngine();
		
		String pName = processEngine.getName();
		String pVersion = ProcessEngine.VERSION;
		System.out.println("ProcessEngine [" + pName + "] Version [" + pVersion + "]");
		
		/*
		 * Get repository service
		 */
		RepositoryService repositoryService = processEngine.getRepositoryService();
		/*
		 * Get deployment with our process definition
		 */
		Deployment deployment = repositoryService.createDeployment().addClasspathResource("constructs.bpmn20.xml")
				.deploy();
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
				.deploymentId(deployment.getId()).singleResult();
		System.out.println("Found process definition [" + processDefinition.getName() + "] with id ["
				+ processDefinition.getId() + "]");
	}

}
