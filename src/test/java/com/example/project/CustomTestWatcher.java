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

public class CustomTestWatcher implements TestWatcher {

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
				
				System.out.println(bambooPlanKey);
				System.out.println(bambooBuildKey);
			} else {
				//this.logTestResult(testPlanKeyValue.toString(), testCaseKeyValue.toString(), "PASS");
				System.out.println("here");
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
			Object testObj = context.getRequiredTestInstance();
			Field testPlanKeyField = testObj.getClass().getDeclaredField("testPlanKey");
			Field testCaseKeyField = testObj.getClass().getDeclaredField("testCaseKey");
			Object testPlanKeyValue = testPlanKeyField.get(testObj);
			Object testCaseKeyValue = testCaseKeyField.get(testObj);
						
			//this.logTestResult(testPlanKeyValue.toString(), testCaseKeyValue.toString(), "FAIL");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
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
	private void logTestResult(String testPlanKey, String testCaseKey, String status) {
		try {
			URL url = new URL("http://10.25.33.47:8084/ords/dev_anthem/twb/service/run");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			
			RequestBody bodyObj = new RequestBody(testPlanKey, testCaseKey, status);
			
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
}
