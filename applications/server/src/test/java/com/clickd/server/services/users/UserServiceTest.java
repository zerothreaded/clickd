package com.clickd.server.services.users;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoOperations;

import com.clickd.server.services.ApplicationService;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;

public class UserServiceTest {

	private static ApplicationContext context;

	private static DB mongoDb;
	
	private ResponseHandler<String> successResponseHandler;
	private ResponseHandler<String> failureResponseHandler;

	static {
		// Start the Service
		try {
			ApplicationService.main(new String[] { "server", "src\\test\\resources\\application\\application.yaml" });
			context = ApplicationService.getContext();
			MongoDbFactory mongoDbFactory = (MongoDbFactory) context.getBean("mongoDbFactory");
			mongoDb = mongoDbFactory.getDb();
			
			// Import test data fixtures into DB
			// Users
			importCollectionFromFile(mongoDb, "src\\test\\resources\\database\\users.json" , "users");

			// Questions and Answers
			importCollectionFromFile(mongoDb, "src\\test\\resources\\database\\questions.json" , "questions");
			importCollectionFromFile(mongoDb, "src\\test\\resources\\database\\answers.json" , "answers");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void importCollectionFromFile(DB mongoDb, String pathToFile, String collectionName) {
	    FileInputStream fileInputStream = null;
	    try {
	        fileInputStream = new FileInputStream(pathToFile);
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	        return;
	    }
	    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
	    String line;
	    DBCollection dbCollection =  mongoDb.getCollection(collectionName);
	    try {
	        while ((line = bufferedReader.readLine()) != null) {
				// convert line by line to BSON
	            DBObject dbObject = (DBObject) JSON.parse(line);
	            try {
	                dbCollection.insert(dbObject);
	            }
	            catch (MongoException e) {
	              e.printStackTrace();
	            }
	        }
	        bufferedReader.close();
	    } catch (IOException e) {
	        e.printStackTrace(); 
	    }
	}

	@Before
	public void setup() throws Exception {
		successResponseHandler = new ResponseHandler<String>() {
			public String handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
				int status = response.getStatusLine().getStatusCode();
				Assert.assertEquals(true, status == 200);
				if (status >= 200 && status < 300) {
					HttpEntity entity = response.getEntity();
					return entity != null ? EntityUtils.toString(entity) : null;
				} else {
					throw new ClientProtocolException("Unexpected response status: " + status);
				}
			}
		};
		failureResponseHandler = new ResponseHandler<String>() {
			public String handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
				int status = response.getStatusLine().getStatusCode();
				Assert.assertEquals(true, status == 300);
				HttpEntity entity = response.getEntity();
				return entity != null ? EntityUtils.toString(entity) : null;
			}
		};
	}

	// @SuppressWarnings("unchecked")
	@Test
	public void getAllUsersReturnsCorrectUsers() throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		URI uri = new URIBuilder().setScheme("http").setHost("localhost:8080").setPath("/users").build();
		HttpGet httpget = new HttpGet(uri);
		String jsonResponse = httpclient.execute(httpget, successResponseHandler);
		// List<Entity> allUsers = new Gson().fromJson(jsonResponse,
		// List.class);
		System.out.println("GET /users returned \n" + jsonResponse);
	}

	@Test
	public void signInSucceeds() throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpUriRequest login = RequestBuilder.post().setUri(new URI("http://localhost:8080/users/signin"))
			.addParameter("email", "ralph.masilamani@clickd.org")
			.addParameter("password", "rr0101").build();

		String jsonResponse = httpclient.execute(login, successResponseHandler);
		System.out.println("POST /users/signin returned \n" + jsonResponse);
	}

	@Test
	public void signInFails() throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpUriRequest login = RequestBuilder.post().setUri(new URI("http://localhost:8080/users/signin"))
			.addParameter("email", "ralph.masilamani@clickd.org")
			.addParameter("password", "BAD_PASSWORD").build();
		
		httpclient.execute(login, failureResponseHandler);
	}

}
