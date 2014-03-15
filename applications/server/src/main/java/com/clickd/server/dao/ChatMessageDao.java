package com.clickd.server.dao;

import java.util.List;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.clickd.server.model.ChatMessage;
import com.clickd.server.model.Choice;

public class ChatMessageDao {

	private MongoOperations mongoOperations;
	private String collectionName;

	public ChatMessageDao() {
		System.out.println("ChatMessageDao() called.");
		this.collectionName = "chatMessages";
	}

	public Choice create(Choice choice) {
		mongoOperations.save(choice, collectionName);
		return choice;
	}

	public Choice update(Choice choice) {
		delete(choice);
		create(choice);
		return choice;
	}

	public void delete(Choice choice) {
		mongoOperations.remove(choice);
	}

	public List<Choice> findAll() {
		return mongoOperations.findAll(Choice.class, collectionName);
	}

	public ChatMessage findById(String id) {
		ChatMessage chatMessage = mongoOperations.findOne(new Query(Criteria.where("_id").is(id)), ChatMessage.class, collectionName);
		return chatMessage;
	}

	public ChatMessage findByRef(String ref) {
		ChatMessage chatMessage = mongoOperations.findOne(new Query(Criteria.where("ref").is(ref)), ChatMessage.class, collectionName);
		return chatMessage;
	}

	public MongoOperations getMongoOperations() {
		return mongoOperations;
	}

	public void setMongoOperations(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}
}
