package com.example.project;

public class TestRunBambooRequestBody {
	public String bamboo_plan_key;
	public String bamboo_job_key;
	public String bamboo_build_key;
	public String status;
	
	public TestRunBambooRequestBody(String bamboo_plan_key, String bamboo_job_key, String bamboo_build_key,
			String status) {
		super();
		this.bamboo_plan_key = bamboo_plan_key;
		this.bamboo_job_key = bamboo_job_key;
		this.bamboo_build_key = bamboo_build_key;
		this.status = status;
	}
}
