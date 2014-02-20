package com.clickd.server.model;

import java.util.UUID;

import org.springframework.data.annotation.Id;

public class Answer extends Resource {

	@Id
	protected String id;
	protected String ref;

	protected String answerText;
	protected String imageName;

	public Answer() {
		super();
		createRef();
	}

	private void createRef() {
		UUID uuid = UUID.randomUUID();
		String ref = "/answers/" + ((Long) Math.abs(uuid.getMostSignificantBits())).toString();
		this.ref = ref;
	}

	public Answer(String answerText) {
		super();

		createRef();
		this.answerText = answerText;
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

	public String getAnswerText() {
		return answerText;
	}

	public void setAnswerText(String answerText) {
		this.answerText = answerText;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

}
