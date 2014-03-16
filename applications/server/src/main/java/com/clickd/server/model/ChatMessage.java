package com.clickd.server.model;

import java.util.Date;
import java.util.UUID;

import org.springframework.data.annotation.Id;

public class ChatMessage extends Resource {

	@Id
	protected String id;
	protected String ref;


	protected String userRef;
	protected String postText;
	
	protected Date dateCreated;
	protected User userFrom;

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public ChatMessage() {
		super();
		createRef();
	}

	public ChatMessage(User user, String postText, Date date) {
		this.userFrom = user;
		this.postText = postText;
		this.dateCreated = date;
		// TODO Auto-generated constructor stub
	}

	private void createRef() {
		UUID uuid = UUID.randomUUID();
		String ref = "/answers/" + ((Long) Math.abs(uuid.getMostSignificantBits())).toString();
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
	
	public String getUserRef() {
		return userRef;
	}

	public void setUserRef(String userRef) {
		this.userRef = userRef;
	}

	public String getPostText() {
		return postText;
	}

	public void setPostText(String post) {
		this.postText = post;
	}

}
