package com.clickd.server.services.likes;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;

import com.clickd.server.dao.LikeDao;
import com.clickd.server.model.ErrorMessage;
import com.clickd.server.model.Like;
import com.clickd.server.utilities.Utilities;
import com.yammer.metrics.annotation.Timed;

@Path("/likes")
@Produces(MediaType.APPLICATION_JSON)
public class LikeResource {
	
	@Autowired
	private LikeDao likeDao;

	@GET
	@Timed
	public Response getAll() {
		try {
			List<Like> allLikes = likeDao.findAll();
			return Response.status(200).entity(Utilities.toJson(allLikes)).build();
		} catch(Exception e) {
			e.printStackTrace();
			return Response.status(300).entity(new ErrorMessage("failed", e.getMessage())).build();			
		}
	}

	
	@GET
	@Timed
	@Path("/{likeRef}")
	public Response get(@PathParam("likeRef") String likeRef) {
		try {
			
			Like like = likeDao.findByRef("/likes/"+likeRef);
				
			return Response.status(200).entity(Utilities.toJson(like)).build();
		} catch(Exception e) {
			e.printStackTrace();
			return Response.status(300).entity(new ErrorMessage("failed", e.getMessage())).build();			
		}
	}
}
