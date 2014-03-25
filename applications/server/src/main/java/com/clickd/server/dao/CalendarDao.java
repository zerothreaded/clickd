package com.clickd.server.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.mongodb.core.MongoOperations;

import com.clickd.server.model.Calendar;

public class CalendarDao implements InitializingBean {

	public Map<String, Calendar> cache;
	
	private MongoOperations mongoOperations;
	private String collectionName;

	public CalendarDao() {
		System.out.println("CalendarDao() called.");
		cache = new ConcurrentHashMap<String, Calendar>();
		this.collectionName = "calendars";
	}

	public MongoOperations getMongoOperations() {
		return mongoOperations;
	}

	public void setMongoOperations(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}

	public Calendar create(Calendar Calendar) {
		mongoOperations.save(Calendar, collectionName);
		cache.put(Calendar.getRef(), Calendar);
		return Calendar;
	}

	public Calendar update(Calendar Calendar) {
		delete(Calendar);
		create(Calendar);
		return Calendar;
	}

	public void delete(Calendar Calendar) {
		mongoOperations.remove(Calendar);
		cache.remove(Calendar.getRef());
	}

	public List<Calendar> findAll() {
		List<Calendar> results = new ArrayList<Calendar>();
		results.addAll(cache.values());
		return results;
	}

	public Calendar findByRef(String ref) {
		Calendar Calendar =  cache.get(ref);
		return Calendar;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		long now = new Date().getTime();
		System.out.println("afterPropertiesSet() called. Loading cache..");
		List<Calendar> allCalendars = mongoOperations.findAll(Calendar.class, collectionName);
		for (Calendar Calendar : allCalendars) {
			cache.put(Calendar.getRef(), Calendar);
		}
		System.out.println("Calendar cache has " + cache.size() + " Calendars. Loaded in " + (new Date().getTime() - now) + "ms");

	}

}
