package com.clickd.server.services;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoOperations;

import com.clickd.server.utilities.Utilities;
import com.mongodb.DB;

public abstract class AbstractResourceTest {

	protected ApplicationContext applicationContext;
	protected MongoOperations mongoOperations;
	protected DB mongoDb;
	
	@Before
	public void setup() {
		applicationContext = new ClassPathXmlApplicationContext(new String[] { "spring/application.xml" });
		MongoDbFactory mongoDbFactory = (MongoDbFactory) applicationContext.getBean("mongoDbFactory");
		mongoOperations = (MongoOperations) applicationContext.getBean("mongoTemplate");
		mongoDb = mongoDbFactory.getDb();

		// STEP 1 : CLEAR THE DB
		mongoOperations.dropCollection("users");
		mongoOperations.dropCollection("sessions");
		mongoOperations.dropCollection("questions");
		mongoOperations.dropCollection("answers");
		mongoOperations.dropCollection("question_answers");
		mongoOperations.dropCollection("choices");
		mongoOperations.dropCollection("collections");
		
		// STEP 2 : SETUP INITIAL DATA
		Utilities.importFixtureFromFile(mongoDb, "src\\test\\resources\\database\\users.json" , "users");
		Utilities.importFixtureFromFile(mongoDb, "src\\test\\resources\\database\\questions.json" , "questions");
		Utilities.importFixtureFromFile(mongoDb, "src\\test\\resources\\database\\answers.json" , "answers");
		Utilities.importFixtureFromFile(mongoDb, "src\\test\\resources\\database\\question_answers.json" , "question_answers");
	}

	@After
	public void tearDown() {
		MongoOperations mongoOperations = (MongoOperations) applicationContext.getBean("mongoTemplate");
		mongoOperations.dropCollection("users");
		mongoOperations.dropCollection("sessions");
		mongoOperations.dropCollection("connections");
		mongoOperations.dropCollection("questions");
		mongoOperations.dropCollection("answers");
		mongoOperations.dropCollection("question_answers");
		
	}
	
	@Test
	public void nothing() {
		
	}
}
