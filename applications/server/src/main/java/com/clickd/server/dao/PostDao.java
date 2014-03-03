package com.clickd.server.dao;

import java.util.List;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.clickd.server.model.Choice;
import com.clickd.server.model.Post;

public class PostDao {

	private MongoOperations mongoOperations;
	private String collectionName;

	public PostDao() {
		System.out.println("PostDao() called.");
		this.collectionName = "posts";
	}

	public Choice create(Choice choice) {
		mongoOperations.save(choice, collectionName);
		return choice;
	}

	public Choice update(Choice choice) {
		delete(choice);
		create(choice);
		return choice;
	}

	public void delete(Choice choice) {
		mongoOperations.remove(choice);
	}

	public List<Choice> findAll() {
		return mongoOperations.findAll(Choice.class, collectionName);
	}

	public Post findById(String id) {
		Post post = mongoOperations.findOne(new Query(Criteria.where("_id").is(id)), Post.class, collectionName);
		return post;
	}

	public Post findByRef(String ref) {
		Post post = mongoOperations.findOne(new Query(Criteria.where("ref").is(ref)), Post.class, collectionName);
		return post;
	}

	public MongoOperations getMongoOperations() {
		return mongoOperations;
	}

	public void setMongoOperations(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}
}
