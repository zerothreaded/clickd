package com.clickd.server.services;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.clickd.server.dao.EntityDao;
import com.clickd.server.services.user.UserConfiguration;
import com.clickd.server.services.user.UserResource;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;

public class ApplicationService extends Service<UserConfiguration> {
    
	private ApplicationContext context;
	
	public static void main(String[] args) throws Exception {
        new ApplicationService().run(args);
    }

    @Override
    public void initialize(Bootstrap<UserConfiguration> bootstrap) {
        bootstrap.setName("application");
    }

    @Override
    public void run(UserConfiguration configuration, Environment environment) {
    	final String template = configuration.getTemplate();
        final String defaultName = configuration.getDefaultName();
        
        context = new ClassPathXmlApplicationContext(new String[] { "application.xml" });
        EntityDao entityDao = (EntityDao) context.getBean("entityDao");
        UserResource userResource = new UserResource(template, defaultName);
        userResource.setEntityDao(entityDao);
        environment.addResource(userResource);
        environment.addHealthCheck(new ApplicationHealthCheck("application"));
    }

}
