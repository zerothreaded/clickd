package com.clickd.server.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.mongodb.core.MongoOperations;

import com.clickd.server.model.Place;

public class PlaceDao implements InitializingBean {

	private MongoOperations mongoOperations;
	private String collectionName;
	private Map<String, Place> cache;

	public PlaceDao() {
		System.out.println("PlaceDao() called.");
		this.collectionName = "places";
		this.cache = new ConcurrentHashMap<String, Place>();
	}

	public MongoOperations getMongoOperations() {
		return mongoOperations;
	}

	public void setMongoOperations(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}

	public Place create(Place place) {
		synchronized (this) {
			mongoOperations.save(place, collectionName);
			cache.put(place.getRef(), place);
			return place;
		}
	}

	public Place update(Place place) {
		delete(place);
		create(place);
		return place;
	}

	public void delete(Place place) {
		mongoOperations.remove(place);
		cache.remove(place.getRef());
	}

	public List<Place> findAll() {
		return mongoOperations.findAll(Place.class, collectionName);
	}

	public Place findByRef(String ref) {
		return cache.get(ref);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		long now = new Date().getTime();
		System.out.println("afterPropertiesSet() called. Loading cache..");
		List<Place> allPlaces = mongoOperations.findAll(Place.class, collectionName);
		for (Place user : allPlaces) {
			cache.put(user.getRef(), user);
		}
		System.out.println("Place cache has " + cache.size() + " places. Loaded in " + (new Date().getTime() - now) + "ms");
	}

}
