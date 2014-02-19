package com.clickd.server.model;

import java.util.HashMap;
import java.util.Map;

public abstract class Resource {

	public static final String KEY_LINK_SELF = "self";

	public static final String KEY_LINK_SESSION_LIST = "user-session-list";

	private Map<String, Object> _links = new HashMap<String, Object>();
	private Map<String, Object> _embedded = new HashMap<String, Object>();

	public Map<String, Object> get_Links() {
		return _links;
	}

	public void set_Links(Map<String, Object> links) {
		this._links = links;
	}

	public Map<String, Object> get_Embedded() {
		return _embedded;
	}

	public void set_Embedded(Map<String, Object> embedded) {
		this._embedded = embedded;
	}

}
