package com.clickd.server.services.questions;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.clickd.server.services.AbstractResourceTest;

public class QuestionResourceTest extends AbstractResourceTest {

	private QuestionResource questionResource;
	
	@Before
	public void setup() {
		applicationContext = new ClassPathXmlApplicationContext(new String[] { "spring/application.xml" });
	}

	@Test
	public void nextQuestionReturnsFirstQuestionWhenNoPreviousChoices() {
		
	}
}
