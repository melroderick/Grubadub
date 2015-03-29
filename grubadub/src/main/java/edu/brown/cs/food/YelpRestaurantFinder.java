package edu.brown.cs.food;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
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
    String searchResponseJSON =
        YELP_API.searchForRestaurantsByBounds(
            bb.getSW().getLat(), bb.getSW().getLng(),
            bb.getNE().getLat(), bb.getNE().getLng());
    System.out.println(searchResponseJSON);

    JSONParser parser = new JSONParser();
    JSONObject responseObject = null;
    try {
      responseObject = (JSONObject) parser.parse(searchResponseJSON);
    } catch (ParseException pe) {
      // TODO: ERROR response
      pe.printStackTrace();
      return null;
    }

    JSONArray jsonRestaurants = (JSONArray) responseObject.get("businesses");
    List<Restaurant> restaurants = new ArrayList<Restaurant>();
    for (int i = 0; i < jsonRestaurants.size(); i++) {
      restaurants.add(new Restaurant((JSONObject) jsonRestaurants.get(i)));
    }

    return restaurants;
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
