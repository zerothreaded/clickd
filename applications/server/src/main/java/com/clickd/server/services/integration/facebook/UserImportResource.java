package com.clickd.server.services.integration.facebook;

import java.net.URI;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;

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
import com.clickd.server.model.ErrorMessage;
import com.clickd.server.utilities.Constants;
import com.clickd.server.utilities.Utilities;
import com.yammer.metrics.annotation.Timed;

import edu.emory.mathcs.backport.java.util.concurrent.ExecutorService;
import edu.emory.mathcs.backport.java.util.concurrent.Executors;

@Path("/integration/facebook/user")
@Produces(MediaType.APPLICATION_JSON)
public class UserImportResource {

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
	
	@Autowired
	CalendarDao calendarDao;
	
	@GET
	@Timed
	@Path("/authResponse")
	@Produces(MediaType.APPLICATION_JSON)
	public Response importUser(@QueryParam ("code") String code, @Context HttpServletRequest request) {
		try {
			long startImportTime = new Date().getTime();
			System.out.println(request.getRequestURL());

			String url = "https://graph.facebook.com/oauth/access_token?client_id="+Constants.FB_APP_ID+"&redirect_uri="+request.getRequestURL()+"&client_secret="+Constants.FB_APP_SECRET+"&code="+code;
			String data = Utilities.getFromUrl(url);
			String[] dataArray = data.split("&");
			
			String accessToken = dataArray[0].substring(dataArray[0].indexOf("=")+1); //access token
			String accesssTokenExpiry = dataArray[1].substring(dataArray[1].indexOf("=")+1); //access expiry

			// STEP 1 : Get $ME data
			
			// Check if the user exists
			// New imported user
			String myUrl  = "https://graph.facebook.com/me/?access_token="+accessToken;
			String myData = Utilities.getFromUrl(myUrl);
			Map<String,Object> myDataMap = Utilities.fromJson(myData);
			ImportUserWorker worker = new ImportUserWorker((String)myDataMap.get("id"), accessToken);
			
			worker.setUserDao(userDao);
			worker.setQuestionDao(questionDao);
			worker.setChoiceDao(choiceDao);
			worker.setCheckinDao(checkinDao);
			worker.setBookDao(bookDao);
			worker.setTelevisionDao(televisionDao);
			worker.setPlaceDao(placeDao);
			worker.setMovieDao(movieDao);
			worker.setLikeDao(likeDao);
			worker.setCalendarDao(calendarDao);
			Thread t = new Thread(worker);
			t.start();
			
		//	importUserData("me", meData, accessToken, user);
				
			//get my friends
			String friendsUrl = "https://graph.facebook.com/me/friends?access_token="+accessToken;
			String friendsData = Utilities.getFromUrl(friendsUrl);
			Map<String,Object> friendsList = Utilities.fromJson(friendsData);
			Map<String,Object> friendsListData = (Map<String,Object>)friendsList.get("data");
			System.out.println("gotFriendsList() with : " + friendsListData.size());
			
			int thread_limit = 50;
	        ExecutorService executor = Executors.newFixedThreadPool(thread_limit);

	        int maxFriends = 1;
			int numFriendsDone = 0;
			for (String key : friendsListData.keySet()) {
				long start = new Date().getTime();
//				if (numFriendsDone >= maxFriends) {
//					 continue;
//				}
				numFriendsDone++;
				Map<String,Object> friendData = (Map<String,Object>)friendsListData.get(key);
				String friendId = (String)friendData.get("id");
				
				ImportUserWorker friendWorker = new ImportUserWorker(friendId, accessToken);
				friendWorker.setUserDao(userDao);
				friendWorker.setQuestionDao(questionDao);
				friendWorker.setChoiceDao(choiceDao);
				friendWorker.setCheckinDao(checkinDao);
				friendWorker.setBookDao(bookDao);
				friendWorker.setTelevisionDao(televisionDao);
				friendWorker.setPlaceDao(placeDao);
				friendWorker.setMovieDao(movieDao);
				friendWorker.setLikeDao(likeDao);
				friendWorker.setCalendarDao(calendarDao);
				executor.execute(friendWorker);
			}
			executor.shutdown();
	        while (!executor.isTerminated()) {
		        }
	        long end = new Date().getTime();
	        long difference = end-startImportTime;
	        System.out.println("Finished importing users. Took " + (difference/1000)+" seconds.");

	        return Response.temporaryRedirect(URI.create("/clickd#fbsuccess")).build();
			//return Response.status(200).entity(Utilities.toJson("success")).build();
		}
		catch (Exception E)
		{
			E.printStackTrace();
			return Response.status(300).entity(new ErrorMessage("failed", "Could not import facebook user")).build();

		}
	}

}
