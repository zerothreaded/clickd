package com.clickd.server.dao;

import java.util.List;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.clickd.server.model.Connection;

public class ConnectionDao {

	private MongoOperations mongoOperations;
	private String collectionName;

	public ConnectionDao() {
		System.out.println("ConnectionDao() called.");
		this.collectionName = "connections";
	}

	public MongoOperations getMongoOperations() {
		return mongoOperations;
	}

	public void setMongoOperations(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}

	public Connection create(Connection connection) {
		mongoOperations.save(connection, collectionName);
		return connection;
	}

	public Connection update(Connection connection) {
		delete(connection);
		create(connection);
		return connection;
	}

	public void delete(Connection connection) {
		mongoOperations.remove(connection);
	}

	public Connection findById(String id) {
		Connection Connection = mongoOperations.findOne(new Query(Criteria.where("_id").is(id)), Connection.class, collectionName);
		return Connection;
	}

	public Connection findByToken(Long token) {
		Connection Connection = mongoOperations.findOne(new Query(Criteria.where("token").is(token)), Connection.class, collectionName);
		return Connection;
	}

	public List<Connection> findAll() {
		return mongoOperations.findAll(Connection.class, collectionName);
	}

	public Connection findByRef(String ref) {
		Connection Connection = mongoOperations.findOne(new Query(Criteria.where("ref").is(ref)), Connection.class, collectionName);
		return Connection;
	}
}
