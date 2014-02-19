package com.clickd.server.dao;

import java.util.List;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.clickd.server.model.Choice;

public class ChoiceDao {

	private MongoOperations mongoOperations;
	private String collectionName;

	public ChoiceDao() {
		System.out.println("ChoiceDao() called.");
		this.collectionName = "choices";
	}

	public MongoOperations getMongoOperations() {
		return mongoOperations;
	}

	public void setMongoOperations(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}
	
	public Choice create(Choice choice) {
		mongoOperations.save(choice, collectionName);
		return choice;
	}
	
	public Choice update(Choice choice)
	{
		delete(choice);
		create(choice);
		return choice;
	}
	
	public void delete(Choice choice)
	{
		mongoOperations.remove(choice);
	}
	
	public List<Choice> findAll() {
		return mongoOperations.findAll(Choice.class, collectionName);
	}

	public Choice findById(String id)
	{
		Choice choice = mongoOperations.findOne(new Query(Criteria.where("_id").is(id)), Choice.class, collectionName);
		return choice;
	}


	public Choice findByRef(String ref) {
		Choice choice = mongoOperations.findOne(new Query(Criteria.where("ref").is(ref)), Choice.class, collectionName);
		return choice;
	}

}
