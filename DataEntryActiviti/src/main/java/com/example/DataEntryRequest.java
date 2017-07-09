package com.example;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.activiti.engine.impl.form.StringFormType;
import org.activiti.engine.impl.util.Activiti5Util;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

import com.example.util.ActivitiConstants;

public class DataEntryRequest {

	
	
	public static void main(String[] args) {
		/*
		 * Load Properties files
		 */
		Properties jdbc = new Properties();
		Properties form = new Properties();
		InputStream jdbcInput = null;
		InputStream formInput = null;
		
		try {
			jdbcInput = ClassLoader.getSystemResourceAsStream("jdbc.properties");
			formInput = ClassLoader.getSystemResourceAsStream("form.properties");
			jdbc.load(jdbcInput);
			form.load(formInput);
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
			if (formInput != null) {
				try {
					formInput.close();
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
		Deployment deployment = repositoryService
				.createDeployment()
				.addClasspathResource(form.getProperty("PROCESS"))
				.deploy();
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
				.deploymentId(deployment.getId()).singleResult();
		System.out.println("Found process definition [" + processDefinition.getName() + "] with id ["
				+ processDefinition.getId() + "]");
		
		/*
		 * Get the process instance
		 */
		RuntimeService runtimeService = processEngine.getRuntimeService();
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(ActivitiConstants.SAMPLE_PROCESS_ID);
		System.out.println("Sample constructs process started with process instance id ["
				+ processInstance.getProcessInstanceId() + "] key [" + processInstance.getProcessDefinitionKey() + "]");
		
		/*
		 * Create references to the different services
		 */
		TaskService taskService = processEngine.getTaskService();
		FormService formService = processEngine.getFormService();
		HistoryService historyService = processEngine.getHistoryService();
		
		Scanner scanner = new Scanner(System.in);
		while(processInstance != null && !processInstance.isEnded()) {
			List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup(ActivitiConstants.USER_GROUP).list();
			System.out.println("Active outstanding tasks: [" + tasks.size() + "]");
			
			/*
			 * Fill in the task form
			 */
			for(int i = 0; i < tasks.size(); i++) {
				Task task = tasks.get(i);
				System.out.println("Processing Task [" + task.getName() + "]");
				Map<String, Object> variables = new HashMap<String, Object>();
				TaskFormData formData = formService.getTaskFormData(task.getId());
				formData.getFormProperties().stream().forEach(property -> {
					if (StringFormType.class.isInstance(property.getType())) {
						System.out.println(property.getName() + "?");
						String value = scanner.nextLine();
						variables.put(property.getId(), value);
					} else {
						System.out.println("<form type not supported>");
					}
				});
				
				/*
				 * Complete the task
				 */
				taskService.complete(task.getId(), variables);
				HistoricActivityInstance endActivity = null;
				
				/*
				 * List completed activities
				 */
				List<HistoricActivityInstance> activities = historyService.createHistoricActivityInstanceQuery()
						.processInstanceId(processInstance.getId())
						.finished()
						.orderByHistoricActivityInstanceEndTime()
						.asc()
						.list();
				
				/*
				 * Display activiti sequemce
				 */
				for(HistoricActivityInstance activity : activities) {
					/*
					 * Log start events
					 */
					if(activity.getActivityType().equals(ActivitiConstants.START_EVENT)) {
						System.out.println("BEGIN " + processDefinition.getName() + " ["
								+ processInstance.getProcessDefinitionKey() + "] " + activity.getStartTime());
					}
					/*
					 * Catch the end event OR log intermediates
					 */
					if (activity.getActivityType().equals(ActivitiConstants.END_EVENT)) {
						endActivity = activity;
					} else {
						System.out.println("-- " + activity.getActivityName() + " [" + activity.getActivityId() + "] "
								+ activity.getDurationInMillis() + " ms");
					}
				}
				/*
				 * Log the end event
				 */
				if (endActivity != null) {
					System.out.println("-- " + endActivity.getActivityName() + " [" + endActivity.getActivityId() + "] "
							+ endActivity.getDurationInMillis() + " ms");
					System.out.println("COMPLETE " + processDefinition.getName() + " ["
							+ processInstance.getProcessDefinitionKey() + "] " + endActivity.getEndTime());
				}
				/*
				 * Re-query process instance to make sure latest state is available
				 */
				processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstance.getId())
						.singleResult();
			}
			scanner.close();
		}
	}

}
