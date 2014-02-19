package com.clickd.server.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.Id;

public class User extends Resource {

	@Id
	protected String id;
	protected String ref;

	protected String firstName;
	protected String lastName;

	protected Date dateOfBirth;
	protected String gender;
	protected String postCode;

	protected String email;
	protected String password;
		
	// protected List<Link> sessionLinks = new ArrayList<Link>();

	public User()
	{
		super();
		createRef();
	}
	
	private void createRef()
	{
		UUID uuid = UUID.randomUUID();
		String ref = "/users/" + ((Long)Math.abs(uuid.getMostSignificantBits())).toString();
		this.ref = ref;
	}
	
	public User(String firstName, String lastName, Date dateOfBirth, String gender, String postCode, String email, String password) {
		super();

		createRef();
		this.firstName = firstName;
		this.lastName = lastName;
		this.dateOfBirth = dateOfBirth;
		this.gender = gender;
		this.postCode = postCode;
		this.email = email;
		this.password = password;
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
	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
//	public void addSession(Link sessionLink)
//	{
//		sessionLinks.add(sessionLink);
//	}
//	
//	public void removeSessionLink(Link sessionLink)
//	{
//		sessionLinks.remove(sessionLink);
//	}
//
//	public List<Link> getSessionLinks() {
//		return sessionLinks;
//	}
//	
//	public void setSessionLinks(List <Link> sessionLinks) {
//		this.sessionLinks = sessionLinks;
//	}

}
