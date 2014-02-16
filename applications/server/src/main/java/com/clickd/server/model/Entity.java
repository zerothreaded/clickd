package com.clickd.server.model;

import java.util.Map;
import java.util.TreeMap;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Entity {

	@Id
	protected String id;
	
	protected Map<String, Object> values;
	
	public Entity() {
		this.values = new TreeMap<String, Object>();
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Map<String, Object> getValues() {
		return values;
	}

	public void setValues(Map<String, Object> values) {
		this.values = values;
	}
	

	public void setValue(String key, Object value) {
		this.values.put(key, value);
	}
	
	public Object getValue(String propertyName) {
		return this.values.get(propertyName);
	}

	public String getStringValue(String propertyName) {
		if (null != this.values.get(propertyName)) {
			return (String)this.values.get(propertyName);
		} else {
			System.out.println("WARNING::CLICKD ENTITY HAS NULL VALUE for proprety [" + propertyName + "]");
			return "<null>";
		}
	}

	public Long getLongValue(String propertyName) {
		if (null != this.values.get(propertyName)) {
			return (Long)this.values.get(propertyName);
		} else {
			System.out.println("WARNING::CLICKD ENTITY HAS NULL VALUE");
			return 0L;
		}
	}

}
