package com.clickd.server.services.users;

import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.clickd.server.model.ErrorMessage;
import com.clickd.server.model.Session;
import com.clickd.server.model.User;
import com.clickd.server.services.AbstractResourceTest;
import com.google.gson.Gson;

public class UserResourceTest extends AbstractResourceTest {

	private UserResource userResource;

	@Before
	public void setup() {
		super.setup();
		userResource = (UserResource) applicationContext.getBean("userResource");
	}

	@Test
	public void getUserByRefSucceedsForExistingUser() throws Exception {
		Response response = userResource.getUser("1");
		Assert.assertEquals(200, response.getStatus());
		User user = (User)response.getEntity();
		
		// TODO: Verify REMAINING expected session state
		Assert.assertEquals("/users/1", user.getRef());
		
		// Verify 1 and ONLY 1 USER in DB
		Assert.assertEquals(1, userResource.getUserDao().findAll().size());

	}
	
	@Test
	public void getUserByRefFailsForNonExistingUser() throws Exception {
		Response response = userResource.getUser("DOESNT_EXIST");
		Assert.assertEquals(300, response.getStatus());
		ErrorMessage errorMessage = (ErrorMessage)response.getEntity();
		Assert.assertEquals(errorMessage.getStatus(), "failed");
		Assert.assertEquals(errorMessage.getMessage(), "User Not Found");
	}
	
	
	@Test
	public void signInSucceedsWithCorrectCredentials() throws Exception {
		Response response = userResource.signIn("ralph.masilamani@clickd.org", "rr0101");
		Assert.assertEquals(200, response.getStatus());
		Session session = (Session) response.getEntity();
		
		// TODO: Verify REMAINING expected session state
		Assert.assertEquals(session.getIsLoggedIn(), true);
		
		// Verify 1 and ONLY 1 session created in DB
		Assert.assertEquals(1, userResource.getSessionDao().findAll().size());
	}
	
	
	@Test
	public void signOutSucceedsWithSignedInUserRef() throws Exception {
		Response response = userResource.signIn("ralph.masilamani@clickd.org", "rr0101");
		Assert.assertEquals(200, response.getStatus());
		Session session = (Session) response.getEntity();
		
		// TODO: Verify REMAINING expected session state
		Assert.assertEquals(session.getIsLoggedIn(), true);
		
		// Verify 1 and ONLY 1 session created in DB
		Assert.assertEquals(1, userResource.getSessionDao().findAll().size());
		
		String userRef = session.getUserRef();
		userRef = userRef.split("/")[2];
		
		//make the sign out call
		Response response2 = userResource.signOut(userRef);
		
		// TODO: Verify REMAINING expected session state
		Assert.assertEquals(200, response2.getStatus());

		//VERIFY response is empty
		Assert.assertEquals(null, response2.getEntity());

		
		// Verify 0 and ONLY 0 sessions exist in DB
		Assert.assertEquals(0, userResource.getSessionDao().findAll().size());		
	}
	
	@Test
	public void signOutFailsWithIncorrectUserRef() throws Exception {

		String userRef = "NOTRIGHT";
		
		//make the sign out call
		Response response = userResource.signOut(userRef);
		
		// TODO: Verify REMAINING expected session state
		Assert.assertEquals(300, response.getStatus());
		
		ErrorMessage errorMessage = (ErrorMessage)response.getEntity();
		Assert.assertEquals(errorMessage.getStatus(), "failed");
		Assert.assertEquals(errorMessage.getMessage(), "User not found");
	}
	
	@Test
	public void signInFailsWithInCorrectCredentials() throws Exception {
		Response response = userResource.signIn("ralph.masilamani@clickd.org", "WRONG");
		Assert.assertEquals(300, response.getStatus());
		ErrorMessage errorMessage = (ErrorMessage)response.getEntity();
		Assert.assertEquals(errorMessage.getStatus(), "failed");
		Assert.assertEquals(errorMessage.getMessage(), "Incorrect Password");
	}
	
	@Test
	public void signInFailsWithMissingCredentials() throws Exception {
		Response response = userResource.signIn(null, null);
		Assert.assertEquals(300, response.getStatus());
		ErrorMessage errorMessage = (ErrorMessage)response.getEntity();
		Assert.assertEquals(errorMessage.getStatus(), "failed");
	}
	
	@Test
	public void registerSucceedsWithCorrectDetails() throws Exception {
		Response response = userResource.register(
				"test_ralph.masilamani@clickd.org", 
				"Ralph",
				"Masilamani", 
				"rr0101", 
				"01-02-03", 
				"male",
				"SE1 3BB");
		Assert.assertEquals(200, response.getStatus());
		String userJson = (String)response.getEntity();
		User user = new Gson().fromJson(userJson, User.class);
		
		// TODO: Verify REMAINING expected USER state
		Assert.assertEquals("test_ralph.masilamani@clickd.org", user.getEmail());
		
		// Verify 1 and ONLY 1 USER created in DB
		Assert.assertEquals(2, userResource.getUserDao().findAll().size());
	}
	
	@Test
	public void registerFailsWithMissingDetails() throws Exception {
		Response response = userResource.register(
				"test_ralph.masilamani@clickd.org", 
				null, 
				"Masilamani", 
				"rr0101", 
				"01-02-03", 
				"male",
				"SE1 3BB");
		Assert.assertEquals(300, response.getStatus());
		ErrorMessage errorMessage = (ErrorMessage)response.getEntity();
		Assert.assertEquals(errorMessage.getStatus(), "failed");
		Assert.assertEquals(errorMessage.getMessage(), "Missing Registration Details");
	}
	
	@Test
	public void registerFailsWithEmailNotAvailable() throws Exception {
		Response response = userResource.register(
				"test_ralph.masilamani@clickd.org", 
				"Ralph", 
				"Masilamani", 
				"rr0101", 
				"01-02-03", 
				"male",
				"SE1 3BB");
		Assert.assertEquals(200, response.getStatus());
		response = userResource.register(
				"test_ralph.masilamani@clickd.org", 
				"Ralph", 
				"Masilamani", 
				"rr0101", 
				"01-02-03", 
				"male",
				"SE1 3BB");
		
		Assert.assertEquals(300, response.getStatus());
		ErrorMessage errorMessage = (ErrorMessage)response.getEntity();
		Assert.assertEquals(errorMessage.getStatus(), "failed");
		Assert.assertEquals(errorMessage.getMessage(), "Email address not available");
	}
	

}
