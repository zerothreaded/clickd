package com.clickd.server.dao;

import java.io.Serializable;
import java.util.List;

public interface IDao <TYPE, KEY extends Serializable> {
	
	// CRU methods
	public TYPE create(TYPE type);
	
	public TYPE update(TYPE type);
	
	public void delete(TYPE type);
	
	public TYPE findOneByKey(KEY key);
	
	// Default FINDERS
	public TYPE findOneByPropertyValue(String property, Object value);
	
	public List<TYPE> findAllByPropertyValue(String property, Object value);
	
}