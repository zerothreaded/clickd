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
		List<Question> questions = questionDao.findAllSortedBy("ref");
		//List<Question> questions = questionDao.findAll();
		List<Choice> userChoices = choiceDao.findByUserRef("/users/"+userRef);

		ArrayList<Question> toDeleteSet = new ArrayList<Question>();
		for (Choice choice : userChoices)
		{
			for (Question choiceQuestion : questions)
			{
				if (choiceQuestion.getRef().equals(choice.getLinkByName("question").getHref()))
				{
					toDeleteSet.add(choiceQuestion);
				}
			}
		}
			
		for (Question toReturn : questions)
		{
			boolean skip = false;
			for (Question toDeleteQuestion : toDeleteSet)
				if (toReturn.getRef().equals(toDeleteQuestion.getRef()))
					{
						skip = true;
					}
			
			if (skip)
				continue;
			else
				return Utilities.toJson(toReturn);
		}
	
		
		return "";
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
