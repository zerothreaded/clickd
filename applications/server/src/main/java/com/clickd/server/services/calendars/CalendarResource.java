package com.clickd.server.services.calendars;

import java.util.ArrayList;
import java.util.Date;
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
	@Path("/{calendarRef}/thisweek")
	public Response getThisWeek(@PathParam("calendarRef") String calendarRef) {
		try {
			Calendar calendar = calendarDao.findByRef("/calendars/" + calendarRef);
			ArrayList<Date> daysOfThisWeek = new ArrayList<Date>();
			
			// get today and clear time of day
			java.util.Calendar cal = java.util.Calendar.getInstance();
			cal.set(java.util.Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
			cal.clear(java.util.Calendar.MINUTE);
			cal.clear(java.util.Calendar.SECOND);
			cal.clear(java.util.Calendar.MILLISECOND);

			// get start of this week in milliseconds
			cal.set(java.util.Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
			
			for (int day = 0 ; day < 7; day++)
			{
				Date today = new Date();
				today = cal.getTime();
				cal.add(java.util.Calendar.DATE, 1);
				daysOfThisWeek.add(today);
			}
			
			calendar.setDays(daysOfThisWeek);
			
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
