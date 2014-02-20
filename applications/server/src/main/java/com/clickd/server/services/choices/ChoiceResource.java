package com.clickd.server.services.choices;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import com.clickd.server.dao.ChoiceDao;
import com.clickd.server.model.Choice;
import com.clickd.server.model.Link;
import com.clickd.server.model.Resource;
import com.clickd.server.utilities.Utilities;
import com.yammer.metrics.annotation.Timed;

@Path("/choices")
@Produces(MediaType.APPLICATION_JSON)
public class ChoiceResource
{
	private ChoiceDao choiceDao;

	@GET
	@Timed
	public String getAll(@PathParam("user") String user,
			@Context HttpServletRequest request, 
			@Context HttpServletResponse response,
			@Context HttpHeaders headers)
	{
		List<Choice> allChoices = choiceDao.findAll();
		String result = Utilities.toJson(allChoices);
		return result;
	}
	
	@GET
	@Timed
	@Path("/{userRef}")
	public String getUsersChoices(@PathParam("userRef") String userRef,
			@Context HttpServletRequest request, 
			@Context HttpServletResponse response,
			@Context HttpHeaders headers)
	{
		List<Choice> usersChoices = choiceDao.findChoicesByUserRef(userRef);
		String result = Utilities.toJson(usersChoices);
		return result;
	}

	@POST
	@Timed
	@Path("/{userRef}/{questionRef}/{answerRef}")
	public String create(
			@PathParam("userRef") String userRef,
			@PathParam("questionRef") String questionRef,
			@PathParam("answerRef") String answerRef,
			@Context HttpServletRequest request, 
			@Context HttpServletResponse response,
			@Context HttpHeaders headers)
	{
		Choice choice = new Choice();
		choice.get_Links().put(Resource.KEY_LINK_SELF, new Link(choice.getRef(), "self"));
		choice.get_Links().put(Resource.KEY_LINK_CHOICE_USER, new Link("/users/" + userRef, "user"));
		choice.get_Links().put(Resource.KEY_LINK_CHOICE_QUESTION, new Link("/questions/" + questionRef, "question"));
		choice.get_Links().put(Resource.KEY_LINK_CHOICE_ANSWER, new Link("/answers/" + answerRef, "answer"));
		choiceDao.create(choice);
		String result = Utilities.toJson(choice);
		return result;
	}
	
	public ChoiceDao getChoiceDao() {
		return choiceDao;
	}

	public void setChoiceDao(ChoiceDao choiceDao) {
		this.choiceDao = choiceDao;
	}

}
