package com.clickd.server.services.questions;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;

import com.clickd.server.dao.AnswerDao;
import com.clickd.server.dao.ChoiceDao;
import com.clickd.server.dao.MovieDao;
import com.clickd.server.dao.QuestionDao;
import com.clickd.server.dao.TelevisionDao;
import com.clickd.server.model.Answer;
import com.clickd.server.model.Choice;
import com.clickd.server.model.Link;
import com.clickd.server.model.Movie;
import com.clickd.server.model.Question;
import com.clickd.server.model.Resource;
import com.clickd.server.model.Television;
import com.clickd.server.utilities.Utilities;
import com.yammer.metrics.annotation.Timed;

@Path("/questions")
@Produces(MediaType.APPLICATION_JSON)
public class QuestionResource {
	@Autowired
	private QuestionDao questionDao;
	
	@Autowired
	private AnswerDao answerDao;
	
	@Autowired
	private ChoiceDao choiceDao;

	@Autowired
	private MovieDao movieDao;
	
	@Autowired
	private TelevisionDao televisionDao;

	
	private boolean conditionalGetMovieImage(String movieHref)
	{
		try
		{
			Movie movie = movieDao.findByRef(movieHref);
			String movieImageUrl = movie.getPosterImageUrl();
			
			if (movieImageUrl == null)
				return false;
			
			// Get the MOVIES IMDB image and save it locally
			 String dataDir = System.getProperty("dataDir");
			 if (null == dataDir) {
				 dataDir = "C:\\sandbox\\data\\profile-img\\users\\";
			 }
			String targetFileName = dataDir + movieHref + ".jpg";
			File file = new File(targetFileName);
			if (!file.exists()) {
				System.out.println("Getting movie Image.."+movieImageUrl+" to "+targetFileName);
				 URL url = new URL(movieImageUrl);
				 InputStream in = new BufferedInputStream(url.openStream());
				 ByteArrayOutputStream out = new ByteArrayOutputStream();
				 byte[] buf = new byte[1024];
				 int n = 0;
				 while (-1!=(n=in.read(buf)))
				 {
				    out.write(buf, 0, n);
				 }
				 out.close();
				 in.close();
				 byte[] response = out.toByteArray();
				 FileOutputStream fos = new FileOutputStream(targetFileName);
				 fos.write(response);
				 fos.close();
				 System.out.println("Saved friends Image.");
				 return true;
			}
			 return false;
		}
		catch (Exception e)
		{
			return false;
		}
	}
	
	private boolean conditionalGetTelevisionImage(String televisionHref)
	{
		try
		{
			Television television = televisionDao.findByRef(televisionHref);
			String televisionImageUrl = television.getPosterImageUrl();
			
			if (televisionImageUrl == null)
				return false;
			
			// Get the MOVIES IMDB image and save it locally
			 String dataDir = System.getProperty("dataDir");
			 if (null == dataDir) {
				 dataDir = "C:\\sandbox\\data\\profile-img\\users\\";
			 }
			String targetFileName = dataDir + televisionHref.substring(1) + ".jpg";
			if (targetFileName.contains("\\"))
				targetFileName = targetFileName.replace("television/", "television\\");
			File file = new File(targetFileName);
			if (!file.exists()) {
				System.out.println("Getting television Image.."+televisionImageUrl+" to "+targetFileName);
				 URL url = new URL(televisionImageUrl);
				 InputStream in = new BufferedInputStream(url.openStream());
				 ByteArrayOutputStream out = new ByteArrayOutputStream();
				 byte[] buf = new byte[1024];
				 int n = 0;
				 while (-1!=(n=in.read(buf)))
				 {
				    out.write(buf, 0, n);
				 }
				 out.close();
				 in.close();
				 byte[] response = out.toByteArray();
				 FileOutputStream fos = new FileOutputStream(targetFileName);
				 fos.write(response);
				 fos.close();
				 System.out.println("Saved friends Image.");
				 return true;
			}
			 return false;
		}
		catch (Exception e)
		{
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	@GET
	@Path("/next/{userRef}/{tags}")
	@Timed
	public String getNextQuestion(@PathParam("userRef") String userRef, @PathParam("tags") String tags ) {
		
		try {
			//List<Question> questions = questionDao.findAllSortedBy("ref");
			List<Question> questions = questionDao.findAll();
			//List<Question> questions = questionDao.findAll();
			List<Choice> userChoices = choiceDao.findByUserRef("/users/"+userRef);
	
			ArrayList<Question> toDeleteSet = new ArrayList<Question>();
			for (Choice choice : userChoices)
			{
				for (Question choiceQuestion : questions)
				{
					if (choiceQuestion.getRef().equals(choice.getLinkByName("question").getHref()))
					{
						toDeleteSet.add(choiceQuestion);
					}
				}
			}
				
			for (Question toReturn : questions)
			{
				boolean skip = false;
				for (Question toDeleteQuestion : toDeleteSet)
					if (toReturn.getRef().equals(toDeleteQuestion.getRef()))
					{
						skip = true;
					}
				
				if (skip) {
					continue;
				} else {
					// GO FOR MOVIE QUESTIONS ONLY
					if (toReturn.getTags().contains(tags)) {
						// Got a movie one
						if (tags.equals("fb.movies"))
						{
							String movieHref = toReturn.getLinkByName("movie-data").getHref();
							boolean gotImage = conditionalGetMovieImage(movieHref);
							if (!gotImage)
								continue;
							toReturn.get_Embedded().put("image-url", "/profile-img/users/" +  movieHref + ".jpg");
							toReturn.setImg("/profile-img/users" +  movieHref + ".jpg");
						} else if (tags.equals("fb.televisions"))
						{
							String televisionHref = toReturn.getLinkByName("television-data").getHref();
							boolean gotImage = conditionalGetTelevisionImage(televisionHref);
							if (!gotImage)
								continue;
							else
							{
							toReturn.get_Embedded().put("image-url", "/profile-img/users/" +  televisionHref + ".jpg");
							toReturn.setImg("/profile-img/users" +  televisionHref + ".jpg");
							}
						}
						else
						{
							toReturn.get_Embedded().put("image-url", "/profile-img/users/blankImg.jpg");
							toReturn.setImg("/profile-img/users/blankImg.jpg");						
						}
						
						return Utilities.toJson(toReturn);
					} else {
						continue;
					}
				}
			}
			
			return "";
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}
	

	@GET
	@Path("/tags/all")
	@Timed
	public Map<String,String> getTags()
	{
		Map<String,String> toReturn = new HashMap<String,String>();
		toReturn.put("fb.movies", "movies");
		toReturn.put("fb.televisions", "television");
		toReturn.put("fb.books", "books");
		toReturn.put("fb.checkin", "places");

		return toReturn;
	}

//	public QuestionDao getQuestionDao() {
//		return questionDao;
//	}
//
//	public void setQuestionDao(QuestionDao questionDao) {
//		this.questionDao = questionDao;
//	}
//
//	public void setAnswerDao(AnswerDao answerDao) {
//		this.answerDao = answerDao;
//	}
//
//	public ChoiceDao getChoiceDao() {
//		return choiceDao;
//	}
//
//	public void setChoiceDao(ChoiceDao choiceDao) {
//		this.choiceDao = choiceDao;
//	}

}
