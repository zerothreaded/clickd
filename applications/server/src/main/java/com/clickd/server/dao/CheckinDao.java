package com.clickd.server.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.mongodb.core.MongoOperations;

import com.clickd.server.model.Checkin;

public class CheckinDao implements InitializingBean {

	private MongoOperations mongoOperations;
	private String collectionName;
	private Map<String, Checkin> cache;

	public CheckinDao() {
		System.out.println("CheckinDao() called.");
		this.collectionName = "checkins";
		this.cache = new ConcurrentHashMap<String, Checkin>();
	}

	public MongoOperations getMongoOperations() {
		return mongoOperations;
	}

	public void setMongoOperations(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}

	public Checkin create(Checkin checkin) {
		mongoOperations.save(checkin, collectionName);
		cache.put(checkin.getRef(), checkin);
		return checkin;
	}

	public Checkin update(Checkin checkin) {
		delete(checkin);
		create(checkin);
		return checkin;
	}

	public void delete(Checkin checkin) {
		mongoOperations.remove(checkin);
		cache.remove(checkin.getRef());
	}

	public List<Checkin> findAll() {
		return mongoOperations.findAll(Checkin.class, collectionName);
	}

	public Checkin findByRef(String ref) {
		return cache.get(ref);
	}
	
	public List<Checkin> findForUserRef(String userRef) {
		List<Checkin> results = new ArrayList<Checkin>();
		for (String key : cache.keySet()) {
			Checkin checkin = cache.get(key);
			if (checkin.getLinkByName("user").getHref().equals(userRef)) {
				results.add(checkin);
			}
		}
		return results;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		long now = new Date().getTime();
		System.out.println("afterPropertiesSet() called. Loading cache..");
		List<Checkin> allCheckins = mongoOperations.findAll(Checkin.class, collectionName);
		for (Checkin user : allCheckins) {
			cache.put(user.getRef(), user);
		}
		System.out.println("Checkin cache has " + cache.size() + " Checkins. Loaded in " + (new Date().getTime() - now) + "ms");
	}	

}
