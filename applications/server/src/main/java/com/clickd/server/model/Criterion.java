package com.clickd.server.model;

import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.Id;

public class Criterion extends Resource {

	public static enum Operator {
		// EXISTENCE Operators
		EXISTS,
		NOT_EXISTS,
		
		// UNARY Operators
		EQUAL,
		NOT_EQUAL,
		
		LESS_THAN,
		GREATER_THAN,
		
		// BINARY Operators
		BETWEEN,
		
		// SET Operators
		IN,
		NOT_IN
	
	}
	
	@Id
	protected String id;
	protected String ref;
	
	protected Operator operator;
	
	protected List<Object> values;
	
	public Criterion() {	
		super();
		createRef();
	}

	private void createRef() {
		UUID uuid = UUID.randomUUID();
		String ref = "/criterion/" + ((Long) Math.abs(uuid.getMostSignificantBits())).toString();
		this.ref = ref;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getRef() {
		return ref;
	}
	
	public void setRef(String ref) {
		this.ref = ref;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public List<Object> getValues() {
		return values;
	}

	public void setValues(List<Object> values) {
		this.values = values;
	}
}
