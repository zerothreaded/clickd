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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.clickd.server.dao.BookDao;
import com.clickd.server.dao.CalendarDao;
import com.clickd.server.dao.CheckinDao;
import com.clickd.server.dao.ChoiceDao;
import com.clickd.server.dao.LikeDao;
import com.clickd.server.dao.MovieDao;
import com.clickd.server.dao.PlaceDao;
import com.clickd.server.dao.QuestionDao;
import com.clickd.server.dao.TelevisionDao;
import com.clickd.server.dao.UserDao;
import com.clickd.server.model.Book;
import com.clickd.server.model.Calendar;
import com.clickd.server.model.Checkin;
import com.clickd.server.model.Choice;
import com.clickd.server.model.Like;
import com.clickd.server.model.Link;
import com.clickd.server.model.Movie;
import com.clickd.server.model.Place;
import com.clickd.server.model.Question;
import com.clickd.server.model.Television;
import com.clickd.server.model.User;
import com.clickd.server.utilities.Utilities;

public class ImportUserWorker implements Runnable {
	protected String facebookUserDataUrl;
	protected String accessToken;

	protected ChoiceDao choiceDao;
	protected UserDao userDao;
	protected QuestionDao questionDao;
	protected MovieDao movieDao;
	protected TelevisionDao televisionDao;
	protected LikeDao likeDao;
	protected PlaceDao placeDao;
	protected BookDao bookDao;
	protected CheckinDao checkinDao;
	protected CalendarDao calendarDao;
	
	private String friendId;
	private String facebookUserData;
	

	public void importUserTelevision(String userRef, String televisionsData)
	{
		HashMap<String, Object> map = Utilities.fromJson(televisionsData);
		HashMap<String, Object> televisionMap = (HashMap<String, Object> )map.get("data");
		
		for (String key : televisionMap.keySet()) {
			Map<String,Object> televisionData = (Map<String,Object>)televisionMap.get(key);
			Television newTelevision = new Television();
			newTelevision.setName((String)televisionData.get("name"));
			newTelevision.setRef("/television/"+(String)televisionData.get("id"));
			
			// Enrich with OMDB call
			String omdbUrl = "http://www.omdbapi.com/?t=" + URLEncoder.encode(newTelevision.getName());
			String omdbContent;
			try {
				omdbContent = Utilities.getFromUrl(omdbUrl);
				Map<String, Object> omdbProperties = Utilities.fromJson(omdbContent);
				newTelevision.setPosterImageUrl((String) omdbProperties.get("Poster"));
				newTelevision.setCountry((String) omdbProperties.get("Country"));
				newTelevision.setGenres((String) omdbProperties.get("Genre"));
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("OMDB FAILED FOR "+ newTelevision.getName());
			}
			
			if (getTelevisionDao().findByRef(newTelevision.getRef()) == null)
			{
				// CREATE PATH
				getTelevisionDao().create(newTelevision);
				
				Question televisionQuestion = getQuestionDao().findByTags(newTelevision.getName());	
				if (televisionQuestion == null)
				{
					televisionQuestion = new Question("Do you like " + newTelevision.getName() + "?", "system");
					televisionQuestion.getTags().add(newTelevision.getName());
					televisionQuestion.getTags().add("fb.televisions");
					televisionQuestion.addLink("television-data", new Link(newTelevision.getRef(), "television-data"));
					televisionQuestion.setAnswerRule("yes|no");
					getQuestionDao().create(televisionQuestion);
				}
//				
				Choice myChoice = new Choice();
				myChoice.addLink("question", new Link(televisionQuestion.getRef(), "choice-question"));
				myChoice.addLink("user", new Link("/users/"+userRef, "choice-user"));
				myChoice.setAnswerText("yes");
			
				getChoiceDao().create(myChoice);
				
			
			} else {
				// UPDATE PATH
				
				Question televisionQuestion = getQuestionDao().findByTags(newTelevision.getName());	
				System.out.println("\t\tUpdate Television for " + televisionQuestion.getQuestionText());

				List<Choice> myChoices = getChoiceDao().findByUserRef("/users/"+userRef);
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
				
					getChoiceDao().create(myChoice);
					myChoices.add(myChoice);
				}

				
			}
		}
	}
	
	public User importUserBooks(String userRef, String booksData)
	{
		User user = getUserDao().findByRef("/users/"+userRef);
		
		HashMap<String, Object> map = Utilities.fromJson(booksData);
		HashMap<String, Object> booksMap = (HashMap<String, Object> )map.get("data");
		
		for (String key : booksMap.keySet()) {
			Map<String,Object> bookData = (Map<String,Object>)booksMap.get(key);
			if (null == bookData.get("name"))
			{
				continue;	
			}
			
			String bookRef = "/books/"+(String)bookData.get("id");
			if (getBookDao().findByRef(bookRef) == null)
			{
				// Create path
				Book newBook = new Book();
				newBook.setName((String)bookData.get("name"));
				newBook.setRef(bookRef);
				
				getBookDao().create(newBook);
				
				Question bookQuestion = getQuestionDao().findByTags(newBook.getName());
				if (bookQuestion == null)
				{
					bookQuestion = new Question("Do you like "+newBook.getName()+"?", "system");
					bookQuestion.getTags().add(newBook.getName());
					bookQuestion.getTags().add("fb.books");
					bookQuestion.addLink("book-data", new Link(newBook.getRef(), "book-data"));
					bookQuestion.setAnswerRule("yes|no");
					getQuestionDao().create(bookQuestion);
				}
				
			}
			else
			{
				// UPDATE PATH
				System.out.println("\t\tSkipping UPDATE book for " + (String)bookData.get("name"));
			}
			
			Question bookQuestion = getQuestionDao().findByTags((String)bookData.get("name"));
			List<Choice> myChoices = getChoiceDao().findByUserRef("/users/"+userRef);
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
				getChoiceDao().create(myChoice);
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
			if (getMovieDao().findByRef(movieRef) == null)
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
				newMovie = getMovieDao().create(newMovie);
				System.out.println("\t\tCreated Movie : "+ (String)facebookMovie.get("name"));
				
				// Create Question
				Question movieQuestion = getQuestionDao().findByTags(newMovie.getName());
				if (movieQuestion == null)
				{
					movieQuestion = new Question("Do you like " + newMovie.getName() + "?", "system");
					movieQuestion.getTags().add(newMovie.getName());
					movieQuestion.getTags().add("fb.movies");
					movieQuestion.addLink("movie-data", new Link(newMovie.getRef(), "movie-data"));
					movieQuestion.setAnswerRule("yes|no");
					getQuestionDao().create(movieQuestion);
				}
				
				// Create Do you like {MOVIE} Choice
				List<Choice> myChoices = getChoiceDao().findByUserRef("/users/"+userRef);
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
					getChoiceDao().create(myChoice);
					myChoices.add(myChoice);
				} else {
					// UPDATE PRIOR CHOICE
				}
				
				// Create Genre Questions and Choices
				String genres = newMovie.getGenres();
				if (null != genres && !genres.equals("")) {
					StringTokenizer tokenizer = new StringTokenizer(genres, ",");
					while (tokenizer.hasMoreTokens()) {
						String genre = tokenizer.nextToken().trim();
						Question genreQuestion = getQuestionDao().findByTags(genre);
						if (genreQuestion == null)
						{
							genreQuestion = new Question("Do you like " + genre + " movies ?", "system");
							genreQuestion.getTags().add("genre");
							genreQuestion.getTags().add(genre);
							genreQuestion.setAnswerRule("yes|no");
							getQuestionDao().create(genreQuestion);
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
							getChoiceDao().create(myChoice);
							myChoices.add(myChoice);
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
			
			if (getLikeDao().findByRef(newLike.getRef()) == null)
			{
				getLikeDao().create(newLike);
				
				Question likeQuestion = getQuestionDao().findByTags(newLike.getName());
				if (likeQuestion == null)
				{
					likeQuestion = new Question("Do you like "+newLike.getName()+"?", "system");
					likeQuestion.getTags().add(newLike.getName());
					likeQuestion.getTags().add(newLike.getCategory());
					likeQuestion.getTags().add("fb.likes");
					likeQuestion.addLink("like-data", new Link(newLike.getRef(), "like-data"));
					likeQuestion.setAnswerRule("yes|no");
					getQuestionDao().create(likeQuestion);
				}
				
		
			}
			else
			{
				//update path
			}
			
			Question likeQuestion = getQuestionDao().findByTags((String)likeData.get("name"));
			List<Choice> myChoices = getChoiceDao().findByUserRef("/users/"+userRef);
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
				getChoiceDao().create(myChoice);
				myChoices.add(myChoice);
			}
		}
	}
	
	public User importUserCheckins (String userRef, String checkinData)
	{
		try {
			User user = getUserDao().findByRef("/users/" + userRef);
			// System.out.println(checkinData);
			
			HashMap<String, Object> map = Utilities.fromJson(checkinData);
			HashMap<String, Object> checkinsMap = (HashMap<String, Object> )map.get("data");
			
			List<Choice> myChoices = getChoiceDao().findByUserRef("/users/"+userRef);
			
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

						
						Place placeResource = getPlaceDao().findByRef("/places/"+placeId);
								
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
							getPlaceDao().create(placeResource);
						}
						
						Checkin checkin = new Checkin(checkinId, message, Utilities.dateFromString((String)checkinDetails.get("checkinTime")));
						checkin.addLink("user", new Link("/users/"+userRef, "checkin-user"));
						checkin.addLink("place", new Link("/places/"+placeId, "checkin-place"));
						checkin.setRef("/checkins/"+checkinId);
						getCheckinDao().create(checkin);
						
						if (placeName == null)
							continue;

						
						Question checkinQuestion = getQuestionDao().findByTags(placeName);
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
							getQuestionDao().create(checkinQuestion);
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
							getChoiceDao().create(checkinChoice);
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
			if (userDao.findByRef("/users/" +this.friendId) != null) {
				return null;
			}
			HashMap<String, Object> map = Utilities.fromJson(facebookData);

			// Create the new user
			User newUser = new User();
			System.out.println("Start import of "+(String)map.get("first_name")+" "+(String)map.get("last_name"));
			newUser.setFirstName((String)map.get("first_name"));
			newUser.setLastName((String)map.get("last_name"));
			newUser.setGender((String)map.get("gender"));
			
			HashMap<String, Object >location = (HashMap<String, Object>) map.get("location");
			if (location != null) {
				System.out.println("Location : " + location.toString() + " for user " + location.toString());
				newUser.setLocation(location);
			} else {
				System.out.println("NO LOCATION FOR " + newUser.getEmail());
			}
			if ((String)map.get("email") == null) {
				newUser.setEmail(newUser.getFirstName().toLowerCase()+"."+newUser.getLastName().toLowerCase()+"@clickd.org");
			} else {
				newUser.setEmail((String)map.get("email"));
			}
			newUser.setDateOfBirth(Utilities.dateFromString((String)map.get("user_birthday")));
			newUser.setPassword("fb99");
			newUser.setRef("/users/" + (String)map.get("id"));
			getUserDao().create(newUser);
			
			// Create the users dating calendar
			Calendar calendar = new Calendar();
			calendar.setName(newUser.getFirstName() + "-dating-calendar");
			if (calendarDao == null) {
				int wait = 1;
			}
			calendarDao.create(calendar);
			
			// Get the Users FB image and save it locally
			 String dataDir = System.getProperty("dataDir");
			 if (null == dataDir) {
				 dataDir = "C:\\sandbox\\data\\profile-img\\";
				 System.out.println("\n\nData Directory = " + dataDir);
			 } else {
				 System.out.println("\n\nData Directory = " + dataDir);
			 }
			String targetFileName = dataDir + (String)map.get("id").toString()+".jpg";
			File file = new File(targetFileName);
			if (!file.exists()) {
				// System.out.println("Getting friends Image..");
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
				 FileOutputStream fos = new FileOutputStream(dataDir + "/users/" + (String)map.get("id").toString()+".jpg");
				 fos.write(response);
				 fos.close();
				 System.out.println("Saved friends Image.");
			} else {
				 // System.out.println("Skipping Image Load");
			}
			
			//get big picture
			String targetFileNameBig = dataDir + (String)map.get("id").toString()+"-big.jpg";
			File fileBig = new File(targetFileName);
			if (!fileBig.exists()) {
				// System.out.println("Getting friends Image..");
				 URL url = new URL("http://graph.facebook.com/"+(String)map.get("id")+"/picture?width=400&height=400");
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
				 FileOutputStream fos = new FileOutputStream(dataDir + "/users/" + (String)map.get("id").toString()+"-big.jpg");
				 fos.write(response);
				 fos.close();
				 System.out.println("Saved friends Image.");
			} else {
				 // System.out.println("Skipping Image Load");
			}
			
			
			Question likeFaceQuestion = new Question();
			List<String> likeFaceQuestionTags = new ArrayList<String>();
			likeFaceQuestionTags.add("clickd.members.face");
			likeFaceQuestion.setQuestionText("Do you like "+newUser.getFirstName());
			likeFaceQuestion.setTags(likeFaceQuestionTags);
			likeFaceQuestion.setImg("/profile-img/users/"+(String)map.get("id")+"-big.jpg");
			likeFaceQuestion.addLink("user", new Link(newUser.getRef(), "question-user"));
			likeFaceQuestion.setAnswerRule("yes|no");
			questionDao.create(likeFaceQuestion);
			
			Question genderQuestion = getQuestionDao().findByTags("gender");
			Choice genderChoice = new Choice();
			genderChoice.setAnswerText((String)map.get("gender"));
			genderChoice.addLink("question", new Link(genderQuestion.getRef(), "choice-question"));
			genderChoice.addLink("user", new Link(newUser.getRef(), "choice-user"));
			getChoiceDao().create(genderChoice);

			Question nameQuestion = getQuestionDao().findByTags("name");
			Choice nameChoice = new Choice();
			nameChoice.setAnswerText((String)map.get("first_name")+" "+(String)map.get("last_name"));
			nameChoice.addLink("question", new Link(nameQuestion.getRef(), "choice-question"));
			nameChoice.addLink("user", new Link(newUser.getRef(), "choice-user"));
			getChoiceDao().create(nameChoice);
				
			Question dateOfBirthQuestion = getQuestionDao().findByTags("dateofbirth");
			Choice dateOfBirthChoice = new Choice();
			dateOfBirthChoice.setAnswerText((String)map.get("birthday"));
			dateOfBirthChoice.addLink("question", new Link(dateOfBirthQuestion.getRef(), "choice-question"));
			dateOfBirthChoice.addLink("user", new Link(newUser.getRef(), "choice-user"));
			getChoiceDao().create(dateOfBirthChoice);
			
			if (map.get("location") != null)
			{
				HashMap<String,String> locationDetails = ((HashMap<String,String>)map.get("location"));
				if (locationDetails.get("name") != null)
				{
					Question locationQuestion = getQuestionDao().findByTags("location");
					Choice locationChoice = new Choice();
					locationChoice.setAnswerText(locationDetails.get("name"));
					locationChoice.addLink("question", new Link(locationQuestion.getRef(), "choice-question"));
					locationChoice.addLink("user", new Link(newUser.getRef(), "choice-user"));
					getChoiceDao().create(locationChoice);
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
	
	@Override
	public void run() {
		String meUrl = "https://graph.facebook.com/"+this.friendId+"/?access_token="+accessToken;
		try {
			this.facebookUserData = Utilities.getFromUrl(meUrl);
			User newUser = createUserFromFacebookData(this.facebookUserData);
			if (newUser != null) {
				importUserData(this.friendId, this.facebookUserData, this.accessToken, newUser);
				System.out.println("imported "+newUser.getFirstName()+" "+newUser.getLastName());
			} else {
				System.out.println("Not Importing User Id : " + this.friendId);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ImportUserWorker(String friendId, String accessToken) {
		this.friendId = friendId;
		this.accessToken = accessToken;
	}

	public String getFacebookUserData() {
		return facebookUserData;
	}

	public void setFacebookUserData(String facebookUserData) {
		this.facebookUserData = facebookUserData;
	}

	public ChoiceDao getChoiceDao() {
		return choiceDao;
	}

	public void setChoiceDao(ChoiceDao choiceDao) {
		this.choiceDao = choiceDao;
	}

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public QuestionDao getQuestionDao() {
		return questionDao;
	}

	public void setQuestionDao(QuestionDao questionDao) {
		this.questionDao = questionDao;
	}

	public TelevisionDao getTelevisionDao() {
		return televisionDao;
	}

	public void setTelevisionDao(TelevisionDao televisionDao) {
		this.televisionDao = televisionDao;
	}

	public MovieDao getMovieDao() {
		return movieDao;
	}

	public void setMovieDao(MovieDao movieDao) {
		this.movieDao = movieDao;
	}

	public LikeDao getLikeDao() {
		return likeDao;
	}

	public void setLikeDao(LikeDao likeDao) {
		this.likeDao = likeDao;
	}

	public PlaceDao getPlaceDao() {
		return placeDao;
	}

	public void setPlaceDao(PlaceDao placeDao) {
		this.placeDao = placeDao;
	}

	public BookDao getBookDao() {
		return bookDao;
	}

	public void setBookDao(BookDao bookDao) {
		this.bookDao = bookDao;
	}

	public CheckinDao getCheckinDao() {
		return checkinDao;
	}

	public void setCheckinDao(CheckinDao checkinDao) {
		this.checkinDao = checkinDao;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public CalendarDao getCalendarDao() {
		return calendarDao;
	}

	public void setCalendarDao(CalendarDao calendarDao) {
		this.calendarDao = calendarDao;
	}
}