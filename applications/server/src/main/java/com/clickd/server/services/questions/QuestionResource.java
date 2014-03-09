package com.clickd.server.services.questions;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;

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
public class QuestionResource {
	@Autowired
	private QuestionDao questionDao;
	
	@Autowired
	private AnswerDao answerDao;
	
	@Autowired
	private ChoiceDao choiceDao;

	@SuppressWarnings("unchecked")
	@GET
	@Path("/next/{userRef}")
	@Timed
	public String getNextQuestion(@PathParam("userRef") String userRef) {
		List<Question> unansweredQuestions = new ArrayList<Question>();
		List<Question> questions = questionDao.findAllSortedBy("questionText");
		//List<Question> questions = questionDao.findAll();
		List<Choice> userChoices = choiceDao.findByUserRef("/users/"+userRef);

		if (userChoices.size() == 0) {
			// No choices - first time clicking
			unansweredQuestions = questions;
		} else {
			// Only consider questions NOT answered by this user
			for (Question question : questions) {
				// Check if user has answered this - i.e. Question in the
				// userChoices
				String questionRef = question.getRef();
				System.out.println("Evaluating question " + questionRef);
				boolean hasAnswered = false;
				for (Choice choice : userChoices) {
					String choiceQuestionRef = choice.getLinkByName("question").getHref();
					if (choiceQuestionRef.equals(questionRef)) {
						// User has answered this one so flag it
						hasAnswered = true;
						System.out.println("Adding question " + choiceQuestionRef);
						break;
					} else {
						System.out.println("Skipping question " + choiceQuestionRef);
					}
				}
				if (!hasAnswered && !question.getTags().contains("bio")) {
					unansweredQuestions.add(question);
				}
			}
		}
		
		// TODO: Ralph clear up this conditional logic
		System.out.println("\n\n UNANSWERED QUESTION SIZE =" + unansweredQuestions.size() + "\n");
		if (unansweredQuestions.size() != 0) {
			int index = 0;
			Question question = unansweredQuestions.get(index);
			return Utilities.toJson(question);
		} else {
			System.out.println("NO MORE QUESTIONS TO ANSWER");
			return "{ \"status\" : \"done\" }";
		}
	}

//	public QuestionDao getQuestionDao() {
//		return questionDao;
//	}
//
//	public void setQuestionDao(QuestionDao questionDao) {
//		this.questionDao = questionDao;
//	}
//
//	public void setAnswerDao(AnswerDao answerDao) {
//		this.answerDao = answerDao;
//	}
//
//	public ChoiceDao getChoiceDao() {
//		return choiceDao;
//	}
//
//	public void setChoiceDao(ChoiceDao choiceDao) {
//		this.choiceDao = choiceDao;
//	}

}
