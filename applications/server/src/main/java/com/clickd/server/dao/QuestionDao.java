package com.clickd.server.dao;

import java.util.List;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.clickd.server.model.Question;

public class QuestionDao {

	private MongoOperations mongoOperations;
	private String collectionName;

	public QuestionDao() {
		System.out.println("QuestionDao() called.");
		this.collectionName = "questions";
	}

	public MongoOperations getMongoOperations() {
		return mongoOperations;
	}

	public void setMongoOperations(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}
	
	public Question create(Question question) {
		mongoOperations.save(question, collectionName);
		return question;
	}
	
	public Question update(Question question)
	{
		delete(question);
		create(question);
		return question;
	}
	
	public void delete(Question question)
	{
		mongoOperations.remove(question);
	}
	
	public List<Question> findAll() {
		return mongoOperations.findAll(Question.class, collectionName);
	}

	public Question findById(String id)
	{
		Question question = mongoOperations.findOne(new Query(Criteria.where("_id").is(id)), Question.class, collectionName);
		return question;
	}

	public Question findByEmail(String email) {
		Question question = mongoOperations.findOne(new Query(Criteria.where("email").is(email)), Question.class, collectionName);
		return question;
	}

	public Question findByRef(String ref) {
		Question question = mongoOperations.findOne(new Query(Criteria.where("ref").is(ref)), Question.class, collectionName);
		return question;
	}

}
