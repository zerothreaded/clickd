package com.clickd.server.model;

import java.util.UUID;

import org.springframework.data.annotation.Id;

public class Place extends Resource {

	@Id
	protected String id;
	protected String ref;
	
	// TODO: Ralph - extract this to an abstract FB resource
	protected String fbId;
	
	protected String name;
	protected String street;
	protected String city;
	protected String state;
	protected String country;
	protected String zip;
	protected String latitude;
	protected String longitude;
	
	/*
    "street": "55 Parkway",
    "city": "London",
    "state": "",
    "country": "United Kingdom",
    "zip": "NW1 7PN",
    "latitude": 51.537812325599,
    "longitude": -0.14480018556184
	*/

	public Place() {
		super();
		createRef();
	}

	public Place(String fbId, String name, String street, String city, String state, String country, String zip, String latitude, String longitude) {
		super();
		this.fbId = fbId;
		this.name = name;
		this.street = street;
		this.city = city;
		this.state = state;
		this.country = country;
		this.zip = zip;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	private void createRef() {
		UUID uuid = UUID.randomUUID();
		String ref = "/places/" + ((Long) Math.abs(uuid.getMostSignificantBits())).toString();
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

	public String getFbId() {
		return fbId;
	}

	public void setFbId(String fbId) {
		this.fbId = fbId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	
	
	
}
