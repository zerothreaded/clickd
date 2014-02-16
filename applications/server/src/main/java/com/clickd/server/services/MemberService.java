package com.clickd.server.services;

import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.clickd.server.services.user.UserResource;
import com.yammer.metrics.annotation.Timed;

@Path("/members")
@Produces(MediaType.APPLICATION_JSON)
public class MemberService {

	private UserResource userResource;
	
	@POST
	@Timed
	@Path("/signin")
	@Produces(MediaType.APPLICATION_JSON)
	public Response signIn(
			String body,
			@QueryParam("foo") String foo,
			@HeaderParam("X-Auth-Token") String token,
			@Context HttpServletRequest request) throws URISyntaxException {

		return userResource.signIn(body, foo, token, request);
	}

	public UserResource getUserResource() {
		return userResource;
	}

	public void setUserResource(UserResource userResource) {
		this.userResource = userResource;
	}
}
