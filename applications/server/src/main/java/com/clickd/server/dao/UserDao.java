package com.clickd.server.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.clickd.server.model.User;

public class UserDao implements InitializingBean {

	private MongoOperations mongoOperations;
	private String collectionName;
	private static Map<String, User> cache;

	public UserDao() {
		System.out.println("UserDao() called.");
		this.collectionName = "users";
		this.cache = new TreeMap<String, User>();
	}

	public MongoOperations getMongoOperations() {
		return mongoOperations;
	}

	public void setMongoOperations(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}

	public User create(User user) {
		mongoOperations.save(user, collectionName);
		cache.put(user.getRef(), user);
		return user;
	}

	public User update(User user) {
		delete(user);
		create(user);
		return user;
	}

	public void delete(User user) {
		mongoOperations.remove(user);
		cache.remove(user.getRef());
	}

	public List<User> findAll() {
		return mongoOperations.findAll(User.class, collectionName);
	}

	public User findByEmail(String email) {
		User user = mongoOperations.findOne(new Query(Criteria.where("email").is(email)), User.class, collectionName);
		return user;
	}

	public User findByRef(String ref) {
		// User user = mongoOperations.findOne(new Query(Criteria.where("ref").is(ref)), User.class, collectionName);
		return cache.get(ref);
	}


	@Override
	public void afterPropertiesSet() throws Exception {
		long now = new Date().getTime();
		System.out.println("afterPropertiesSet() called. Loading cache..");
		List<User> allusers = mongoOperations.findAll(User.class, collectionName);
		for (User user : allusers) {
			cache.put(user.getRef(), user);
		}
		System.out.println("User cache has " + cache.size() + " users. Loaded in " + (new Date().getTime() - now) + "ms");
	}

}
