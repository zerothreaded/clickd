package com.clickd.server.services.member;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import com.clickd.server.dao.SessionDao;
import com.clickd.server.dao.UserDao;
import com.clickd.server.model.Link;
import com.clickd.server.model.Session;
import com.clickd.server.model.User;
import com.clickd.server.utilities.Utilities;
import com.yammer.metrics.annotation.Timed;

@Path("/members")
@Produces(MediaType.APPLICATION_JSON)
public class MemberResource {
	
	private UserDao userDao;
	private SessionDao sessionDao;
	
    public MemberResource(String template, String defaultName) {
    	
    }

    @GET
    @Timed
    public String getAll(@Context HttpServletRequest request, 
			@Context HttpServletResponse response,
			@Context HttpHeaders headers)
    {
		for (String key : headers.getCookies().keySet()) {
			javax.ws.rs.core.Cookie cookie = headers.getCookies().get(key);
			System.out.println("cookie.name=" + cookie.getName());
			System.out.println("cookie.value=" + cookie.getValue());
		}
    	List<User> allMembers = userDao.findAll();
    	String result = Utilities.toJson(allMembers);
    	return result;
    }
    
    @GET
    @Path("/numberofregisteredmembers")
    @Timed
    public String getNumberOfRegisteredMembers(@Context HttpServletRequest request, 
			@Context HttpServletResponse response,
			@Context HttpHeaders headers) 
	{

    	int count = userDao.findAll().size();
    	return "{ \"value\" : \"" + count + "\" }";
    }

    @GET
    @Path("/numberofsignedinmembers")
    @Timed
    public String getNumberOfSignedInMembers() {
    	int count = sessionDao.findAll().size();
    	return "{ \"value\" : \"" + count + "\" }";
    }
    
    @POST
    @Timed
    @Path("/signin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response signIn(
    		@FormParam(value = "email") String email,
     		@FormParam(value = "password") String password,
     		@Context HttpServletRequest request,
     		@Context HttpServletResponse response) throws URISyntaxException
    {
        User user = userDao.findByEmail(email);
        if (user != null) {
        	if (user.getPassword().equals(password)) {
        		// User Authentication OK
        		
        		// Lookup Existing Sessions for this user
        		List <Link> sessionLinks = user.getSessionLinks();
        		for (Link link : sessionLinks)
        		{
        			String sessionHref = link.getHref();
        			Session session = sessionDao.findByRef(sessionHref);
        			if (session != null && session.getIsLoggedIn()) {
        				// EXISTING session - user did not log out or time out 
        				// FORCE LOG OUT of session
        				session.setIsLoggedIn(Boolean.FALSE);
        				sessionDao.update(session);
        			}
        		}
        		
        		// Previous sessions terminated - create a new one
            	Session session = new Session(user, Session.createToken(), new Date(), new Date(), 1L, true);
            	sessionDao.create(session);
            	
            	Link userSessionLink = new Link(session.getRef(), "user_session");
            	user.getSessionLinks().add(userSessionLink);
            	
            	userDao.update(user);
              	
        		NewCookie newCookie = new NewCookie("token", session.getToken().toString(), "/", "", "", 60*60, false);
        		return Response.status(200).cookie(newCookie).entity(session).build();
        	}
        }
    	return Response.status(300).entity(" { \"status\" : \"failed\" } ").build();
    }
    
    @POST
    @Timed
    @Path("/register")
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(
    		@FormParam("email") String email,
     		@FormParam("firstName") String firstName,
     		@FormParam("lastName") String lastName,
     		@FormParam("password") String password ) throws URISyntaxException
    {
        
        //check if member exists
        User member = userDao.findByEmail(email);
        
        if (member == null)
        {
        	User newMember = new User();
        	newMember.setEmail(email);
        	newMember.setFirstName(firstName);
        	newMember.setLastName(lastName);
        	newMember.setPassword(password);
        	userDao.create(newMember);
        	
        	return Response.status(200).entity(" { \"status\" : \"ok\" } ").build();
        }
        else
        {
        	return Response.status(300).entity(" { \"status\" : \"failed\" } ").build();
        }
    }

    
    @DELETE
    @Timed
    public void deleteMembers() {
    //	userDao.dropCollection("members");
    }
    
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	public void setSessionDao(SessionDao sessionDao) {
		this.sessionDao = sessionDao;
	}
    
    
}