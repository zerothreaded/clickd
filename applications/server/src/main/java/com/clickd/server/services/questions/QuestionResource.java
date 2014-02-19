package com.clickd.server.services.questions;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import com.clickd.server.dao.AnswerDao;
import com.clickd.server.dao.SessionDao;
import com.clickd.server.dao.QuestionDao;
import com.clickd.server.model.Answer;
import com.clickd.server.model.Link;
import com.clickd.server.model.Resource;
import com.clickd.server.model.Session;
import com.clickd.server.model.Question;
import com.clickd.server.utilities.Utilities;
import com.yammer.metrics.annotation.Timed;

@Path("/questions")
@Produces(MediaType.APPLICATION_JSON)
public class QuestionResource
{
	private QuestionDao questionDao;
	private AnswerDao answerDao;


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

	@GET
    @Path("/next/{userRef}")
    @Timed
    public String getNextQuestion(@PathParam("userRef") String userRef) {
		List<Question> questions = questionDao.findAll();
		int idx = (int)(Math.random() * (questions.size()-1));
		Question question = questions.get(idx);
		
		ArrayList<Answer> answerList = new ArrayList<Answer>();
		
		List<Link> answerLinks = (List<Link>)question.get_Links().get("question-answer-list");
		
		for (Link answerLink : answerLinks)
		{
			Answer answer = answerDao.findByRef(answerLink.getHref());
			answerList.add(answer);
			
		}
		
		question.get_Embedded().put("question-answer-list", answerList);
		
		return Utilities.toJson(question);
	}
	
	public QuestionDao getQuestionDao() {
		return questionDao;
	}

	public void setQuestionDao(QuestionDao questionDao) {
		this.questionDao = questionDao;
	}

	public void setAnswerDao(AnswerDao answerDao) {
		this.answerDao = answerDao;
		
	}

}
