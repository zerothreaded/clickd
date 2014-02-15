package com.clickd.server.utilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

}
