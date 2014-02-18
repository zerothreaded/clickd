package com.clickd.server.dao;

import java.util.List;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.clickd.server.model.Session;
import com.clickd.server.model.User;

public class SessionDao {

	private MongoOperations mongoOperations;
	private String collectionName;

	public SessionDao() {
		System.out.println("SessionDao() called.");
		this.collectionName = "sessions";
	}

	public MongoOperations getMongoOperations() {
		return mongoOperations;
	}

	public void setMongoOperations(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}
	
	public Session create(Session Session) {
		mongoOperations.save(Session, collectionName);
		return Session;
	}
	
	public Session update(Session Session)
	{
		mongoOperations.save(Session);
		return Session;
	}
	
	public void delete(Session Session)
	{
		mongoOperations.remove(Session);
	}
	
	public Session findById(String id)
	{
		Session Session = mongoOperations.findOne(new Query(Criteria.where("_id").is(id)), Session.class, collectionName);
		return Session;
	}
	
	public Session findByToken(Long token)
	{
		Session Session = mongoOperations.findOne(new Query(Criteria.where("token").is(token)), Session.class, collectionName);
		return Session;
	}
	
	public List<Session> findAll() {
		return mongoOperations.findAll(Session.class, collectionName);
	}
}
