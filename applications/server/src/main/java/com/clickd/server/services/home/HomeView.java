package com.clickd.server.services.home;

import com.yammer.dropwizard.views.View;

public class HomeView extends View {
	private final String name;

	public HomeView(String name) {
		super("home.ftl");
		this.name = name;
	}
	
	public String getTitle()
	{
		return "CLICKD HOME";
	}

	public String getName() {
		return name;
	}
}
