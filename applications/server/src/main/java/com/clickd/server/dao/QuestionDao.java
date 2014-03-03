package com.clickd.server.dao;

import java.util.List;

import org.springframework.data.domain.Sort;
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

	public Question update(Question question) {
		delete(question);
		create(question);
		return question;
	}

	public void delete(Question question) {
		mongoOperations.remove(question);
	}


	public List<Question> findAllSortedBy(String field) {
		Query query = new Query();
		query.with(new Sort(Sort.Direction.ASC, field));	
		return mongoOperations.find(query, Question.class, collectionName);
	}

	public List<Question> findAll() {
		return mongoOperations.findAll(Question.class, collectionName);
	}

	public Question findById(String id) {
		Question question = mongoOperations.findOne(new Query(Criteria.where("_id").is(id)), Question.class, collectionName);
		return question;
	}

	public Question findByRef(String ref) {
		Question question = mongoOperations.findOne(new Query(Criteria.where("ref").is(ref)), Question.class, collectionName);
		return question;
	}
	
	public Question findByTags(String tag)
	{
		Question question = mongoOperations.findOne(new Query(Criteria.where("tags").is(tag)), Question.class, collectionName);
		return question;
	}

}
