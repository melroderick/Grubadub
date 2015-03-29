package edu.brown.cs.food;

import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import edu.brown.cs.map.BoundingBox;

public class YelpRestaurantFinder implements RestaurantFinder {
  private static YelpAPI YELP_API = new YelpAPI();
  
  public YelpRestaurantFinder() {
    
  }

  @Override
  public List<Restaurant> findRestaurants(BoundingBox bb) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public DetailedRestaurant detailedRestaurantForID(String id) {
    String searchResponseJSON = YELP_API.searchByBusinessId(id);
    System.out.println(searchResponseJSON);

    JSONParser parser = new JSONParser();
    JSONObject restaurantObject = null;
    try {
      restaurantObject = (JSONObject) parser.parse(searchResponseJSON);
    } catch (ParseException pe) {
      // TODO: ERROR response
      pe.printStackTrace();
      return null;
    }
    
    DetailedRestaurant dRest = new DetailedRestaurant(restaurantObject);
    return dRest;
  }
}
