package com.clickd.server.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.clickd.server.model.Chatroom;
import com.clickd.server.model.Link;

public class ChatroomDao {

	private MongoOperations mongoOperations;
	private String collectionName;

	public ChatroomDao() {
		System.out.println("ChatroomDao() called.");
		this.collectionName = "chatrooms";
	}

	public Chatroom create(Chatroom chatroom) {
		mongoOperations.save(chatroom, collectionName);
		return chatroom;
	}

	public Chatroom update(Chatroom chatroom) {
		delete(chatroom);
		create(chatroom);
		return chatroom;
	}

	public void delete(Chatroom chatroom) {
		mongoOperations.remove(chatroom);
	}

	public List<Chatroom> findAll() {
		return mongoOperations.findAll(Chatroom.class, collectionName);
	}

	public Chatroom findById(String id) {
		Chatroom chatroom = mongoOperations.findOne(new Query(Criteria.where("_id").is(id)), Chatroom.class, collectionName);
		return chatroom;
	}

	public Chatroom findByRef(String ref) {
		Chatroom chatroom = mongoOperations.findOne(new Query(Criteria.where("ref").is(ref)), Chatroom.class, collectionName);
		return chatroom;
	}

	public List<Chatroom> findByUserRef(String userRef) {
		List<Chatroom> allChatrooms =  mongoOperations.findAll(Chatroom.class, collectionName);
		List<Chatroom> response = new ArrayList<Chatroom>();
		
		for (Chatroom room : allChatrooms)
		{
			List<Link> memberList = room.getLinkLists("member-list");
			for (Link l : memberList)
			{
				if (l.getHref().equals("/users/" + userRef))
				{
					response.add(room);
				}
			}
		}
		return response;
	}
	
	public MongoOperations getMongoOperations() {
		return mongoOperations;
	}

	public void setMongoOperations(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}

}
