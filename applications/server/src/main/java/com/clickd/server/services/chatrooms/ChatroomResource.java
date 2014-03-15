package com.clickd.server.services.chatrooms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;

import com.clickd.server.dao.ChatroomDao;
import com.clickd.server.dao.UserDao;
import com.clickd.server.model.Chatroom;
import com.clickd.server.model.ErrorMessage;
import com.clickd.server.model.Link;
import com.clickd.server.model.ChatMessage;
import com.clickd.server.model.User;
import com.clickd.server.utilities.Utilities;
import com.yammer.metrics.annotation.Timed;

@Path("/chatrooms")
@Produces(MediaType.APPLICATION_JSON)
public class ChatroomResource {
	@Autowired
	private ChatroomDao chatroomDao;

	@Autowired
	private UserDao userDao;
	
	@GET
	@Timed
	public String getAll(@PathParam("user") String user, @Context HttpServletRequest request, @Context HttpServletResponse response,
			@Context HttpHeaders headers) {
		List<Chatroom> allChatrooms = chatroomDao.findAll();
		String result = Utilities.toJson(allChatrooms);
		return result;
	}
	
	@POST
	@Timed
	@Path("/get/clique/{cliqueName}")
	public Response get(@PathParam("cliqueName") String cliqueName, @Context HttpServletRequest request, @Context HttpServletResponse response,
			@Context HttpHeaders headers) {
		try
			{
			List<Chatroom> allChatrooms = chatroomDao.findAll();
			
			boolean chatroomExists = false;
			for (Chatroom chatroom : allChatrooms)
			{
				if (chatroom.getChatroomType().equals("clique"))
				{
					if (chatroom.getName().equals(cliqueName))
					{
						chatroomExists = true;
						return Response.status(200).entity(Utilities.toJson(chatroom)).build();		 
					}
				}
			}
			
			if (!chatroomExists)
			{
				Chatroom newChatroom = new Chatroom("clique");
				newChatroom.setName(cliqueName);
				chatroomDao.create(newChatroom);
				return Response.status(200).entity(Utilities.toJson(newChatroom)).build();		 

			}
			
			return Response.status(300).entity(new ErrorMessage("failed", "shouldnt get here")).build();	
		}
		catch (Exception e)
		{
			return Response.status(300).entity(new ErrorMessage("failed", e.getMessage())).build();	
		}
	}
	
	@POST
	@Timed
	@Path("/get/{chatroomRef}")
	public Response getByChatroomRef(@PathParam("chatroomRef") String chatroomRef, @Context HttpServletRequest request, @Context HttpServletResponse response,
			@Context HttpHeaders headers) {
		try
			{
				Chatroom chatroom = chatroomDao.findByRef("/chatrooms/"+chatroomRef);
				
				if (chatroom == null)
				{
					return Response.status(300).entity(new ErrorMessage("failed", "chatroom not found")).build();
				}
				else
				{
					return Response.status(200).entity(Utilities.toJson(chatroom)).build();		
				}
		}
		catch (Exception e)
		{
			return Response.status(300).entity(new ErrorMessage("failed", e.getMessage())).build();	
		}
	}
	
	
	@POST
	@Timed
	@Path("/get/user/{userRef1}/{userRef2}")
	public Response get(@PathParam("userRef1") String userRef1, @PathParam("userRef2") String userRef2, @Context HttpServletRequest request, @Context HttpServletResponse response,
			@Context HttpHeaders headers) {
		try
			{
			List<Chatroom> allChatrooms = chatroomDao.findAll();
			
			boolean chatroomExists = false;
			for (Chatroom chatroom : allChatrooms)
			{
				if (chatroom.getChatroomType().equals("user"))
				{
					List<Link> chatroomUserLinks = chatroom.getLinkLists("users");
					
					boolean user1Found = false;
					boolean user2Found = false;
					for (Link chatroomUserLink : chatroomUserLinks)
					{
						if (chatroomUserLink.getHref().equals("/users/"+userRef1))
							user1Found = true;
						if (chatroomUserLink.getHref().equals("/users/"+userRef2))
							user2Found = true;
					}
					
					if (user1Found && user2Found)
					{
						chatroomExists = true;
						return Response.status(200).entity(Utilities.toJson(chatroom)).build();		 
					}
				}
			}
			
			if (!chatroomExists)
			{
				Chatroom newChatroom = new Chatroom("user");
				newChatroom.setName(userRef1+"/"+userRef2);

				ArrayList<Link> chatroomUsers = new ArrayList<Link>();
				chatroomUsers.add(new Link("/users/"+userRef1, "chatroom-user"));
				chatroomUsers.add(new Link("/users/"+userRef2, "chatroom-user"));
				newChatroom.addLinkLists("users", chatroomUsers);

				chatroomDao.create(newChatroom);
				return Response.status(200).entity(Utilities.toJson(newChatroom)).build();		 

			}
			
			return Response.status(300).entity(new ErrorMessage("failed", "shouldnt get here")).build();	
		}
		catch (Exception e)
		{
			return Response.status(300).entity(new ErrorMessage("failed", e.getMessage())).build();	
		}
	}
	
	@GET
	@Timed
	@Path("/add/{chatroomType}")
	public String addChatroom( @PathParam("chatroomType") String chatroomType, 
			@Context HttpServletRequest request, @Context HttpServletResponse response,@Context HttpHeaders header)
	{
		Chatroom chatroom = new Chatroom(chatroomType);
		chatroomDao.create(chatroom);
		return Utilities.toJson(chatroom);
	}
	
	@GET
	@Timed
	@Path("/{chatroomRef}/join/{userRef}")
	public String addMemberToChatroom(@PathParam("chatroomRef") String chatroomRef,  @PathParam("userRef") String userRef, 
			@Context HttpServletRequest request, @Context HttpServletResponse response,@Context HttpHeaders header)
	{
		Chatroom chatroom = chatroomDao.findByRef("/chatrooms/"+chatroomRef);
		Link memberLink = new Link("/users/"+userRef, "chatroom-member");
		chatroom.getLinkLists("member-list").add(memberLink);
		chatroomDao.update(chatroom);
		return Utilities.toJson(chatroom);
	}
	
	@POST
	@Timed
	@Path("/{chatroomRef}/{userRef}/posts")
	@Produces(MediaType.APPLICATION_JSON)
	public Response addPost(@PathParam("chatroomRef") String chatroomRef,  @PathParam("userRef") String userRef, @FormParam	("postText") String postText)
	{
		try
		{
			Chatroom chatroom = chatroomDao.findByRef("/chatrooms/"+chatroomRef);
			User user = userDao.findByRef("/users/"+userRef);
			
			ChatMessage message = new ChatMessage(user, postText, new Date());
			List<ChatMessage> messageList;
			if (null == chatroom.get_Embedded().get("message-list")) {
				messageList = new ArrayList<ChatMessage>();
			} else {
				messageList = (List<ChatMessage>)chatroom.get_Embedded().get("message-list");
			}
			messageList.add(message);
			chatroom.get_Embedded().put("message-list", messageList);
			chatroomDao.update(chatroom);
			
			return Response.status(200).entity(Utilities.toJson(chatroom)).build();	
		}
		catch (Exception e)
		{
			return Response.status(300).entity(new ErrorMessage("failed", e.getMessage())).build();	

		}
	}

	@GET
	@Timed
	@Path("/{chatroomRef}")
	public String getChatroom(@PathParam("chatroomRef") String chatroomRef) {
		Chatroom chatroom = chatroomDao.findByRef("/chatrooms/" + chatroomRef);
		String result = Utilities.toJson(chatroom);
		return result;
	}
	
	@GET
	@Timed
	@Path("/byuser/{userRef}")
	public String getUsersChatrooms(@PathParam("userRef") String userRef) {
		List<Chatroom> usersChatrooms = chatroomDao.findByUserRef(userRef);
		String result = Utilities.toJson(usersChatrooms);
		return result;
	}

	public ChatroomDao getChatroomDao() {
		return chatroomDao;
	}

	public void setChatroomDao(ChatroomDao chatroomDao) {
		this.chatroomDao = chatroomDao;
	}

}
