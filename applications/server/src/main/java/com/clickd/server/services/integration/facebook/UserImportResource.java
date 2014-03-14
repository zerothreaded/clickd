package com.clickd.server.services.integration.facebook;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.fest.util.Strings.StringToAppend;
import org.springframework.beans.factory.annotation.Autowired;

import com.clickd.server.dao.BookDao;
import com.clickd.server.dao.CheckinDao;
import com.clickd.server.dao.ChoiceDao;
import com.clickd.server.dao.LikeDao;
import com.clickd.server.dao.MovieDao;
import com.clickd.server.dao.PlaceDao;
import com.clickd.server.dao.QuestionDao;
import com.clickd.server.dao.TelevisionDao;
import com.clickd.server.dao.UserDao;
import com.clickd.server.model.Book;
import com.clickd.server.model.Checkin;
import com.clickd.server.model.Choice;
import com.clickd.server.model.ErrorMessage;
import com.clickd.server.model.Like;
import com.clickd.server.model.Link;
import com.clickd.server.model.Movie;
import com.clickd.server.model.Place;
import com.clickd.server.model.Question;
import com.clickd.server.model.Television;
import com.clickd.server.model.User;
import com.clickd.server.utilities.Constants;
import com.clickd.server.utilities.Utilities;
import com.yammer.metrics.annotation.Timed;

@Path("/integration/facebook/user")
@Produces(MediaType.APPLICATION_JSON)
public class UserImportResource {
//	FacebookDataDao facebookCheckinsDao;
//	FacebookDataDao facebookLikesDao;

	@Autowired 
	UserDao userDao;
	
	@Autowired
	ChoiceDao choiceDao;
	
	@Autowired
	QuestionDao questionDao;

	@Autowired
	MovieDao movieDao;
	
	@Autowired
	TelevisionDao televisionDao;
	
	@Autowired
	LikeDao likeDao;
	
	@Autowired
	PlaceDao placeDao;
	
	@Autowired
	BookDao bookDao;
	
	@Autowired
	CheckinDao checkinDao;
	
	//	FacebookDataDao facebookTelevisionDao;
//	FacebookDataDao facebookBooksDao;
	
//	@GET
//	@Path("/{ref}")
//	@Timed
//	public Response getFacebookResource(@PathParam("ref") String ref) {
//		try {
//			User user = facebookDao.findByRef("/facebook/" + ref);
//			if (null != user) {
//				return Response.status(200).entity(user).build();
//			} else {
//				return Response.status(300).entity(new ErrorMessage("failed", "User Not Found")).build();
//			}
//		} catch(Exception e) {
//			return Response.status(300).entity(new ErrorMessage("failed", e.getMessage())).build();			
//		}
//	}
//	
	public void importUserTelevision(String userRef, String televisionsData)
	{
		HashMap<String, Object> map = Utilities.fromJson(televisionsData);
		HashMap<String, Object> televisionMap = (HashMap<String, Object> )map.get("data");
		
		for (String key : televisionMap.keySet()) {
			Map<String,Object> televisionData = (Map<String,Object>)televisionMap.get(key);
			Television newTelevision = new Television();
			newTelevision.setName((String)televisionData.get("name"));
			newTelevision.setRef("/television/"+(String)televisionData.get("id"));
			
			if (televisionDao.findByRef(newTelevision.getRef()) == null)
			{
				// CREATE PATH
				televisionDao.create(newTelevision);
				
				Question televisionQuestion = questionDao.findByTags(newTelevision.getName());	
				if (televisionQuestion == null)
				{
					televisionQuestion = new Question("Do you like " + newTelevision.getName() + "?", "system");
					televisionQuestion.getTags().add(newTelevision.getName());
					televisionQuestion.getTags().add("fb.televisions");
					televisionQuestion.addLink("television-data", new Link(newTelevision.getRef(), "television-data"));
					televisionQuestion.setAnswerRule("yes|no");
					questionDao.create(televisionQuestion);
				}
//				
				Choice myChoice = new Choice();
				myChoice.addLink("question", new Link(televisionQuestion.getRef(), "choice-question"));
				myChoice.addLink("user", new Link("/users/"+userRef, "choice-user"));
				myChoice.setAnswerText("yes");
			
				choiceDao.create(myChoice);

			
			} else {
				// UPDATE PATH
				
				Question televisionQuestion = questionDao.findByTags(newTelevision.getName());	
				System.out.println("\t\tUpdate Television for " + televisionQuestion.getQuestionText());

				List<Choice> myChoices = choiceDao.findByUserRef("/users/"+userRef);
				boolean alreadyExists = false;
				for (Choice choice : myChoices)
				{
					if (choice.getLinkByName("question").getHref().equals(televisionQuestion.getRef())
					 && choice.getLinkByName("user").getHref().equals("/users/"+userRef))
						alreadyExists = true;
				}
				if (alreadyExists) {
					// DO NOTHING TO IT - NO UPDATE CHOICE YET
				} else {
					Choice myChoice = new Choice();
					myChoice.addLink("question", new Link(televisionQuestion.getRef(), "choice-question"));
					myChoice.addLink("user", new Link("/users/"+userRef, "choice-user"));
					myChoice.setAnswerText("yes");
				
					choiceDao.create(myChoice);
					
				}

				
			}
		}
	}
	
	public User importUserBooks(String userRef, String booksData)
	{
		User user = userDao.findByRef("/users/"+userRef);
		
		HashMap<String, Object> map = Utilities.fromJson(booksData);
		HashMap<String, Object> booksMap = (HashMap<String, Object> )map.get("data");
		
		for (String key : booksMap.keySet()) {
			Map<String,Object> bookData = (Map<String,Object>)booksMap.get(key);
			if (null == bookData.get("name"))
			{
				continue;	
			}
			
			String bookRef = "/books/"+(String)bookData.get("id");
			if (bookDao.findByRef(bookRef) == null)
			{
				// Create path
				Book newBook = new Book();
				newBook.setName((String)bookData.get("name"));
				newBook.setRef(bookRef);
				
				bookDao.create(newBook);
				
				Question bookQuestion = questionDao.findByTags(newBook.getName());
				if (bookQuestion == null)
				{
					bookQuestion = new Question("Do you like "+newBook.getName()+"?", "system");
					bookQuestion.getTags().add(newBook.getName());
					bookQuestion.getTags().add("fb.books");
					bookQuestion.addLink("book-data", new Link(newBook.getRef(), "book-data"));
					bookQuestion.setAnswerRule("yes|no");
					questionDao.create(bookQuestion);
				}
				
			}
			else
			{
				// UPDATE PATH
				System.out.println("\t\tSkipping UPDATE book for " + (String)bookData.get("name"));
			}
			
			Question bookQuestion = questionDao.findByTags((String)bookData.get("name"));
			List<Choice> myChoices = choiceDao.findByUserRef("/users/"+userRef);
			boolean alreadyExists = false;
			for (Choice choice : myChoices)
			{
				if (choice.getLinkByName("question").getHref().equals(bookQuestion.getRef())
						&& choice.getLinkByName("user").getHref().equals("/users/"+userRef))
					alreadyExists = true;
			}
			
			if (!alreadyExists)
			{
				Choice myChoice = new Choice();
				myChoice.addLink("question", new Link(bookQuestion.getRef(), "choice-question"));
				myChoice.addLink("user", new Link("/users/"+userRef, "choice-user"));
				myChoice.setAnswerText("yes");
				choiceDao.create(myChoice);
				myChoices.add(myChoice);
			}
		}
		return user;
	}
	
	@SuppressWarnings("unchecked")
	public void importUserMovies(String userRef, String moviesData)
	{
		HashMap<String, Object> map = Utilities.fromJson(moviesData);
		HashMap<String, Object> moviesMap = (HashMap<String, Object> )map.get("data");
		for (String key : moviesMap.keySet()) {
			Map<String,Object> facebookMovie = (Map<String,Object>)moviesMap.get(key);
			
			if (null == (String)facebookMovie.get("name"))
			{
				System.out.println("[NULL] Movie Name detected. Skipping movie import");
				continue;	
			}
			
			// Create or Update test
			String movieRef = "/movies/"+(String)facebookMovie.get("id");
			if (movieDao.findByRef(movieRef) == null)
			{
				// Create
				Movie newMovie = new Movie();
				newMovie.setName((String)facebookMovie.get("name"));
				newMovie.setRef(movieRef);
				
				// Enrich with OMDB call
				String omdbUrl = "http://www.omdbapi.com/?t=" + URLEncoder.encode(newMovie.getName());
				String omdbContent;
				try {
					omdbContent = Utilities.getFromUrl(omdbUrl);
					Map<String, Object> omdbProperties = Utilities.fromJson(omdbContent);
					newMovie.setPosterImageUrl((String) omdbProperties.get("Poster"));
					newMovie.setCountry((String) omdbProperties.get("Country"));
					newMovie.setGenres((String) omdbProperties.get("Genre"));
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("OMDB FAILED FOR "+ newMovie.getName());
				}
				
				// PERSIST
				newMovie = movieDao.create(newMovie);
				System.out.println("\t\tCreated Movie : "+ (String)facebookMovie.get("name"));
				
				// Create Question
				Question movieQuestion = questionDao.findByTags(newMovie.getName());
				if (movieQuestion == null)
				{
					movieQuestion = new Question("Do you like " + newMovie.getName() + "?", "system");
					movieQuestion.getTags().add(newMovie.getName());
					movieQuestion.getTags().add("fb.movies");
					movieQuestion.addLink("movie-data", new Link(newMovie.getRef(), "movie-data"));
					movieQuestion.setAnswerRule("yes|no");
					questionDao.create(movieQuestion);
				}
				
				// Create Do you like {MOVIE} Choice
				List<Choice> myChoices = choiceDao.findByUserRef("/users/"+userRef);
				boolean alreadyExists = false;
				for (Choice choice : myChoices)
				{
				if (choice.getLinkByName("question").getHref().equals(movieQuestion.getRef())
							&& choice.getLinkByName("user").getHref().equals("/users/"+userRef))
						alreadyExists = true;
				}
				if (!alreadyExists)
				{
					Choice myChoice = new Choice();
					myChoice.addLink("question", new Link(movieQuestion.getRef(), "choice-question"));
					myChoice.addLink("user", new Link("/users/"+userRef, "choice-user"));
					myChoice.setAnswerText("yes");
					choiceDao.create(myChoice);
					myChoices.add(myChoice);
				} else {
					// UPDATE PRIOR CHOICE
				}
				
				// Create Genre Questions and Choices
				String genres = newMovie.getGenres();
				if (null != genres && !genres.equals("")) {
					StringTokenizer tokenizer = new StringTokenizer(genres, ",");
					while (tokenizer.hasMoreTokens()) {
						String genre = tokenizer.nextToken();
						Question genreQuestion = questionDao.findByTags(genre);
						if (genreQuestion == null)
						{
							genreQuestion = new Question("Do you like " + genre + " movies ?", "system");
							genreQuestion.getTags().add("genre");
							genreQuestion.getTags().add(genre);
							genreQuestion.setAnswerRule("yes|no");
							questionDao.create(genreQuestion);
						}
						
						// Create GENRE Choice
						alreadyExists = false;
						for (Choice choice : myChoices)
						{
						if (choice.getLinkByName("question").getHref().equals(genreQuestion.getRef())
									&& choice.getLinkByName("user").getHref().equals("/users/"+userRef))
								alreadyExists = true;
						}
						
						if (!alreadyExists)
						{
							Choice myChoice = new Choice();
							myChoice.addLink("question", new Link(genreQuestion.getRef(), "choice-question"));
							myChoice.addLink("user", new Link("/users/"+userRef, "choice-user"));
							myChoice.setAnswerText("yes");
							choiceDao.create(myChoice);
						} else {
							System.out.println("update asking genre question " + genreQuestion.getQuestionText() + " again");
						}
					} // END GENRE LOOP
				}
			} else {
				//update
				System.out.println("\t\tUpdate Movie for "+ (String)facebookMovie.get("name"));
			}
		}
	}
	
	public void importUserLikes(String userRef, String likesData)
	{
		
		HashMap<String, Object> map = Utilities.fromJson(likesData);
		HashMap<String, Object> likesMap = (HashMap<String, Object> )map.get("data");
		
		for (String key : likesMap.keySet()) {
			Map<String,Object> likeData = (Map<String,Object>)likesMap.get(key);
			Like newLike = new Like();
		
			newLike.setCategory((String)likeData.get("category"));
			newLike.setName((String)likeData.get("name"));
			newLike.setRef("/likes/"+(String)likeData.get("id"));
			
			if (newLike.getName() == null)
				continue;
			
			if (likeDao.findByRef(newLike.getRef()) == null)
			{
				likeDao.create(newLike);
				
				Question likeQuestion = questionDao.findByTags(newLike.getName());
				if (likeQuestion == null)
				{
					likeQuestion = new Question("Do you like "+newLike.getName()+"?", "system");
					likeQuestion.getTags().add(newLike.getName());
					likeQuestion.getTags().add(newLike.getCategory());
					likeQuestion.getTags().add("fb.likes");
					likeQuestion.addLink("like-data", new Link(newLike.getRef(), "like-data"));
					likeQuestion.setAnswerRule("yes|no");
					questionDao.create(likeQuestion);
				}
				
		
			}
			else
			{
				//update path
			}
			
			Question likeQuestion = questionDao.findByTags((String)likeData.get("name"));
			List<Choice> myChoices = choiceDao.findByUserRef("/users/"+userRef);
			boolean alreadyExists = false;
			for (Choice choice : myChoices)
			{
				if (choice.getLinkByName("question").getHref().equals(likeQuestion.getRef())
						&& choice.getLinkByName("user").getHref().equals("/users/"+userRef))
					alreadyExists = true;
			}
			
			if (!alreadyExists)
			{
				Choice myChoice = new Choice();
				myChoice.addLink("question", new Link(likeQuestion.getRef(), "choice-question"));
				myChoice.addLink("user", new Link("/users/"+userRef, "choice-user"));
				myChoice.setAnswerText("yes");
				choiceDao.create(myChoice);
				myChoices.add(myChoice);
			}
		}
	}
	
	public User importUserCheckins (String userRef, String checkinData)
	{
		try {
			User user = userDao.findByRef("/users/" + userRef);
			// System.out.println(checkinData);
			
			HashMap<String, Object> map = Utilities.fromJson(checkinData);
			HashMap<String, Object> checkinsMap = (HashMap<String, Object> )map.get("data");
			
			List<Choice> myChoices = choiceDao.findByUserRef("/users/"+userRef);
			
			for (String dataKey : checkinsMap.keySet()) {
				// System.out.println("DATA KEY  = " + dataKey + " val = " + data.get(dataKey));
				Map<String, Object> checkinDetails = (Map<String, Object>) checkinsMap.get(dataKey);
				
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
						String checkinId = (String) checkinDetails.get("id");
						String placeId = (String) place.get("id");

						
						Place placeResource = placeDao.findByRef("/places/"+placeId);
								
						if (placeResource == null && place.get("location") instanceof Map) {
							Map<String, Object> location = (Map<String, Object>) place.get("location");
							String nameOfThePlace = (String)location.get("name");
							String street = (String) location.get("street");
							String city = (String) location.get("city");
							String state = (String) location.get("state");
							String country = (String) location.get("country");
							String zip = (String) location.get("zip");
							String latitude = (String) location.get("latitude");
							String longitude = (String) location.get("longitude");
							
							placeResource = new Place(placeId, placeName, street, city, state, country, zip, latitude, longitude);
							placeResource.setRef("/places/"+placeId);
							placeDao.create(placeResource);
						}
						
						Checkin checkin = new Checkin(checkinId, message, Utilities.dateFromString((String)checkinDetails.get("checkinTime")));
						checkin.addLink("user", new Link("/users/"+userRef, "checkin-user"));
						checkin.addLink("place", new Link("/places/"+placeId, "checkin-place"));
						checkin.setRef("/checkins/"+checkinId);
						checkinDao.create(checkin);
						
						if (placeName == null)
							continue;

						
						Question checkinQuestion = questionDao.findByTags(placeName);
						if (checkinQuestion == null) {
							// N0 question - make it
							checkinQuestion = new Question();
							checkinQuestion.setQuestionText("Have you been to " + placeName + " ?");
							checkinQuestion.setAnswerRule("yes|no");
							checkinQuestion.setType("text");
							checkinQuestion.setSource("system");
							checkinQuestion.addLink("self", new Link(checkinQuestion.getRef(), "self"));
							List<String> tagList = new ArrayList<String>();
							tagList.add("fb.checkin");
							if (placeName != null) {
								tagList.add(placeName);
							}
							if(locationCity != null) {
								tagList.add(locationCity);
							}
							checkinQuestion.setTags(tagList);
							questionDao.create(checkinQuestion);
						}

						Choice checkinChoice = new Choice();
						checkinChoice.setAnswerText("yes");
						checkinChoice.addLink("question", new Link(checkinQuestion.getRef(), "choice-question"));
						checkinChoice.addLink("user", new Link("/users/" + userRef, "choice-user"));
						checkinChoice.addLink("self", new Link(checkinChoice.getRef(), "self"));

						boolean alreadyExists = false;
						for (Choice otherChoice : myChoices)
						{
							if (otherChoice.getLinkByName("question").getHref().equals(checkinQuestion.getRef())
									&& otherChoice.getAnswerText().equals("yes"))
								alreadyExists = true;
						}
						
						if (!alreadyExists)
						{
							choiceDao.create(checkinChoice);
							myChoices.add(checkinChoice);
						}
					} else {
						// PLACE NOT NULL - LOCATION NULL
					}
				} else {
					// PLACE IS NULL
				}
				// System.out.println("[ " + message + " ] at [" + placeName + "] in [" + locationCity + "]");
			}
			return user;
		}
		catch (Exception E)
		{
			E.printStackTrace();
			return null;
		}
	}
	
	public User importUserData(String userRef, String facebookData, String accessToken, User newUser)
	{
		System.out.println("importUserData() called for " + newUser.getEmail());
		try
		{
			System.out.println("\tGetting movies for Friend Id : " + newUser.getEmail());
			String myMoviesUrl = "https://graph.facebook.com/"+userRef+"/movies"+"?access_token="+accessToken;
			String myMovies =  Utilities.getFromUrl(myMoviesUrl);
			if (myMovies != null)
				importUserMovies(newUser.getRef().split("/")[2], myMovies);
	
			System.out.println("\tGetting Television for Friend Id : " + newUser.getEmail());
			String myTelevisionUrl = "https://graph.facebook.com/"+userRef+"/television"+"?access_token="+accessToken;
			String myTelevision =  Utilities.getFromUrl(myTelevisionUrl);
			if (myTelevision != null)
				importUserTelevision(newUser.getRef().split("/")[2], myTelevision);
		
			System.out.println("\tGetting books for Friend Id : " + newUser.getEmail());
			String myBooksUrl = "https://graph.facebook.com/"+userRef+"/books"+"?access_token="+accessToken;
			String myBooks =  Utilities.getFromUrl(myBooksUrl);
			if (myBooks != null)
				importUserBooks(newUser.getRef().split("/")[2], myBooks);
			
			System.out.println("\tGetting Checkins for Friend Id : " + newUser.getEmail());
			String myCheckinsUrl = "https://graph.facebook.com/"+userRef+"/checkins"+"?access_token="+accessToken;
			String myCheckins =  Utilities.getFromUrl(myCheckinsUrl);
			if(myCheckins != null)
				importUserCheckins(newUser.getRef().split("/")[2], myCheckins);

			System.out.println("\tGetting LIKES for Friend Id : " + newUser.getEmail());
			String myLikesUrl = "https://graph.facebook.com/"+userRef+"/likes"+"?access_token="+accessToken;
			String myLikes =  Utilities.getFromUrl(myLikesUrl);
			if(myLikes != null)
				importUserLikes(newUser.getRef().split("/")[2], myLikes);
			
			return newUser;
		}
		catch (Exception E)
		{
			E.printStackTrace();
			return null;
		}
	}
	
	public User createUserFromFacebookData(String facebookData)
	{
		try {
			//System.out.println("createUserFromFacebookData() Starting for " + facebookData);
			HashMap<String, Object> map = Utilities.fromJson(facebookData);

			// Create the new user
			User newUser = new User();
			newUser.setFirstName((String)map.get("first_name"));
			newUser.setLastName((String)map.get("last_name"));
			newUser.setGender((String)map.get("gender"));
			
			if ((String)map.get("email") == null) {
				newUser.setEmail(newUser.getFirstName().toLowerCase()+"."+newUser.getLastName().toLowerCase()+"@clickd.org");
			} else {
				newUser.setEmail((String)map.get("email"));
			}
			newUser.setDateOfBirth(Utilities.dateFromString((String)map.get("user_birthday")));
			newUser.setPassword("fb99");
			newUser.setRef("/users/"+(String)map.get("id"));
			userDao.create(newUser);
			
			// Get the Users FB image and save it locally
			String targetFileName = "C:\\sandbox\\data\\profile-img\\users\\"+(String)map.get("id").toString()+".jpg";
			File file = new File(targetFileName);
			if (!file.exists()) {
				System.out.println("Getting friends Image..");
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
				 FileOutputStream fos = new FileOutputStream("C:\\sandbox\\data\\profile-img\\users\\"+(String)map.get("id").toString()+".jpg");
				 fos.write(response);
				 fos.close();
				 System.out.println("Saved friends Image.");
			} else {
				 // System.out.println("Skipping Image Load");
			}
			
			
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
			
			return newUser;
		}
		catch (Exception E)
		{
			E.printStackTrace();
			return null;
		}
	}

	@GET
	@Timed
	@Path("/authResponse")
	@Produces(MediaType.APPLICATION_JSON)
	public Response importUser(@QueryParam ("code") String code, @Context HttpServletRequest request) {
		try {
			System.out.println(request.getRequestURL());

			String url = "https://graph.facebook.com/oauth/access_token?client_id="+Constants.FB_APP_ID+"&redirect_uri="+request.getRequestURL()+"&client_secret="+Constants.FB_APP_SECRET+"&code="+code;
			String data = Utilities.getFromUrl(url);
			String[] dataArray = data.split("&");
			
			String accessToken = dataArray[0].substring(dataArray[0].indexOf("=")+1); //access token
			String accesssTokenExpiry = dataArray[1].substring(dataArray[1].indexOf("=")+1); //access expiry

			// STEP 1 : Get $ME data
			String meUrl = "https://graph.facebook.com/me/?access_token="+accessToken;
			String meData = Utilities.getFromUrl(meUrl);
			HashMap<String, Object> facebookUser = Utilities.fromJson(meData);
			
			// Check if the user exists
			User user = userDao.findByRef("/users/" + (String)facebookUser.get("id"));
			
			if (user == null) {
				// New imported user
				user = createUserFromFacebookData(meData);
			} else {
				System.out.println("User " + user.getEmail() + " already exists.");
			}
			
			importUserData("me", meData, accessToken, user);
				
			//get my friends
			String friendsUrl = "https://graph.facebook.com/me/friends?access_token="+accessToken;
			String friendsData = Utilities.getFromUrl(friendsUrl);
			Map<String,Object> friendsList = Utilities.fromJson(friendsData);
			Map<String,Object> friendsListData = (Map<String,Object>)friendsList.get("data");
			System.out.println("gotFriendsList() with : " + friendsListData.size());
			// Throttler
 			int maxFriends = 50;
			int numFriendsDone = 0;
			for (String key : friendsListData.keySet()) {
				long start = new Date().getTime();
				if (numFriendsDone >= maxFriends) {
					// continue;
				}
				numFriendsDone++;
				Map<String,Object> friendData = (Map<String,Object>)friendsListData.get(key);
				String friendId = (String)friendData.get("id");
				
				String friendDetailsUrl = "https://graph.facebook.com/"+friendId+"/"+"?access_token="+accessToken;
				String friendDetails =  Utilities.getFromUrl(friendDetailsUrl);
				
				System.out.println("gotFriendsData() for Friend Id : " + friendId);
				User newFriendUser = createUserFromFacebookData(friendDetails);

				importUserData(friendId, friendDetails, accessToken, newFriendUser);
				
				long end = new Date().getTime();
				System.out.println("ImportFriend " + numFriendsDone + "/" + friendsListData.keySet().size() + " and their data for Id : " + friendId + " took " + (end - start) + "ms");

			}
			return Response.status(200).entity(Utilities.toJson(null)).build();
		}
		catch (Exception E)
		{
			E.printStackTrace();
			return Response.status(300).entity(new ErrorMessage("failed", "Could not import facebook user")).build();

		}
	}

}
