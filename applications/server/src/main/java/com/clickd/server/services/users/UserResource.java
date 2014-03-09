package com.clickd.server.services.users;

import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
	@Path("/register/source/facebook")
	@Produces(MediaType.APPLICATION_JSON)
	public Response registerWithFacebook(@FormParam("facebookData") String facebookData) throws URISyntaxException {

		
		try {
			System.out.println(facebookData);
			
			HashMap<String, Object> map = Utilities.fromJson(facebookData);
			
			User existingUser = userDao.findByEmail((String)map.get("email"));
				if (null != existingUser)
				{
					return Response.status(300).entity(new ErrorMessage("failed", "Email address not available")).build();
				}
			
			User newUser = new User();
			newUser.setFirstName((String)map.get("first_name"));
			newUser.setLastName((String)map.get("last_name"));
			newUser.setGender((String)map.get("gender"));
			newUser.setEmail((String)map.get("email"));
			newUser.setDateOfBirth(Utilities.dateFromString((String)map.get("user_birthday")));
			newUser.setPassword("fb0101");
			userDao.create(newUser);
			
			Question genderQuestion = questionDao.findByTags("gender");
			Choice genderChoice = new Choice();
			genderChoice.setAnswerText((String)map.get("gender"));
			genderChoice.addLink("question", new Link(genderQuestion.getRef(), "choice-question"));
			genderChoice.addLink("user", new Link(newUser.getRef(), "choice-user"));
			choiceDao.create(genderChoice);

			Question nameQuestion = questionDao.findByTags("name");
			Choice nameChoice = new Choice();
			nameChoice.setAnswerText((String)map.get("first_name")+" "+(String)map.get("last_name"));
			nameChoice.addLink("question", new Link(nameQuestion.getRef(), "choice-question"));
			nameChoice.addLink("user", new Link(newUser.getRef(), "choice-user"));
			choiceDao.create(nameChoice);
			

			
			Question dateOfBirthQuestion = questionDao.findByTags("dateofbirth");
			Choice dateOfBirthChoice = new Choice();
			dateOfBirthChoice.setAnswerText((String)map.get("birthday"));
			dateOfBirthChoice.addLink("question", new Link(dateOfBirthQuestion.getRef(), "choice-question"));
			dateOfBirthChoice.addLink("user", new Link(newUser.getRef(), "choice-user"));
			choiceDao.create(dateOfBirthChoice);
			

			HashMap<String,String> locationDetails = ((HashMap<String,String>)map.get("location"));

			Question locationQuestion = questionDao.findByTags("location");
			Choice locationChoice = new Choice();
			locationChoice.setAnswerText(locationDetails.get("name"));
			locationChoice.addLink("question", new Link(locationQuestion.getRef(), "choice-question"));
			locationChoice.addLink("user", new Link(newUser.getRef(), "choice-user"));
			choiceDao.create(locationChoice);

			
			return Response.status(200).entity(Utilities.toJson(newUser)).build();
		}
		catch (Exception E)
		{
			return Response.status(300).entity(new ErrorMessage("failed", "Email address not available")).build();

		}
	};

	@POST
	@Timed
	@Path("/register/likes")
	@Produces(MediaType.APPLICATION_JSON)
	public Response registerLikes(@FormParam("likeData") String likeData, @FormParam("userRef") String userRef) throws URISyntaxException {
		try {
			System.out.println(likeData);
			HashMap<String, Object> map = Utilities.fromJson(likeData);
			for (String key : map.keySet()) {
				System.out.println(key + " = " + map.get(key));
				if (key.equals("data")) {
					Map<String, Object> data = (Map<String, Object>) map.get(key);
					for (String dataKey : data.keySet()) {
						Map<String, Object> likeDetails = (Map<String, Object>) data.get(dataKey);
						// System.out.println(dataKey + " = " + likeDetails);
						System.out.println("Category" + " = " + likeDetails.get("category"));
						System.out.println("Name" + " = " + likeDetails.get("name"));
						
						Question likeQuestion = questionDao.findByTags((String) likeDetails.get("name"));
						if (likeQuestion == null) {
							// N0 question - make it
							likeQuestion = new Question();
							likeQuestion.setQuestionText("Do you like " + likeDetails.get("name"));
							likeQuestion.setAnswerRule("yes|no");
							likeQuestion.setType("text");
							likeQuestion.setSource("system");
							likeQuestion.addLink("self", new Link(likeQuestion.getRef(), "self"));
							List<String> tagList = new ArrayList<String>();
							tagList.add((String)likeDetails.get("name"));
							tagList.add((String)likeDetails.get("category"));
							likeQuestion.setTags(tagList);
							questionDao.create(likeQuestion);
						}
						
						Choice likeChoice = new Choice();
						likeChoice.setAnswerText("yes");
						likeChoice.addLink("question", new Link(likeQuestion.getRef(), "choice-question"));
						likeChoice.addLink("user", new Link("/users/"+userRef, "choice-user"));
						likeChoice.addLink("self", new Link(likeChoice.getRef(), "self"));

						choiceDao.create(likeChoice);
						
					}
				}
			}
			
			int hangon = 1;

			User user = userDao.findByRef("/users/" + userRef);
			
			return Response.status(200).entity(Utilities.toJson("NEIN!!!!")).build();
		}
		catch (Exception E)
		{
			return Response.status(300).entity(new ErrorMessage("failed", "Email address not available")).build();

		}
	};

	
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
				Question genderQuestion = questionDao.findByTags("gender");
				Choice genderChoice = new Choice();
				genderChoice.setAnswerText(gender);
				genderChoice.addLink("question", new Link(genderQuestion.getRef(), "choice-question"));
				genderChoice.addLink("user", new Link(newUser.getRef(), "choice-user"));
				choiceDao.create(genderChoice);

				Question nameQuestion = questionDao.findByTags("name");
				Choice nameChoice = new Choice();
				nameChoice.setAnswerText(firstName+" "+lastName);
				nameChoice.addLink("question", new Link(nameQuestion.getRef(), "choice-question"));
				nameChoice.addLink("user", new Link(newUser.getRef(), "choice-user"));
				choiceDao.create(nameChoice);
				

				
				Question dateOfBirthQuestion = questionDao.findByTags("dateofbirth");
				Choice dateOfBirthChoice = new Choice();
				dateOfBirthChoice.setAnswerText(dateOfBirth);
				dateOfBirthChoice.addLink("question", new Link(dateOfBirthQuestion.getRef(), "choice-question"));
				dateOfBirthChoice.addLink("user", new Link(newUser.getRef(), "choice-user"));
				choiceDao.create(dateOfBirthChoice);
				

				Question locationQuestion = questionDao.findByTags("location");
				Choice locationChoice = new Choice();
				locationChoice.setAnswerText(postcode);
				locationChoice.addLink("question", new Link(locationQuestion.getRef(), "choice-question"));
				locationChoice.addLink("user", new Link(newUser.getRef(), "choice-user"));
				choiceDao.create(locationChoice);
	
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
				sameAnswerChoices.addAll(choiceDao.findChoicesWithTheSameAnswerByAnswerTextAndQuestionRef(choice.getAnswerText(), choice.getLinkByName("question").getHref()));

				
				ArrayList<Connection> myConnections = (ArrayList<Connection>)connectionDao.findAllByUserRef("/users/" + userRef);
				// Now we have all the choices that gave the same answer
				// Get the users that gave them, filter out ourself and score candidates
				for (Choice otherUsersChoice : sameAnswerChoices) {
					Link otherUserLink = (Link) otherUsersChoice.getLinkByName("user");
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
					Question question  = questionDao.findByRef(choice.getLinkByName("question").getHref());
						if (choice.getAnswerText().equals(choice2.getAnswerText()) && choice.getLinkByName("question").getHref().equals(choice2.getLinkByName("question").getHref()) )
							same.add(question.getTags().toString()+" - "+choice.getAnswerText());
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
			
			ArrayList<Connection> userConnectionsResponse = new ArrayList();
			
			for (Connection connection : userConnections)
			{
				if (!connection.getStatus().equals("rejected"))
				{
					userConnectionsResponse.add(connection);
				}
			}
			
			return Response.status(200).entity(Utilities.toJson(userConnectionsResponse)).build();
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
			Connection connection = connectionDao.findByRef("/connections/"+connectionRef);
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
			connection.setStatus("rejected");
			connectionDao.update(connection);
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
			
			// Add BIO wired cliques
			/*Clique genderClique = new Clique(user, new Date(), new Date(), "system", user.getGender());
			Clique postcodeClique = new Clique(user, new Date(), new Date(), "system", user.getPostCode());
			DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
			Clique dateOfBirthClique = new Clique(user, new Date(), new Date(), "system", df.format(user.getDateOfBirth()));
			myCliques.add(genderClique);
			myCliques.add(postcodeClique);
			myCliques.add(dateOfBirthClique);*/
			
			// Add CHOICE based cliques
			List<Choice> myChoices = choiceDao.findByUserRef(userRef);
			for (Choice myChoice : myChoices)
			{
				// TODO: CHANGE THIS SHIT
//				Link answerRefLink = myChoice.getLinkByName("choice-answer");
//				String answerRef = "";
//				if (answerRefLink != null) {
//					answerRef = myChoice.getLinkByName("choice-answer").getHref();
//				}
//				// So BIO questions have no ANSWER LINK - that's just WRONG!
//				String name = "";
//				Answer answer = answerDao.findByRef(answerRef);
//				if (null != answer) {
//					name = answer.getAnswerText();
//				} else {
//					name = myChoice.getAnswerText();
//				}
				
				//now get list of users who made that choice
				Question question = questionDao.findByRef(myChoice.getLinkByName("question").getHref());
				Clique thisClique = new Clique(user, new Date(), new Date(), "system", question.getTags().toString()+" "+myChoice.getAnswerText());
				List<Choice> cliqueMemberChoices = choiceDao.findChoicesWithTheSameAnswerByAnswerText(myChoice.getAnswerText());
				List<User> cliqueMembers = new ArrayList<User>();
				for (Choice cliqueMemberChoice : cliqueMemberChoices)
				{
					Link cliqueMemberLink = cliqueMemberChoice.getLinkByName("user");
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

	public void setConnectionDao(ConnectionDao connectionDao) {
		this.connectionDao = connectionDao;
	}
	
}
