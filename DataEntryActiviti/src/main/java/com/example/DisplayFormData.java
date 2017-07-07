package com.example;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public class DisplayFormData implements JavaDelegate {

	Properties prop = new Properties();
	String[] attrArray = null;
	List<String> attrList = new ArrayList<String>();
	InputStream input = null;
	
	@Override
	public void execute(DelegateExecution execution) {
		
		try {
			input = ClassLoader.getSystemResourceAsStream("form.properties");
			prop.load(input);
			attrArray = prop.get("ATTRIBUTES").toString().split(",");
			attrList = new ArrayList<String>(Arrays.asList(attrArray));
			displayFormData(execution, attrList);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	private void displayFormData(DelegateExecution execution, List<String> attrs) {
		attrs.stream().forEach(attr -> {
			System.out.println("[" + attr + "," + execution.getVariable(attr) + "]");
		});
	}

}
