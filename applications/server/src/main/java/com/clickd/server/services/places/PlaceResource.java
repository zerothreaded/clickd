package com.clickd.server.services.places;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;

import com.clickd.server.dao.CheckinDao;
import com.clickd.server.dao.PlaceDao;
import com.clickd.server.dao.UserDao;
import com.clickd.server.model.Checkin;
import com.clickd.server.model.ErrorMessage;
import com.clickd.server.model.Link;
import com.clickd.server.model.Place;
import com.clickd.server.model.User;
import com.clickd.server.utilities.Utilities;
import com.yammer.metrics.annotation.Timed;

@Path("/places")
@Produces(MediaType.APPLICATION_JSON)
public class PlaceResource {
	
	@Autowired
	private PlaceDao placeDao;

	@Autowired
	private CheckinDao checkinDao;
	
	@Autowired
	private UserDao userDao;
	
	@GET
	@Timed
	public Response getAll() {
		try {
			List<Place> allPlaces = placeDao.findAll();
			return Response.status(200).entity(Utilities.toJson(allPlaces)).build();
		} catch(Exception e) {
			e.printStackTrace();
			return Response.status(300).entity(new ErrorMessage("failed", e.getMessage())).build();			
		}
	}
	
	public class MapImage {
	    // url: '/profile-img/users/751545291.jpg',
		public String url;
		
	    // This marker is 20 pixels wide by 32 pixels tall.
	    //size: new google.maps.Size(50, 50),
	    public String size;
		
		//scaledSize : new google.maps.Size(50, 50),
	    public String scaledSize;
	    
	    // The origin for this image is 0,0.
	    // origin: new google.maps.Point(0,0),
	    public String origin;
	    
	    // The anchor for this image is the base of the flagpole at 0,32.
	    // anchor: new google.maps.Point(-40, 60)
	    public String anchor;
	    
	}
	
	public class MapMarker {
		// var myLatlng = new google.maps.LatLng(51.537812325599, -0.14480018556184);
		public String position;
		
		public String title;
		
		public MapImage icon;
	
	}

	
	@GET
	@Timed
	@Path("/map/{currentSelection}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCheckinsForMap(@QueryParam("currentSelection") String currentSelection) {
		System.out.println("\n\ngetCheckinsForMap() called with [" + currentSelection + "]");
		try {
			List<Checkin> allCheckins = checkinDao.findAll();
			List<Checkin> results = new ArrayList<Checkin>();
			for (Checkin checkin : allCheckins) {
				Link userLink = checkin.getLinkByName("user");
				User user = userDao.findByRef(userLink.getHref());
				Link placeLink = checkin.getLinkByName("place");
				Place place = placeDao.findByRef(placeLink.getHref());
				// DONT RETURN EMPTY PLACES
				if (place != null) {
					// EMBED THE USER AND PLACE - TUT TUT TUT!!!!
					checkin.get_Embedded().put("the-user", user);
					checkin.get_Embedded().put("the-place", place);
					results.add(checkin);
				}
			}
			results = results.subList(0, 50);
			return Response.status(200).entity(Utilities.toJson(results)).build();
		} catch(Exception e) {
			e.printStackTrace();
			return Response.status(300).entity(new ErrorMessage("failed", e.getMessage())).build();			
		}
	}
	
}
