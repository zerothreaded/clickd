package com.clickd.server.services.integration.facebook;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.xml.ws.RequestWrapper;

import org.springframework.beans.factory.annotation.Autowired;

import com.clickd.server.dao.AnswerDao;
import com.clickd.server.dao.BookDao;
import com.clickd.server.dao.ChoiceDao;
import com.clickd.server.dao.CliqueDao;
import com.clickd.server.dao.ConnectionDao;
import com.clickd.server.dao.MovieDao;
import com.clickd.server.dao.QuestionDao;
import com.clickd.server.dao.SessionDao;
import com.clickd.server.dao.TelevisionDao;
import com.clickd.server.dao.UserDao;
import com.clickd.server.model.Answer;
import com.clickd.server.model.Book;
import com.clickd.server.model.Choice;
import com.clickd.server.model.Clique;
import com.clickd.server.model.Connection;
import com.clickd.server.model.ErrorMessage;
import com.clickd.server.model.Link;
import com.clickd.server.model.Movie;
import com.clickd.server.model.Question;
import com.clickd.server.model.Resource;
import com.clickd.server.model.Session;
import com.clickd.server.model.Television;
import com.clickd.server.model.User;
import com.clickd.server.utilities.Constants;
import com.clickd.server.utilities.Utilities;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
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
	BookDao bookDao;
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
	public User importUserTelevision(String userRef, String televisionsData)
	{
		User user = userDao.findByRef("/users/"+userRef);
		
		HashMap<String, Object> map = Utilities.fromJson(televisionsData);
		HashMap<String, Object> televisionMap = (HashMap<String, Object> )map.get("data");
		

		
		for (String key : televisionMap.keySet()) {
			Map<String,Object> televisionData = (Map<String,Object>)televisionMap.get(key);
			Television newTelevision = new Television();
			newTelevision.setName((String)televisionData.get("name"));
			newTelevision.setRef("/television/"+(String)televisionData.get("id"));
			televisionDao.create(newTelevision);
		}
			
		
		return user;
	}
	
	public User importUserBooks(String userRef, String booksData)
	{
		User user = userDao.findByRef("/users/"+userRef);
		
		HashMap<String, Object> map = Utilities.fromJson(booksData);
		HashMap<String, Object> booksMap = (HashMap<String, Object> )map.get("data");
		

		
		for (String key : booksMap.keySet()) {
			Map<String,Object> bookData = (Map<String,Object>)booksMap.get(key);
			Book newBook = new Book();
			newBook.setName((String)bookData.get("name"));
			newBook.setRef("/books/"+(String)bookData.get("id"));
			bookDao.create(newBook);
		}
			
		
		return user;
	}
	
	public User importUserMovies(String userRef, String moviesData)
	{
		User user = userDao.findByRef("/users/"+userRef);
		
		HashMap<String, Object> map = Utilities.fromJson(moviesData);
		HashMap<String, Object> moviesMap = (HashMap<String, Object> )map.get("data");
		

		
		for (String key : moviesMap.keySet()) {
			Map<String,Object> movieData = (Map<String,Object>)moviesMap.get(key);
			Movie newMovie = new Movie();
			newMovie.setName((String)movieData.get("name"));
			newMovie.setRef("/movies/"+(String)movieData.get("id"));
			movieDao.create(newMovie);
		}
			
		
		return user;
	}
	
//	public User importUserActivity(String userRef, String facebookData)
//	{
//		
//	}
//	
	public User createUserFromFacebookData(String facebookData)
	{
		try {
			// System.out.println(facebookData);
			
			HashMap<String, Object> map = Utilities.fromJson(facebookData);
			User existingUser = userDao.findByRef("/users/" + (String)map.get("id"));
			if (null != existingUser)
			{
				return null;
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
			 FileOutputStream fos = new FileOutputStream("C:\\sandbox\\data\\profile-img\\"+(String)map.get("id").toString()+".jpg");
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

			String meUrl = "https://graph.facebook.com/me/?access_token="+accessToken;
			String meData = Utilities.getFromUrl(meUrl);
			User newUser = createUserFromFacebookData(meData);

			String myMoviesUrl = "https://graph.facebook.com/me/movies"+"?access_token="+accessToken;
			String myMovies =  Utilities.getFromUrl(myMoviesUrl);
			importUserMovies(newUser.getRef().split("/")[2], myMovies);

			String myTelevisionUrl = "https://graph.facebook.com/me/television"+"?access_token="+accessToken;
			String myTelevision =  Utilities.getFromUrl(myTelevisionUrl);
			importUserTelevision(newUser.getRef().split("/")[2], myTelevision);
			
			String myBooksUrl = "https://graph.facebook.com/me/books"+"?access_token="+accessToken;
			String myBooks =  Utilities.getFromUrl(myBooksUrl);
			importUserBooks(newUser.getRef().split("/")[2], myBooks);
			
			//get my friends
			String friendsUrl = "https://graph.facebook.com/me/friends?access_token="+accessToken;
			String friendsData = Utilities.getFromUrl(friendsUrl);
			Map<String,Object> friendsList = Utilities.fromJson(friendsData);
			Map<String,Object> friendsListData = (Map<String,Object>)friendsList.get("data");
			
			for (String key : friendsListData.keySet()) {
				Map<String,Object> friendData = (Map<String,Object>)friendsListData.get(key);
				String friendId = (String)friendData.get("id");
				
				String friendDetailsUrl = "https://graph.facebook.com/"+friendId+"/"+"?access_token="+accessToken;
				String friendDetails =  Utilities.getFromUrl(friendDetailsUrl);
				User newFriendUser = createUserFromFacebookData(friendDetails);
				
				String friendMoviesUrl = "https://graph.facebook.com/"+friendId+"/movies/?access_token="+accessToken;
				String friendMovies =  Utilities.getFromUrl(friendMoviesUrl);
				
				importUserMovies(newUser.getRef().split("/")[2], friendMovies);

				String friendTelevisionUrl = "https://graph.facebook.com/"+friendId+"/television"+"/?access_token="+accessToken;
				String friendTelevision =  Utilities.getFromUrl(friendTelevisionUrl);
				
				importUserTelevision(newUser.getRef().split("/")[2], friendTelevision);
				
				String friendBooksUrl = "https://graph.facebook.com/"+friendId+"/books/"+"?access_token="+accessToken;
				String friendBooks =  Utilities.getFromUrl(friendBooksUrl);
				
				importUserBooks(newUser.getRef().split("/")[2], friendBooks);
				
			}
			
			//register my friends
			
			System.out.println(meData);

			return Response.status(200).entity(Utilities.toJson(null)).build();
		}
		catch (Exception E)
		{
			E.printStackTrace();
			return Response.status(300).entity(new ErrorMessage("failed", "Could not import facebook user")).build();

		}
	};

}
