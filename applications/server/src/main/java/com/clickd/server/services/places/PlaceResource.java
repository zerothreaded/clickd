package com.clickd.server.services.places;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;

import com.clickd.server.dao.PlaceDao;
import com.clickd.server.model.ErrorMessage;
import com.clickd.server.model.Place;
import com.clickd.server.utilities.Utilities;
import com.yammer.metrics.annotation.Timed;

@Path("/places")
@Produces(MediaType.APPLICATION_JSON)
public class PlaceResource {
	
	@Autowired
	private PlaceDao placeDao;

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
	@Path("/map")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getForMap() {
		try {
			List<Place> allPlaces = placeDao.findAll();
			
			for (Place place : allPlaces) {
				// Create marker for place
				String latitude = place.getLatitude();
				String longitude = place.getLongitude();
				String name = place.getName();
				
				MapMarker placeMarker = new MapMarker();
				placeMarker.title = name;
				placeMarker.position = latitude + "," + longitude;
				
				MapImage placeImage = new MapImage();
				placeImage.url = "/profile-img/users/751545291.jpg";
				placeImage.size = "size(50,50)";
				placeImage.scaledSize = "size(50,50)";
				placeImage.origin = "point(0,0)";
				placeImage.anchor = "point(0,0)";
				
				placeMarker.icon = placeImage;
				
				System.out.println(Utilities.toJson(placeMarker));
				
				int x  =1;
				
				
			}
			return Response.status(200).entity(Utilities.toJson(allPlaces)).build();
		} catch(Exception e) {
			e.printStackTrace();
			return Response.status(300).entity(new ErrorMessage("failed", e.getMessage())).build();			
		}
	}
	
}
