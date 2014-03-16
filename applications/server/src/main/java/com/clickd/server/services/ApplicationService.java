package com.clickd.server.services;

import javax.servlet.Filter;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.clickd.server.dao.ChatroomDao;
import com.clickd.server.services.answers.AnswerResource;
import com.clickd.server.services.chatrooms.ChatroomResource;
import com.clickd.server.services.choices.ChoiceResource;
import com.clickd.server.services.integration.facebook.UserImportResource;
import com.clickd.server.services.places.PlaceResource;
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
		bootstrap.addBundle(new AssetsBundle("/web/internal/integration", "/int", "index.html"));

		bootstrap.addBundle(new AssetsBundle("/web/data", "/data"));
	}

	@Override
	public void run(UserConfiguration configuration, Environment environment) {
		final String springContextFileName = configuration.getSpringContextFileName();

		// context = new ClassPathXmlApplicationContext(new String[] { springContextFileName });
		
		final String dataFolder = System.getProperty("dataFolder");
		String fileName = dataFolder + "/" + springContextFileName;
		
		System.out.println("\n\nUSER CONFIG FileNme = " + dataFolder);
		
		context = new ClassPathXmlApplicationContext(new String[] { springContextFileName });

		// Create REST End Points

		UserResource userResource = context.getBean(UserResource.class);
		environment.addResource(userResource);

		QuestionResource questionResource = context.getBean(QuestionResource.class);
		environment.addResource(questionResource);

		AnswerResource answerResource = context.getBean(AnswerResource.class);
		environment.addResource(answerResource);

		ChoiceResource choiceResource = context.getBean(ChoiceResource.class);
		environment.addResource(choiceResource);
		
		ChatroomResource chatroomResource = context.getBean(ChatroomResource.class);
		environment.addResource(chatroomResource);
		
		PlaceResource placeResource = context.getBean(PlaceResource.class);
		environment.addResource(placeResource);
		
		UserImportResource facebookUserImportResource = context.getBean(UserImportResource.class);
		environment.addResource(facebookUserImportResource);

		
		// TODO: Sort out healthchecks
		environment.addHealthCheck(new ApplicationHealthCheck("application"));
		
		// FILTER
		Filter filter = new TokenCheckFilter();
		environment.addFilter(filter, "*");
	}

}
