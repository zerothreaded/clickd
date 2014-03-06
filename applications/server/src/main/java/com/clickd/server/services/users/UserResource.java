package com.clickd.server.services.users;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;

import com.clickd.server.dao.AnswerDao;
import com.clickd.server.dao.ChoiceDao;
import com.clickd.server.dao.ConnectionDao;
import com.clickd.server.dao.QuestionDao;
import com.clickd.server.dao.SessionDao;
import com.clickd.server.dao.UserDao;
import com.clickd.server.model.Answer;
import com.clickd.server.model.Choice;
import com.clickd.server.model.Clique;
import com.clickd.server.model.Connection;
import com.clickd.server.model.ErrorMessage;
import com.clickd.server.model.Link;
import com.clickd.server.model.Question;
import com.clickd.server.model.Resource;
import com.clickd.server.model.Session;
import com.clickd.server.model.User;
import com.clickd.server.utilities.Utilities;
import com.yammer.metrics.annotation.Timed;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private SessionDao sessionDao;
	
	@Autowired
	private ChoiceDao choiceDao;
	
	@Autowired
	private QuestionDao questionDao;
	
	@Autowired
	private ConnectionDao connectionDao;
	
	@Autowired
	private AnswerDao answerDao;

	@GET
	@Path("/{ref}")
	@Timed
	public Response getUser(@PathParam("ref") String ref) {
		try {
			User user = userDao.findByRef("/users/" + ref);
			if (null != user) {
				return Response.status(200).entity(user).build();
			} else {
				return Response.status(300).entity(new ErrorMessage("failed", "User Not Found")).build();
			}
		} catch(Exception e) {
			return Response.status(300).entity(new ErrorMessage("failed", e.getMessage())).build();			
		}
	}

	@POST
	@Timed
	@Path("/register")
	@Produces(MediaType.APPLICATION_JSON)
	public Response register(@FormParam("email") String email, @FormParam("firstName") String firstName, @FormParam("lastName") String lastName,
			@FormParam("password") String password, @FormParam("dateOfBirth") String dateOfBirth, @FormParam("gender") String gender,
			@FormParam("postcode") String postcode) throws URISyntaxException {

		try {
			User user = userDao.findByEmail(email);
			if (null != user) {
				return Response.status(300).entity(new ErrorMessage("failed", "Email address not available")).build();
			}
			
			boolean missingRegistrationDetails = false;
			if (email == null || firstName == null || lastName == null || password == null || dateOfBirth == null || gender == null || postcode == null) {
				missingRegistrationDetails = true;
			}
			
			if (!missingRegistrationDetails) {
				User newUser = new User();
				newUser.setEmail(email);
				newUser.setFirstName(firstName);
				newUser.setLastName(lastName);
				newUser.setPassword(password);
				newUser.setDateOfBirth(Utilities.dateFromString(dateOfBirth));
				newUser.setGender(gender);
				newUser.setPostCode(postcode);
				userDao.create(newUser);
				
				String userRef = newUser.getRef();
				
				Choice ageChoice = new Choice();
				Question ageQuestion = questionDao.findByTags("user.bio.age");
				ageChoice.addLink(Resource.KEY_LINK_SELF, new Link(ageChoice.getRef(), "self"));
				ageChoice.addLink(Resource.KEY_LINK_CHOICE_USER, new Link(userRef, "user"));
				ageChoice.addLink(Resource.KEY_LINK_CHOICE_QUESTION, new Link(ageQuestion.getRef(), "question"));
				ageChoice.setAnswerText(dateOfBirth);
				choiceDao.create(ageChoice);
				
				Choice genderChoice = new Choice();
				Question genderQuestion = questionDao.findByTags("user.bio.gender");
				genderChoice.addLink(Resource.KEY_LINK_SELF, new Link(genderChoice.getRef(), "self"));
				genderChoice.addLink(Resource.KEY_LINK_CHOICE_USER, new Link(userRef, "user"));
				genderChoice.addLink(Resource.KEY_LINK_CHOICE_QUESTION, new Link(genderQuestion.getRef(), "question"));
				genderChoice.setAnswerText(gender);
				choiceDao.create(genderChoice);
				
				Choice postcodeChoice = new Choice();
				Question postcodeQuestion = questionDao.findByTags("user.bio.postcode");
				postcodeChoice.addLink(Resource.KEY_LINK_SELF, new Link(postcodeChoice.getRef(), "self"));
				postcodeChoice.addLink(Resource.KEY_LINK_CHOICE_USER, new Link(userRef, "user"));
				postcodeChoice.addLink(Resource.KEY_LINK_CHOICE_QUESTION, new Link(postcodeQuestion.getRef(), "question"));
				postcodeChoice.setAnswerText(postcode);
				choiceDao.create(postcodeChoice);
	
				return Response.status(200).entity(Utilities.toJson(newUser)).build();
			} else {
				return Response.status(300).entity(new ErrorMessage("failed", "Missing Registration Details")).build();
			}
		} catch (Exception e) {
			return Response.status(300).entity(new ErrorMessage("failed", e.getMessage())).build();
		}
	}

	@POST
	@Timed
	@Path("/signin")
	@Produces(MediaType.APPLICATION_JSON)
	public Response signIn(@FormParam(value = "email") String email, @FormParam(value = "password") String password)  {
		try {
			User user = userDao.findByEmail(email);
			if (user.getPassword().equals(password)) {
				// User Authentication OK
				// Lookup Existing Sessions for this user
				
				List<Link> sessionLinks = user.getLinkLists(Resource.KEY_LINK_USER_SESSION_LIST);
				
				if (sessionLinks == null) {
					sessionLinks = new ArrayList<Link>();
				}
				else
				{
					for (Link sessionLink : sessionLinks) {
						Session session = sessionDao.findByRef(sessionLink.getHref());
						sessionDao.delete(session);
					}
					sessionLinks = new ArrayList<Link>();
				}
				// Previous sessions DELETED - create a new one
				Session session = new Session(user, new Date(), new Date(), 1L, true);
				Link sessionLink = new Link(session.getRef(), "self");
				session.addLink(Resource.KEY_LINK_SELF, sessionLink);
				sessionDao.create(session);

				// Add the new session to the user
				Link userSessionLink = new Link(user.getRef(), "self");
				user.addLink(Resource.KEY_LINK_SELF, userSessionLink);
				sessionLinks.add(new Link(session.getRef(), "user-session"));
				user.addLinkLists(Resource.KEY_LINK_USER_SESSION_LIST, sessionLinks);
				userDao.update(user);

				Map<String, String> cookieData = new HashMap<String, String>();
				cookieData.put("sessionRef", session.getRef().toString());
				cookieData.put("userRef", user.getRef());

				NewCookie newCookie = new NewCookie("userSession", Utilities.toJson(cookieData), "/", "", "", 60 * 60, false);
				return Response.status(200).cookie(newCookie).entity(session).build();
			} else {
				return Response.status(300).entity(new ErrorMessage("failed", "Incorrect Password")).build();
			}
		} catch(Exception e) {
			return Response.status(300).entity(new ErrorMessage("failed", e.getMessage())).build();
		}
	}

	@PUT
	@Path("/{userRef}/signout")
	@Timed
	public Response signOut(@PathParam("userRef") String userRef) {
		try
		{
			User user = userDao.findByRef("/users/" + userRef);
			if (user == null) {
				return Response.status(300).entity(new ErrorMessage("failed", "User not found")).build();
			}
			List<Link> sessionLinks =user.getLinkLists(Resource.KEY_LINK_USER_SESSION_LIST);
			for (Link sessionLink : sessionLinks) {
				Session session = sessionDao.findByRef(sessionLink.getHref());
				sessionDao.delete(session);
			}
			return Response.status(200).build();			
		}
		catch (Exception e)
		{
			return Response.status(300).entity(new ErrorMessage("failed", e.getMessage())).build();
		}
	}

	@GET
	@Path("/{userRef}/sessions/{sessionRef}")
	@Timed
	public Response getSession(@PathParam("userRef") String userRef, @PathParam("sessionRef") String sessionRef) {
		try {
			Session session = sessionDao.findByRef("/users/" + userRef + "/sessions/" + sessionRef);
			return Response.status(200).entity(Utilities.toJson(session)).build();
		} catch (Exception e) {
			return Response.status(300).entity(new ErrorMessage("failed", e.getMessage())).build();
		}
	}
	
	@GET
	@Path("/{userRef}/candidates")
	@Timed
	public Response getCandidates(@PathParam("userRef") String userRef) {
		try {
			// get my answers
			List<Choice> myChoices = choiceDao.findByUserRef(userRef);
			ArrayList<CandidateResponse> responseList = new ArrayList<CandidateResponse>();
			for (Choice choice : myChoices) {
				ArrayList<Choice> sameAnswerChoices = new ArrayList<Choice>();
				sameAnswerChoices.addAll(choiceDao.findChoicesWithTheSameAnswerByAnswerText(choice.getAnswerText()));
				// This test checks if there is a LINK to an answer i.e. The answer is a reference
				if (null != choice.getLinkByName("choice-answer")) {
					Link answerLink = choice.getLinkByName("choice-answer");
					sameAnswerChoices.addAll(choiceDao.findChoicesWithTheSameAnswerByHref(answerLink.getHref()));
				}
				ArrayList<Connection> myConnections = (ArrayList<Connection>)connectionDao.findAllByUserRef("/users/" + userRef);
				// Now we have all the choices that gave the same answer
				// Get the users that gave them, filter out ourself and score candidates
				for (Choice otherUsersChoice : sameAnswerChoices) {
					Link otherUserLink = (Link) otherUsersChoice.getLinkByName("choice-user");
					User otherUser = userDao.findByRef(otherUserLink.getHref());
						if (!otherUser.getRef().equals("/users/" + userRef))
						{
							boolean alreadyExists = false;
							for (CandidateResponse responseRow : responseList) {
								if (responseRow.getUser().getRef().equals(otherUser.getRef())) {
									responseList.remove(responseRow);
									responseRow.setScore(responseRow.getScore() + 1);
									responseList.add(responseRow);
									alreadyExists = true;
									break;
								}
							}
							boolean isAConnection = false;
							for (Connection connection : myConnections)
							{
									for (Link link : connection.getLinkLists("connection-user"))
									{
										if (link.getHref().equals(otherUser.getRef()))
										{
											isAConnection = true;
										}
									}
							}
							if (!alreadyExists && !isAConnection) {
								CandidateResponse responseRow = new CandidateResponse(otherUser, 1);
								responseList.add(responseRow);
							}
						}
				}

			
			}
			
			// Sort the responses
			Collections.sort(responseList, new Comparator<CandidateResponse>() {
				@Override
				public int compare(CandidateResponse cr1, CandidateResponse cr2) {
					return cr2.getScore() - cr1.getScore();
				}
			});
			
			return Response.status(200).entity(responseList).build();
		} catch (Exception e) {
			return Response.status(300).entity(new ErrorMessage("failed", e.getMessage())).build();
		}
	}
	
	
	@GET
	@Path("/{userRef}/candidates/comparison/{otherUserRef}")
	@Timed
	public Response compareCandidate(@PathParam("userRef") String userRef, @PathParam("otherUserRef") String otherUserRef) {
		try {
			//get my answers
			List<Choice> myChoices = choiceDao.findByUserRef(userRef);
			List<Choice> otherUserChoices = choiceDao.findByUserRef(otherUserRef);
			ArrayList<String> same = new ArrayList<String>();
			for (Choice choice : myChoices)
			{
				for (Choice choice2 : otherUserChoices)
				{
					Link answerLink = choice.getLinkByName("choice-answer");
					Link answerLink2 = choice2.getLinkByName("choice-answer");
					if (null == answerLink || null == answerLink2)
						continue;
					if (answerLink.getHref().equals(answerLink2.getHref()))
					{
						Answer answer = answerDao.findByRef(answerLink.getHref());
						same.add(answer.getAnswerText());
					}
				}
			}
			return Response.status(200).entity(Utilities.toJson(same)).build();
		} catch (Exception e) {
			return Response.status(300).entity(new ErrorMessage("failed", e.getMessage())).build();
		}
	}
	
	@POST
	@Path("/{fromUserRef}/connections/add/{toUserRef}")
	@Timed
	public Response addConnectionRequest(@PathParam("fromUserRef") String fromUserRef, @PathParam("toUserRef") String toUserRef)
	{
		// am i already connected
		Connection preExisting = connectionDao.findByBothUserRefsIgnoreRole("/users/" + fromUserRef, "/users/" + toUserRef);
		if (preExisting == null)
		{
			Connection connection = new Connection(new Date(), new Date(), "pending");
			Link myUserLink = new Link("/users/" + fromUserRef, "from-user");
			Link otherUserLink = new Link("/users/" + toUserRef, "to-user");
			ArrayList<Link> userLinks = new ArrayList<Link>();
			userLinks.add(myUserLink);
			userLinks.add(otherUserLink);
			connection.addLinkLists("connection-user", userLinks);
			connectionDao.create(connection);
			return Response.status(200).entity(Utilities.toJsonNoPretty(connection)).build();
		} else {
			return Response.status(300).entity(new ErrorMessage("failed", "User already connected")).build();
		}
	}
	
	@GET
	@Path("/{userRef}/connections")
	@Timed
	public Response getConnections(@PathParam("userRef") String userRef) {
		try
		{
			List<Connection> userConnections = connectionDao.findAllByUserRef("/users/"+userRef);
			return Response.status(200).entity(Utilities.toJson(userConnections)).build();
		}
		catch (Exception e)
		{
			return Response.status(300).entity(new ErrorMessage("failed", e.getMessage())).build(); 
		}
	}
	
	@GET
	@Path("/{userRef}/connections/{connectionRef}/accept")
	@Timed
	public Response acceptConnection(@PathParam("userRef") String userRef, @PathParam("connectionRef") String connectionRef) {
		try
		{
			Connection connection = connectionDao.findByRef(connectionRef);
			connection.setStatus("active");
			connectionDao.update(connection);
			return Response.status(200).entity(Utilities.toJson(connection)).build();
		}
		catch (Exception e)
		{
			return Response.status(300).entity(new ErrorMessage("failed", e.getMessage())).build(); 
		}
	}
	
	@GET
	@Path("/{userRef}/connections/{connectionRef}/reject")
	@Timed
	public Response rejectConnection(@PathParam("userRef") String userRef, @PathParam("connectionRef") String connectionRef)
	{
		try
		{
			Connection connection = connectionDao.findByRef("/connections/"+connectionRef);
			connectionDao.delete(connection);
			return Response.status(200).build();
		}
		catch (Exception e)
		{
			return Response.status(300).entity(new ErrorMessage("failed", e.getMessage())).build();
		}
	}
	
	@GET
	@Path("/{userRef}/cliques")
	@Timed
	public Response getCliquesForUser(@PathParam("userRef") String userRef) {
	
		try {
			User user = userDao.findByRef("/users/" + userRef);
			List<Clique> myCliques = new ArrayList<Clique>();	
			List<Choice> myChoices = choiceDao.findByUserRef(userRef);
			for (Choice myChoice : myChoices)
			{
				String answerRef = myChoice.getLinkByName("choice-answer").getHref();
				
				// TODO: CHANGE THIS SHIT
				// So BIO questions have no ANSWER LINK - that's just WRONG!
				String name = "";
				Answer answer = answerDao.findByRef(answerRef);
				if (null != answer) {
					name = answer.getAnswerText();
				} else {
					name = myChoice.getAnswerText();
				}
				
				//now get list of users who made that choice
				Clique thisClique = new Clique(user, new Date(), new Date(), "system", name);
				List<Choice> cliqueMemberChoices = choiceDao.findChoicesWithTheSameAnswerByHref(answerRef);
				List<User> cliqueMembers = new ArrayList<User>();
				for (Choice cliqueMemberChoice : cliqueMemberChoices)
				{
					Link cliqueMemberLink = cliqueMemberChoice.getLinkByName("choice-user");
					User cliqueMember = userDao.findByRef(cliqueMemberLink.getHref());
					if (!cliqueMember.getRef().equals("/users/" + userRef))
						cliqueMembers.add(cliqueMember);
				}
				thisClique.get_Embedded().put("clique-members", cliqueMembers);
				thisClique.get_Embedded().put("clique-choice", myChoice);
				myCliques.add(thisClique);
			}
			return Response.status(200).entity(Utilities.toJson(myCliques)).build();
		} catch (Exception e) {
			return Response.status(300).entity(new ErrorMessage("failed", e.getMessage())).build(); 
		}

	}

	@GET
	@Path("/{userRef}/connections/{connectionRef}")
	@Timed
	public String getConnection(@PathParam("userRef") String userRef, @PathParam("connectionRef") String connectionRef) {
		Connection connection = connectionDao.findByRef("/users/" + userRef + "/connections/" + connectionRef);
		return Utilities.toJson(connection);
	}
	
	public ConnectionDao getConnectionDao() {
		return connectionDao;
	}
	
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

//	public void setChoiceDao(ChoiceDao choiceDao) {
//		this.choiceDao = choiceDao;
//	}
//
//	public void setQuestionDao(QuestionDao questionDao) {
//		this.questionDao = questionDao;
//	}

	public void setConnectionDao(ConnectionDao connectionDao) {
		this.connectionDao = connectionDao;
	}

//	public void setAnswerDao(AnswerDao answerDao) {
//		this.answerDao = answerDao;
//	}
}
