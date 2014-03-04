package com.clickd.server.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.clickd.server.model.Choice;
import com.clickd.server.model.Link;
import com.clickd.server.model.Resource;

public class ChoiceDao {

	private MongoOperations mongoOperations;
	private String collectionName;

	public ChoiceDao() {
		System.out.println("ChoiceDao() called.");
		this.collectionName = "choices";
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

	public Choice findById(String id) {
		Choice choice = mongoOperations.findOne(new Query(Criteria.where("_id").is(id)), Choice.class, collectionName);
		return choice;
	}

	public Choice findByRef(String ref) {
		Choice choice = mongoOperations.findOne(new Query(Criteria.where("ref").is(ref)), Choice.class, collectionName);
		return choice;
	}

	public List<Choice> findByUserRef(String userRef) {
		List<Choice> usersChoices = new ArrayList<Choice>();
		List<Choice> allChoices = findAll();
		for (Choice choice : allChoices) {
			String choiceUserRef = choice.getLink(Resource.KEY_LINK_CHOICE_USER).getHref();
			if (choiceUserRef.equals("/users/" + userRef)) {
				usersChoices.add(choice);
			}
		}
		return usersChoices;
	}

	public MongoOperations getMongoOperations() {
		return mongoOperations;
	}

	public void setMongoOperations(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}

	public List<Choice> findChoicesWithTheSameAnswerByHref(String href) {
		List<Choice> answerChoices = new ArrayList<Choice>();
		List<Choice> allChoices = findAll();
		for (Choice choice : allChoices) {
			Link choiceAnswerLink = choice.getLink("choice-answer");
			String choiceAnswerRef =  choiceAnswerLink.getHref();
			if (choiceAnswerRef.equals(href)) {
				answerChoices.add(choice);
			}
		}
		return answerChoices;
	}

	public List<Choice> findChoicesWithTheSameAnswerByAnswerText(String answerText) {
		List<Choice> answerChoices = new ArrayList<Choice>();
		List<Choice> allChoices = findAll();
		for (Choice choice : allChoices) {
			if (null == choice.getAnswerText())
				continue;
			if (choice.getAnswerText().equals(answerText)) {
				answerChoices.add(choice);
			}
		}
		return answerChoices;
	}

}
