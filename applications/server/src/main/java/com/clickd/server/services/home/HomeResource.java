package com.clickd.server.services.home;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.yammer.metrics.annotation.Timed;

@Path("/home")
@Produces(MediaType.TEXT_HTML)
public class HomeResource {


	public HomeResource(String template, String defaultName) {
		
	}

	@GET
	@Timed
	public HomeView getHome() {
		return new HomeView("clickd title");
	}

}
