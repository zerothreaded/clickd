package com.clickd.server.services.user;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicLong;

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

import org.apache.commons.lang.StringEscapeUtils;

import com.clickd.server.dao.EntityDao;
import com.clickd.server.model.Entity;
import com.clickd.server.utilities.Utilities;
import com.yammer.metrics.annotation.Timed;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
	
	private EntityDao entityDao;
	
    private final String template;
    private final String defaultName;
    private final AtomicLong counter;

    public UserResource(String template, String defaultName) {
        this.template = template;
        this.defaultName = defaultName;
        this.counter = new AtomicLong();
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
        
        Entity user = entityDao.findUserByEmailAddress(formParameters.get("email"));
        if (user != null) {
        	if (user.getValue("password").equals(formParameters.get("password"))) {
        		// User Sign In OK
        		
        		// Create a session for the user and the user TOKEN
        		Entity session = new Entity();
        		session.setValue("user", formParameters.get("email"));
        		session.setValue("token", new Integer(new Double(Math.random() * 1000 * 1000).intValue()));
        		session.setValue("data", new HashMap<String, Object>());
        		entityDao.save("sessions", session);
        		
              	return Response.status(200).entity(session).build();
        	}
        }
    	return Response.status(300).entity(" {\"status\" : \"failed\" }").build();
    }
    
    @DELETE
    @Timed
    public void deleteUsers() {
    	entityDao.delete("users");
    }
    
	public void setEntityDao(EntityDao entityDao) {
		this.entityDao = entityDao;
	}
    
}