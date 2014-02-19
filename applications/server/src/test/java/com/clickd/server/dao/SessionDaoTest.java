package com.clickd.server.dao;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.clickd.server.model.Session;
import com.clickd.server.model.User;

public class SessionDaoTest {
	private SessionDao sessionDao;
	private Session session;
	private ApplicationContext context;

	
	@Before
	public void setup()
	{
		this.context =  new ClassPathXmlApplicationContext(new String[] { "application.xml" });
		this.sessionDao = (SessionDao)context.getBean("sessionDao");
	}
	
	@Test
	public void createSucceeds()
	{
		session = new Session(new User(),  new Date(), new Date(), 0l, false);
		sessionDao.create(session);
		Session session2 = sessionDao.findById(session.getId());
		assert (session2.equals(session));
	}
}
