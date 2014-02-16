package com.clickd.server.services.users;

import java.io.IOException;
import java.net.URI;
import java.util.List;

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

import com.clickd.server.model.Entity;
import com.clickd.server.services.ApplicationService;
import com.google.gson.Gson;

public class UserServiceTest {

	private ResponseHandler<String> successResponseHandler;
	private ResponseHandler<String> failureResponseHandler;	
	
	static {
		// Start the Service
		try {
			ApplicationService.main(new String[] {"server", "src\\main\\resources\\application.yaml" } );
		} catch (Exception e) {
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
					throw new ClientProtocolException( "Unexpected response status: " + status);
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
	
	@SuppressWarnings("unchecked")
	@Test
	public void getAllUsersReturnsCorrectUsers() throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		URI uri = new URIBuilder()
	        .setScheme("http")
	        .setHost("localhost:8080")
	        .setPath("/users")
	        .build();
		HttpGet httpget = new HttpGet(uri);
        String jsonResponse = httpclient.execute(httpget, successResponseHandler);
		List<Entity> allUsers = new Gson().fromJson(jsonResponse, List.class);
	    System.out.println("GET /users returned \n" + jsonResponse);
    }

	@Test
	public void signInSucceeds() throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpUriRequest login = RequestBuilder.post()
                   .setUri(new URI("http://localhost:8080/users/signin"))
                   .addParameter("email", "ralph.masilamani@clickd.org")
                   .addParameter("password", "rr00")
                   .build();

	    String jsonResponse = httpclient.execute(login, successResponseHandler);
	    System.out.println("POST /users/signin returned \n" + jsonResponse);
	}
	
	@Test
	public void signInFails() throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpUriRequest login = RequestBuilder.post()
                   .setUri(new URI("http://localhost:8080/users/signin"))
                   .addParameter("email", "ralph.masilamani@clickd.org")
                   .addParameter("password", "BAD_PASSWORD")
                   .build();

	    httpclient.execute(login, failureResponseHandler);
	}
	
}
