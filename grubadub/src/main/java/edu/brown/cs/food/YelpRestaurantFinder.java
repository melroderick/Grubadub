package edu.brown.cs.food;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import edu.brown.cs.map.BoundingBox;

public class YelpRestaurantFinder implements RestaurantFinder {
  final static int RESULTS_PER_PAGE = 20;
  final static int NUMBER_OF_RESULTS = 100;

  private static YelpAPI YELP_API = new YelpAPI();

  public YelpRestaurantFinder() {

  }

  @Override
  public List<Restaurant> findRestaurants(BoundingBox bb, int offset) {
    List<List<Restaurant>> restaurantsLists = new ArrayList<List<Restaurant>>();
    List<FindRestaurantThread> threads = new ArrayList<FindRestaurantThread>();
    for (int o = offset;
        o < NUMBER_OF_RESULTS + offset;
        o += RESULTS_PER_PAGE) {
      List<Restaurant> restaurants = new ArrayList<Restaurant>();
      FindRestaurantThread thread =
          new FindRestaurantThread(bb, o, restaurants);
      thread.start();
      threads.add(thread);
      restaurantsLists.add(restaurants);
    }

    List<Restaurant> allRestaurants = new ArrayList<Restaurant>();
    for (int i = 0; i < threads.size(); i++) {
      FindRestaurantThread thread = threads.get(i);
      List<Restaurant> restaurants = restaurantsLists.get(i);
      try {
        thread.join();
        allRestaurants.addAll(restaurants);
      } catch (InterruptedException e) {
        // TODO: handle error if needed
        e.printStackTrace();
      }
    }

    return allRestaurants;
  }

  @Override
  public DetailedRestaurant detailedRestaurantForID(String id) {
    String searchResponseJSON = YELP_API.searchByBusinessId(id);

    JSONParser parser = new JSONParser();
    JSONObject restaurantObject = null;
    try {
      restaurantObject = (JSONObject) parser.parse(searchResponseJSON);
    } catch (ParseException pe) {
      System.out.println("Error parsing Yelp json result: " +
          searchResponseJSON);
      System.out.println("for DetailedRestaurant of id = " +
          id);
      return null;
    }

    DetailedRestaurant dRest = new DetailedRestaurant(restaurantObject);
    return dRest;
  }

  public class FindRestaurantThread extends Thread{
    private BoundingBox bb;
    private int offset;
    private List<Restaurant> restaurants;

    public FindRestaurantThread(
        BoundingBox bb, int offset, List<Restaurant> restaurants){
      this.bb = bb;
      this.offset = offset;
      this.restaurants = restaurants;
    }

    @Override
    public void run() {
      String searchResponseJSON =
          YELP_API.searchForRestaurantsByBounds(
              bb.getSW().getLat(), bb.getSW().getLng(),
              bb.getNE().getLat(), bb.getNE().getLng(),
              offset);

      JSONParser parser = new JSONParser();
      JSONObject responseObject = null;
      try {
        responseObject = (JSONObject) parser.parse(searchResponseJSON);
      } catch (ParseException pe) {
        System.out.println("Error parsing Yelp json result: " +
            searchResponseJSON);
        System.out.println("for restaurants in BoundingBox: " +
            bb);
      }

      JSONArray jsonRestaurants = (JSONArray) responseObject.get("businesses");
      for (int i = 0; i < jsonRestaurants.size(); i++) {
        restaurants.add(new Restaurant((JSONObject) jsonRestaurants.get(i)));
      }
    }
  }
}
