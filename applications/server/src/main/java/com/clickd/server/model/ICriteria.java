package com.clickd.server.model;

import java.util.List;

public interface ICriteria {

	public static enum Operator {
		// EXISTENCE Operators
		EXISTS, NOT_EXISTS,
		
		// UNARY Operators
		EQUAL, NOT_EQUAL, 
		LESS_THAN, GREATER_THAN,
		
		// BINARY Operators
		BETWEEN,
		
		// SET Operators
		IN, NOT_IN,
		
		// BOOLEAN Operators
		AND, OR, NOT
	}

	// Criteria Operator and Values State
	public Operator getOperator();
	public void setOperator(Operator operator);
	
	public List<?> getValues();
	public void setValues(List<?> values);
	
	// Single Subject - Default Filter
	public boolean match(User user, Question question);
	
	// Single Subject - Explicit Filter
	public boolean matchAgainst(User user, Question question, Operator operator, List<?> values);

	// Multiple Subjects - Default Filter
	public List<User> getMatches(List<User> users, Question question);
	
	// Multiple Subjects - Explicit Filter
	public List<User> getMatchesAgainst(List<User> users, Question question, Operator operator, List<?> values);
	
	
	
}
