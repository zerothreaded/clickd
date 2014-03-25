package com.clickd.server.services.users;

import java.util.ArrayList;

public class ComparisonResponse
{
	private ArrayList<String> agree;

	private ArrayList<String> disagree;
	
	
	public ArrayList<String> getDisagree() {
		return disagree;
	}


	public void setDisagree(ArrayList<String> disagree) {
		this.disagree = disagree;
	}


	public ComparisonResponse() {
		this.agree = new ArrayList<String>();
		this.disagree = new ArrayList<String>();
	}
	
	
	public ArrayList<String> getAgree() {
		return agree;
	}

	public void setAgree(ArrayList<String> agree) {
		this.agree = agree;
	}
	
	
}