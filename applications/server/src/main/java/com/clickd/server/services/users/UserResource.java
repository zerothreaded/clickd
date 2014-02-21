package com.clickd.server.services.users;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import com.clickd.server.dao.ChoiceDao;
import com.clickd.server.dao.QuestionDao;
import com.clickd.server.dao.SessionDao;
import com.clickd.server.dao.UserDao;
import com.clickd.server.model.Choice;
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
	private UserDao userDao;
	private SessionDao sessionDao;
	private ChoiceDao choiceDao;
	private QuestionDao questionDao;

	@POST
	@Timed
	@Path("/register")
	@Produces(MediaType.APPLICATION_JSON)
	public Response register(@FormParam("email") String email, @FormParam("firstName") String firstName, @FormParam("lastName") String lastName,
			@FormParam("password") String password, @FormParam("dateOfBirth") String dateOfBirth, @FormParam("gender") String gender,
			@FormParam("postcode") String postcode) throws URISyntaxException {

		// check if user exists
		User user = userDao.findByEmail(email);

		if (user == null) {
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
			ageChoice.get_Links().put(Resource.KEY_LINK_SELF, new Link(ageChoice.getRef(), "self"));
			ageChoice.get_Links().put(Resource.KEY_LINK_CHOICE_USER, new Link(userRef, "user"));
			ageChoice.get_Links().put(Resource.KEY_LINK_CHOICE_QUESTION, new Link(ageQuestion.getRef(), "question"));
			ageChoice.setAnswerText(dateOfBirth);
			choiceDao.create(ageChoice);
			
			Choice genderChoice = new Choice();
			Question genderQuestion = questionDao.findByTags("user.bio.gender");
			genderChoice.get_Links().put(Resource.KEY_LINK_SELF, new Link(genderChoice.getRef(), "self"));
			genderChoice.get_Links().put(Resource.KEY_LINK_CHOICE_USER, new Link(userRef, "user"));
			genderChoice.get_Links().put(Resource.KEY_LINK_CHOICE_QUESTION, new Link(genderQuestion.getRef(), "question"));
			genderChoice.setAnswerText(gender);
			choiceDao.create(genderChoice);
			
			Choice postcodeChoice = new Choice();
			Question postcodeQuestion = questionDao.findByTags("user.bio.postcode");
			postcodeChoice.get_Links().put(Resource.KEY_LINK_SELF, new Link(postcodeChoice.getRef(), "self"));
			postcodeChoice.get_Links().put(Resource.KEY_LINK_CHOICE_USER, new Link(userRef, "user"));
			postcodeChoice.get_Links().put(Resource.KEY_LINK_CHOICE_QUESTION, new Link(postcodeQuestion.getRef(), "question"));
			postcodeChoice.setAnswerText(postcode);
			choiceDao.create(postcodeChoice);
			
			

			return Response.status(200).entity(" { \"status\" : \"ok\" } ").build();
		} else {
			return Response.status(300).entity(" { \"status\" : \"failed\" } ").build();
		}
	}

	@SuppressWarnings("unchecked")
	@POST
	@Timed
	@Path("/signin")
	@Produces(MediaType.APPLICATION_JSON)
	public Response signIn(@FormParam(value = "email") String email, @FormParam(value = "password") String password, @Context HttpServletRequest request,
			@Context HttpServletResponse response) throws URISyntaxException {
		User user = userDao.findByEmail(email);
		if (user != null) {
			if (user.getPassword().equals(password)) {
				// User Authentication OK
				// Lookup Existing Sessions for this user
				List<Link> sessionLinks = (List<Link>) user.get_Links().get(Resource.KEY_LINK_USER_SESSION_LIST);
				if (sessionLinks == null) {
					sessionLinks = new ArrayList<Link>();
				}
				for (Link sessionLink : sessionLinks) {
					Session session = sessionDao.findByRef(sessionLink.getHref());
					if (session.getIsLoggedIn()) {
						session.setIsLoggedIn(Boolean.FALSE);
						sessionDao.update(session);
					}
				}

				// Previous sessions SIGNED OUT - create a new one
				Session session = new Session(user, new Date(), new Date(), 1L, true);
				Link sessionLink = new Link(session.getRef(), "self");
				session.get_Links().put(Resource.KEY_LINK_SELF, sessionLink);
				sessionDao.create(session);

				// Add the new session to the user
				Link userSessionLink = new Link(user.getRef(), "self");
				user.get_Links().put(Resource.KEY_LINK_SELF, userSessionLink);
				sessionLinks.add(new Link(session.getRef(), "user-session"));
				user.get_Links().put(Resource.KEY_LINK_USER_SESSION_LIST, sessionLinks);
				userDao.update(user);

				Map<String, String> cookieData = new HashMap<String, String>();
				cookieData.put("sessionRef", session.getRef().toString());
				cookieData.put("userRef", user.getRef());

				NewCookie newCookie = new NewCookie("userSession", Utilities.toJson(cookieData), "/", "", "", 60 * 60, false);

				return Response.status(200).cookie(newCookie).entity(session).build();
			}
		}
		return Response.status(300).entity(" { \"status\" : \"failed\" } ").build();
	}

	@SuppressWarnings("unchecked")
	@PUT
	@Path("/{ref}/signout")
	@Timed
	public String signOut(@PathParam("ref") String ref) {
		User user = userDao.findByRef("/users/" + ref);
		ArrayList<Session> userSessions = new ArrayList<Session>();
		List<Link> sessionLinks = (List<Link>) user.get_Links().get(Resource.KEY_LINK_USER_SESSION_LIST);
		for (Link sessionLink : sessionLinks) {
			Session session = sessionDao.findByRef(sessionLink.getHref());
			if (session.getIsLoggedIn()) {
				session.setIsLoggedIn(Boolean.FALSE);
				session = sessionDao.update(session);
				userSessions.add(session);
			}
		}
		return Utilities.toJson(userSessions);
	}

	@GET
	@Timed
	public String getAll(@Context HttpServletRequest request, @Context HttpServletResponse response, @Context HttpHeaders headers) {
		for (String key : headers.getCookies().keySet()) {
			javax.ws.rs.core.Cookie cookie = headers.getCookies().get(key);
			System.out.println("cookie.name=" + cookie.getName());
			System.out.println("cookie.value=" + cookie.getValue());
		}
		List<User> allUsers = userDao.findAll();
		String result = Utilities.toJson(allUsers);
		return result;
	}

	@GET
	@Path("/numberofregisteredusers")
	@Timed
	public String getNumberOfRegisteredUsers(@Context HttpServletRequest request, @Context HttpServletResponse response, @Context HttpHeaders headers) {
		List<User> allUsers = userDao.findAll();
		return " { \"count\" : " + allUsers.size() + " }";
	}

	@GET
	@Path("/numberofsignedinusers")
	@Timed
	public String getNumberOfSignedInUsers() {
		int count = sessionDao.findAll().size();
		return "{ \"value\" : \"" + count + "\" }";
	}

	@GET
	@Path("/{ref}")
	@Timed
	public String getUser(@PathParam("ref") String ref) {
		User user = userDao.findByRef("/users/" + ref);
		return Utilities.toJson(user);
	}

	@SuppressWarnings("unchecked")
	@GET
	@Path("/{userRef}/sessions/")
	@Timed
	public String getUserSessions(@PathParam("userRef") String userRef) {
		User user = userDao.findByRef("/users/" + userRef);
		ArrayList<Session> userSessions = new ArrayList<Session>();
		List<Link> sessionLinks = (List<Link>) user.get_Links().get(Resource.KEY_LINK_USER_SESSION_LIST);
		for (Link sessionLink : sessionLinks) {
			Session session = sessionDao.findByRef(sessionLink.getHref());
			userSessions.add(session);
		}
		return Utilities.toJson(userSessions);
	}

	@GET
	@Path("/{userRef}/sessions/{sessionRef}")
	@Timed
	public String getSession(@PathParam("userRef") String userRef, @PathParam("sessionRef") String sessionRef) {
		Session session = sessionDao.findByRef("/users/" + userRef + "/sessions/" + sessionRef);
		return Utilities.toJson(session);
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

	public void setChoiceDao(ChoiceDao choiceDao) {
		this.choiceDao = choiceDao;
	}

	public void setQuestionDao(QuestionDao questionDao) {
		this.questionDao = questionDao;
	}
}
