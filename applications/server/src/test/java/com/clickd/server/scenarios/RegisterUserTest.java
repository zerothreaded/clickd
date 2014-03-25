package com.clickd.server.scenarios;

import java.net.URISyntaxException;
import java.util.List;

import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.clickd.server.model.Clique;
import com.clickd.server.model.Connection;
import com.clickd.server.model.Session;
import com.clickd.server.services.AbstractResourceTest;
import com.clickd.server.services.choices.ChoiceResource;
import com.clickd.server.services.users.CandidateResponse;
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
	
	private String registerThenSignOutAndSignIn( String email, String firstName, String lastName, String password, String dateOfBirth, String gender, String postcode)
			 throws URISyntaxException 
	{
		// STEP 1 : REGISTER

		Response response = userResource.register( email, firstName, lastName, password, dateOfBirth, gender, postcode);
		
		Response signinResponse = userResource.signIn(email, password);
		Session session = (Session) signinResponse.getEntity();
		String userRef = session.getUserRef();
		userRef = userRef.split("/")[2];
		
		//make the sign out call
		Response response2 = userResource.signOut(userRef);
		
		// TODO: Verify REMAINING expected session state
		Assert.assertEquals(200, response2.getStatus());

		Response signinResponse2 = userResource.signIn(email, password);
		Session session2 = (Session) signinResponse2.getEntity();
		Assert.assertEquals(200, signinResponse2.getStatus());
		
		return userRef;
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void registerSignOutSignInCheckCCCForTwoUsers() throws URISyntaxException {
		String userRef1 = registerThenSignOutAndSignIn("ralph.masilamani@gmail.com", "Ralph", "Masilamani", "rr0101", "01-01-01", "male", "NW1");
	
		// GET CANDIDATES
		Response getCandidatesResponse = userResource.getCandidates(userRef1);
		Assert.assertEquals(200, getCandidatesResponse.getStatus());
		
		List<CandidateResponse> responseList = (List<CandidateResponse>) getCandidatesResponse.getEntity();
		Assert.assertEquals(0,  responseList.size());
		
		// GET CONNECTIONS
		Response responseConnections = userResource.getConnections(userRef1);
		Assert.assertEquals(200, responseConnections.getStatus());
		
		String json = ((String)responseConnections.getEntity());
		List<Connection> connections = ((List<Connection>)new Gson().fromJson(json, List.class));
		Assert.assertEquals(0, connections.size());
		
		// GET CLIQUES
		Response getCliquesResponse = userResource.getCliquesForUserAsResponse(userRef1);
		Assert.assertEquals(200, getCliquesResponse.getStatus());
		
		String json2 = ((String)getCliquesResponse.getEntity());
		List<Clique> cliques = ((List<Clique>)new Gson().fromJson(json2, List.class));
		Assert.assertEquals(4, cliques.size());

		
		String userRef2 = registerThenSignOutAndSignIn("johndodds90@gmail.com", "John", "Dodds", "jj0101", "01-01-01", "male", "NW1");
		
		
		//make the get candidates call
		Response getCandidatesResponse2 = userResource.getCandidates(userRef2);
		
		// TODO: Verify REMAINING expected session state
		Assert.assertEquals(200, getCandidatesResponse.getStatus());
		
		List<CandidateResponse> responseList2 = (List<CandidateResponse>) getCandidatesResponse2.getEntity();
		Assert.assertEquals(1,  responseList2.size());
		
		Response responseConnections2 = userResource.getConnections(userRef2);
		Assert.assertEquals(200, responseConnections2.getStatus());
		
		String json3 = ((String)responseConnections2.getEntity());
		List<Connection> connections2 = ((List<Connection>)new Gson().fromJson(json3, List.class));
		Assert.assertEquals(0, connections2.size());
		
		// Get Cliques for John
		Response getCliquesResponse2 = userResource.getCliquesForUserAsResponse(userRef2);
		Assert.assertEquals(200, getCliquesResponse2.getStatus());
		
		// Verify Cliques match choices
		String json4 = ((String)getCliquesResponse2.getEntity());
		List<Clique> cliques2 = ((List<Clique>)new Gson().fromJson(json4, List.class));
		Assert.assertEquals(4, cliques2.size());

		Response signInEd = userResource.signIn("edward.dodds@clickd.org", "ee01010");
	}
	
}
