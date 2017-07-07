package com.example;

import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.junit.Test;

public class AbstractConverterTest {

	String resource = "onboarding.bpmn20.xml";
	
	@Test
	public void readXMLFile() throws Exception {
		InputStream xmlStream = this.getClass().getClassLoader().getResourceAsStream(resource);
		XMLInputFactory xif = XMLInputFactory.newInstance();
		InputStreamReader in = new InputStreamReader(xmlStream, "UTF-8");
		XMLStreamReader xtr = xif.createXMLStreamReader(in);
		BpmnModel bpmnModel = new BpmnXMLConverter().convertToBpmnModel(xtr);
		System.out.println(bpmnModel.toString());
	}
	
}