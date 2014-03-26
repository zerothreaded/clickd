package com.clickd.server.model;

import java.util.Set;
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

	protected Object singleValue;
	
	protected Object binaryValue1;
	protected Object binaryValue2;
	
	protected Set<Object> setValues;
	
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
}
