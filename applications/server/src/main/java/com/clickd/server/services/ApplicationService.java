package com.clickd.server.services;

import javax.servlet.Filter;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.clickd.server.services.answers.AnswerResource;
import com.clickd.server.services.choices.ChoiceResource;
import com.clickd.server.services.questions.QuestionResource;
import com.clickd.server.services.users.UserConfiguration;
import com.clickd.server.services.users.UserResource;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.views.ViewBundle;

public class ApplicationService extends Service<UserConfiguration> {

	private static ApplicationContext context;

	public static ApplicationContext getContext() {
		return context;
	}

	public static void main(String[] args) throws Exception {
		new ApplicationService().run(args);
	}

	@Override
	public void initialize(Bootstrap<UserConfiguration> bootstrap) {
		bootstrap.setName("application");
		bootstrap.addBundle(new ViewBundle());
	//	bootstrap.addBundle(new AssetsBundle("/resources", "/web2"));

		bootstrap.addBundle(new AssetsBundle("/profile-img", "/profile-img"));

		
		bootstrap.addBundle(new AssetsBundle("/web", "/web"));
		bootstrap.addBundle(new AssetsBundle("/web/internal/home", "/clickd", "index.html"));
		bootstrap.addBundle(new AssetsBundle("/web/internal/home", "/d3", "d31.html"));
		bootstrap.addBundle(new AssetsBundle("/web/data", "/data"));
	}

	@Override
	public void run(UserConfiguration configuration, Environment environment) {
		final String springContextFileName = configuration.getSpringContextFileName();

		context = new ClassPathXmlApplicationContext(new String[] { springContextFileName });
//		
//		UserDao userDao = (UserDao) context.getBean("userDao");
//		SessionDao sessionDao = (SessionDao) context.getBean("sessionDao");
//		ApplicationDao applicationDao = (ApplicationDao) context.getBean("applicationDao");
//		QuestionDao questionDao = (QuestionDao) context.getBean("questionDao");
//		AnswerDao answerDao = (AnswerDao) context.getBean("answerDao");
//		ChoiceDao choiceDao = (ChoiceDao) context.getBean("choiceDao");
//		ConnectionDao connectionDao = (ConnectionDao) context.getBean("connectionDao");
//		ChatroomDao chatroomDao = (ChatroomDao) context.getBean("chatroomDao");
//		PostDao postDao = (PostDao) context.getBean("postDao");

		// Create REST End Points

//		// Application 	
//		ApplicationResource applicationResource = new ApplicationResource();
//		applicationResource.setApplicationDao(applicationDao);
//		applicationResource.setSessionDao(sessionDao);
//		applicationResource.setUserDao(userDao);

//		ApplicationResource applicationResource = context.getBean(ApplicationResource.class);
//		environment.addResource(applicationResource);
//
//		// /users/*
//		UserResource userResource = new UserResource();
//		userResource.setUserDao(userDao);
//		userResource.setSessionDao(sessionDao);
//		userResource.setChoiceDao(choiceDao);
//		userResource.setQuestionDao(questionDao);
//		userResource.setConnectionDao(connectionDao);
//		userResource.setAnswerDao(answerDao);
		UserResource userResource = context.getBean(UserResource.class);
		environment.addResource(userResource);
//
//		// /questions/*
//		QuestionResource questionResource = new QuestionResource();
//		questionResource.setQuestionDao(questionDao);
//		questionResource.setAnswerDao(answerDao);
//		questionResource.setChoiceDao(choiceDao);
		QuestionResource questionResource = context.getBean(QuestionResource.class);
		environment.addResource(questionResource);
//
//		// /answers/*
//		AnswerResource answerResource = new AnswerResource();
//		answerResource.setAnswerDao(answerDao);
		AnswerResource answerResource = context.getBean(AnswerResource.class);
		environment.addResource(answerResource);
//
//		// /choices/*
//		ChoiceResource choiceResource = new ChoiceResource();
//		choiceResource.setChoiceDao(choiceDao);
		ChoiceResource choiceResource = context.getBean(ChoiceResource.class);
		environment.addResource(choiceResource);
		
//		// /chatrooms/*
//		ChatroomResource chatroomResource = new ChatroomResource();
//		chatroomResource.setChatroomDao(chatroomDao);
//		environment.addResource(chatroomResource);
		
		
		environment.addHealthCheck(new ApplicationHealthCheck("application"));
		Filter filter = new TokenCheckFilter();
		environment.addFilter(filter, "*");
	}

}
