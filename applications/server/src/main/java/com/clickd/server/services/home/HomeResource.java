package com.clickd.server.services.home;

import java.util.concurrent.atomic.AtomicLong;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.clickd.server.dao.EntityDao;
import com.yammer.metrics.annotation.Timed;

@Path("/home")
@Produces(MediaType.TEXT_HTML)
public class HomeResource {

	private EntityDao entityDao;

	private final String template;
	private final String defaultName;
	private final AtomicLong counter;

	public HomeResource(String template, String defaultName) {
		this.template = template;
		this.defaultName = defaultName;
		this.counter = new AtomicLong();
	}

	@GET
	@Timed
	public HomeView getPerson() {
		return new HomeView("clickd title");
	}

}
