package com.clickd.server.services.questions;

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

import com.clickd.server.dao.QuestionDao;
import com.clickd.server.model.Question;
import com.clickd.server.utilities.Utilities;
import com.yammer.metrics.annotation.Timed;

@Path("/questions")
@Produces(MediaType.APPLICATION_JSON)
public class QuestionResource
{
	private QuestionDao questionDao;

	@GET
	@Timed
	public String getAll(@Context HttpServletRequest request, 
			@Context HttpServletResponse response,
			@Context HttpHeaders headers)
	{
		List<Question> allQuestions = questionDao.findAll();
		String result = Utilities.toJson(allQuestions);
		return result;
	}

	@GET
    @Path("/{ref}")
    @Timed
    public String getQuestion(@PathParam("ref") String ref) {
		Question question = questionDao.findByRef("/questions/" + ref);
		
		return Utilities.toJson(question);
	}

	public QuestionDao getQuestionDao() {
		return questionDao;
	}

	public void setQuestionDao(QuestionDao questionDao) {
		this.questionDao = questionDao;
	}

}
