package com.clickd.server.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.mongodb.core.MongoOperations;

import com.clickd.server.model.MemberDate;

public class MemberDateDao implements InitializingBean {

	public Map<String, MemberDate> cache;
	
	private MongoOperations mongoOperations;
	private String collectionName;

	public MemberDateDao() {
		System.out.println("MemberDateDao() called.");
		cache = new ConcurrentHashMap<String, MemberDate>();
		this.collectionName = "dates";
	}

	public MongoOperations getMongoOperations() {
		return mongoOperations;
	}

	public void setMongoOperations(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}

	public MemberDate create(MemberDate Date) {
		mongoOperations.save(Date, collectionName);
		cache.put(Date.getRef(), Date);
		return Date;
	}

	public MemberDate update(MemberDate Date) {
		delete(Date);
		create(Date);
		return Date;
	}

	public void delete(MemberDate Date) {
		mongoOperations.remove(Date);
		cache.remove(Date.getRef());
	}

	public List<MemberDate> findAll() {
		List<MemberDate> results = new ArrayList<MemberDate>();
		results.addAll(cache.values());
		return results;
	}

	public MemberDate findByRef(String ref) {
		MemberDate Date = cache.get(ref);
		return Date;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		long now = new java.util.Date().getTime();
		System.out.println("afterPropertiesSet() called. Loading cache..");
		List<MemberDate> allDates = mongoOperations.findAll(MemberDate.class, collectionName);
		for (MemberDate Date : allDates) {
			cache.put(Date.getRef(), Date);
		}
		System.out.println("Date cache has " + cache.size() + " Dates. Loaded in " + (new java.util.Date().getTime() - now) + "ms");

	}

}
