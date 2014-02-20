package com.clickd.server.dao;

import java.util.List;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.clickd.server.model.Answer;

public class AnswerDao {

	private MongoOperations mongoOperations;
	private String collectionName;

	public AnswerDao() {
		System.out.println("AnswerDao() called.");
		this.collectionName = "answers";
	}

	public MongoOperations getMongoOperations() {
		return mongoOperations;
	}

	public void setMongoOperations(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}

	public Answer create(Answer answer) {
		mongoOperations.save(answer, collectionName);
		return answer;
	}

	public Answer update(Answer answer) {
		delete(answer);
		create(answer);
		return answer;
	}

	public void delete(Answer answer) {
		mongoOperations.remove(answer);
	}

	public List<Answer> findAll() {
		return mongoOperations.findAll(Answer.class, collectionName);
	}

	public Answer findById(String id) {
		Answer answer = mongoOperations.findOne(new Query(Criteria.where("_id").is(id)), Answer.class, collectionName);
		return answer;
	}

	public Answer findByRef(String ref) {
		Answer answer = mongoOperations.findOne(new Query(Criteria.where("ref").is(ref)), Answer.class, collectionName);
		return answer;
	}

}
