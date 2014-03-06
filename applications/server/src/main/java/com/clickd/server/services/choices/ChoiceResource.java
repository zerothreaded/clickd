	package com.clickd.server.services.choices;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;

import com.clickd.server.dao.ChoiceDao;
import com.clickd.server.model.Choice;
import com.clickd.server.model.ErrorMessage;
import com.clickd.server.model.Link;
import com.clickd.server.model.Resource;
import com.clickd.server.utilities.Utilities;
import com.yammer.metrics.annotation.Timed;

@Path("/choices")
@Produces(MediaType.APPLICATION_JSON)
public class ChoiceResource {
	
	@Autowired
	private ChoiceDao choiceDao;

	@GET
	@Timed
	@Path("/{userRef}")
	public String getUsersChoices(@PathParam("userRef") String userRef) {
		List<Choice> usersChoices = choiceDao.findByUserRef(userRef);
		String result = Utilities.toJson(usersChoices);
		return result;
	}
	

	@POST
	@Timed
	@Path("/{userRef}/{questionRef}/{answerRef}")
	public Response createWithAnswerRef(@PathParam("userRef") String userRef, @PathParam("questionRef") String questionRef, @PathParam("answerRef") String answerRef ) {
		try {
			Choice choice = new Choice();
			choice.addLink(Resource.KEY_LINK_SELF, new Link(choice.getRef(), "self"));
			choice.addLink(Resource.KEY_LINK_CHOICE_USER, new Link("/users/" + userRef, "user"));
			choice.addLink(Resource.KEY_LINK_CHOICE_QUESTION, new Link("/questions/" + questionRef, "question"));
			choice.addLink(Resource.KEY_LINK_CHOICE_ANSWER, new Link("/answers/" + answerRef, "answer"));
			
			// Ensure question has not been answered before
			// TODO : Revisit this once we allow editing of previous answers
			List<Choice> usersChoices = choiceDao.findByUserRef(userRef);
			for (Choice existingChoice : usersChoices) {
				Link questionLink = existingChoice.getLinkByName(Resource.KEY_LINK_CHOICE_QUESTION);
				if (questionLink.getHref().equals("/questions/" + questionRef)) {
					// Already answered so don't save it
					choice = existingChoice;
				}
			}
			choiceDao.create(choice);
			return Response.status(200).entity(Utilities.toJson(choice)).build();

		} catch (Exception e) {
			return Response.status(300).entity(new ErrorMessage("failed", e.getMessage())).build(); 
		}
	}
	

	@POST
	@Timed
	@Path("/{userRef}/{questionRef}/answerText/{text}")
	public String createWithAnswerText(@PathParam("userRef") String userRef, @PathParam("questionRef") String questionRef, @PathParam("answerText") String answerText ) {
		Choice choice = new Choice();
		choice.addLink(Resource.KEY_LINK_SELF, new Link(choice.getRef(), "self"));
		choice.addLink(Resource.KEY_LINK_CHOICE_USER, new Link("/users/" + userRef, "user"));
		choice.addLink(Resource.KEY_LINK_CHOICE_QUESTION, new Link("/questions/" + questionRef, "question"));
		choice.setAnswerText(answerText);
		choiceDao.create(choice);
		String result = Utilities.toJson(choice);
		return result;
	}

	public ChoiceDao getChoiceDao() {
		return choiceDao;
	}

	public void setChoiceDao(ChoiceDao choiceDao) {
		this.choiceDao = choiceDao;
	}

}
