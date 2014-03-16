package com.clickd.server.services.users;

import java.util.List;

import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.clickd.server.dao.ConnectionDao;
import com.clickd.server.model.Clique;
import com.clickd.server.model.Connection;
import com.clickd.server.model.ErrorMessage;
import com.clickd.server.model.Session;
import com.clickd.server.model.User;
import com.clickd.server.services.AbstractResourceTest;
import com.clickd.server.services.choices.ChoiceResource;
import com.clickd.server.utilities.Utilities;
import com.google.gson.Gson;

public class UserResourceTest extends AbstractResourceTest {

	private UserResource userResource;
	private ChoiceResource choiceResource;

	@Before
	public void setup() {
		super.setup();
		userResource = (UserResource) applicationContext.getBean("userResource");
		choiceResource = (ChoiceResource) applicationContext.getBean("choiceResource");
	}

	@Test
	public void getSessionSucceeds() throws Exception {
		Response response = userResource.signIn("ralph.masilamani@clickd.org", "rr0101");
		Assert.assertEquals(200, response.getStatus());
		Session session = (Session) response.getEntity();
		Response getSessionResponse = userResource.getSession("1", session.getRef());
		Assert.assertEquals(200, getSessionResponse.getStatus());
	}

	@Test
	public void getSessionFails() throws Exception {
		Response response = userResource.signIn("ralph.masilamani@clickd.org", "rr0101");
		Assert.assertEquals(200, response.getStatus());
		Session session = (Session) response.getEntity();
		userResource.setSessionDao(null);
		Response getSessionResponse = userResource.getSession("1", session.getRef());
		Assert.assertEquals(300, getSessionResponse.getStatus());
	}

	@Test
	public void getUserByRefSucceedsForExistingUser() throws Exception {
		Response response = userResource.getUser("1");
		Assert.assertEquals(200, response.getStatus());
		User user = (User)response.getEntity();
		Assert.assertEquals("/users/1", user.getRef());
	}

	@Test
	public void getUserByRefFailsForNull() throws Exception {
		userResource.setUserDao(null);
		Response response = userResource.getUser(null);
		Assert.assertEquals(300, response.getStatus());
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
	public void signOutFailsWithNullRef() throws Exception {
		userResource.setUserDao(null);
		//make the sign out call
		Response response = userResource.signOut(null);
		Assert.assertEquals(300, response.getStatus());
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
		Assert.assertEquals(4, userResource.getUserDao().findAll().size());
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
	public void registerFailsWithWithNull() throws Exception {
		userResource.setUserDao(null);
		Response response = userResource.register(
				"test_ralph.masilamani@clickd.org", 
				null, 
				"Masilamani", 
				"rr0101", 
				"01-02-03", 
				"male",
				"SE1 3BB");
		Assert.assertEquals(300, response.getStatus());
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
	public void getCandidatesFailsWithNULL() throws Exception {
		//make the get candidates call
		Response response = userResource.getCandidates(null);
		Assert.assertEquals(300, response.getStatus());
	}
	
	@Test
	public void getCandidatesWithEqualAnswerTextChoicesBetweenUsers() throws Exception {
		
		Utilities.importFixtureFromFile(mongoDb, "src\\test\\resources\\database\\choices.json" , "choices");

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
		Assert.assertEquals(2,  responseList.size());
	}
	
	@Test
	public void getCandidatesReturnsCandidatesRankedByScore() throws Exception {
		
		// Setup 3 user set
		mongoOperations.dropCollection("users");
		Utilities.importFixtureFromFile(mongoDb, "src\\test\\resources\\database\\users.json" , "users");

		// Setup 2 questions for each 3 users - Score = 2 and 1
		Utilities.importFixtureFromFile(mongoDb, "src\\test\\resources\\database\\choices.json" , "choices");

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
				Assert.assertEquals(1,  candidateResponse.getScore().intValue());
			}
			if (candidateResponse.getUser().getEmail().equals("simone.wagener@clickd.org")) {
				Assert.assertEquals(1,  candidateResponse.getScore().intValue());
			}
		}
	}
	
	@Test
	public void getCandidatesIgnoresConnections() throws Exception {
		
		// Setup 3 user set
		mongoOperations.dropCollection("users");
		mongoOperations.dropCollection("connections");
			
		Utilities.importFixtureFromFile(mongoDb, "src\\test\\resources\\database\\users.json" , "users");

		// Setup 2 questions for each 3 users - Score = 2 and 1
		Utilities.importFixtureFromFile(mongoDb, "src\\test\\resources\\database\\choices.json" , "choices");

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
		Response addConnectionRequestResponse = userResource.addConnectionRequest(userRef1, userRef2);
		Assert.assertEquals(200, addConnectionRequestResponse.getStatus());
		
		// Grab the connections - there should be only 1 in the DB
		Connection connection = userResource.getConnectionDao().findAll().get(0);
		
		// Accept the connection
		userResource.acceptConnection(userRef1, connection.getRef());
		
		//make the get candidates call
		Response getCandidatesResponse = userResource.getCandidates(userRef1);
		
		// TODO: Verify REMAINING expected session state
		Assert.assertEquals(200, getCandidatesResponse.getStatus());

		// VERIFY response list contains the 1 other users
		
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
	
	@Test
	public void addConnectionRequestSucceeds() throws Exception {
		// Setup test data
		String userRefRalph = "1";
		String userRefJohn = "2";
		
		// Request a connection between Ralph and John
		Response response = userResource.addConnectionRequest(userRefJohn, userRefRalph);
		Assert.assertEquals(200, response.getStatus());
		
		String connectionJson = (String)response.getEntity();
		Connection connection = new Gson().fromJson(connectionJson, Connection.class);
		Assert.assertNotNull(connection);
		
		Assert.assertEquals("pending", connection.getStatus());
	}
	
	
	@Test
	public void addConnectionRequestFailsIfConnectionExists() throws Exception {
		// Setup test data
		String userRefRalph = "1";
		String userRefJohn = "2";
		
		// Request a connection between Ralph and John
		Response response = userResource.addConnectionRequest(userRefJohn, userRefRalph);
		Assert.assertEquals(200, response.getStatus());
		
		Response secondRequestResponse = userResource.addConnectionRequest(userRefJohn, userRefRalph);
		Assert.assertEquals(300, secondRequestResponse.getStatus());
	
		String connectionJson = (String)response.getEntity();
		Connection connection = new Gson().fromJson(connectionJson, Connection.class);
		Assert.assertNotNull(connection);
		
		Assert.assertEquals("pending", connection.getStatus());
	}
	
	@Test
	public void getConnectionsSucceeds() throws Exception {
		// Setup test data
		String userRefRalph = "1";
		String userRefJohn = "2";
		
		// Request a connection between Ralph and John
		Response response = userResource.addConnectionRequest(userRefJohn, userRefRalph);
		Assert.assertEquals(200, response.getStatus());

		// Check if there is 1 connection
		Response responseConnections = userResource.getConnections(userRefJohn);
		Assert.assertEquals(200, response.getStatus());
		
		String json = ((String)responseConnections.getEntity());
		List<Connection> connections = ((List<Connection>)new Gson().fromJson(json, List.class));
		Assert.assertEquals(1, connections.size());

	}
	
	@Test
	public void getConnectionsFails() throws Exception {
		// Setup test data
		String userRefRalph = "1";
		String userRefJohn = "2";
		
		// Request a connection between Ralph and John
		Response response = userResource.addConnectionRequest(userRefJohn, userRefRalph);
		Assert.assertEquals(200, response.getStatus());

		// Check if there is a failure on NULL
		userResource.setConnectionDao(null);
		Response responseConnections = userResource.getConnections(null);
		Assert.assertEquals(300, responseConnections.getStatus());

	}
	
	@Test
	public void acceptConnectionSucceeds() throws Exception {
		// Setup test data
		String userRefRalph = "1";
		String userRefJohn = "2";
		
		// Request a connection between Ralph and John
		Response response = userResource.addConnectionRequest(userRefJohn, userRefRalph);
		Assert.assertEquals(200, response.getStatus());

		String connectionJson = (String)response.getEntity();
		Connection connection = new Gson().fromJson(connectionJson, Connection.class);
		Assert.assertNotNull(connection);
		Assert.assertEquals("pending", connection.getStatus());

		String connectionRef = connection.getRef().split("/")[2];
		Response acceptResponse = userResource.acceptConnection(userRefJohn, connectionRef);
		Assert.assertEquals(200, acceptResponse.getStatus());
		// TODO: VERIFY connection NOT IN DB
	}
	
	@Test
	public void acceptConnectionFailsWithNull() throws Exception {
		userResource.setUserDao(null);
		Response response = userResource.acceptConnection(null, null);
		Assert.assertEquals(300, response.getStatus());
	}
	
	@Test
	public void rejectConnectionSucceeds() throws Exception {
		// Setup test data
		String userRefRalph = "1";
		String userRefJohn = "2";
		
		// Request a connection between Ralph and John
		Response response = userResource.addConnectionRequest(userRefJohn, userRefRalph);
		Assert.assertEquals(200, response.getStatus());

		String connectionJson = (String)response.getEntity();
		Connection connection = new Gson().fromJson(connectionJson, Connection.class);
		Assert.assertNotNull(connection);
		Assert.assertEquals("pending", connection.getStatus());

		String connectionRef = connection.getRef().split("/")[2];
		Response rejectResponse = userResource.rejectConnection(userRefRalph, connectionRef);
		Assert.assertEquals(200, rejectResponse.getStatus());

		//check empty response for from user (john)
		Response fromUserResponseConnections = userResource.getConnections(userRefJohn);
		Assert.assertEquals(200, fromUserResponseConnections.getStatus());
		String fromUserJson = ((String)fromUserResponseConnections.getEntity());
		List<Connection> fromUserConnections = ((List<Connection>)new Gson().fromJson(fromUserJson, List.class));
		Assert.assertEquals(0, fromUserConnections.size());
		

		//check empty response for to user (ralph)
		Response toUserResponseConnections = userResource.getConnections(userRefRalph);
		Assert.assertEquals(200, toUserResponseConnections.getStatus());
		String toUserJson = ((String)toUserResponseConnections.getEntity());
		List<Connection> toUserConnections = ((List<Connection>)new Gson().fromJson(toUserJson, List.class));
		Assert.assertEquals(0, toUserConnections.size());
	}
	
	@Test
	public void rejectConnectionFails() throws Exception {
		// Setup test data
		String userRefRalph = "1";
		String userRefJohn = "2";
		
		// Request a connection between Ralph and John
		Response response = userResource.addConnectionRequest(userRefJohn, userRefRalph);
		Assert.assertEquals(200, response.getStatus());

		String connectionJson = (String)response.getEntity();
		Connection connection = new Gson().fromJson(connectionJson, Connection.class);
		Assert.assertNotNull(connection);
		Assert.assertEquals("pending", connection.getStatus());

		userResource.setConnectionDao(null);
		Response rejectResponse = userResource.rejectConnection(null, null);
		Assert.assertEquals(300, rejectResponse.getStatus());
	}
	
	@Test
	public void compareCandidatesSucceeds() throws Exception {
		// Setup test data
		String userRefRalph = "1";
		String userRefJohn = "2";

		// Make user choices
		String questionRef = "1";
		Response choiceResponse = choiceResource.createWithAnswerText(userRefRalph, questionRef, "beer");
		Assert.assertEquals(200, choiceResponse.getStatus());
		
		questionRef = "1";
		choiceResponse = choiceResource.createWithAnswerText(userRefJohn, questionRef, "beer");
		Assert.assertEquals(200, choiceResponse.getStatus());
		
		Response compareResponse = userResource.compareCandidate(userRefRalph, userRefJohn);
		Assert.assertEquals(200, compareResponse.getStatus());
		
		String json = (String) compareResponse.getEntity();
		List<String> answerTexts = ((List<String>)new Gson().fromJson(json, List.class));
		Assert.assertEquals(1, answerTexts.size());
	}

	@Test
	public void compareCandidatesFails() throws Exception {
		Response compareResponse = userResource.compareCandidate(null, null);
		Assert.assertEquals(300, compareResponse.getStatus());
	}		


	@Test
	public void getUserCliquesSucceeds() throws Exception {
			// Setup 3 user set
			mongoOperations.dropCollection("users");
			mongoOperations.dropCollection("connections");
				
			Utilities.importFixtureFromFile(mongoDb, "src\\test\\resources\\database\\users.json" , "users");

			// Setup 2 questions for each 3 users - Score = 2 and 1
			Utilities.importFixtureFromFile(mongoDb, "src\\test\\resources\\database\\choices.json" , "choices");

			
		// Setup test data
		String userRefRalph = "1";
		String userRefJohn = "2";
		
		// Make user choices
		String questionRef = "1";
		Response response = choiceResource.createWithAnswerText(userRefRalph, questionRef, "beer");
		Assert.assertEquals(200, response.getStatus());
		
		// Get Cliques for Ralph
		Response getCliquesResponse = userResource.getCliquesForUserAsResponse(userRefRalph);
		Assert.assertEquals(200, getCliquesResponse.getStatus());
		
		// Verify Cliques match choices
		String json = ((String)getCliquesResponse.getEntity());
		List<Clique> cliques = ((List<Clique>)new Gson().fromJson(json, List.class));
		Assert.assertEquals(5, cliques.size());

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
