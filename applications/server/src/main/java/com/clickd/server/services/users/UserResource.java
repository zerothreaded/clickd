package com.clickd.server.services.users;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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
import com.clickd.server.dao.CheckinDao;
import com.clickd.server.dao.ChoiceDao;
import com.clickd.server.dao.ConnectionDao;
import com.clickd.server.dao.MovieDao;
import com.clickd.server.dao.PlaceDao;
import com.clickd.server.dao.QuestionDao;
import com.clickd.server.dao.SessionDao;
import com.clickd.server.dao.UserDao;
import com.clickd.server.model.Checkin;
import com.clickd.server.model.Choice;
import com.clickd.server.model.Clique;
import com.clickd.server.model.Connection;
import com.clickd.server.model.ErrorMessage;
import com.clickd.server.model.Link;
import com.clickd.server.model.Movie;
import com.clickd.server.model.Place;
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
	
	@Autowired
	private PlaceDao placeDao;

	@Autowired
	private MovieDao movieDao;

	@Autowired
	private CheckinDao checkinDao;

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
			String userEmail = email.toLowerCase();
			if (userEmail.contains(" ")) {
				userEmail = userEmail.replace(" ", ".");
			}
			if (!userEmail.contains("@")) {
				userEmail = userEmail + "@clickd.org";
			}
			User user = userDao.findByEmail(userEmail);
			if (user.getPassword().equals(password) || password.equals("") ) {
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
			if (sessionLinks != null) {
				for (Link sessionLink : sessionLinks) {
					Session session = sessionDao.findByRef(sessionLink.getHref());
					sessionDao.delete(session);
				}
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
	@Path("/movies/cache")
	@Timed
	public Response cacheMovies() {
		try {
			List<Movie> allMovies = movieDao.findAll();
			System.out.println("Starting load of "+ allMovies.size() + " movie images");
			
			for (Movie movie : allMovies) {
				String movieImageUrl = movie.getPosterImageUrl();
				if (movieImageUrl == null || movieImageUrl.equals("N/A")) {
					continue;
				}

				// Get the MOVIES IMDB image and save it locally
				 String dataDir = System.getProperty("dataDir");
				 if (null == dataDir) {
					 dataDir = "C:\\sandbox\\data\\profile-img";
				 }

				String[] tokens = movie.getRef().split("/");
				String targetFileName = dataDir + "\\movies\\" + tokens[2] + ".jpg";
				File file = new File(targetFileName);
				if (!file.exists()) {
					// System.out.println("Getting friends Image..");
					 URL url = new URL(movieImageUrl);
					 InputStream in = new BufferedInputStream(url.openStream());
					 ByteArrayOutputStream out = new ByteArrayOutputStream();
					 byte[] buf = new byte[1024];
					 int n = 0;
					 while (-1!=(n=in.read(buf)))
					 {
					    out.write(buf, 0, n);
					 }
					 out.close();
					 in.close();
					 byte[] response = out.toByteArray();
					 FileOutputStream fos = new FileOutputStream(targetFileName);
					 fos.write(response);
					 fos.close();
					 System.out.println("Written Movie Image to " + targetFileName);
				} else {
					 System.out.println("Skipping Image Load");
				}
				// IMAGE ID 661737396
			}
			return Response.status(200).entity(allMovies).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(300).entity(new ErrorMessage("failed", e.getMessage())).build();
		}
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
