package com.clickd.server.services.users;

import java.util.List;

import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.clickd.server.model.Connection;
import com.clickd.server.model.ErrorMessage;
import com.clickd.server.model.Session;
import com.clickd.server.model.User;
import com.clickd.server.services.AbstractResourceTest;
import com.clickd.server.utilities.Utilities;
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
		Assert.assertEquals(2, userResource.getUserDao().findAll().size());

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
		Assert.assertEquals(3, userResource.getUserDao().findAll().size());
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
	public void getCandidatesWithNoChoices() throws Exception {
		Response response = userResource.signIn("ralph.masilamani@clickd.org", "rr0101");
		Assert.assertEquals(200, response.getStatus());
		
		Session session = (Session) response.getEntity();
		
		// TODO: Verify REMAINING expected session state
		Assert.assertEquals(session.getIsLoggedIn(), true);
		
		// Verify 1 and ONLY 1 session created in DB
		Assert.assertEquals(1, userResource.getSessionDao().findAll().size());
		
		String userRef = session.getUserRef();
		userRef = userRef.split("/")[2];
		
		//make the get candidates call
		Response response2 = userResource.getCandidates(userRef);
		
		// TODO: Verify REMAINING expected session state
		Assert.assertEquals(200, response2.getStatus());

		//VERIFY response list is empty
		List<CandidateResponse> responseList = (List<CandidateResponse>) response2.getEntity();

		Assert.assertEquals(0, responseList.size());
	}
	
	@Test
	public void getCandidatesWithEqualAnswerTextChoicesBetweenUsers() throws Exception {
		
		Utilities.importFixtureFromFile(mongoDb, "src\\test\\resources\\database\\choices_answer_texts.json" , "choices");

		Response response = userResource.signIn("ralph.masilamani@clickd.org", "rr0101");
		Assert.assertEquals(200, response.getStatus());
		Session session = (Session) response.getEntity();
		
		Response response2 = userResource.signIn("john.dodds@clickd.org", "jj0101");
		Assert.assertEquals(200, response2.getStatus());
		Session session2 = (Session) response2.getEntity();
		
		// TODO: Verify REMAINING expected session state
		Assert.assertEquals(session.getIsLoggedIn(), true);

		// TODO: Verify REMAINING expected session state
		Assert.assertEquals(session2.getIsLoggedIn(), true);
		
		// Verify 2 and ONLY 2 sessions created in DB
		Assert.assertEquals(2, userResource.getSessionDao().findAll().size());
		
		String userRef1 = session.getUserRef();
		userRef1 = userRef1.split("/")[2];
		
		String userRef2 = session.getUserRef();
		userRef2 = userRef2.split("/")[2];
		
		//make the get candidates call
		Response getCandidatesResponse = userResource.getCandidates(userRef1);
		
		// TODO: Verify REMAINING expected session state
		Assert.assertEquals(200, getCandidatesResponse.getStatus());

		// VERIFY response list is not empty
		List<CandidateResponse> responseList = (List<CandidateResponse>) getCandidatesResponse.getEntity();
		Assert.assertEquals(1,  responseList.size());
	}
	
	@Test
	public void getCandidatesWithEqualAnswerRefChoicesBetweenUsers() throws Exception {
		
		Utilities.importFixtureFromFile(mongoDb, "src\\test\\resources\\database\\choices_answer_refs.json" , "choices");

		Response response = userResource.signIn("ralph.masilamani@clickd.org", "rr0101");
		Assert.assertEquals(200, response.getStatus());
		Session session = (Session) response.getEntity();
		
		Response response2 = userResource.signIn("john.dodds@clickd.org", "jj0101");
		Assert.assertEquals(200, response2.getStatus());
		Session session2 = (Session) response2.getEntity();
		
		// TODO: Verify REMAINING expected session state
		Assert.assertEquals(session.getIsLoggedIn(), true);

		// TODO: Verify REMAINING expected session state
		Assert.assertEquals(session2.getIsLoggedIn(), true);
		
		// Verify 2 and ONLY 2 sessions created in DB
		Assert.assertEquals(2, userResource.getSessionDao().findAll().size());
		
		String userRef1 = session.getUserRef();
		userRef1 = userRef1.split("/")[2];
		
		String userRef2 = session.getUserRef();
		userRef2 = userRef2.split("/")[2];
		
		//make the get candidates call
		Response getCandidatesResponse = userResource.getCandidates(userRef1);
		
		// TODO: Verify REMAINING expected session state
		Assert.assertEquals(200, getCandidatesResponse.getStatus());

		//VERIFY response list is not empty
		List<CandidateResponse> responseList = (List<CandidateResponse>) getCandidatesResponse.getEntity();
		Assert.assertEquals(1,  responseList.size());
	}
	
	@Test
	public void getCandidatesReturnsCandidatesRankedByScore() throws Exception {
		
		// Setup 3 user set
		mongoOperations.dropCollection("users");
		Utilities.importFixtureFromFile(mongoDb, "src\\test\\resources\\database\\users_3.json" , "users");

		// Setup 2 questions for each 3 users - Score = 2 and 1
		Utilities.importFixtureFromFile(mongoDb, "src\\test\\resources\\database\\choices_answer_texts_scores.json" , "choices");

		Response response = userResource.signIn("ralph.masilamani@clickd.org", "rr0101");
		Assert.assertEquals(200, response.getStatus());
		Session session = (Session) response.getEntity();
		
		Response response2 = userResource.signIn("john.dodds@clickd.org", "jj0101");
		Assert.assertEquals(200, response2.getStatus());
		Session session2 = (Session) response2.getEntity();
		
		// TODO: Verify REMAINING expected session state
		Assert.assertEquals(session.getIsLoggedIn(), true);

		// TODO: Verify REMAINING expected session state
		Assert.assertEquals(session2.getIsLoggedIn(), true);
		
		// Verify 2 and ONLY 2 sessions created in DB
		Assert.assertEquals(2, userResource.getSessionDao().findAll().size());
		
		String userRef1 = session.getUserRef();
		userRef1 = userRef1.split("/")[2];
		
		String userRef2 = session.getUserRef();
		userRef2 = userRef2.split("/")[2];
		
		//make the get candidates call
		Response getCandidatesResponse = userResource.getCandidates(userRef1);
		
		// TODO: Verify REMAINING expected session state
		Assert.assertEquals(200, getCandidatesResponse.getStatus());

		// VERIFY response list contains the 2 other users
		
		List<CandidateResponse> responseList = (List<CandidateResponse>) getCandidatesResponse.getEntity();
		Assert.assertEquals(2,  responseList.size());
		
		// VERIFY the scores of the candidates as 2 then 1
		// i.e. John score = 2
		// and  Ed score  = 1
		for (CandidateResponse candidateResponse : responseList) {
			if (candidateResponse.getUser().getEmail().equals("john.dodds@clickd.org")) {
				Assert.assertEquals(2,  candidateResponse.getScore().intValue());
			}
			if (candidateResponse.getUser().getEmail().equals("edward.dodds@clickd.org")) {
				Assert.assertEquals(1,  candidateResponse.getScore().intValue());
			}
		}
	}
	
	@Test
	public void getCandidatesIgnoresConnections() throws Exception {
		
		// Setup 3 user set
		mongoOperations.dropCollection("users");
		mongoOperations.dropCollection("connections");
			
		Utilities.importFixtureFromFile(mongoDb, "src\\test\\resources\\database\\users_3.json" , "users");

		// Setup 2 questions for each 3 users - Score = 2 and 1
		Utilities.importFixtureFromFile(mongoDb, "src\\test\\resources\\database\\choices_answer_texts_scores.json" , "choices");

		Response response = userResource.signIn("ralph.masilamani@clickd.org", "rr0101");
		Assert.assertEquals(200, response.getStatus());
		Session session = (Session) response.getEntity();
		
		Response response2 = userResource.signIn("john.dodds@clickd.org", "jj0101");
		Assert.assertEquals(200, response2.getStatus());
		Session session2 = (Session) response2.getEntity();
		
		// TODO: Verify REMAINING expected session state
		Assert.assertEquals(session.getIsLoggedIn(), true);

		// TODO: Verify REMAINING expected session state
		Assert.assertEquals(session2.getIsLoggedIn(), true);
		
		// Verify 2 and ONLY 2 sessions created in DB
		Assert.assertEquals(2, userResource.getSessionDao().findAll().size());
		
		String userRef1 = session.getUserRef();
		userRef1 = userRef1.split("/")[2];
		
		String userRef2 = session2.getUserRef();
		userRef2 = userRef2.split("/")[2];
		
		// Request a connection between John and Ralph
		userResource.addConnectionRequest(userRef1, userRef2);
		
		// Grab the connections - there should be only 1 in the DB
		Connection connection = userResource.getConnectionDao().findAll().get(0);
		
		// Accept the connection
		userResource.acceptConnection(userRef1, connection.getRef().split("/")[4]);
		
		//make the get candidates call
		Response getCandidatesResponse = userResource.getCandidates(userRef1);
		
		// TODO: Verify REMAINING expected session state
		Assert.assertEquals(200, getCandidatesResponse.getStatus());

		// VERIFY response list contains the 2 other users
		
		List<CandidateResponse> responseList = (List<CandidateResponse>) getCandidatesResponse.getEntity();
		Assert.assertEquals(1,  responseList.size());
		
		// VERIFY the scores of the 1 candidate 
		// and  Ed score  = 1
		for (CandidateResponse candidateResponse : responseList) {
			if (candidateResponse.getUser().getEmail().equals("edward.dodds@clickd.org")) {
				Assert.assertEquals(1,  candidateResponse.getScore().intValue());
			}
		}
	}
	
	//@Test
	public void addConnectionSucceeds() throws Exception {
		
		// Setup test data
		String userRefRalph = "1";
		String userRefJohn = "2";
		
		// Request a connection between Ralph and John
		Response response = userResource.addConnectionRequest(userRefJohn, userRefRalph);
		Assert.assertEquals(200, response.getStatus());
		
		Connection connection = (Connection)response.getEntity();
		
		// Verify User session updates
		
		// Verify 2 Sessions created
		
		
		int stop = 123;
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
