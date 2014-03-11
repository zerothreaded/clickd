package com.clickd.server.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.clickd.server.model.Question;

public class QuestionDao implements InitializingBean {

	public Map<String, Question> cache;
	
	private MongoOperations mongoOperations;
	private String collectionName;

	public QuestionDao() {
		System.out.println("QuestionDao() called.");
		cache = new ConcurrentHashMap<String, Question>();
		this.collectionName = "questions";
	}

	public MongoOperations getMongoOperations() {
		return mongoOperations;
	}

	public void setMongoOperations(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}

	public Question create(Question question) {
		mongoOperations.save(question, collectionName);
		cache.put(question.getRef(), question);
		return question;
	}

	public Question update(Question question) {
		delete(question);
		create(question);
		return question;
	}

	public void delete(Question question) {
		mongoOperations.remove(question);
		cache.remove(question.getRef());
	}
//
////
////	public List<Question> findAllSortedBy(String field) {
////		Query query = new Query();
////		query.with(new Sort(Sort.Direction.ASC, field));	
////		return mongoOperations.find(query, Question.class, collectionName);
////	}

	public List<Question> findAll() {
		List<Question> results = new ArrayList<Question>();
		results.addAll(cache.values());
		return results;
	}

//	public Question findById(String id) {
//		Question question = mongoOperations.findOne(new Query(Criteria.where("_id").is(id)), Question.class, collectionName);
//		return question;
//	}

	public Question findByRef(String ref) {
		return cache.get(ref);
	}
	
	public Question findByTags(String tag)
	{
//		Question question = mongoOperations.findOne(new Query(Criteria.where("tags").is(tag)), Question.class, collectionName);
//		Collection questionList = Collections.unmodifiableCollection(cache.values());
		for (Question question : cache.values()) {
			if (question.getTags() == null)
			{
				System.out.println("Question Tags == null for Question " + question.getQuestionText());
				continue;
			}
			List<String> questionTags = question.getTags();
			for (String questionTag : questionTags) {
				if (questionTag == null)
				{
					System.out.println("Question TAG == null for Question " + question.getQuestionText());
					continue;
				}
				if (questionTag.equals(tag)) {
					return question;
				}
			}
		}
		return null;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		long now = new Date().getTime();
		System.out.println("afterPropertiesSet() called. Loading cache..");
		List<Question> allQuestions = mongoOperations.findAll(Question.class, collectionName);
		for (Question question : allQuestions) {
			cache.put(question.getRef(), question);
		}
		System.out.println("Question cache has " + cache.size() + " questions. Loaded in " + (new Date().getTime() - now) + "ms");

	}

}
