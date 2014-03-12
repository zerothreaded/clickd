package com.clickd.server.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.clickd.server.model.Choice;
import com.clickd.server.model.Link;
import com.clickd.server.model.Question;
import com.clickd.server.model.Resource;

import edu.emory.mathcs.backport.java.util.Collections;

public class ChoiceDao implements InitializingBean {

	private Map<String, Choice> cache;
	private Map<String, List<Choice>> userChoicesCache;
	
	private MongoOperations mongoOperations;
	private String collectionName;

	public ChoiceDao() {
		System.out.println("ChoiceDao() called.");
		cache = new ConcurrentHashMap<String, Choice>();
		userChoicesCache = new ConcurrentHashMap<String, List<Choice>>();
		this.collectionName = "choices";
	}

	public Choice create(Choice choice) {
		synchronized (this)
		{
			mongoOperations.save(choice, collectionName);
			cache.put(choice.getRef(), choice);
			Link choiceUser = choice.getLinkByName("user");
			if (choiceUser != null) {
				String choiceUserRef = choiceUser.getHref();
				if (userChoicesCache.get(choiceUserRef) == null) {
					userChoicesCache.put(choiceUserRef, new ArrayList<Choice>());
				}
				System.out.println("Adding choice " + choice.getAnswerText() + " for user " + choiceUserRef);
				userChoicesCache.get(choiceUserRef).add(choice);
			}
			return choice;
		}
	}

	public Choice update(Choice choice) {
		delete(choice);
		create(choice);
		return choice;
	}

	public void delete(Choice choice) {
		mongoOperations.remove(choice);
		cache.remove(choice.getRef());
		// TODO: Remove from user choice cache
	}

	public List<Choice> findAll() {
		List<Choice> results = new ArrayList<Choice>();
		results.addAll(cache.values());
		return results;
	}

	public Choice findById(String id) {
		Choice choice = mongoOperations.findOne(new Query(Criteria.where("_id").is(id)), Choice.class, collectionName);
		return choice;
	}

	public Choice findByRef(String ref) {
		return cache.get(ref);
	}
	
	public List<Choice> findByUserRef(String userRef) {
		if (userChoicesCache.get(userRef) == null) {
			userChoicesCache.put(userRef, new ArrayList<Choice>());
		}
		for (String key : userChoicesCache.keySet()) {
			System.out.println("User " + key + " has " + userChoicesCache.get(key).size() + " choices");
		}
		
		ArrayList<Choice> result = new ArrayList<Choice>(userChoicesCache.get(userRef));
		return result;
	}

	public List<Choice> findByUserRefOLD(String userRef) {
		List<Choice> usersChoices = new ArrayList<Choice>();
		for (Choice choice : cache.values()) {
			Link choiceUser = choice.getLinkByName("user");
			if (choiceUser != null) {
				String choiceUserRef = choiceUser.getHref();
				if (choiceUserRef.equals(userRef)) {
					usersChoices.add(choice);
				}
			}
		}
		System.out.println("findByUserRef() returnd [" + usersChoices.size() + "] choices for User " + userRef);
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
			Link choiceAnswerLink = choice.getLinkByName("choice-answer");
			
			if (choiceAnswerLink != null){
				
				String choiceAnswerRef =  choiceAnswerLink.getHref();
				if (choiceAnswerRef.equals(href)) {
					answerChoices.add(choice);
				}
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
	

	public List<Choice> findChoicesWithTheSameAnswerByAnswerTextAndQuestionRef(String answerText, String questionRef) {
		try {
//			Query query = Query.query(Criteria.where("answerText").is(answerText).and("_links.question.href").is(questionRef));
//			List<Choice> toReturn = mongoOperations.find(query, Choice.class);
//			return toReturn;
//		}
			
			List<Choice> answerChoices = new ArrayList<Choice>();
			List<Choice> allChoices = findAll();
			for (Choice choice : allChoices) {
				if (choice == null) {
					// HMMMMMMM - WTF
					System.out.println("WTF WTF");
					
				} else {
					Link choiceHrefLink = choice.getLinkByName("question");
					if (choiceHrefLink != null) {
						String choiceAnswerText = choice.getAnswerText();
						if (choiceAnswerText == null) {
							// HMMMMMMM - WTF
							// System.out.println("WTF choiceAnswerText = null");
							continue;
						}
						String choiceHref = choiceHrefLink.getHref();
						if (choiceAnswerText.equals(answerText) && choiceHref.equals(questionRef)) {
							answerChoices.add(choice);
						} 
						int here = 1;
					} else {
						System.out.println("No question Link in choice  [" + choice.getId());
					}
				}
			}
			// System.out.println("findChoicesWithTheSameAnswerByAnswerTextAndQuestionRef() returnd [" + answerChoices.size() + "] choices for Answer " + answerText);
			return answerChoices;
		}
		 catch(Exception e) {
			int argh = 1;
			return null;
		}

	}

	@Override
	public void afterPropertiesSet() throws Exception {
		long now = new Date().getTime();
		System.out.println("afterPropertiesSet() called. Loading cache..");
		List<Choice> allChoices = mongoOperations.findAll(Choice.class, collectionName);
		for (Choice choice : allChoices) {
			cache.put(choice.getRef(), choice);
			Link choiceUser = choice.getLinkByName("user");
			if (userChoicesCache.get(choiceUser.getHref()) == null) {
				userChoicesCache.put(choiceUser.getHref(), new ArrayList<Choice>());
			}
			userChoicesCache.get(choiceUser.getHref()).add(choice);
		}
		System.out.println("Choice cache has " + cache.size() + " choices. Loaded in " + (new Date().getTime() - now) + "ms");

	}

}
