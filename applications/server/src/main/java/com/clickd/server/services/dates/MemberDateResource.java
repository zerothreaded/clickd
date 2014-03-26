package com.clickd.server.services.dates;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;

import com.clickd.server.dao.MemberDateDao;
import com.clickd.server.dao.UserDao;
import com.clickd.server.model.MemberDate;
import com.clickd.server.model.ErrorMessage;
import com.clickd.server.utilities.Utilities;
import com.yammer.metrics.annotation.Timed;

@Path("/dates")
@Produces(MediaType.APPLICATION_JSON)
public class MemberDateResource {

	@Autowired
	private MemberDateDao dateDao;
	
	@Autowired
	private UserDao userDao;

	@GET
	@Timed
	@Path("/{dateRef}")
	public Response get(@PathParam("dateRef") String dateRef) {
		try {
			MemberDate date = dateDao.findByRef("/dates/" + dateRef);
			if (date != null) {
				return Response.status(200).entity(Utilities.toJson(date)).build();
			} else {
				return Response.status(404).build();			
			}
		} catch(Exception e) {
			e.printStackTrace();
			return Response.status(300).entity(new ErrorMessage("failed", e.getMessage())).build();			
		}
	}
	
	@POST
	@Timed
	@Path("/new")
	public Response create(@FormParam("date") String date, @FormParam("time") String time) {
		try {
			if (date != null) {
				MemberDate newDate = new MemberDate();
				String dateTime = date.substring(1, 11)+" "+time;
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
				Date dateStartTime = dateFormat.parse(dateTime);
				
				newDate.setStartDate(dateStartTime);
				dateDao.create(newDate);
				return Response.status(200).entity(Utilities.toJson(date)).build();
			} else {
				return Response.status(300).entity(new ErrorMessage("failed", "")).build();			
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
			List<MemberDate> allDates = dateDao.findAll();
			return Response.status(200).entity(Utilities.toJson(allDates)).build();
		} catch(Exception e) {
			e.printStackTrace();
			return Response.status(300).entity(new ErrorMessage("failed", e.getMessage())).build();			
		}
	}

}
