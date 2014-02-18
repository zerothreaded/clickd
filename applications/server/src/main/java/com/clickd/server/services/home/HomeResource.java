package com.clickd.server.services.home;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import com.clickd.server.dao.EntityDao;
import com.yammer.dropwizard.views.View;
import com.yammer.metrics.annotation.Timed;

@Path("/home")
@Produces(MediaType.TEXT_HTML)
public class HomeResource {

	private EntityDao entityDao;

	public HomeResource(String template, String defaultName) {
		
	}

	@GET
	@Timed
	public View getHome(@Context HttpServletRequest request, 
			@Context HttpServletResponse response,
			@Context HttpHeaders headers) 
	{
//		for (String key : headers.getCookies().keySet()) {
//			javax.ws.rs.core.Cookie cookie = headers.getCookies().get(key);
//			System.out.println("cookie.name=" + cookie.getName());
//		}
//		
//		Cookie[] allCookies = request.getCookies();
//		for (int i = 0; i < allCookies.length; i++) {
//			Cookie cookie = allCookies[i];
//			if (cookie.getName().equals("token")) {
//				// Found TOKEN - someone has logged in before
//				String token = cookie.getValue();
//				System.out.println("FOUND TOKEN Cookie : " + token);
//				Entity session = entityDao.findSessionByToken(token);
//				if (session != null) {
//					// Session exists
//					
//					// TODO : Check if valid
//					
//					return new UserHomeView("User Home");
//					
//				} else {
//					// OLD session
//				}
//				
//			} else {
//				// NO TOKEN - new user 
//				System.out.println("FOUND NON TOKEN Cookie:" + cookie.getName());
//							
//				
//			}
//		}
		return new HomeView("clickd title");
	}

	public EntityDao getEntityDao() {
		return entityDao;
	}

	public void setEntityDao(EntityDao entityDao) {
		this.entityDao = entityDao;
	}

}