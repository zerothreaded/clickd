package com.clickd.server.services.users;

import com.clickd.server.model.User;

public class CandidateResponse
{
	private User user;
	private Integer score;
	
//	public CandidateResponse() {
//		
//	}
	
	public CandidateResponse(User user, Integer score) {
		this.user = user;
		this.score = score;
	}
	
	public User getUser() {
		return user;
	}
	
//	public void setUser(User user) {
//		this.user = user;
//	}

	public Integer getScore() {
		return score;
	}
	
	public void setScore(Integer score) {
		this.score = score;
	}
	
}