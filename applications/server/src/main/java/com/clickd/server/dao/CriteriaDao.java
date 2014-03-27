package com.clickd.server.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.mongodb.core.MongoOperations;

import com.clickd.server.model.Criteria;

public class CriteriaDao implements InitializingBean {

	private MongoOperations mongoOperations;
	private String collectionName;
	private Map<String, Criteria> cache;

	public CriteriaDao() {
		System.out.println("CriteriaDao() called.");
		this.collectionName = "criteria";
		this.cache = new ConcurrentHashMap<String, Criteria>();
	}

	public MongoOperations getMongoOperations() {
		return mongoOperations;
	}

	public void setMongoOperations(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}

	public Criteria create(Criteria criteria) {
		mongoOperations.save(criteria, collectionName);
		cache.put(criteria.getRef(), criteria);
		return criteria;
	}

	public Criteria update(Criteria criteria) {
		delete(criteria);
		create(criteria);
		return criteria;
	}

	public void delete(Criteria criteria) {
		mongoOperations.remove(criteria);
		cache.remove(criteria.getRef());
	}

	public List<Criteria> findAll() {
		return mongoOperations.findAll(Criteria.class, collectionName);
	}

	public Criteria findByRef(String ref) {
		return cache.get(ref);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		long now = new Date().getTime();
		System.out.println("afterPropertiesSet() called. Loading cache..");
		List<Criteria> allCriterias = mongoOperations.findAll(Criteria.class, collectionName);
		for (Criteria user : allCriterias) {
			cache.put(user.getRef(), user);
		}
		System.out.println("Criteria cache has " + cache.size() + " Criterias. Loaded in " + (new Date().getTime() - now) + "ms");
	}	

}
