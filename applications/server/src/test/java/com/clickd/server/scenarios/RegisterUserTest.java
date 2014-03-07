package com.clickd.server.scenarios;

import java.net.URISyntaxException;

import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.clickd.server.model.Session;
import com.clickd.server.model.User;
import com.clickd.server.services.AbstractResourceTest;
import com.clickd.server.services.choices.ChoiceResource;
import com.clickd.server.services.users.UserResource;
import com.google.gson.Gson;

public class RegisterUserTest extends AbstractResourceTest {

	private UserResource userResource;
	private ChoiceResource choiceResource;
	
	@Before
	public void setup() {
		super.setup();
		userResource = (UserResource) applicationContext.getBean("userResource");
		choiceResource = (ChoiceResource) applicationContext.getBean("choiceResource");
	}
	
	@Test
	public void registerThenSignOutAndSignIn() throws URISyntaxException {
		
		// STEP 1 : REGISTER
		// Response response = userResource.register( "test_ralph.masilamani@clickd.org", "Ralph", "Masilamani", "rr0101", "01-02-03", "male", "SE1 3BB");
		
		Response signinResponse = userResource.signIn("ralph.masilamani@clickd.org", "rr0101");
		Session session = (Session) signinResponse.getEntity();
		String userRef = session.getUserRef();
		userRef = userRef.split("/")[2];
		
		//make the sign out call
		Response response2 = userResource.signOut(userRef);
		
		// TODO: Verify REMAINING expected session state
		Assert.assertEquals(200, response2.getStatus());

		
	}
	
}
