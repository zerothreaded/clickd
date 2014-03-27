package com.clickd.server.services.criteria;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;

import com.clickd.server.dao.CriteriaDao;
import com.clickd.server.dao.UserDao;
import com.clickd.server.model.Criteria;
import com.clickd.server.model.ErrorMessage;
import com.clickd.server.utilities.Utilities;
import com.yammer.metrics.annotation.Timed;

@Path("/criterias")
@Produces(MediaType.APPLICATION_JSON)
public class CriteriaResource {

	@Autowired
	private CriteriaDao criteriaDao;
	
	@Autowired
	private UserDao userDao;

	@GET
	@Timed
	@Path("/{criteriaRef}")
	public Response get(@PathParam("criteriaRef") String criteriaRef) {
		try {
			Criteria criteria = criteriaDao.findByRef("/criterias/" + criteriaRef);
			if (criteria != null) {
				return Response.status(200).entity(Utilities.toJson(criteria)).build();
			} else {
				return Response.status(404).build();			
			}
		} catch(Exception e) {
			e.printStackTrace();
			return Response.status(300).entity(new ErrorMessage("failed", e.getMessage())).build();			
		}
	}
	
	@POST
	@Timed
	@Path("/new")
	public Response create(@FormParam("criteria") String criteria, @FormParam("time") String time) {
		try {
				return Response.status(300).entity(new ErrorMessage("failed", "")).build();			
		} catch(Exception e) {
			e.printStackTrace();
			return Response.status(300).entity(new ErrorMessage("failed", e.getMessage())).build();			
		}
	}
	
	@GET
	@Timed
	public Response getAll() {
		try {
			List<Criteria> allDates = criteriaDao.findAll();
			return Response.status(200).entity(Utilities.toJson(allDates)).build();
		} catch(Exception e) {
			e.printStackTrace();
			return Response.status(300).entity(new ErrorMessage("failed", e.getMessage())).build();			
		}
	}

}
