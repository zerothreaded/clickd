 	package com.clickd.server.services;

import javax.servlet.Filter;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.clickd.server.dao.SessionDao;
import com.clickd.server.dao.UserDao;
import com.clickd.server.services.home.HomeResource;
import com.clickd.server.services.member.MemberConfiguration;
import com.clickd.server.services.member.MemberResource;
import com.clickd.server.services.users.UserResource;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.views.ViewBundle;

public class ApplicationService extends Service<MemberConfiguration> {
    
	private ApplicationContext context;
	
	public static void main(String[] args) throws Exception {
        new ApplicationService().run(args);
    }

    @Override
    public void initialize(Bootstrap<MemberConfiguration> bootstrap) {
        bootstrap.setName("application");
        bootstrap.addBundle(new ViewBundle());
        bootstrap.addBundle(new AssetsBundle("/assets", "/assets"));
    }

    @Override
    public void run(MemberConfiguration configuration, Environment environment) {
    	final String template = configuration.getTemplate();
        final String defaultName = configuration.getDefaultName();
        
        context = new ClassPathXmlApplicationContext(new String[] { "application.xml" });
        UserDao userDao = (UserDao) context.getBean("userDao");
        SessionDao sessionDao = (SessionDao) context.getBean("sessionDao");
       
        // Create REST End Points
       
        // /members/*
        MemberResource memberResource = new MemberResource(template, defaultName);
        memberResource.setUserDao(userDao);
        memberResource.setSessionDao(sessionDao);
        environment.addResource(memberResource);
        
        // /users/*
        UserResource userResource = new UserResource();
        userResource.setUserDao(userDao);
        userResource.setSessionDao(sessionDao);
        environment.addResource(userResource);
        
        // /home/*
        HomeResource homeResource = new HomeResource(template, defaultName);
        homeResource.setSessionDao(sessionDao);
        environment.addResource(homeResource);
        
        environment.addHealthCheck(new ApplicationHealthCheck("application"));
        Filter filter = new TokenCheckFilter();
		environment.addFilter(filter, "*");
    }

}
