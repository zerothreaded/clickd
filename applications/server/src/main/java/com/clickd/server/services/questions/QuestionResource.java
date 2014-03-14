package com.clickd.server.services.questions;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
import com.clickd.server.model.Answer;
import com.clickd.server.model.Choice;
import com.clickd.server.model.Link;
import com.clickd.server.model.Movie;
import com.clickd.server.model.Question;
import com.clickd.server.model.Resource;
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
	
	@SuppressWarnings("unchecked")
	@GET
	@Path("/next/{userRef}")
	@Timed
	public String getNextQuestion(@PathParam("userRef") String userRef) {
		
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
					if (toReturn.getTags().contains("fb.movies")) {
						// Got a movie one
						String movieHref = toReturn.getLinkByName("movie-data").getHref();
						Movie movie = movieDao.findByRef(movieHref);
						String movieImageUrl = movie.getPosterImageUrl();
						if (movieImageUrl == null || movieImageUrl.equals("N/A")) {
							continue;
						}
						toReturn.setType(movieImageUrl);	
						// Get the MOVIES IMDB image and save it locally
						 String dataDir = System.getProperty("dataDir");
						 if (null == dataDir) {
							 dataDir = "C:\\sandbox\\data\\profile-img\\users\\";
						 }
						String targetFileName = dataDir + movieHref + ".jpg";
						File file = new File(targetFileName);
						if (!file.exists()) {
							System.out.println("Getting friends Image..");
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
						} else {
							 // System.out.println("Skipping Image Load");
						}
						
						toReturn.get_Embedded().put("movie-image-url", "/profile-img/users/" +  movieHref + ".jpg");
						toReturn.setType("/profile-img/users/" +  movieHref + ".jpg");
						
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
