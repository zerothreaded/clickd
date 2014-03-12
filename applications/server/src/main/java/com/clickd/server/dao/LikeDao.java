package com.clickd.server.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.mongodb.core.MongoOperations;

import com.clickd.server.model.Like;

public class LikeDao implements InitializingBean {

	private MongoOperations mongoOperations;
	private String collectionName;
	private Map<String, Like> cache;

	public LikeDao() {
		System.out.println("LikeDao() called.");
		this.collectionName = "likes";
		this.cache = new ConcurrentHashMap<String, Like>();
	}

	public MongoOperations getMongoOperations() {
		return mongoOperations;
	}

	public void setMongoOperations(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}

	public Like create(Like like) {
		mongoOperations.save(like, collectionName);
		cache.put(like.getRef(), like);
		return like;
	}

	public Like update(Like like) {
		delete(like);
		create(like);
		return like;
	}

	public void delete(Like like) {
		mongoOperations.remove(like);
		cache.remove(like.getRef());
	}

	public List<Like> findAll() {
		return mongoOperations.findAll(Like.class, collectionName);
	}

	public Like findByRef(String ref) {
		return cache.get(ref);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		long now = new Date().getTime();
		System.out.println("afterPropertiesSet() called. Loading cache..");
		List<Like> allLikes = mongoOperations.findAll(Like.class, collectionName);
		for (Like user : allLikes) {
			cache.put(user.getRef(), user);
		}
		System.out.println("Like cache has " + cache.size() + " Likes. Loaded in " + (new Date().getTime() - now) + "ms");
	}	

}
