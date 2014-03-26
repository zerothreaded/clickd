package com.clickd.server.services.dates;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.clickd.server.dao.QuestionDao;
import com.clickd.server.dao.UserDao;
import com.clickd.server.model.Criteria;
import com.clickd.server.model.Criteria.Operator;
import com.clickd.server.model.Link;
import com.clickd.server.model.MemberDate;
import com.clickd.server.model.ErrorMessage;
import com.clickd.server.model.Question;
import com.clickd.server.utilities.Utilities;
import com.yammer.metrics.annotation.Timed;

@Path("/dates")
@Produces(MediaType.APPLICATION_JSON)
public class MemberDateResource {

	@Autowired
	private MemberDateDao dateDao;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private QuestionDao questionDao;

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
	
	public List<Criteria> getDefaultCriteria()
	{
		ArrayList<Criteria> toReturn = new ArrayList<Criteria>();
		Criteria ageAtLeast = new Criteria();
		
		ageAtLeast.setOperator(Operator.GREATER_THAN);
		List<Object> ageAtLeastValues = new ArrayList<Object>();
		ageAtLeastValues.add(18);
		Question ageQuestion = questionDao.findByTags("dateofbirth");
		ageAtLeast.getLinks().put("question", new Link(ageQuestion.getRef(), "criteria-question"));
		ageAtLeast.setValues(ageAtLeastValues);
		
		Criteria ageAtMost = new Criteria();
		ageAtLeast.setOperator(Operator.LESS_THAN);
		List<Object> ageAtMostValues = new ArrayList<Object>();
		ageAtMostValues.add(50);
		Question ageQuestion2 = questionDao.findByTags("dateofbirth");
		ageAtMost.getLinks().put("question", new Link(ageQuestion2.getRef(), "criteria-question"));
		ageAtMost.setValues(ageAtMostValues);
		
		Criteria gender = new Criteria();
		gender.setOperator(Operator.EQUAL);
		List<Object> genderValues = new ArrayList<Object>();
		Question genderQuestion = questionDao.findByTags("gender");
		gender.getLinks().put("question", new Link(genderQuestion.getRef(), "criteria-question"));
		genderValues.add("female");
		gender.setValues(genderValues);
		
		Criteria location = new Criteria();
		location.setOperator(Operator.EQUAL);
		List<Object> locationValues = new ArrayList<Object>();
		locationValues.add("London");
		Question locationQuestion = questionDao.findByTags("location");
		location.getLinks().put("question", new Link(locationQuestion.getRef(), "criteria-question"));
		location.setValues(locationValues);
		
		
		
		return toReturn;
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
				
				newDate.setCriteria(getDefaultCriteria());
				
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
