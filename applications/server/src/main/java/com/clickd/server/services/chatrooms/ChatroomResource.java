	package com.clickd.server.services.chatrooms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import com.clickd.server.dao.ChatroomDao;
import com.clickd.server.model.Chatroom;
import com.clickd.server.model.Link;
import com.clickd.server.model.Post;
import com.clickd.server.utilities.Utilities;
import com.yammer.metrics.annotation.Timed;

@Path("/chatrooms")
@Produces(MediaType.APPLICATION_JSON)
public class ChatroomResource {
	private ChatroomDao chatroomDao;

	@GET
	@Timed
	public String getAll(@PathParam("user") String user, @Context HttpServletRequest request, @Context HttpServletResponse response,
			@Context HttpHeaders headers) {
		List<Chatroom> allChatrooms = chatroomDao.findAll();
		String result = Utilities.toJson(allChatrooms);
		return result;
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
		((List<Link>)chatroom.get_Links().get("member-list")).add(memberLink);
		chatroomDao.update(chatroom);
		
		return Utilities.toJson(chatroom);
	}
	
	@GET
	@Timed
	@Path("/{chatroomRef}/{userRef}/posts/{postText}")
	public String post(@PathParam("chatroomRef") String chatroomRef,  @PathParam("userRef") String userRef, @PathParam("postText") String postText, @Context HttpServletRequest request, @Context HttpServletResponse response,
			@Context HttpHeaders header)
	{
		Chatroom chatroom = chatroomDao.findByRef("/chatrooms/"+chatroomRef);
		
		Post post = new Post(userRef, postText, new Date());
		
		
		List<Post> postList;
		
		
		if (null == chatroom.get_Embedded().get("post-list"))
			postList = new ArrayList<Post>();
		else
			postList = (List<Post>)chatroom.get_Embedded().get("post-list");
		
		postList.add(post);
		chatroom.get_Embedded().put("post-list", postList);
		
		chatroomDao.update(chatroom);
		
		return Utilities.toJson(chatroom);
	}


	@GET
	@Timed
	@Path("/{chatroomRef}")
	public String getChatroom(@PathParam("chatroomRef") String chatroomRef, @Context HttpServletRequest request, @Context HttpServletResponse response,
			@Context HttpHeaders headers) {
		Chatroom chatroom = chatroomDao.findByRef("/chatrooms/"+chatroomRef);
		String result = Utilities.toJson(chatroom);
		return result;
	}
	
	
	@GET
	@Timed
	@Path("/byuser/{userRef}")
	public String getUsersChatrooms(@PathParam("userRef") String userRef, @Context HttpServletRequest request, @Context HttpServletResponse response,
			@Context HttpHeaders headers) {
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
