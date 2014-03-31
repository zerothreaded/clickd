package com.clickd.server.services.dates;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;

import com.clickd.server.dao.CriteriaDao;
import com.clickd.server.dao.MemberDateDao;
import com.clickd.server.dao.QuestionDao;
import com.clickd.server.dao.UserDao;
import com.clickd.server.model.Criteria;
import com.clickd.server.model.Criteria.Operator;
import com.clickd.server.model.Link;
import com.clickd.server.model.MemberDate;
import com.clickd.server.model.ErrorMessage;
import com.clickd.server.model.Question;
import com.clickd.server.model.User;
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
	
	@Autowired
	private CriteriaDao criteriaDao;

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
	

	@Path("/bydate/{date}")
	public Response getByDay(@PathParam("date") String date) {
		try {
			MemberDate newDate = new MemberDate();
			String dateTime = date.substring(1, 11);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date dateStartTime = dateFormat.parse(dateTime);
			
			List<MemberDate> dates = dateDao.findAll();
			
			ArrayList<MemberDate> toReturn = new ArrayList<MemberDate>();
			
			for (MemberDate thisDate : dates)
			{
				if (dateFormat.format(thisDate.getStartDate()).equals(dateTime))
				{
					toReturn.add(thisDate);
				}
			}
			
			return Response.status(200).entity(Utilities.toJson(toReturn)).build();
		} catch(Exception e) {
			e.printStackTrace();
			return Response.status(300).entity(new ErrorMessage("failed", e.getMessage())).build();			
		}
	}
	
	public List<Criteria> getDefaultCriteria(String dateRef)
	{
		ArrayList<Criteria> toReturn = new ArrayList<Criteria>();
		
		Criteria ageAtLeast = new Criteria();
		ageAtLeast.setName("age");
		ageAtLeast.setOperator(Operator.GREATER_THAN);
		List<Object> ageAtLeastValues = new ArrayList<Object>();
		ageAtLeastValues.add(18);
		Question ageQuestion = questionDao.findByTags("dateofbirth");
		ageAtLeast.getLinks().put("question", new Link(ageQuestion.getRef(), "criteria-question"));
		ageAtLeast.setValues(ageAtLeastValues);
		ageAtLeast.setDateRef(dateRef);
		criteriaDao.create(ageAtLeast);
		toReturn.add(ageAtLeast);
		
		Criteria ageAtMost = new Criteria();
		ageAtMost.setName("age");
		ageAtMost.setOperator(Operator.LESS_THAN);
		List<Object> ageAtMostValues = new ArrayList<Object>();
		ageAtMostValues.add(50);
		Question ageQuestion2 = questionDao.findByTags("dateofbirth");
		ageAtMost.getLinks().put("question", new Link(ageQuestion2.getRef(), "criteria-question"));
		ageAtMost.setValues(ageAtMostValues);
		ageAtMost.setDateRef(dateRef);
		criteriaDao.create(ageAtMost);
		toReturn.add(ageAtMost);
		
		Criteria gender = new Criteria();
		gender.setName("gender");
		gender.setOperator(Operator.EQUAL);
		List<Object> genderValues = new ArrayList<Object>();
		Question genderQuestion = questionDao.findByTags("gender");
		gender.getLinks().put("question", new Link(genderQuestion.getRef(), "criteria-question"));
		genderValues.add("female");
		gender.setValues(genderValues);
		gender.setDateRef(dateRef);
		criteriaDao.create(gender);
		toReturn.add(gender);
		
		Criteria location = new Criteria();
		location.setName("location");
		location.setOperator(Operator.EQUAL);
		List<Object> locationValues = new ArrayList<Object>();
		locationValues.add("London");
		Question locationQuestion = questionDao.findByTags("location");
		location.getLinks().put("question", new Link(locationQuestion.getRef(), "criteria-question"));
		location.setValues(locationValues);
		location.setDateRef(dateRef);
		criteriaDao.create(location);
		toReturn.add(location);
		
		
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
				
				newDate.setCriteria(getDefaultCriteria(newDate.getRef()));
				
				newDate.setStartDate(dateStartTime);
				
				// Calculate candidates for date
				List<User> allUsers = userDao.findAll();
				List<User> dateCandidates = new ArrayList<User>();
				List<Criteria> dateCriteria = newDate.getCriteria();
				for (User user : allUsers) {
					// Evaluate the user against the date criteria
					for (Criteria criteria : dateCriteria) {
						String criteriaName = criteria.getName();
						List<Object> criteriaValues = criteria.getValues();
						Operator criteriaOperator = criteria.getOperator();
						// AGE, GENDER, LOCATION 
						if (criteriaName.equals("age")) {
							Date userDob = user.getDateOfBirth();
							Date now = new Date();
							long ageInYears = (now.getTime() - userDob.getTime()) / (365 * 24 * 60 * 60 * 1000);
							if (matches(ageInYears, criteriaOperator, criteriaValues)) {
								dateCandidates.add(user);							
							}
						}
						if (criteriaName.equals("gender")) {
							String userGender = user.getGender();
							if (matches(userGender, criteriaOperator, criteriaValues)) {
								dateCandidates.add(user);							
							}
						}						
						if (criteriaName.equals("location")) {
							Map<String, Object> userLocationMap = user.getLocation();
							String userLocation = (String) userLocationMap.get("location");
							if (matches(userLocation, criteriaOperator, criteriaValues)) {
								dateCandidates.add(user);							
							}
						}						
					}
					
				}
				System.out.println("User has " + dateCandidates.size() + " candidate dates");
				// Attach them to the user
				for (User user : dateCandidates) {
					Link dateLink = new Link(user.getRef(), "date-candidate");
				}
				
				dateDao.create(newDate);
				return Response.status(200).entity(Utilities.toJson(newDate)).build();
			} else {
				return Response.status(300).entity(new ErrorMessage("failed", "")).build();			
			}
		} catch(Exception e) {
			e.printStackTrace();
			return Response.status(300).entity(new ErrorMessage("failed", e.getMessage())).build();			
		}
	}
	
	private boolean matches(Object value, Operator operator, List<Object> values) {
		boolean matches = false;
		if (operator.equals(Operator.EQUAL)) {
			return value.equals(values.get(0));
		}
		if (operator.equals(Operator.LESS_THAN)) {
			// Match as number
			Long longValue  = (Long) value;
			for (Object valueObject : values) {
				// Convert To String then Long
				String valueAsString = (String)valueObject;
				Long valueAsLong = new Long(valueAsString);
				return longValue < valueAsLong;
			}
			return value.equals(values.get(0));
		}
		if (operator.equals(Operator.GREATER_THAN)) {
			// Match as number
			Long longValue  = (Long) value;
			for (Object valueObject : values) {
				// Convert To String then Long
				String valueAsString = (String)valueObject;
				Long valueAsLong = new Long(valueAsString);
				return longValue > valueAsLong;
			}
			return value.equals(values.get(0));
		}
		
		return matches;
		
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
