package com.clickd.server.services.application;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import com.clickd.server.dao.SessionDao;
import com.clickd.server.dao.UserDao;
import com.yammer.metrics.annotation.Timed;

@Path("/application")
@Produces(MediaType.APPLICATION_JSON)
public class ApplicationResource
{
	private UserDao userDao;
	private SessionDao sessionDao;

	@GET
	@Timed
	public String getAll(@Context HttpServletRequest request, 
			@Context HttpServletResponse response,
			@Context HttpHeaders headers)
	{
		String applicationJson =  " { \"application\" : \"clickd\" } ";
		return applicationJson;
	}

	public SessionDao getSessionDao() {
		return sessionDao;
	}

	public void setSessionDao(SessionDao sessionDao) {
		this.sessionDao = sessionDao;
	}

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

}
