package com.clickd.server.services.questions;

import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.clickd.server.services.AbstractResourceTest;
import com.clickd.server.services.choices.ChoiceResource;
import com.clickd.server.services.users.UserResource;

public class QuestionResourceTest extends AbstractResourceTest {

	private UserResource userResource;
	private ChoiceResource choiceResource;
	private QuestionResource questionResource;
	
	@Before
	public void setup() {
		super.setup();
		questionResource = applicationContext.getBean(QuestionResource.class);
		userResource = applicationContext.getBean(UserResource.class);
		choiceResource = applicationContext.getBean(ChoiceResource.class);
	}

	@Test
	public void nextQuestionReturnsFirstQuestionWhenNoPreviousChoices() {
		String ralphUserRef = "1";
		questionResource.getNextQuestion(ralphUserRef);
	}
	
	@Test
	public void nextQuestionReturnsSecondQuestionWheFirstChoiceMade() {
		String ralphUserRef = "1";
		
		// Make user choices
		String questionRef = "1";
		String answerText = "beer";
		Response response = choiceResource.createWithAnswerText(ralphUserRef, questionRef, answerText);
		Assert.assertEquals(200, response.getStatus());
		
		questionResource.getNextQuestion(ralphUserRef);
	}
}
