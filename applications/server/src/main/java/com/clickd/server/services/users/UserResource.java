package com.clickd.server.services.users;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.clickd.server.dao.EntityDao;
import com.clickd.server.model.Entity;
import com.clickd.server.services.home.HomeView;
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


}
