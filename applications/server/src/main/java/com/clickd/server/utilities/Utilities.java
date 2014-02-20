package com.clickd.server.utilities;

import java.text.SimpleDateFormat;
import java.util.Date;

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

	public static String toJsonNoPretty(Object object) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
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

}
