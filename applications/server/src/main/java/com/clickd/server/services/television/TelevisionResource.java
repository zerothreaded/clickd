package com.clickd.server.services.television;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;

import com.clickd.server.dao.TelevisionDao;
import com.clickd.server.model.ErrorMessage;
import com.clickd.server.model.Television;
import com.clickd.server.utilities.Utilities;
import com.yammer.metrics.annotation.Timed;

@Path("/televisions")
@Produces(MediaType.APPLICATION_JSON)
public class TelevisionResource {
	
	@Autowired
	private TelevisionDao televisionDao;

	@GET
	@Timed
	public Response getAll() {
		try {
			List<Television> allTelevisions = televisionDao.findAll();
			return Response.status(200).entity(Utilities.toJson(allTelevisions)).build();
		} catch(Exception e) {
			e.printStackTrace();
			return Response.status(300).entity(new ErrorMessage("failed", e.getMessage())).build();			
		}
	}

	
	@GET
	@Timed
	@Path("/{televisionRef}")
	public Response getForMap(@PathParam("televisionRef") String televisionRef) {
		try {
			
			Television television = televisionDao.findByRef("/televisions/"+televisionRef);
				
			return Response.status(200).entity(Utilities.toJson(television)).build();
		} catch(Exception e) {
			e.printStackTrace();
			return Response.status(300).entity(new ErrorMessage("failed", e.getMessage())).build();			
		}
	}
	
}
