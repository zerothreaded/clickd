package com.clickd.server.model;

import java.util.UUID;

import org.springframework.data.annotation.Id;

public class Movie extends Resource {

	@Id
	protected String id;
	protected String ref;
	
	// TODO: Ralph - extract this to an abstract FB resource
	protected String fbId;
	protected String name;
	
	protected String posterImageUrl;
	protected String country;
	protected String genres;
	
	/*
	 "Title": "Memento",
	  "Year": "2000",
	  "Rated": "R",
	  "Released": "11 Oct 2000",
	  "Runtime": "113 min",
	  "Genre": "Mystery, Thriller",
	  "Director": "Christopher Nolan",
	  "Writer": "Christopher Nolan (screenplay), Jonathan Nolan (short story \"Memento Mori\")",
	  "Actors": "Guy Pearce, Carrie-Anne Moss, Joe Pantoliano, Mark Boone Junior",
	  "Plot": "A man, suffering from short-term memory loss, uses notes and tattoos to hunt for the man he thinks killed his wife.",
	  "Language": "English",
	  "Country": "USA",
	  "Awards": "Nominated for 2 Oscars. Another 47 wins & 34 nominations.",
	  "Poster": "http:\/\/ia.media-imdb.com\/images\/M\/MV5BMTc4MjUxNDAwN15BMl5BanBnXkFtZTcwMDMwNDg3OA@@._V1_SX300.jpg",
	  "Metascore": "80",
	  "imdbRating": "8.6",
	  "imdbVotes": "593,886",
	  "imdbID": "tt0209144",
	  "Type": "movie",
	  */
	public Movie() {
		super();
		createRef();
	}

	public Movie(String fbId, String name) {
		super();
		this.fbId = fbId;
		this.name = name;
	}

	private void createRef() {
		UUID uuid = UUID.randomUUID();
		String ref = "/movies/" + ((Long) Math.abs(uuid.getMostSignificantBits())).toString();
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

	public String getPosterImageUrl() {
		return posterImageUrl;
	}

	public void setPosterImageUrl(String posterImageUrl) {
		this.posterImageUrl = posterImageUrl;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getGenres() {
		return genres;
	}

	public void setGenres(String genres) {
		this.genres = genres;
	}
	
}
