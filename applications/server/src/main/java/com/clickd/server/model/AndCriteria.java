package com.clickd.server.model;

import java.util.ArrayList;
import java.util.List;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class AndCriteria implements ICriteria {

	private Operator operator;
	private List<?> values;
	private ICriteria firstCriteria;
	private ICriteria secondCriteria;

	public AndCriteria(ICriteria firstCriteria, ICriteria secondCriteria) {
		this.firstCriteria = firstCriteria;
		this.secondCriteria = secondCriteria;
		this.operator = Operator.AND;
		this.values = new ArrayList<>();
	}
	
	@Override
	public Operator getOperator() {
		return operator;
	}

	@Override
	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	@Override
	public List<?> getValues() {
		return values;
	}

	@Override
	public void setValues(List<?> values) {
		this.values = values;
	}

	@Override
	public boolean match(User user, Question question) {
		return firstCriteria.match(user, question) && secondCriteria.match(user, question);
	}

	@Override
	public boolean matchAgainst(User user, Question question, Operator operator, List<?> values) {
		return firstCriteria.matchAgainst(user, question, operator, values) && secondCriteria.matchAgainst(user, question, operator, values);
	}

	@Override
	public List<User> getMatches(List<User> users, Question question) {
		throw new NotImplementedException();
	}

	@Override
	public List<User> getMatchesAgainst(List<User> users, Question question, Operator operator, List<?> values) {
		throw new NotImplementedException();
	}



}
