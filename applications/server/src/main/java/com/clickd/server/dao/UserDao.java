package com.clickd.server.dao;

import java.util.List;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.clickd.server.model.User;

public class UserDao {

	private MongoOperations mongoOperations;
	private String collectionName;

	public UserDao() {
		System.out.println("UserDao() called.");
		this.collectionName = "users";
	}

	public MongoOperations getMongoOperations() {
		return mongoOperations;
	}

	public void setMongoOperations(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}

	public User create(User user) {
		mongoOperations.save(user, collectionName);
		return user;
	}

	public User update(User user) {
		delete(user);
		create(user);
		return user;
	}

	public void delete(User user) {
		mongoOperations.remove(user);
	}

	public List<User> findAll() {
		return mongoOperations.findAll(User.class, collectionName);
	}

	public User findById(String id) {
		User user = mongoOperations.findOne(new Query(Criteria.where("_id").is(id)), User.class, collectionName);
		return user;
	}

	public User findByEmail(String email) {
		User user = mongoOperations.findOne(new Query(Criteria.where("email").is(email)), User.class, collectionName);
		return user;
	}

	public User findByRef(String ref) {
		User user = mongoOperations.findOne(new Query(Criteria.where("ref").is(ref)), User.class, collectionName);
		return user;
	}

}
