package com.clickd.server.dao;

import java.util.List;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.clickd.server.model.Session;

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

	public Session create(Session session) {
		mongoOperations.save(session, collectionName);
		return session;
	}

	public Session update(Session session) {
		delete(session);
		create(session);
		return session;
	}

	public void delete(Session session) {
		mongoOperations.remove(session);
	}

	public Session findById(String id) {
		Session Session = mongoOperations.findOne(new Query(Criteria.where("_id").is(id)), Session.class, collectionName);
		return Session;
	}

	public Session findByToken(Long token) {
		Session Session = mongoOperations.findOne(new Query(Criteria.where("token").is(token)), Session.class, collectionName);
		return Session;
	}

	public List<Session> findAll() {
		return mongoOperations.findAll(Session.class, collectionName);
	}

	public Session findByRef(String ref) {
		Session Session = mongoOperations.findOne(new Query(Criteria.where("ref").is(ref)), Session.class, collectionName);
		return Session;
	}
}
