package com.clickd.server.model;

import java.util.UUID;

import org.springframework.data.annotation.Id;

public class Question extends Resource {

	@Id
	protected String id;
	protected String ref;

	protected String questionText;
	protected String source;
		
	public Question()
	{
		super();
		createRef();
	}
	
	private void createRef()
	{
		UUID uuid = UUID.randomUUID();
		String ref = "/questions/" + ((Long)Math.abs(uuid.getMostSignificantBits())).toString();
		this.ref = ref;
	}
	
	public Question(String questionText, String source) {
		super();

		createRef();
		this.questionText = questionText;
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
	
	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

}
