package com.clickd.server.services.users;

import com.yammer.dropwizard.views.View;

public class UserHomeView extends View {

	private String title;
	private String memberEmail;
	
	public UserHomeView(String title) {
		super("home.ftl");
		this.title = title;
	}
	
	public String getTitle()
	{
		return "CLICKD MEMBER HOME";
	}

	public String getMemberEmail() {
		return this.memberEmail;
	}
	
	public void setMemberEmail(String memberEmail) {
		this.memberEmail = memberEmail;
	}
	
}
