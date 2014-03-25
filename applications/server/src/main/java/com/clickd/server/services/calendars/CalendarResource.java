package com.clickd.server.services.calendars;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;

import com.clickd.server.dao.CalendarDao;
import com.clickd.server.dao.UserDao;
import com.clickd.server.model.Calendar;
import com.clickd.server.model.ErrorMessage;
import com.clickd.server.utilities.Utilities;
import com.yammer.metrics.annotation.Timed;

@Path("/calendars")
@Produces(MediaType.APPLICATION_JSON)
public class CalendarResource {

	@Autowired
	private CalendarDao calendarDao;
	
	@Autowired
	private UserDao userDao;

	@GET
	@Timed
	@Path("/{calendarRef}")
	public Response get(@PathParam("calendarRef") String calendarRef) {
		try {
			Calendar calendar = calendarDao.findByRef("/calendars/" + calendarRef);
			if (calendar != null) {
				return Response.status(200).entity(Utilities.toJson(calendar)).build();
			} else {
				return Response.status(404).build();
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
			List<Calendar> allCalendars = calendarDao.findAll();
			return Response.status(200).entity(Utilities.toJson(allCalendars)).build();
		} catch(Exception e) {
			e.printStackTrace();
			return Response.status(300).entity(new ErrorMessage("failed", e.getMessage())).build();			
		}
	}

}
