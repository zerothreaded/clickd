package com.clickd.server.services.answers;

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

import com.clickd.server.dao.AnswerDao;
import com.clickd.server.model.Answer;
import com.clickd.server.utilities.Utilities;
import com.yammer.metrics.annotation.Timed;

@Path("/answers")
@Produces(MediaType.APPLICATION_JSON)
public class AnswerResource {
	private AnswerDao answerDao;

	@GET
	@Timed
	public String getAll(@Context HttpServletRequest request, @Context HttpServletResponse response, @Context HttpHeaders headers) {
		List<Answer> allAnswers = answerDao.findAll();
		String result = Utilities.toJson(allAnswers);
		return result;
	}

	@GET
	@Path("/{ref}")
	@Timed
	public String getAnswer(@PathParam("ref") String ref) {
		Answer answer = answerDao.findByRef("/answers/" + ref);

		return Utilities.toJson(answer);
	}

	public AnswerDao getAnswerDao() {
		return answerDao;
	}

	public void setAnswerDao(AnswerDao answerDao) {
		this.answerDao = answerDao;
	}

}
