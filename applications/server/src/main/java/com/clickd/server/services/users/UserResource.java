package com.clickd.server.services.users;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.rmi.server.Skeleton;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import com.clickd.server.dao.CliqueDao;
import com.clickd.server.dao.ConnectionDao;
import com.clickd.server.dao.MovieDao;
import com.clickd.server.dao.PlaceDao;
import com.clickd.server.dao.QuestionDao;
import com.clickd.server.dao.SessionDao;
import com.clickd.server.dao.UserDao;
import com.clickd.server.model.Answer;
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
import com.clickd.server.utilities.Constants;
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
	@Path("/register/source/facebook")
	@Produces(MediaType.APPLICATION_JSON)
	public Response registerWithFacebook(@FormParam("facebookData") String facebookData) throws URISyntaxException {
		try {
			// System.out.println(facebookData);
			
			HashMap<String, Object> map = Utilities.fromJson(Utilities.urlDecode(facebookData));
			User existingUser = userDao.findByRef("/users/" + (String)map.get("id"));
			if (null != existingUser)
			{
				return Response.status(300).entity(new ErrorMessage("failed", "User already registered")).build();
			}
			
			User newUser = new User();
			newUser.setFirstName((String)map.get("first_name"));
			newUser.setLastName((String)map.get("last_name"));
			newUser.setGender((String)map.get("gender"));
			newUser.setEmail((String)map.get("email"));
			newUser.setDateOfBirth(Utilities.dateFromString((String)map.get("user_birthday")));
			newUser.setPassword("fb999");
			newUser.setRef("/users/"+(String)map.get("id"));
			userDao.create(newUser);
			
			 URL url = new URL("http://graph.facebook.com/"+(String)map.get("id")+"/picture?width=160&height=160");
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
			 String dataDir = System.getProperty("dataDir");
			 if (null == dataDir) {
				 dataDir = "C:\\sandbox\\data\\profile-img\\";
				 // System.out.println("\n\nData Directory = " + dataDir);
			 } else {
				 // System.out.println("\n\nData Directory = " + dataDir);
			 }
			 FileOutputStream fos = new FileOutputStream(dataDir + "/profile-img/users" + (String)map.get("id").toString() + ".jpg");
			 fos.write(response);
			 fos.close();
			
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
			
			if (map.get("location") != null)
			{
			
				HashMap<String,String> locationDetails = ((HashMap<String,String>)map.get("location"));
	
				if (locationDetails.get("name") != null)
				{
					Question locationQuestion = questionDao.findByTags("location");
					Choice locationChoice = new Choice();
					locationChoice.setAnswerText(locationDetails.get("name"));
					locationChoice.addLink("question", new Link(locationQuestion.getRef(), "choice-question"));
					locationChoice.addLink("user", new Link(newUser.getRef(), "choice-user"));
					choiceDao.create(locationChoice);
				}
			}
			
			return Response.status(200).entity(Utilities.toJson(newUser)).build();
		}
		catch (Exception E)
		{
			E.printStackTrace();
			return Response.status(300).entity(new ErrorMessage("failed", "Email address not available")).build();

		}
	};

	@POST
	@Timed
	@Path("/register/checkins")
	@Produces(MediaType.APPLICATION_JSON)
	public Response registerCheckins(@FormParam("checkinData") String checkinData, @FormParam("userRef") String userRef) throws URISyntaxException {
		try {
			// System.out.println(checkinData);
			HashMap<String, Object> map = Utilities.fromJson(Utilities.urlDecode(checkinData));
			List<Choice> myChoices = choiceDao.findByUserRef("/users/"+userRef);

			for (String key : map.keySet()) {
				// System.out.println(key + " = " + map.get(key));
				if (key.equals("data")) {
					Map<String, Object> data = (Map<String, Object>) map.get(key);
					for (String dataKey : data.keySet()) {
						// System.out.println("DATA KEY  = " + dataKey + " val = " + data.get(dataKey));
						Map<String, Object> checkinDetails = (Map<String, Object>) data.get(dataKey);
						
						String message = "";
						if (checkinDetails.get("message") != null)
							 message = (String) checkinDetails.get("message");
						
						String placeName = "";
						String locationCity = "";
						if (checkinDetails.get("place") != null)
						{
							Map<String, Object> place = (Map<String, Object>) checkinDetails.get("place");
							placeName = (String)place.get("name");
							
							if (place.get("location") != null)
							{
								// PLACE AND LOCATION NOT NULL
								
								// NOW : Make Resource
								// TODO: IMPLEMENTTRANSFORMER <T> PATTERN RALPH
								String fbId = (String) checkinDetails.get("id");
								String name = (String) checkinDetails.get("place.name");
								
								
								if (place.get("location") instanceof Map) {
									Map<String, Object> location = (Map<String, Object>) place.get("location");
									String nameOfThePlace = (String)location.get("name");
									String street = (String) location.get("street");
									String city = (String) location.get("city");
									String state = (String) location.get("state");
									String country = (String) location.get("country");
									String zip = (String) location.get("zip");
									String latitude = (String) location.get("latitude");
									String longitude = (String) location.get("longitude");
									
									Place placeResource = new Place(fbId, placeName, street, city, state, country, zip, latitude, longitude);
									if (placeResource.getRef() == null) {
										int x = 1;
										
									}
									placeDao.create(placeResource);
								}
								
								
								if (place.get("location") instanceof Map) {
									Map<String, Object> location = (Map<String, Object>) place.get("location");
									locationCity = (String) location.get("city");
								} else {
									System.out.println("\n\n" + place.get("location"));
									locationCity = (String)place.get("location");
								}

								Question checkinQuestions = questionDao.findByTags(placeName);
								if (checkinQuestions == null) {
									// N0 question - make it
									checkinQuestions = new Question();
									checkinQuestions.setQuestionText("Have you been to " + placeName + " ?");
									checkinQuestions.setAnswerRule("yes|no");
									checkinQuestions.setType("text");
									checkinQuestions.setSource("system");
									checkinQuestions.addLink("self", new Link(checkinQuestions.getRef(), "self"));
									List<String> tagList = new ArrayList<String>();
									tagList.add("fb.checkin");
									if (placeName != null) {
										tagList.add(placeName);
									}
									if(locationCity != null) {
										tagList.add(locationCity);
									}
									checkinQuestions.setTags(tagList);
									questionDao.create(checkinQuestions);
								}

								Choice checkinChoice = new Choice();
								checkinChoice.setAnswerText("yes");
								checkinChoice.addLink("question", new Link(checkinQuestions.getRef(), "choice-question"));
								checkinChoice.addLink("user", new Link("/users/" + userRef, "choice-user"));
								checkinChoice.addLink("self", new Link(checkinChoice.getRef(), "self"));

								boolean alreadyExists = false;
								for (Choice otherChoice : myChoices)
								{
									if (otherChoice.getLinkByName("question").getHref().equals(checkinQuestions.getRef())
											&& otherChoice.getAnswerText().equals("yes"))
										alreadyExists = true;
								}
								
								if (!alreadyExists)
								{
									choiceDao.create(checkinChoice);
								}
							} else {
								// PLACE NOT NULL - LOCATION NULL
							}
						} else {
							// PLACE IS NULL
						}
						// System.out.println("[ " + message + " ] at [" + placeName + "] in [" + locationCity + "]");


					}
				}
			}
			return Response.status(200).entity(Utilities.toJson("Facebook Likes Imported.")).build();
		} catch (Exception E) {
			E.printStackTrace();
			return Response.status(300).entity(new ErrorMessage("failed", "Email address not available")).build();
		}
	};
	
	@POST
	@Timed
	@Path("/register/tv")
	@Produces(MediaType.APPLICATION_JSON)
	public Response registerTv(@FormParam("tvData") String tvData, @FormParam("userRef") String userRef) throws URISyntaxException {
		try {
			// System.out.println(likeData);
			HashMap<String, Object> map = Utilities.fromJson(Utilities.urlDecode(tvData));
			List<Choice> myChoices = choiceDao.findByUserRef("/users/"+userRef);

			
			for (String key : map.keySet()) {
				// System.out.println(key + " = " + map.get(key));
				if (key.equals("data")) {
					Map<String, Object> data = (Map<String, Object>) map.get(key);
					for (String dataKey : data.keySet()) {
						// System.out.println(dataKey + " = " + data.get(dataKey));
						Map<String, Object> tvDetails = (Map<String, Object>) data.get(dataKey);
						Question tvQuestion = questionDao.findByTags((String) tvDetails.get("name"));
						if (tvQuestion == null) {
							// N0 question - make it
							tvQuestion = new Question();
							tvQuestion.setQuestionText("Do you like " + tvDetails.get("name"));
							tvQuestion.setAnswerRule("yes|no");
							tvQuestion.setType("text");
							tvQuestion.setSource("system");
							tvQuestion.addLink("self", new Link(tvQuestion.getRef(), "self"));
							List<String> tagList = new ArrayList<String>();
							tagList.add("fb.television");
							tagList.add("television");
							tagList.add((String)tvDetails.get("name"));
							tvQuestion.setTags(tagList);
							questionDao.create(tvQuestion);
						}
						
						Choice likeChoice = new Choice();
						likeChoice.setAnswerText("yes");
						likeChoice.addLink("question", new Link(tvQuestion.getRef(), "choice-question"));
						likeChoice.addLink("user", new Link("/users/"+userRef, "choice-user"));
						likeChoice.addLink("self", new Link(likeChoice.getRef(), "self"));
						
						boolean alreadyExists = false;
						for (Choice otherChoice : myChoices)
						{
							if (otherChoice.getLinkByName("question").getHref().equals(tvQuestion.getRef())
									&& otherChoice.getAnswerText().equals("yes"))
								alreadyExists = true;
						}
						
						if (!alreadyExists)
						{
							choiceDao.create(likeChoice);
						}
					}
				}
			}
			return Response.status(200).entity(Utilities.toJson("Facebook TV Shows Imported")).build();
		}
		catch (Exception E)
		{
			E.printStackTrace();
			return Response.status(300).entity(new ErrorMessage("failed", "Email address not available")).build();

		}
	};
	
	@POST
	@Timed
	@Path("/register/movies")
	@Produces(MediaType.APPLICATION_JSON)
	public Response registerMovies(@FormParam("movieData") String movieData, @FormParam("userRef") String userRef) throws URISyntaxException {
		try {
			// System.out.println(likeData);
			HashMap<String, Object> map = Utilities.fromJson(Utilities.urlDecode(movieData));
			List<Choice> myChoices = choiceDao.findByUserRef("/users/"+userRef);

			for (String key : map.keySet()) {
				// System.out.println(key + " = " + map.get(key));
				if (key.equals("data")) {
					Map<String, Object> data = (Map<String, Object>) map.get(key);
					for (String dataKey : data.keySet()) {
						// System.out.println(dataKey + " = " + data.get(dataKey));
						Map<String, Object> movieDetails = (Map<String, Object>) data.get(dataKey);
						Question movieQuestion = questionDao.findByTags((String) movieDetails.get("name"));
						if (movieQuestion == null) {
							// N0 question - make it
							movieQuestion = new Question();
							movieQuestion.setQuestionText("Do you like " + movieDetails.get("name"));
							movieQuestion.setAnswerRule("yes|no");
							movieQuestion.setType("text");
							movieQuestion.setSource("system");
							movieQuestion.addLink("self", new Link(movieQuestion.getRef(), "self"));
							List<String> tagList = new ArrayList<String>();
							tagList.add("fb.movie");
							tagList.add("movies");
							tagList.add((String)movieDetails.get("name"));
							movieQuestion.setTags(tagList);
							questionDao.create(movieQuestion);
						
						}
						
						boolean alreadyExists = false;
						for (Choice otherChoice : myChoices)
						{
							if (otherChoice.getLinkByName("question").getHref().equals(movieQuestion.getRef())
									&& otherChoice.getAnswerText().equals("yes"))
								alreadyExists = true;
						}
						
						if (!alreadyExists)
						{
							Choice likeChoice = new Choice();
							likeChoice.setAnswerText("yes");
							likeChoice.addLink("question", new Link(movieQuestion.getRef(), "choice-question"));
							likeChoice.addLink("user", new Link("/users/"+userRef, "choice-user"));
							likeChoice.addLink("self", new Link(likeChoice.getRef(), "self"));
							
							choiceDao.create(likeChoice);
						}
					}
				}
			}
			return Response.status(200).entity(Utilities.toJson("Facebook Movies Imported")).build();
		}
		catch (Exception E)
		{
			E.printStackTrace();
			return Response.status(300).entity(new ErrorMessage("failed", "Email address not available")).build();

		}
	};
	
	
	@POST
	@Timed
	@Path("/register/likes")
	@Produces(MediaType.APPLICATION_JSON)
	public Response registerLikes(@FormParam("likeData") String likeData, @FormParam("userRef") String userRef) throws URISyntaxException {
		try {
			//get my choices so we can skip duplicates
			List<Choice> myChoices = choiceDao.findByUserRef("/users/"+userRef);
			// System.out.println(likeData);
			HashMap<String, Object> map = Utilities.fromJson(Utilities.urlDecode(likeData));
			for (String key : map.keySet()) {
				// System.out.println(key + " = " + map.get(key));
				if (key.equals("data")) {
					Map<String, Object> data = (Map<String, Object>) map.get(key);
					for (String dataKey : data.keySet()) {
						Map<String, Object> likeDetails = (Map<String, Object>) data.get(dataKey);
						// System.out.println(dataKey + " = " + likeDetails);
						// System.out.println("Category" + " = " + likeDetails.get("category"));
						// System.out.println("Name" + " = " + likeDetails.get("name"));
						
						if (likeDetails.get("name") == null)
							continue;
						
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
							tagList.add("fb.like");
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
						
						boolean alreadyExists = false;
						for (Choice otherChoice : myChoices)
						{
							if (otherChoice.getLinkByName("question").getHref().equals(likeQuestion.getRef())
									&& otherChoice.getAnswerText().equals("yes"))
								alreadyExists = true;
						}
						
						if (!alreadyExists)
							choiceDao.create(likeChoice);
						
						
					}
				}
			}
			return Response.status(200).entity(Utilities.toJson("NEIN!!!!")).build();
		}
		catch (Exception E)
		{
			E.printStackTrace();

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
	
	@GET
	@Timed
	@Path("/auth/response")
	@Produces(MediaType.APPLICATION_JSON)
	public Response handleResponse() {
		System.out.println("\n\nhandleResponse() called with");

		try {
//			String url = "https://secure.meetup.com/oauth2/authorize?client_id=741959166e521922252746d4621964&response_type=token&redirect_uri=app.clickd.com/users/auth/response";
//			String data = Utilities.getFromUrl(url);
			return Response.status(200).entity(new String("ok")).build();

		} catch (Exception e) {
			return Response.status(300).entity(new ErrorMessage("failed", e.getMessage())).build();			
		}
	}

	@GET
	@Timed
	@Path("/auth")
	@Produces(MediaType.APPLICATION_JSON)
	public Response mSignIn() {
		try {
			String url = "https://secure.meetup.com/oauth2/authorize?client_id=741959166e521922252746d4621964&response_type=token&redirect_uri=app.clickd.com/users/auth/response";
			String data = Utilities.getFromUrl(url);
			return Response.status(200).entity(new String("ok")).build();

		} catch (Exception e) {
			return Response.status(300).entity(new ErrorMessage("failed", e.getMessage())).build();			
		}
	}
	
	@GET
	@Timed
	@Path("/places/map/{userRef}/{currentSelection}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCheckinsForMap(@PathParam("userRef") String userRef, @PathParam("currentSelection") String currentSelection) {
		System.out.println("\n\ngetCheckinsForMap() called with [" + currentSelection + "]");
		List<Checkin> results = new ArrayList<Checkin>();
		
		// Map for candidates
		if (currentSelection.equals("candidates")) {
			try {
				List<CandidateResponse> responseList = (List<CandidateResponse>) getCandidates(userRef).getEntity();
				List<Checkin> allCheckins = checkinDao.findAll();
				
				for (Checkin checkin : allCheckins) {
					Link userLink = checkin.getLinkByName("user");
					User user = userDao.findByRef(userLink.getHref());
					// Only candidate(s) filter
					boolean inCandidateCheckins = false;
					for (CandidateResponse candidateResponse : responseList) {
						if (candidateResponse.getUser().getRef().equals(checkin.getLinkByName("user").getHref())) {
							inCandidateCheckins = true;
							break;
						}
					}
					if (inCandidateCheckins) {
						continue;
					}
					Link placeLink = checkin.getLinkByName("place");
					Place place = placeDao.findByRef(placeLink.getHref());
					// DONT RETURN EMPTY PLACES
					if (place != null) {
						// EMBED THE USER AND PLACE - TUT TUT TUT!!!!
						checkin.get_Embedded().put("the-user", user);
						checkin.get_Embedded().put("the-place", place);
						results.add(checkin);
					}
				}
				List<Checkin> clippedResults = results.subList(0, Math.min(200, results.size()));
				System.out.println("getMap() returning " + clippedResults.size() + " out of " +  results.size() + " possible checkins");
				return Response.status(200).entity(Utilities.toJson(clippedResults)).build();
			} catch(Exception e) {
				e.printStackTrace();
				return Response.status(300).entity(new ErrorMessage("failed", e.getMessage())).build();			
			}
		}
		
		if (currentSelection.startsWith("candidate=")) {
			String candidateRef = currentSelection.substring(currentSelection.indexOf("=") + 1);
			candidateRef = "/users/" + candidateRef;
			try {
				List<Checkin> allCheckins = checkinDao.findAll();
				for (Checkin checkin : allCheckins) {
					Link userLink = checkin.getLinkByName("user");
					User user = userDao.findByRef(userLink.getHref());
					// Only candidate(s) filter
					boolean inCandidateCheckins = false;
					// ONLY THE CANDIDATE AND ME
					if (candidateRef.equals(checkin.getLinkByName("user").getHref()) 
						|| checkin.getLinkByName("user").getHref().equals("/users/" + userRef) ) {
						inCandidateCheckins = true;
					}
					if (!inCandidateCheckins) {
						continue;
					}
					Link placeLink = checkin.getLinkByName("place");
					Place place = placeDao.findByRef(placeLink.getHref());
					// DONT RETURN EMPTY PLACES
					if (place != null) {
						// EMBED THE USER AND PLACE - TUT TUT TUT!!!!
						checkin.get_Embedded().put("the-user", user);
						checkin.get_Embedded().put("the-place", place);
						results.add(checkin);
					}
				}
				List<Checkin> clippedResults = results.subList(0, Math.min(50, results.size()));
				System.out.println("getMap() returning " + clippedResults.size() + " out of " +  results.size() + " possible checkins");
				return Response.status(200).entity(Utilities.toJson(results)).build();
			} catch(Exception e) {
				e.printStackTrace();
				return Response.status(300).entity(new ErrorMessage("failed", e.getMessage())).build();			
			}
		}
		
		// Map for cliques
		if (currentSelection.equals("cliques")) {
			try {
				Set<Checkin> cliqueCheckins = new HashSet<Checkin>();
				List<Clique> myCliques = getCliquesForUser(userRef);
				Set<String> myCliqueMembers = new TreeSet<String>();
				for (Clique clique : myCliques) {
					List<Choice> matchingChoices = (List<Choice>)clique.get_Embedded().get("matching-choices");
					for (Choice choice : matchingChoices) {
						// Extract the clique members 
						Link userLink = choice.getLinkByName("user");
						String choiceUserRef = userLink.getHref();
						myCliqueMembers.add(choiceUserRef);
					}
				}
				System.out.println("User " + userRef + " has " + myCliqueMembers.size() + " total clique members");
				
				// For each person
				for (String cliqueUserRef : myCliqueMembers) {
					// Find checkins per person
					List<Checkin> cliqueMemberCheckins = checkinDao.findForUserRef(cliqueUserRef);
					
					// Embed PLACE + USER for front end
					// TODO: Revisit. This is inefficient
					for (Checkin checkin : cliqueMemberCheckins) {
						Link userLink = checkin.getLinkByName("user");
						User user = userDao.findByRef(userLink.getHref());

						Link placeLink = checkin.getLinkByName("place");
						Place place = placeDao.findByRef(placeLink.getHref());
						// DONT RETURN EMPTY PLACES
						if (place != null) {
							// EMBED THE USER AND PLACE - TUT TUT TUT!!!!
							checkin.get_Embedded().put("the-user", user);
							checkin.get_Embedded().put("the-place", place);
							results.add(checkin);
						}
					}
					// Store in checkin set
					cliqueCheckins.addAll(cliqueMemberCheckins);
				}
				
				List<Checkin> clippedResults = results.subList(0, Math.min(200, results.size()));
				System.out.println("getMap(cliques) returning " + cliqueCheckins.size() + " checkins");
				return Response.status(200).entity(Utilities.toJson(cliqueCheckins)).build();
			} catch(Exception e) {
				e.printStackTrace();
				return Response.status(300).entity(new ErrorMessage("failed", e.getMessage())).build();			
			}
		}
		
		
		// Map for cliques
		if (currentSelection.startsWith("clique=")) {
			String cliqueRef = currentSelection.substring(currentSelection.indexOf("=") + 1);
			// cliqueRef = "/cliques/" + cliqueRef;

			try {
				Set<Checkin> cliqueCheckins = new HashSet<Checkin>();
				Clique clique = getClique(userRef, cliqueRef);
				Set<String> myCliqueMembers = new TreeSet<String>();
				
				List<Choice> myChoices = choiceDao.findByUserRef("/users/"+userRef);
				
				
				List<Choice> matchingChoices = (List<Choice>)clique.get_Embedded().get("matching-choices");
				for (Choice choice : matchingChoices) {
					// Extract the clique members 
					Link userLink = choice.getLinkByName("user");
					String choiceUserRef = userLink.getHref();
					myCliqueMembers.add(choiceUserRef);
				}
				System.out.println("User " + userRef + " has " + myCliqueMembers.size() + " total clique members");
				
				// For each person
				for (String cliqueUserRef : myCliqueMembers) {
					// Find checkins per person
					List<Checkin> cliqueMemberCheckins = checkinDao.findForUserRef(cliqueUserRef);
					
					// Embed PLACE + USER for front end
					// TODO: Revisit. This is inefficient
					for (Checkin checkin : cliqueMemberCheckins) {
						Link userLink = checkin.getLinkByName("user");
						User user = userDao.findByRef(userLink.getHref());

						Link placeLink = checkin.getLinkByName("place");
						Place place = placeDao.findByRef(placeLink.getHref());
						// DONT RETURN EMPTY PLACES
						if (place != null) {
							// EMBED THE USER AND PLACE - TUT TUT TUT!!!!
							checkin.get_Embedded().put("the-user", user);
							checkin.get_Embedded().put("the-place", place);
							results.add(checkin);
						}
					}
					// Store in checkin set
					cliqueCheckins.addAll(cliqueMemberCheckins);
				}
				
				List<Checkin> clippedResults = results.subList(0, Math.min(200, results.size()));
				System.out.println("getMap(cliques) returning " + cliqueCheckins.size() + " checkins");
				return Response.status(200).entity(Utilities.toJson(cliqueCheckins)).build();
			} catch(Exception e) {
				e.printStackTrace();
				return Response.status(300).entity(new ErrorMessage("failed", e.getMessage())).build();			
			}
		}
				
		// KILL THIS WITH AN ELSE
		return Response.status(200).entity(Utilities.toJson(results)).build();
		
	}

	@GET
	@Path("/{userRef}/candidates")
	@Timed
	public Response getCandidates(@PathParam("userRef") String userRef) {
		try {
			User user = userDao.findByRef("/users/"+userRef);
			// get my answers
			List<Choice> myChoices = choiceDao.findByUserRef("/users/"+userRef);
			ArrayList<CandidateResponse> responseList = new ArrayList<CandidateResponse>();
			ArrayList<Connection> myConnections = (ArrayList<Connection>)connectionDao.findAllByUserRef("/users/" + userRef);

			
			System.out.println("getCandidates() searching [" + myChoices.size() + "]");
			for (Choice choice : myChoices) {
				Question choiceQuestion = questionDao.findByRef(choice.getLinkByName("question").getHref());
				if (choiceQuestion != null) {
					// System.out.println(choiceQuestion.getQuestionText() );
				} else {
					System.out.println("\n\nALERT\nNO CHOICE QUESTION for choice :" + choice.getRef());
					
				}

				ArrayList<Choice> sameAnswerChoices = new ArrayList<Choice>();
				sameAnswerChoices.addAll(choiceDao.findChoicesWithTheSameAnswerByAnswerTextAndQuestionRef(choice.getAnswerText(), choice.getLinkByName("question").getHref()));

				// Now we have all the choices that gave the same answer
				// Get the users that gave them, filter out ourself and score candidates
				for (Choice otherUsersChoice : sameAnswerChoices) {
					Link otherUserLink = (Link) otherUsersChoice.getLinkByName("user");
					
					if (otherUserLink.getHref().equals("/users/"+userRef))
						continue;
					
					boolean toSkip = false;
						boolean alreadyExists = false;
						for (CandidateResponse responseRow : responseList) {
							if (responseRow.getUser().getRef().equals(otherUserLink.getHref())) {
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
									if (link.getHref().equals(otherUserLink.getHref()))
									{
										isAConnection = true;
									}
								}
						}
						
						if (!alreadyExists && !isAConnection && !toSkip) {
							User otherUser = userDao.findByRef(otherUserLink.getHref());
							CandidateResponse responseRow = new CandidateResponse(otherUser, 1);
							responseList.add(responseRow);
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
			return Response.status(200).entity(responseList.subList(0, Math.min(responseList.size(), 13))).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(300).entity(new ErrorMessage("failed", e.getMessage())).build();
		}
	}
	
	
	private ComparisonResponse userComparison(String userRef, String otherUserRef)
	{	
		//get my answers
		List<Choice> myChoices = choiceDao.findByUserRef("/users/"+userRef);
		List<Choice> otherUserChoices = choiceDao.findByUserRef("/users/"+otherUserRef);
		ArrayList<String> same = new ArrayList<String>();
		ArrayList<String> different = new ArrayList<String>();
		
		User me = userDao.findByRef("/users/"+userRef);
		User otherUser = userDao.findByRef("/users/"+otherUserRef);

		System.out.println("Starting compare with " + myChoices.size() + " myChoices and " + otherUserChoices.size() + " other user choices");
		for (Choice choice : myChoices)
		{
			for (Choice choice2 : otherUserChoices)
			{
				if (choice.getLinkByName("question") == null || choice2.getLinkByName("question") == null)
					continue;
				
				Question question  = questionDao.findByRef(choice.getLinkByName("question").getHref());
				
				if (null == question || question.getTags() == null || choice.getAnswerText() == null || choice2.getAnswerText() == null)
					continue;
				
				if (choice.getAnswerText().equals("skip") || choice2.getAnswerText().equals("skip"))
					continue;
				
				String responseString = getProcessedCliqueName(question,choice);
				responseString = responseString.replace("likes", "like");
				
				if (same.contains(responseString)) {
					// System.out.println("Skipping Duplicate " + responseString);
					continue;
				}
				
				if (choice.getLinkByName("question").getHref().equals(choice2.getLinkByName("question").getHref()))
				{
					if (choice.getAnswerText().equals(choice2.getAnswerText()))
						same.add(responseString);
					else if (!question.getTags().contains("bio"))
					{
						String otherResponseString = otherUser.getFirstName()+" "+getProcessedCliqueName(question, choice2);
						different.add(otherResponseString);
					}
				}
					
				
			}
			
		}
		
		ComparisonResponse response = new ComparisonResponse();
		
		response.setAgree(same);
		response.setDisagree(different);
		return response;
	}
	
	@GET
	@Path("/{userRef}/candidates/comparison/{otherUserRef}")
	@Timed
	public Response compareCandidate(@PathParam("userRef") String userRef, @PathParam("otherUserRef") String otherUserRef) {
		try {
			ComparisonResponse response = userComparison(userRef, otherUserRef);
			
			return Response.status(200).entity(Utilities.toJson(response)).build();
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
			
			
			return Response.status(200).entity(Utilities.toJson( userConnectionsResponse.subList(0, Math.min(13, userConnectionsResponse.size())))).build();
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
	
	public String getProcessedCliqueName(Question question, Choice myChoice)
	{
		String cliqueName = question.getQuestionText();
		
		String likeVerb = "Likes";
		if (myChoice.getAnswerText().equals("no"))
			likeVerb = "Doesn't Like";
		
		if (question.getTags().get(question.getTags().size()-1).equals("fb.likes"))
			cliqueName = likeVerb+question.getTags().get(0)+" ("+question.getTags().get(1)+")";
	
		if (question.getTags().get(0).equals("genre"))
			cliqueName = likeVerb + " "+ question.getTags().get(1) + " movies";
		
		if (question.getTags().get(0).equals("fb.checkin"))
			cliqueName = "Been to "+question.getTags().get(1);
		
		if (question.getTags().get(1).equals("fb.movies"))
			cliqueName = likeVerb+" movie "+question.getTags().get(0);

		if (question.getTags().get(1).equals("fb.televisions"))
			cliqueName = likeVerb+" TV show "+question.getTags().get(0);

		if (question.getTags().get(1).equals("fb.book"))
			cliqueName = likeVerb+" book "+question.getTags().get(0);
		
		if (question.getTags().get(0).equals("aboutme"))
			cliqueName = question.getTags().get(2)+": "+myChoice.getAnswerText();
		
		
		return cliqueName;
	}
	
	@GET
	@Path("/{userRef}/cliques")
	@Timed
	public Response getCliquesForUserAsResponse(@PathParam("userRef") String userRef) {
		try {
			List<Clique> myCliques = getCliquesForUser(userRef);
			myCliques = myCliques.subList(0, Math.min(13, myCliques.size()));

			return Response.status(200).entity(Utilities.toJson(myCliques)).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(300).entity(new ErrorMessage("failed", e.getMessage())).build(); 
		}
	}
	
	public List<Clique> getCliquesForUser(String userRef) {
		try {
			User user = userDao.findByRef("/users/" + userRef);
			List<Clique> myCliques = new ArrayList<Clique>();	
			// Add CHOICE based cliques
			List<Choice> myChoices = choiceDao.findByUserRef("/users/"+userRef);
			System.out.println("getCliques() looping over " + myChoices.size() + " choices for userRef " + userRef);
			for (Choice myChoice : myChoices)
			{
				// System.out.println("getCliques() testing " + myChoice.getAnswerText());
				if (myChoice.getAnswerText().equals("skip"))
					continue;
				
				//now get list of users who made that choice
				Question question = questionDao.findByRef(myChoice.getLinkByName("question").getHref());
				
				String cliqueName = getProcessedCliqueName(question, myChoice);
				
				Clique thisClique = new Clique(user, new Date(), new Date(), "system", cliqueName);
				thisClique.get_Embedded().put("clique-choice", myChoice);
				List<Choice> matchingChoices = choiceDao.findChoicesWithTheSameAnswerByAnswerTextAndQuestionRef(myChoice.getAnswerText(), myChoice.getLinkByName("question").getHref());
				//thisClique.get_Embedded().put("matching-choices", new ArrayList());
				thisClique.setRef("/cliques/" + myChoice.getRef().split("/")[2]);
				thisClique.setRef("/cliques/"+myChoice.getRef().split("/")[2]);
				thisClique.setCliqueSize(matchingChoices.size());
				//thisClique.setCliqueSize(0);
				myCliques.add(thisClique);
			}
			
			// Sort the responses	
			long startSort = new Date().getTime();
			Collections.sort(myCliques, new Comparator<Clique>() {
				@Override
				public int compare(Clique cl1, Clique cl2) {
					return cl2.getCliqueSize() - cl1.getCliqueSize();
				}
			});
			long endSort = new Date().getTime();
			System.out.println("Sorting " + myCliques.size() + " took " + (endSort - startSort)/1000 + " secs");
			
			return myCliques;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public Clique getClique(String userRef, String cliqueRef) {
		try {
			ArrayList <User> cliqueUsers = new ArrayList();
			Choice myChoice = choiceDao.findByRef("/choices/"+cliqueRef);
			Question question = questionDao.findByRef(myChoice.getLinkByName("question").getHref());
			User me = userDao.findByRef("/users/"+userRef);
			Clique thisClique = new Clique(me, new Date(), new Date(), "system", question.getTags().toString()+" "+myChoice.getAnswerText());
			thisClique.get_Embedded().put("clique-choice", myChoice);
			
			List<Choice> usersWithSameChoice = choiceDao.findChoicesWithTheSameAnswerByAnswerTextAndQuestionRef(myChoice.getAnswerText(), question.getRef());
			for (Choice userChoice : usersWithSameChoice)
			{
				if (!userChoice.getLinkByName("user").getHref().equals("/users/"+userRef))
				{
					User thisUser = userDao.findByRef(userChoice.getLinkByName("user").getHref());
					cliqueUsers.add(thisUser);
				}
			}
			String cliqueName = getProcessedCliqueName(question, myChoice);
			thisClique.get_Embedded().put("clique-members", cliqueUsers);
			thisClique.get_Embedded().put("clique-name", cliqueName);
			thisClique.setRef(question.getRef());
			return thisClique;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@GET
	@Path("/{userRef}/cliques/{cliqueRef}")
	@Timed
	public Response getCliqueResponse(@PathParam("userRef") String userRef, @PathParam("cliqueRef") String cliqueRef) {
		try {
			Clique thisClique = getClique(userRef, cliqueRef);
			return Response.status(200).entity(Utilities.toJson(thisClique)).build();
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
