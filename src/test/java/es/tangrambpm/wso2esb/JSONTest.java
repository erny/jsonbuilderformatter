package es.tangrambpm.wso2esb;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import es.tangrambpm.wso2esb.JSONOMBuilder;
import es.tangrambpm.wso2esb.JSONMessageFormatter;

import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMSourcedElement;
import org.junit.Test;

import java.io.*;

public class JSONTest {
	String s1 = "{ \"hola\": \"Mundo!\", \"Esto\": \"es una prueba\" }";
	String s2 = "[ \"hola\", \"Mundo!\", \"Esto\", \"es una prueba\" ]";

	@Test 
	public void test_builder_jsonObject() {
		testBuilder("Object", s1);
	}

	@Test
	public void test_builder_jsonArray() {
		testBuilder("Array", s2);
	}

	@Test
	public void test_formatter_jsonObject() {
		testFormatter(s1);
	}

	@Test
	public void test_formatter_jsonArray() {
		testFormatter(s2);
	}

	
	OMElement testBuilder(String obj, String s) {
		InputStream istream = new java.io.ByteArrayInputStream(s.getBytes());
		MessageContext mc = new MessageContext();
		JSONOMBuilder ob = new JSONOMBuilder();
		try {
			OMElement om = ob.processDocument(istream, "application/json", mc);
			System.out.println(obj + ": " + om.toString());
			return om;
		} catch (AxisFault e) {
			System.out.println("ERROR en " + obj);
			e.printStackTrace();
		}
		return null;
	}

	void testFormatter(String s){
		// First build the message builder to process the message
		InputStream istream = new java.io.ByteArrayInputStream(s.getBytes());
		MessageContext mc = new MessageContext();
		JSONOMBuilder ob = new JSONOMBuilder();
		OMSourcedElement omse;
		try {
			omse = (OMSourcedElement) ob.processDocument(istream, "application/json", mc);
		} catch (AxisFault e) {
			e.printStackTrace();
			return;
		}
        // go for the formatte
		JSONMessageFormatter of = new JSONMessageFormatter();
		mc.setDoingREST(true);
		OMDataSource datasource = omse.getDataSource();
		String str = of.getStringToWrite(datasource);
		System.out.println(str);
	}
}
