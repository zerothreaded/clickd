package com.clickd.server.dao;

import java.util.List;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.clickd.server.model.Resource;

public class ResourceDao implements IDao<Resource, String>{

	private MongoOperations mongoOperations;
	protected String collectionName;
	
	@Override
	public Resource create(Resource type) {
		mongoOperations.save(type, collectionName);
		return type;
	}

	@Override
	public Resource update(Resource type) {
		delete(type);
		create(type);
		return type;
	}

	@Override
	public void delete(Resource type) {
		mongoOperations.remove(type, collectionName);
	}

	@Override
	public Resource findOneByKey(String key) {
		Resource resource = mongoOperations.findOne(new Query(Criteria.where("_id").is(key)), Resource.class, collectionName);
		return resource;
	}

	@Override
	public Resource findOneByPropertyValue(String property, Object value) {
		Resource resource = mongoOperations.findOne(new Query(Criteria.where(property).is(value)), Resource.class, collectionName);
		return resource;
	}

	@Override
	public List<Resource> findAllByPropertyValue(String property, Object value) {
		List<Resource> resource = mongoOperations.find(new Query(Criteria.where(property).is(value)), Resource.class, collectionName);
		return resource;

	}

	public String getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public MongoOperations getMongoOperations() {
		return mongoOperations;
	}

	public void setMongoOperations(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}
}
