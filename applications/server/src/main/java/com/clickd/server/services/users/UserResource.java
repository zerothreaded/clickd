package com.clickd.server.services.users;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.clickd.server.dao.SessionDao;
import com.clickd.server.dao.UserDao;
import com.clickd.server.model.Link;
import com.clickd.server.model.Session;
import com.clickd.server.model.User;
import com.clickd.server.utilities.Utilities;
import com.yammer.dropwizard.views.View;
import com.yammer.metrics.annotation.Timed;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource
{
	private UserDao userDao;
	private SessionDao sessionDao;

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
	
	
	@GET
    @Path("/{ref}/signout")
    @Timed
    public String signOut(@PathParam("ref") String ref) {
		User user = userDao.findByRef("/users/" + ref);
		
		List <Link> sessionLinks = user.getSessionLinks();
		
		ArrayList <Session> sessions = new ArrayList <Session>();
		for (Link sessionLink : sessionLinks)
		{
			Session session = sessionDao.findByRef(sessionLink.getHref());
			session.setIsLoggedIn(false);
			sessionDao.update(session);
			sessions.add(session);
		}
		
		return Utilities.toJson(sessions);
    }
	
	@GET
    @Path("/{ref}")
    @Timed
    public String getUserDetails(@PathParam("ref") String ref) {
		User user = userDao.findByRef("/users/" + ref);
		
		return Utilities.toJson(user);
    }
	
	@GET
    @Path("/")
    @Timed
    public String getAll() {
		List<User> users = userDao.findAll();
		return Utilities.toJson(users);
    }


	@GET
    @Path("/{userRef}/sessions/")
    @Timed
    public String getUserSessions(@PathParam("userRef") String userRef)
	{
		User user = userDao.findByRef("/users/" + userRef);
		
		List <Link> sessionLinks = user.getSessionLinks();
		
		ArrayList <Session> sessions = new ArrayList <Session>();
		for (Link sessionLink : sessionLinks)
		{
			Session session = sessionDao.findByRef(sessionLink.getHref());
			sessions.add(session);
		}
		
		return Utilities.toJson(sessions);
    }

	
	@GET
    @Path("/{userRef}/sessions/{sessionRef}")
    @Timed
    public String getSession(@PathParam("userRef") String userRef, @PathParam("sessionRef") String sessionRef) {
		User user = userDao.findByRef("/users/" + userRef);
		Session session = sessionDao.findByRef("/users/" + userRef + "/sessions/" + sessionRef);
		
		return Utilities.toJson(session);
    }

}
