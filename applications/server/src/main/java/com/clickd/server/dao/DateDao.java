package com.clickd.server.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.mongodb.core.MongoOperations;

import com.clickd.server.model.Date;

public class DateDao implements InitializingBean {

	public Map<String, Date> cache;
	
	private MongoOperations mongoOperations;
	private String collectionName;

	public DateDao() {
		System.out.println("DateDao() called.");
		cache = new ConcurrentHashMap<String, Date>();
		this.collectionName = "dates";
	}

	public MongoOperations getMongoOperations() {
		return mongoOperations;
	}

	public void setMongoOperations(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}

	public Date create(Date Date) {
		mongoOperations.save(Date, collectionName);
		cache.put(Date.getRef(), Date);
		return Date;
	}

	public Date update(Date Date) {
		delete(Date);
		create(Date);
		return Date;
	}

	public void delete(Date Date) {
		mongoOperations.remove(Date);
		cache.remove(Date.getRef());
	}

	public List<Date> findAll() {
		List<Date> results = new ArrayList<Date>();
		results.addAll(cache.values());
		return results;
	}

	public Date findByRef(String ref) {
		Date Date = cache.get(ref);
		return Date;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		long now = new java.util.Date().getTime();
		System.out.println("afterPropertiesSet() called. Loading cache..");
		List<Date> allDates = mongoOperations.findAll(Date.class, collectionName);
		for (Date Date : allDates) {
			cache.put(Date.getRef(), Date);
		}
		System.out.println("Date cache has " + cache.size() + " Dates. Loaded in " + (new java.util.Date().getTime() - now) + "ms");

	}

}
