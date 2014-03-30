package com.clickd.server.model;

import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.Id;

public class Criteria extends Resource {

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
	protected String dateRef;
	protected String name;
	

	protected Operator operator;
	
	protected List<Object> values;
	
	public Criteria() {	
		super();
		createRef();
	}
	

	public String getDateRef() {
		return dateRef;
	}

	public void setDateRef(String dateRef) {
		this.dateRef = dateRef;
	}

	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	private void createRef() {
		UUID uuid = UUID.randomUUID();
		String ref = "/criteria/" + ((Long) Math.abs(uuid.getMostSignificantBits())).toString();
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
