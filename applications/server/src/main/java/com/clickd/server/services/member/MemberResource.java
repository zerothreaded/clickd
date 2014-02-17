package com.clickd.server.services.member;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.apache.http.HttpRequest;

import com.clickd.server.dao.EntityDao;
import com.clickd.server.model.Entity;
import com.clickd.server.services.home.HomeView;
import com.clickd.server.utilities.Utilities;
import com.yammer.dropwizard.views.View;
import com.yammer.metrics.annotation.Timed;

@Path("/members")
@Produces(MediaType.APPLICATION_JSON)
public class MemberResource {
	
	private EntityDao entityDao;
	
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
    	List<Entity> allMembers = entityDao.getAll("members");
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

    	int count = entityDao.getAll("members").size();
    	return "{ \"value\" : \"" + count + "\" }";
    }

    @GET
    @Path("/numberofsignedinmembers")
    @Timed
    public String getNumberOfSignedInMembers() {
    	int count = entityDao.getAll("sessions").size();
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

        // Map<String, String> formParameters = extractFormParameters(body);
        // String email = formParameters.get("email");
        Entity member = entityDao.findMemberByEmailAddress(email);

        if (member != null) {
        	if (member.getValue("password").equals(password)) {
        		// User Authentication OK
        		
        		// Lookup Existing Session to purge it
                Entity session = entityDao.findSessionByUserEmail(email);
                Long numberOfLogins = 1L;
                if (session != null) {
                	numberOfLogins = session.getLongValue("number_of_logins") + 1;
               		// DELETE the old session
                	entityDao.deleteObject("sessions", session);
                }
            	Date now = new Date();
            	session = new Entity();
            	session.setValue("status", "ok");
        		session.setValue("member_email", email);
        		String token = new Integer(new Double(Math.random() * 1000 * 1000).intValue()).toString();
        		session.setValue("user_token", token);
        		session.setValue("created_on", now);
        		session.setValue("last_modified", now);
        		session.setValue("user_data", new HashMap<String, Object>());
        		session.setValue("user_loggedin", Boolean.TRUE);
        		session.setValue("number_of_logins", numberOfLogins);
        		// Persist 
        		entityDao.save("sessions", session);
              	
        		NewCookie newCookie = new NewCookie("token", token, "/", "", "", 60*60, false);
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
        Entity member = entityDao.findMemberByEmailAddress(email);
        
        if (member == null)
        {
        	Entity newMember = new Entity();
        	newMember.setValue("email", email);
        	newMember.setValue("firstName", firstName);
        	newMember.setValue("lastName", lastName);
        	newMember.setValue("password", password);
        	entityDao.save("users", newMember);
        	
        	return Response.status(200).entity(" { \"status\" : \"ok\" } ").build();
        }
        else
        {
        	return Response.status(300).entity(" { \"status\" : \"failed\" } ").build();
        }
    }

	private Map<String, String> extractFormParameters(String body) throws URISyntaxException {
		StringTokenizer tokenizer = new StringTokenizer(body, "&");
        // System.out.println("COUNT = " + tokenizer.countTokens());
        Map<String, String> formParameters = new HashMap<String, String>();
        while (tokenizer.hasMoreTokens()) {
        	String element = tokenizer.nextToken();
        	// System.out.println("ELEMENT = " + element);
        	String key = element.substring(0, element.indexOf("="));
        	String value = element.substring(element.indexOf("=") + 1);
        	value = new URI(value).getPath();
        	formParameters.put(key, value);
        }
        return formParameters;
	}
    
    @DELETE
    @Timed
    public void deleteMembers() {
    	entityDao.dropCollection("members");
    }
    
	public void setEntityDao(EntityDao entityDao) {
		this.entityDao = entityDao;
	}
    
}