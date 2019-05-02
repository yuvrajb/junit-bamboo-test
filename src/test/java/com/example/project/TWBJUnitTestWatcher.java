package com.example.project;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.Properties;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TWBJUnitTestWatcher implements TestWatcher {
	// URI Endpoints
	private String runEndpoint = "http://10.25.33.47:8084/ords/dev_anthem/twb/service/run";
	private String bambooEndpoint = "http://10.25.33.47:8084/ords/dev_anthem/twb/service/bamboo";

	@Override
	public void testDisabled(ExtensionContext context, Optional<String> reason) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void testSuccessful(ExtensionContext context) {
		// TODO Auto-generated method stub
		try {
			// fetch the object instance
			Object testObj = context.getRequiredTestInstance();
			
			// try to fetch the test plan key & test case key
			Field testPlanKeyField = null;
			Field testCaseKeyField = null;
			Object testPlanKeyValue = null;
			Object testCaseKeyValue = null;
			
			try {
				testPlanKeyField = testObj.getClass().getDeclaredField("testPlanKey");
				testPlanKeyValue = testPlanKeyField.get(testObj);
			} catch (NoSuchFieldException e) {
				testPlanKeyField = null;
			}
			
			try {
				testCaseKeyField = testObj.getClass().getDeclaredField("testCaseKey");
				testCaseKeyValue = testCaseKeyField.get(testObj);
			} catch (NoSuchFieldException e) {
				testCaseKeyField = null;
			}
			
			// if test plan key / test case key is null then try to find the bamboo build number
			if(testPlanKeyValue == null || testCaseKeyValue == null) {
				InputStream stream = this.getClass().getResourceAsStream("/proj.properties"); 
				Properties prop = new Properties();
				prop.load(stream);
				
				String bambooPlanKey = prop.getProperty("bamboo.plan.key");
				String bambooBuildKey = prop.getProperty("bamboo.build.key");
				String bambooJobKey = prop.getProperty("bamboo.job.key");
				
				System.out.println(this.getClass().getResource("/proj.properties").getPath());
				System.out.println(bambooPlanKey);
				System.out.println(bambooBuildKey);
				System.out.println(bambooJobKey);
				this.logTestRunBambooResult(bambooPlanKey, bambooJobKey, bambooBuildKey, "PASS");
			} else {
				this.logTestRunResult(testPlanKeyValue.toString(), testCaseKeyValue.toString(), "PASS");
			}			
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	@Override
	public void testAborted(ExtensionContext context, Throwable cause) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void testFailed(ExtensionContext context, Throwable cause) {
		// TODO Auto-generated method stub
		try {
			// fetch the object instance
			Object testObj = context.getRequiredTestInstance();
			
			// try to fetch the test plan key & test case key
			Field testPlanKeyField = null;
			Field testCaseKeyField = null;
			Object testPlanKeyValue = null;
			Object testCaseKeyValue = null;
			
			try {
				testPlanKeyField = testObj.getClass().getDeclaredField("testPlanKey");
				testPlanKeyValue = testPlanKeyField.get(testObj);
			} catch (NoSuchFieldException e) {
				testPlanKeyField = null;
			}
			
			try {
				testCaseKeyField = testObj.getClass().getDeclaredField("testCaseKey");
				testCaseKeyValue = testCaseKeyField.get(testObj);
			} catch (NoSuchFieldException e) {
				testCaseKeyField = null;
			}
			
			// if test plan key / test case key is null then try to find the bamboo build number
			if(testPlanKeyValue == null || testCaseKeyValue == null) {
				InputStream stream = this.getClass().getResourceAsStream("/proj.properties"); 
				Properties prop = new Properties();
				prop.load(stream);
				
				String bambooPlanKey = prop.getProperty("bamboo.plan.key");
				String bambooBuildKey = prop.getProperty("bamboo.build.key");
				String bambooJobKey = prop.getProperty("bamboo.job.key");
				
				System.out.println(this.getClass().getResource("/proj.properties").getPath());
				System.out.println(bambooPlanKey);
				System.out.println(bambooBuildKey);
				System.out.println(bambooJobKey);
				this.logTestRunBambooResult(bambooPlanKey, bambooJobKey, bambooBuildKey, "FAIL");
			} else {
				this.logTestRunResult(testPlanKeyValue.toString(), testCaseKeyValue.toString(), "FAIL");
			}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * calls TWB REST Services
	 * @param testPlanKey
	 * @param testCaseKey
	 * @param status
	 */
	private void logTestRunResult(String testPlanKey, String testCaseKey, String status) {
		try {
			URL url = new URL(this.runEndpoint);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			
			TestRunRequestBody bodyObj = new TestRunRequestBody(testPlanKey, testCaseKey, status);
			
			ObjectMapper mapper = new ObjectMapper();
			String body = mapper.writeValueAsString(bodyObj);
			
			OutputStream os = conn.getOutputStream();
			os.write(body.getBytes());
			os.flush();
			
			int resp = conn.getResponseCode();
			
			System.out.println("POST Request SENT " + resp);
			
			conn.disconnect();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void logTestRunBambooResult(String bambooPlanKey, String bambooJobKey, String bambooBuildKey, String status) {
		URL url;
		try {
			url = new URL(this.bambooEndpoint);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("PUT");
			conn.setRequestProperty("Content-Type", "application/json");
			
			TestRunBambooRequestBody bodyObj = new TestRunBambooRequestBody(bambooPlanKey, bambooJobKey, bambooBuildKey, status);
						
			ObjectMapper mapper = new ObjectMapper();
			String body = mapper.writeValueAsString(bodyObj);			
			
			OutputStream os = conn.getOutputStream();
			os.write(body.getBytes());
			os.flush();
			
			int resp = conn.getResponseCode();
			
			System.out.println("PUT Request SENT " + resp);
			
			conn.disconnect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
