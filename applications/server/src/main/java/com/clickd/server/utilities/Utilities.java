package com.clickd.server.utilities;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;

public class Utilities {

	public static String logAsJson(String label, Object object) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(object);
		System.out.println(label + " = " + json);
		return json;
	}

	public static String toJson(Object object) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(object);
		return json;
	}

	public static String toJsonNoPretty(Object object) {
		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(object);
		return json;
	}

	public static Date dateFromString(String s) {
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");

		Date parsed = new Date();
		try {
			parsed = format.parse(s);
		} catch (Exception e) {
		}

		return parsed;
	}
	

	public static void importFixtureFromFile(DB mongoDb, String pathToFile, String collectionName) {
	    FileInputStream fileInputStream = null;
	    try {
	        fileInputStream = new FileInputStream(pathToFile);
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	        return;
	    }
	    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
	    String line;
	    DBCollection dbCollection =  mongoDb.getCollection(collectionName);
	    try {
	        while ((line = bufferedReader.readLine()) != null) {
				// convert line by line to BSON
	            DBObject dbObject = (DBObject) JSON.parse(line);
	            try {
	                dbCollection.insert(dbObject);
	            }
	            catch (MongoException e) {
	              e.printStackTrace();
	            }
	        }
	        bufferedReader.close();
	    } catch (IOException e) {
	        e.printStackTrace(); 
	    }
	}

}
