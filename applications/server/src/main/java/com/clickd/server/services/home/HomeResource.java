package com.clickd.server.services.home;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import com.clickd.server.dao.SessionDao;
import com.clickd.server.model.Session;
import com.clickd.server.services.users.UserHomeView;
import com.yammer.dropwizard.views.View;
import com.yammer.metrics.annotation.Timed;

@Path("/home")
@Produces(MediaType.TEXT_HTML)
public class HomeResource {

	private SessionDao sessionDao;

	public HomeResource(String template, String defaultName) {
		
	}

	@GET
	@Timed
	public View getHome(@Context HttpServletRequest request, 
			@Context HttpServletResponse response,
			@Context HttpHeaders headers) 
	{
		Cookie[] allCookies = request.getCookies();
		for (int i = 0; i < allCookies.length; i++) {
			Cookie cookie = allCookies[i];
			if (cookie.getName().equals("token")) {
				// Found TOKEN - someone has logged in before
				String token = cookie.getValue();
				System.out.println("FOUND TOKEN Cookie : " + token);
				Session session = sessionDao.findByToken(new Long(token));
				if (session != null) {
					// Session exists
					// TODO : Check if valid
					if (session.getIsLoggedIn()) {
						return new UserHomeView("User Home");
					} else {
						return new HomeView("clickd.com");
					}
				}
			} else {
				// NO TOKEN - new user 
				System.out.println("FOUND NON TOKEN Cookie:" + cookie.getName());
			}
		}
		return new HomeView("clickd title");
	}

	public SessionDao getSessionDao() {
		return sessionDao;
	}

	public void setSessionDao(SessionDao sessionDao) {
		this.sessionDao = sessionDao;
	}

}