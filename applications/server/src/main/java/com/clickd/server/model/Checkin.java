package com.clickd.server.model;

import java.util.Date;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

public class Checkin extends Resource {

	@Id
	protected String id;
	protected String ref;
	
	protected String fbId;
	
	protected String message;
	@DateTimeFormat(iso=ISO.DATE)
	protected Date checkinTime;
	
	private void createRef() {
		UUID uuid = UUID.randomUUID();
		String ref = "/checkins/" + ((Long) Math.abs(uuid.getMostSignificantBits())).toString();
		this.ref = ref;
	}
	
	public Checkin() {
		super();
		createRef();
	}

	public Checkin(String fbId, String message, Date checkinTime) {
		super();
		this.fbId = fbId;
		this.message = message;
		this.checkinTime = checkinTime;
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

	public String getFbId() {
		return fbId;
	}

	public void setFbId(String fbId) {
		this.fbId = fbId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getCheckinTime() {
		return checkinTime;
	}

	public void setCheckinTime(Date checkinTime) {
		this.checkinTime = checkinTime;
	}
	
	
}
