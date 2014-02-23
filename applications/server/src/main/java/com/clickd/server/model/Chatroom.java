package com.clickd.server.model;

import java.util.ArrayList;
import java.util.UUID;

import org.springframework.data.annotation.Id;

public class Chatroom extends Resource {

	@Id
	protected String id;
	protected String ref;
	protected String chatroomType;

	public Chatroom() {
		super();
		createRef();
	}

	public Chatroom(String chatroomType) {
		this.chatroomType = chatroomType;
		createRef();
		this.get_Links().put("member-list", new ArrayList<Link>());
		this.get_Embedded().put("post-list", new ArrayList<Post>());
	}

	private void createRef() {
		UUID uuid = UUID.randomUUID();
		String ref = "/chatrooms/" + ((Long) Math.abs(uuid.getMostSignificantBits())).toString();
		this.ref = ref;
	}
	
	public String getChatroomType() {
		return chatroomType;
	}

	public void setChatroomType(String chatroomType) {
		this.chatroomType = chatroomType;
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
