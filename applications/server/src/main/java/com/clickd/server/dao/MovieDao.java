package com.clickd.server.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.mongodb.core.MongoOperations;

import com.clickd.server.model.Movie;

public class MovieDao implements InitializingBean {

	private MongoOperations mongoOperations;
	private String collectionName;
	private Map<String, Movie> cache;

	public MovieDao() {
		System.out.println("MovieDao() called.");
		this.collectionName = "movies";
		this.cache = new ConcurrentHashMap<String, Movie>();
	}

	public MongoOperations getMongoOperations() {
		return mongoOperations;
	}

	public void setMongoOperations(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}

	public Movie create(Movie movie) {
		mongoOperations.save(movie, collectionName);
		cache.put(movie.getRef(), movie);
		return movie;
	}

	public Movie update(Movie movie) {
		delete(movie);
		create(movie);
		return movie;
	}

	public void delete(Movie movie) {
		mongoOperations.remove(movie);
		cache.remove(movie.getRef());
	}

	public List<Movie> findAll() {
		return mongoOperations.findAll(Movie.class, collectionName);
	}

	public Movie findByRef(String ref) {
		return cache.get(ref);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		long now = new Date().getTime();
		System.out.println("afterPropertiesSet() called. Loading cache..");
		List<Movie> allMovies = mongoOperations.findAll(Movie.class, collectionName);
		for (Movie user : allMovies) {
			cache.put(user.getRef(), user);
		}
		System.out.println("Movie cache has " + cache.size() + " movies. Loaded in " + (new Date().getTime() - now) + "ms");
	}

}
