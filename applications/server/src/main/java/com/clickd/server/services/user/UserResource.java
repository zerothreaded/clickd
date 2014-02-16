package com.clickd.server.services.user;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.clickd.server.dao.EntityDao;
import com.clickd.server.model.Entity;
import com.clickd.server.utilities.Utilities;
import com.yammer.metrics.annotation.Timed;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
	
	private EntityDao entityDao;
	
    public UserResource(String template, String defaultName) {
    	
    }

    @GET
    @Timed
    public String getAll() {
    	List<Entity> allUsers = entityDao.getAll("users");
    	String result = Utilities.toJson(allUsers);
    	return result;
    }
    
    @GET
    @Path("/{id}")
    @Timed
    public String getUser(@PathParam("id") String id) {
    	return "{ \"id\" : \"" + id + "\" }";
    }

    @POST
    @Timed
    @Path("/signin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response signIn(
    		String body, 
    		@QueryParam("foo") String foo, 
    		@HeaderParam("X-Auth-Token") String authToken, 
    		@Context HttpServletRequest request) throws URISyntaxException
    {

        Map<String, String> formParameters = extractFormParameters(body);
        String email = formParameters.get("email");
        Entity user = entityDao.findUserByEmailAddress(email);

        
        if (user != null) {
        	if (user.getValue("password").equals(formParameters.get("password"))) {
        		// User Authentication OK
        		
        		// Lookup Existing Session 
                Entity session = entityDao.findSessionByUserEmail(email);
                if (session != null) {
               		// DELETE the old session
                	entityDao.deleteObject("sessions", session);
                }
            	Date now = new Date();
            	session = new Entity();
        		session.setValue("user_email", email);
        		session.setValue("user_token", new Integer(new Double(Math.random() * 1000 * 1000).intValue()));
        		session.setValue("created_on", now);
        		session.setValue("last_modified", now);
        		session.setValue("user_data", new HashMap<String, Object>());
        		session.setValue("user_loggedin", Boolean.TRUE);
        		// Persist 
        		entityDao.save("sessions", session);
        		
              	return Response.status(200).entity(session).build();
        	}
        }

    	return Response.status(300).entity(" {\"status\" : \"failed\" }").build();

    }

	private Map<String, String> extractFormParameters(String body)
			throws URISyntaxException {
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
    public void deleteUsers() {
    	entityDao.dropCollection("users");
    }
    
	public void setEntityDao(EntityDao entityDao) {
		this.entityDao = entityDao;
	}
    
}