package com.clickd.server.services.dates;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;

import com.clickd.server.dao.DateDao;
import com.clickd.server.dao.UserDao;
import com.clickd.server.model.Date;
import com.clickd.server.model.ErrorMessage;
import com.clickd.server.utilities.Utilities;
import com.yammer.metrics.annotation.Timed;

@Path("/dates")
@Produces(MediaType.APPLICATION_JSON)
public class DateResource {

	@Autowired
	private DateDao dateDao;
	
	@Autowired
	private UserDao userDao;

	@GET
	@Timed
	@Path("/{dateRef}")
	public Response get(@PathParam("dateRef") String dateRef) {
		try {
			Date date = dateDao.findByRef("/dates/" + dateRef);
			if (date != null) {
				return Response.status(200).entity(Utilities.toJson(date)).build();
			} else {
				return Response.status(300).entity(new ErrorMessage("Not Found for reference : ", dateRef)).build();			
			}
		} catch(Exception e) {
			e.printStackTrace();
			return Response.status(300).entity(new ErrorMessage("failed", e.getMessage())).build();			
		}
	}
	
	@GET
	@Timed
	public Response getAll() {
		try {
			List<Date> allDates = dateDao.findAll();
			return Response.status(200).entity(Utilities.toJson(allDates)).build();
		} catch(Exception e) {
			e.printStackTrace();
			return Response.status(300).entity(new ErrorMessage("failed", e.getMessage())).build();			
		}
	}

}
