package com.clickd.server.services.movies;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;

import com.clickd.server.dao.MovieDao;
import com.clickd.server.model.ErrorMessage;
import com.clickd.server.model.Movie;
import com.clickd.server.utilities.Utilities;
import com.yammer.metrics.annotation.Timed;

@Path("/movies")
@Produces(MediaType.APPLICATION_JSON)
public class MovieResource {
	
	@Autowired
	private MovieDao movieDao;

	@GET
	@Timed
	public Response getAll() {
		try {
			List<Movie> allMovies = movieDao.findAll();
			return Response.status(200).entity(Utilities.toJson(allMovies)).build();
		} catch(Exception e) {
			e.printStackTrace();
			return Response.status(300).entity(new ErrorMessage("failed", e.getMessage())).build();			
		}
	}

	
	@GET
	@Timed
	@Path("/{movieRef}")
	public Response getForMap(@PathParam("movieRef") String movieRef) {
		try {
			
			Movie movie = movieDao.findByRef("/movies/"+movieRef);
				
			return Response.status(200).entity(Utilities.toJson(movie)).build();
		} catch(Exception e) {
			e.printStackTrace();
			return Response.status(300).entity(new ErrorMessage("failed", e.getMessage())).build();			
		}
	}
	
}
