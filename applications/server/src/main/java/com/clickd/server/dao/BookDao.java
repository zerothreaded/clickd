package com.clickd.server.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.mongodb.core.MongoOperations;

import com.clickd.server.model.Book;

public class BookDao implements InitializingBean {

	private MongoOperations mongoOperations;
	private String collectionName;
	private Map<String, Book> cache;

	public BookDao() {
		System.out.println("BookDao() called.");
		this.collectionName = "books";
		this.cache = new ConcurrentHashMap<String, Book>();
	}

	public MongoOperations getMongoOperations() {
		return mongoOperations;
	}

	public void setMongoOperations(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}

	public Book create(Book movie) {
		mongoOperations.save(movie, collectionName);
		cache.put(movie.getRef(), movie);
		return movie;
	}

	public Book update(Book movie) {
		delete(movie);
		create(movie);
		return movie;
	}

	public void delete(Book movie) {
		mongoOperations.remove(movie);
		cache.remove(movie.getRef());
	}

	public List<Book> findAll() {
		return mongoOperations.findAll(Book.class, collectionName);
	}

	public Book findByRef(String ref) {
		return cache.get(ref);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		long now = new Date().getTime();
		System.out.println("afterPropertiesSet() called. Loading cache..");
		List<Book> allBooks = mongoOperations.findAll(Book.class, collectionName);
		for (Book user : allBooks) {
			cache.put(user.getRef(), user);
		}
		System.out.println("Book cache has " + cache.size() + " movies. Loaded in " + (new Date().getTime() - now) + "ms");
	}

}
