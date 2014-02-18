package com.clickd.server.dao;

import org.springframework.data.mongodb.core.MongoOperations;

import com.clickd.server.model.Entity;


public class EntityDao {

	private MongoOperations mongoOperations;

	public EntityDao() {
		System.out.println("EntityDao() called.");
	}

	public MongoOperations getMongoOperations() {
		return mongoOperations;
	}

	public void setMongoOperations(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}

//	public List<Entity> getAll(String collectionName) {
//		// System.out.println("getAll() called for collection : " + collectionName);
//		return mongoOperations.findAll(Entity.class, collectionName);
//	}
//	
//	public void dropCollection(String name) {
//		mongoOperations.dropCollection(name);
//	}
//	
//	public void deleteObject(String collectionName, Entity object) {
//		mongoOperations.remove(object, collectionName);
//	}
//	
	public Entity save(String collectionName, Entity entity) {
		mongoOperations.save(entity, collectionName);
		return entity;
	}
//
//	public List<Entity> findQuestionsByMemberEmail(String memberEmail) {
//		// Get answered questions;
//		List<Entity> questions = mongoOperations.find( new Query(
//			Criteria.where("values.source").is(memberEmail)),
//			Entity.class, "questions");
//		Utilities.logAsJson("findQuestionsByMemberEmail() returned : ", questions);
//		return questions;
//	}
//	
//	
//	public List<Entity> findProfilesByMemberEmail(String memberEmail) {
//		// Get answered questions;
//		List<Entity> profiles = mongoOperations.find( new Query(
//			Criteria.where("values.user_key").is(memberEmail)),
//			Entity.class, "userprofiles");
//		Utilities.logAsJson("findProfilesByMemberEmail() returned : ", profiles);
//		return profiles;
//		
//	}
//	
//	public boolean hasUserAnsweredQuestion(String userKey, String questionKey)
//	{
//		List<Entity> userChoicesForQuestion = mongoOperations.find( new Query(
//				Criteria.where("values.question_key").is(questionKey).and("values.user_key").is(userKey)),
//				Entity.class, "choices");
//			Utilities.logAsJson("hasUserAnsweredQuestion() main query returned : ", userChoicesForQuestion);
//			return userChoicesForQuestion.size() > 0;
//	}
//	
//	public Entity findMemberByEmailAddress(String emailAddress) {
//		Entity user = mongoOperations.findOne(new Query(Criteria.where("values.email").is(emailAddress)), Entity.class, "users");
//		// Utilities.logAsJson("findUserByEmailAddress() returned : ", user);
//		return user;
//	}
//	
//	public Entity findSessionByUserEmail(String email) {
//		Entity user = mongoOperations.findOne(new Query(Criteria.where("values.user_email").is(email)), Entity.class, "sessions");
//		return user;
//	}
//	
//	public Entity findSessionByToken(String token) {
//		Entity session = mongoOperations.findOne(new Query(Criteria.where("values.user_token").is(token)), Entity.class, "sessions");
//		return session;
//	}
//
//	public Entity findAnswerById(String answerKey) {
//		Entity answer = mongoOperations.findOne(new Query(Criteria.where("values.key").is(answerKey)), Entity.class, "answers");
//		// Utilities.logAsJson("findAnswerById() returned : ", answer);
//		return answer;
//	}
//
//	public List<Entity> findPossibleAnswersForQuestions(String questionKey) {
//		List<Entity> answersForQuestions = mongoOperations.find(new Query(
//			Criteria.where("values.question_key").is(questionKey)), 
//			Entity.class, "question_answers");
//		// Utilities.logAsJson("AnswersForQuestions() returned : ", answersForQuestions);
//		
//		// Extract unique question keys
//		Set<String> answersForQuestionsKeys = new HashSet<String>();
//		for (Entity answeredQuestion : answersForQuestions) {
//			answersForQuestionsKeys.add(answeredQuestion.getStringValue("answer_key"));
//		}
//		
//		Collection<?> allAnswers = this.getAll("answers");
//		List<Entity> answers = new ArrayList<Entity>();
//		for (Object result : allAnswers) {
//			Entity answer = (Entity)result;
//			String answerKey = answer.getStringValue("key");
//			if (containsAnswer(answersForQuestionsKeys, answerKey)) {
//				answers.add(answer);
//			}
//		}
//		
//		//System.out.println("ANSWER KEY: "+answers.get(0).getStringValue("key"));
//		if (answers.get(0).getStringValue("key").equals("a.0"))
//		{
//			Entity answer = answers.get(0);
//			answers.remove(0);
//			answers.add(answer);
//		}
//		
//		return answers;
//	}
//	
//	public List<Entity> findChoicesByMemberEmail(String memberEmail) {
//		// Get answered questions;
//		List<Entity> answeredQuestions = mongoOperations.find( new Query(
//			Criteria.where("values.user_key").is(memberEmail)),
//			Entity.class, "choices");
//		Utilities.logAsJson("findChoicesByMemberEmail() returned : ", answeredQuestions);
//		return answeredQuestions;
//	}
//	
//	public List<Entity> findChoicesByQuestionId(String questionId) {
//		// Get answered questions;
//		List<Entity> answeredQuestions = mongoOperations.find( new Query(
//			Criteria.where("values.question_key").is(questionId)),
//			Entity.class, "choices");
//		Utilities.logAsJson("findChoicesByQuestionId() returned : ", answeredQuestions);
//		return answeredQuestions;
//	}
//	
//	public Map<Entity, List<Entity>> findCliquesByUser(String email) {
//		Map<Entity, List<Entity>> cliques = new HashMap<Entity, List<Entity>>();
//		
//		List<Entity> membersChoices = findChoicesByMemberEmail(email);
//		for (Entity memberChoice : membersChoices) {
//			String cliqueKey = memberChoice.getStringValue("answer_key");
//			if (cliqueKey.equals("a.0"))
//				continue;
//			
//			List<Entity> sameChoices = findChoicesByAnswerId(cliqueKey);
//			List<Entity> cliqueMembers = new ArrayList<Entity>();
//			for (Entity choice : sameChoices) {
//				String cliqueMemberEmail = choice.getStringValue("user_key");
//				
//				if (!cliqueMemberEmail.equals(email))
//				{
//					Entity user = findMemberByEmailAddress(cliqueMemberEmail);
//					cliqueMembers.add(user);
//				}
//			}
//			cliques.put(findAnswerById(cliqueKey), cliqueMembers);
//		}
//		Utilities.logAsJson("CLIQUES", cliques);
//		return cliques;
//	}
//	
//	public List<Entity> findChoicesByAnswerId(String answerId) {
//		// Get answered questions;
//		List<Entity> answeredQuestions = mongoOperations.find( new Query(
//			Criteria.where("values.answer_key").is(answerId)),
//			Entity.class, "choices");
//		Utilities.logAsJson("findChoicesByAnswerId)() returned : ", answeredQuestions);
//		return answeredQuestions;
//	}
//	
//	public List<Entity> findUnansweredQuestionsForMember(String topicKey, String memberEmail) {
//		// Get answered questions;
//		List<Entity> answeredQuestions = mongoOperations.find( new Query(
//			Criteria.where("values.user_key").is(memberEmail)),
//			Entity.class, "choices");
//		Utilities.logAsJson("AnsweredQuestions returned : ", answeredQuestions);
//		
//		// Extract unique question keys
//		Set<String> answeredQuestionsKeys = new HashSet<String>();
//		for (Entity answeredQuestion : answeredQuestions) {
//			answeredQuestionsKeys.add(answeredQuestion.getStringValue("question_key"));
//		}
//		
//		// Find questions that HAVE NOT BEEN answered
//		List<Entity> unansweredQuestions = new ArrayList<Entity>();
//		List<Entity> allQuestions = this.getAll("questions");
//		for (Entity question : allQuestions) {
//			if (!questionAnswered(answeredQuestionsKeys, question.getStringValue("key"))) {
//				String questionTopicKey = question.getStringValue("Topics");
//				//if (questionTopicKey.equals(topicKey)) {
//					System.out.println("Adding question TK=" + topicKey + " QTK=" + questionTopicKey);
//					unansweredQuestions.add(question);
//				//} else {
//				//	System.out.println("Skipping question - wrong TOPIC [" + questionTopicKey + "] TK [" + topicKey + "]");
//				//}
//			} else {
//				System.out.println("Skipping question - already answered");
//			}
//		}
//		Utilities.logAsJson("findUnnsweredQuestionsForMember(" + memberEmail + ") returned  : ", unansweredQuestions);
//		return unansweredQuestions;
//	}
//
//	public boolean containsSameAnswerToQuestion(List<Entity> choices, String questionKey, String answerKey) {
//		for (Entity choice : choices) {
//			String choiceQuestionKey = choice.getStringValue("question_key");
//			String choiceAnswerKey = choice.getStringValue("answer_key");	
//			if (questionKey.equals(choiceQuestionKey) && answerKey.equals(choiceAnswerKey)) {
//				return true;
//			}
//		}
//		return false;
//	}
//	
//	public List<String> findPeopleOrderedByNumberOfSameAnswersForMember(String email) {
//		List<String> results = new ArrayList<String>();
//		Map<String, Integer> memberAnswerCount = new HashMap<String, Integer>();
//		// 1. Get existing choices by member
//		List<Entity> memberChoices = findChoicesByMemberEmail(email);
//		
//		// 2. Compare this members choices against choices FOR EVERY OTHER member
//		List<Entity> allMembers = getAll("users");
//		for (Entity member : allMembers) {
//			
//			// Exclude the member we're testing against
//			String memberEmail = member.getStringValue("email");
//			Integer numberOfIdenticalAnswers = 0;
//			if (!memberEmail.equals(email)) {
//				List<Entity> otherMembersChoices = findChoicesByMemberEmail(memberEmail);
//
//				for (Entity otherMembersChoice : otherMembersChoices) {
//					String memberQuestionKey = otherMembersChoice.getStringValue("question_key");
//					String memberAnswerKey = otherMembersChoice.getStringValue("answer_key");
//					if (containsSameAnswerToQuestion(memberChoices, memberQuestionKey, memberAnswerKey)) {
//						numberOfIdenticalAnswers++;
//					}
//				}
//			}
//			
//		//	if (numberOfIdenticalAnswers > 0)
//		//	{
//				memberAnswerCount.put(memberEmail, numberOfIdenticalAnswers);	
//		//	}
//		}
//		Utilities.logAsJson("Members Answer Count", memberAnswerCount);
//		
//		// 3. Sort this by the count - i.e. the values
//		
//		// First find the maximum count of all matches
//		Integer maximumCount = -1;
//		for (String memberEmail : memberAnswerCount.keySet()) {
//			Integer count = memberAnswerCount.get(memberEmail);
//			if (count > maximumCount) {
//				maximumCount = count;
//			}
//		}
//		System.out.println("maximum count" + maximumCount);
//		
//		// Now work down the set in reverse count order
//		for (int i = maximumCount; i >=0 ; i--) {
//			for (String memberEmail : memberAnswerCount.keySet()) {
//				Integer memberCount = memberAnswerCount.get(memberEmail);
//				if (memberCount == i && memberCount > 0) {
//					results.add(memberEmail);
//				}
//			}
//		}
//		Utilities.logAsJson("FINAL Members Answer Sorted List", results);
//		
//		
////		for (Entity memberChoice : memberChoices) {
////			// Get the answer THIS member made for a question
////			String memberQuestionKey = memberChoice.getStringValue("question_key");
////			String memberAnswerKey = memberChoice.getStringValue("answer_key");
////			
////			// Count other members who had the same answer to this question
////			Long numberOfIdenticalAnswers = 0L;
////			List<String> memberEmails = new ArrayList<String>();
////			List<Entity> allAnswersToThisQuestion = findChoicesByQuestionId(memberQuestionKey);
////			for (Entity otherChoice : allAnswersToThisQuestion) {
////				if (otherChoice.getStringValue("answer_key").equals(memberAnswerKey)) {
////					String memberEmail = otherChoice.getStringValue("user_key");
////					// Don't add the member itself
////					if (!memberEmail.equals(email)) {
////						memberEmails.add(memberEmail);
////					}
////					numberOfIdenticalAnswers++;
////				}
////			}
////			// Store the members with the same answer
////			questionAnswers.put(memberQuestionKey, memberEmails);
////			Utilities.logAsJson("Members Answers", questionAnswers);
////
////		}
//		
//		return results;
//	}
//	private boolean questionAnswered(Set<String> answeredQuestionsKeys, String questionKey) {
//		for(String answeredQuestionKey : answeredQuestionsKeys) {
//			// System.out.println("Testing Keys " + answeredQuestionKey + " against " + questionKey);
//			if (answeredQuestionKey.equals(questionKey)){
//				return true;
//			}
//		}
//		return false;
//	}
//
//	private boolean containsAnswer(Set<String> answeredQuestionsKeys, String answerKey) {
//		for (String key : answeredQuestionsKeys) {
//			// System.out.println("Testing Keys " + key + " against " + answerKey);
//			if (key.equals(answerKey)) {
//				return true;
//			}
//		}
//		return false;
//	}

}
