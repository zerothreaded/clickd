package com.clickd.server.dao;

import java.util.List;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.clickd.server.model.Clique;

public class CliqueDao {

	private MongoOperations mongoOperations;
	private String collectionName;

	public CliqueDao() {
		System.out.println("CliqueDao() called.");
		this.collectionName = "cliques";
	}

	public MongoOperations getMongoOperations() {
		return mongoOperations;
	}

	public void setMongoOperations(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}

	public Clique create(Clique connection) {
		mongoOperations.save(connection, collectionName);
		return connection;
	}

	public Clique update(Clique connection) {
		delete(connection);
		create(connection);
		return connection;
	}

	public void delete(Clique connection) {
		mongoOperations.remove(connection);
	}

	public Clique findById(String id) {
		Clique Clique = mongoOperations.findOne(new Query(Criteria.where("_id").is(id)), Clique.class, collectionName);
		return Clique;
	}


	public List<Clique> findAll() {
		return mongoOperations.findAll(Clique.class, collectionName);
	}

	public Clique findByRef(String ref) {
		Clique Clique = mongoOperations.findOne(new Query(Criteria.where("ref").is(ref)), Clique.class, collectionName);
		return Clique;
	}
}
