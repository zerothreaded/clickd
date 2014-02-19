package com.clickd.server.services.choices;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import com.clickd.server.dao.ChoiceDao;
import com.clickd.server.model.Choice;
import com.clickd.server.utilities.Utilities;
import com.yammer.metrics.annotation.Timed;

@Path("/users/")
@Produces(MediaType.APPLICATION_JSON)
public class ChoiceResource
{
	private ChoiceDao choiceDao;

	@GET
	@Timed
	@Path("{user}/choices")
	public String getAll(@PathParam("user") String user,
			@Context HttpServletRequest request, 
			@Context HttpServletResponse response,
			@Context HttpHeaders headers)
	{
		List<Choice> allChoices = choiceDao.findAll();
		String result = Utilities.toJson(allChoices);
		return result;
	}

	public ChoiceDao getChoiceDao() {
		return choiceDao;
	}

	public void setChoiceDao(ChoiceDao choiceDao) {
		this.choiceDao = choiceDao;
	}

}
