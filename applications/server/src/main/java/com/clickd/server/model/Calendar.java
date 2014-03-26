package com.clickd.server.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import org.springframework.data.annotation.Id;

public class Calendar extends Resource {

	@Id
	protected String id;
	protected String ref;
	protected String name;
	protected ArrayList<Date> days;
	
	public ArrayList<Date> getDays() {
		return days;
	}

	public void setDays(ArrayList<Date> daysOfThisWeek) {
		this.days = daysOfThisWeek;
	}

	public Calendar() {	
		super();
		createRef();
	}

	private void createRef() {
		UUID uuid = UUID.randomUUID();
		String ref = "/calendars/" + ((Long) Math.abs(uuid.getMostSignificantBits())).toString();
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
