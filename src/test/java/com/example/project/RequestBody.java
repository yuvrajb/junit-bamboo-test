package com.example.project;

public class RequestBody {
	public String plan_key;
	public String test_key;
	public String status;
	
	public RequestBody(String plan_key, String test_key, String status) {
		super();
		this.plan_key = plan_key;
		this.test_key = test_key;
		this.status = status;
	}
}
