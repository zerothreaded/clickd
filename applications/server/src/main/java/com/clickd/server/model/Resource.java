package com.clickd.server.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Resource {
	public static final String KEY_LINK_SELF = "self";
	public static final String KEY_LINK_USER_SESSION_LIST = "user-session-list";
	public static final String KEY_LINK_CHOICE_USER = "choice-user";
	public static final String KEY_LINK_CHOICE_QUESTION = "choice-question";
	public static final String KEY_LINK_CHOICE_ANSWER = "choice-answer";

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

	public Link getLink(String name) {
		return (Link)_links.get(name);
	}
	
	@SuppressWarnings("unchecked")
	public List<Link> getLinks(String name) {
		return (List<Link>)_links.get(name);
	}
	
	public void addLink(String name, Link link) {
		_links.put(name, link);
	}
	
	public void addLinks(String name, List<Link> link) {
		_links.put(name, link);
	}
	
}
