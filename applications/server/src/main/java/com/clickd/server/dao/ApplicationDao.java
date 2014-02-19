package com.clickd.server.dao;

import java.util.List;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.clickd.server.model.Application;
import com.clickd.server.model.Session;

public class ApplicationDao {

	private MongoOperations mongoOperations;
	private String collectionName;

	public ApplicationDao() {
		System.out.println("ApplicationDao() called.");
		this.collectionName = "applications";
	}

	public MongoOperations getMongoOperations() {
		return mongoOperations;
	}

	public void setMongoOperations(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}
	
	public Application create(Application application) {
		mongoOperations.save(application, collectionName);
		return application;
	}
	
	public Application update(Application application)
	{
		delete(application);
		create(application);
		return application;
	}
	
	public void delete(Application application)
	{
		mongoOperations.remove(application);
	}
	
	public Application findById(String id)
	{
		Application application = mongoOperations.findOne(new Query(Criteria.where("_id").is(id)), Application.class, collectionName);
		return application;
	}
	
	public List<Application> findAll() {
		return mongoOperations.findAll(Application.class, collectionName);
	}
	
}
