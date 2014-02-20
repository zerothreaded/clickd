package com.clickd.server.services.questions;

import java.util.ArrayList;
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
import com.clickd.server.dao.ChoiceDao;
import com.clickd.server.dao.QuestionDao;
import com.clickd.server.model.Answer;
import com.clickd.server.model.Choice;
import com.clickd.server.model.Link;
import com.clickd.server.model.Question;
import com.clickd.server.model.Resource;
import com.clickd.server.utilities.Utilities;
import com.yammer.metrics.annotation.Timed;

@Path("/questions")
@Produces(MediaType.APPLICATION_JSON)
public class QuestionResource
{
	private QuestionDao questionDao;
	private AnswerDao answerDao;
	private ChoiceDao choiceDao;

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

	@SuppressWarnings("unchecked")
	@GET
    @Path("/next/{userRef}")
    @Timed
    public String getNextQuestion(@PathParam("userRef") String userRef) {
		List<Question> unansweredQuestions = new ArrayList<Question>();
		List<Question> questions = questionDao.findAll();
		List<Choice> userChoices = choiceDao.findChoicesByUserRef(userRef);
		
		if (userChoices.size() == 0) {
			// No choices - first time clicking
			unansweredQuestions = questions;
		} else {
			// Only consider questions NOT answered by this user
			for (Question question : questions) {
				// Check if user has answered this - i.e. Question in the userChoices
				String questionRef = question.getRef();
				for (Choice choice : userChoices) {
					String choiceQuestionRef = ((Link)choice.get_Links().get(Resource.KEY_LINK_CHOICE_QUESTION)).getHref();
					if (!choiceQuestionRef.equals(questionRef)) {
						// User has not answered this one so add it
						unansweredQuestions.add(question);
						System.out.println("Adding question " + questionRef);
					} else {
						System.out.println("Skipping question " + questionRef);		
					}
				}
			}
		}
		System.out.println("\n\n UNANSWERED QUESTION SIZE =" + unansweredQuestions.size() + "\n");
		
		int idx = (int)(Math.random() * (unansweredQuestions.size() - 1));
		Question question = unansweredQuestions.get(idx);
		
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

	public ChoiceDao getChoiceDao() {
		return choiceDao;
	}

	public void setChoiceDao(ChoiceDao choiceDao) {
		this.choiceDao = choiceDao;
	}

}
