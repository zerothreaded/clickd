package com.clickd.server.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.clickd.server.model.Connection;
import com.clickd.server.model.Link;

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
	
	public List<Connection> findAllByUserRef(String userRef)
	{
		List<Connection> connections = (List<Connection>)mongoOperations.findAll(Connection.class, collectionName);
	
		ArrayList<Connection> returnList = new ArrayList<Connection>();
		
		for (Connection connection : connections)
		{
			List<Link> links = connection.getLinks("connection-user");
			
			for (Link link : links)
			{
				if (link.getRel().equals("from-user") || link.getRel().equals("to-user"))
				{
					String userHref = link.getHref();
					if (userHref.equals(userRef))
						returnList.add(connection);
				}
			}
		}
		
		return returnList;
	}
	
	public Connection findByBothUserRefs(String fromUserRef, String toUserRef)
	{
		List<Connection> connections = (List<Connection>)mongoOperations.findAll(Connection.class, collectionName);
		for (Connection connection : connections)
		{
			List<Link> links = (List<Link>)connection.get_Links();
			
			boolean firstMatches = false;
			for (Link link : links)
			{
				if (link.getRel() == "user-from")
				{
					String userHref = link.getHref();
					if (userHref == fromUserRef && link.getRel() == "user-from")
					{
						if (firstMatches)
							return connection;
						else
							firstMatches = true;
					}
				}
				
				if (link.getRel() == "user-to")
				{
					String userHref = link.getHref();
					if (userHref == toUserRef && link.getRel() == "user-to")
					{
						if (firstMatches)
							return connection;
						else
							firstMatches = true;
					}
				}
			}
		}
		
		return null;
	}
	

	public Connection findByBothUserRefsIgnoreRole(String fromUserRef, String toUserRef)
	{
		List<Connection> connections = (List<Connection>)mongoOperations.findAll(Connection.class, collectionName);
		for (Connection connection : connections)
		{
			List<Link> links = connection.getLinks("connection-user");
			
			boolean firstMatches = false;
			for (Link link : links)
			{
				if (link.getRel() == "user-from" || link.getRel() == "user-to")
				{
					String userHref = link.getHref();
					if (userHref == fromUserRef)
					{
						if (firstMatches)
							return connection;
						else
							firstMatches = true;
					}
				}
			}
		}
		
		return null;
	}
}
