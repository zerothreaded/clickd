package com.clickd.server.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.mongodb.core.MongoOperations;

import com.clickd.server.model.Television;

public class TelevisionDao implements InitializingBean {

	private MongoOperations mongoOperations;
	private String collectionName;
	private Map<String, Television> cache;

	public TelevisionDao() {
		System.out.println("TelevisionDao() called.");
		this.collectionName = "television";
		this.cache = new ConcurrentHashMap<String, Television>();
	}

	public MongoOperations getMongoOperations() {
		return mongoOperations;
	}

	public void setMongoOperations(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}

	public Television create(Television television) {
		mongoOperations.save(television, collectionName);
		cache.put(television.getRef(), television);
		return television;
	}

	public Television update(Television television) {
		delete(television);
		create(television);
		return television;
	}

	public void delete(Television television) {
		mongoOperations.remove(television);
		cache.remove(television.getRef());
	}

	public List<Television> findAll() {
		return mongoOperations.findAll(Television.class, collectionName);
	}

	public Television findByRef(String ref) {
		return cache.get(ref);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		long now = new Date().getTime();
		System.out.println("afterPropertiesSet() called. Loading cache..");
		List<Television> allTelevisions = mongoOperations.findAll(Television.class, collectionName);
		for (Television user : allTelevisions) {
			cache.put(user.getRef(), user);
		}
		System.out.println("Television cache has " + cache.size() + " televisions. Loaded in " + (new Date().getTime() - now) + "ms");
	}

}
