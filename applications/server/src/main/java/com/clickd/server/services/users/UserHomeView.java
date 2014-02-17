package com.clickd.server.services.users;

import com.yammer.dropwizard.views.View;

public class UserHomeView extends View {

	private String title;
	private String memberEmail;
	private String token;
	private String profileImage;
	private String firstName;
	
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
	
	public void setProfileImage(String imageUrl)
	{
		this.profileImage = imageUrl;
	}
	
	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}
	
	public String getFirstName()
	{
		return this.firstName;
	}
	
	public String getProfileImage()
	{
		return this.profileImage;
	}
	
	public String getToken() {
		return this.token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
	
}
