package com.clickd.server.services.users;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.clickd.server.dao.EntityDao;
import com.clickd.server.model.Entity;
import com.clickd.server.services.home.HomeView;
import com.clickd.server.utilities.Utilities;
import com.yammer.dropwizard.views.View;
import com.yammer.metrics.annotation.Timed;

@Path("/users")
@Produces(MediaType.TEXT_HTML)
public class UserResource
{
	private EntityDao entityDao;

	public EntityDao getEntityDao() {
		return entityDao;
	}

	public void setEntityDao(EntityDao entityDao) {
		this.entityDao = entityDao;
	}
	
	@GET
    @Path("/{token}/home")
    @Timed
    public View getHome(@PathParam("token") String token) {
		Entity session = entityDao.findSessionByToken(token);
		if (session == null) {
			HomeView view = new HomeView("HOME");
			return view;
		} else {
	    	UserHomeView view = new UserHomeView("User Home");
	    	view.setMemberEmail(session.getStringValue("member_email"));
	    	return view;
		}
    }
	
	
	@GET
    @Path("/{token}/signout")
    @Timed
    public View signOut(@PathParam("token") String token) {
		Entity session = entityDao.findSessionByToken(token);
		if (session != null) {
			 entityDao.deleteObject("sessions", session);
		} 
		
	    HomeView view = new HomeView("HOME");
		return view;
    }
	
	@GET
    @Path("/{token}/details")
    @Timed
    public String getUserDetails(@PathParam("token") String token) {
		Entity session = entityDao.findSessionByToken(token);

		String result = "";
		if (null == session) {
			Entity error = new Entity();
			error.setValue("status", "fail");
			result = Utilities.toJson(error);
		} else {
			String email = session.getStringValue("member_email");
			Entity response = new Entity();
			response.setValue("status", "ok");
			response.setValue("member_email", email);
			result = Utilities.toJson(response);
		}
		
		return result;
    }


}
