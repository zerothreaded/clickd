package com.clickd.server.dao;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.clickd.server.model.User;

public class UserDaoTest {
	private UserDao userDao;
	private User user;
	private ApplicationContext context;

	
	@Before
	public void setup()
	{
		this.context =  new ClassPathXmlApplicationContext(new String[] { "application.xml" });
		this.userDao = (UserDao)context.getBean("userDao");
	}
	
	@Test
	public void createSucceeds()
	{
		user = new User("John", "Dodds", new Date(), "male", "NW1", "johndodds90@gmail.com", "sparks90");
		userDao.create(user);
		User user2 = userDao.findById(user.getId());
		assert (user2.equals(user));
	}
}
