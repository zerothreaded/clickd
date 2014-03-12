package com.clickd.server.services.books;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;

import com.clickd.server.dao.BookDao;
import com.clickd.server.model.ErrorMessage;
import com.clickd.server.model.Book;
import com.clickd.server.utilities.Utilities;
import com.yammer.metrics.annotation.Timed;

@Path("/books")
@Produces(MediaType.APPLICATION_JSON)
public class BookResource {
	
	@Autowired
	private BookDao bookDao;

	@GET
	@Timed
	public Response getAll() {
		try {
			List<Book> allBooks = bookDao.findAll();
			return Response.status(200).entity(Utilities.toJson(allBooks)).build();
		} catch(Exception e) {
			e.printStackTrace();
			return Response.status(300).entity(new ErrorMessage("failed", e.getMessage())).build();			
		}
	}

	
	@GET
	@Timed
	@Path("/{bookRef}")
	public Response getForMap(@PathParam("bookRef") String bookRef) {
		try {
			
			Book book = bookDao.findByRef("/books/"+bookRef);
				
			return Response.status(200).entity(Utilities.toJson(book)).build();
		} catch(Exception e) {
			e.printStackTrace();
			return Response.status(300).entity(new ErrorMessage("failed", e.getMessage())).build();			
		}
	}
	
}
