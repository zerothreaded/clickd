package com.clickd.server.utilities;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;

public class Utilities {

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
	
	public static HashMap<String, Object> fromJson(String json) {
		JsonElement parsed = new com.google.gson.JsonParser().parse(json);
		
		HashMap<String, Object> map = new HashMap<String, Object>();

		if (parsed instanceof JsonArray)
		{
			JsonArray object = (JsonArray)parsed;
			

			for (int i = 0; i < object.size(); i++)
			{
				JsonElement value = object.get(i);
				
				if (!value.isJsonPrimitive())
				{
					map.put(String.valueOf(i), fromJson(value.toString()));
				}
				else
				{
					map.put(String.valueOf(i), object.get(i));
				}
			}
		}
		else
		{
			JsonObject object =  (JsonObject)parsed;
			
			Set<Map.Entry<String, JsonElement>> set = object.entrySet();
			Iterator<Map.Entry<String, JsonElement>> iterator = set.iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, JsonElement> entry = iterator.next();
				String key = entry.getKey();
				JsonElement value = entry.getValue();
				if (!value.isJsonPrimitive()) {
					map.put(key, fromJson(value.toString()));
				} else {
					map.put(key, value.getAsString());
				}
			}
		}

		
		return map;
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
