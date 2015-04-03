package edu.brown.cs.grubadub;

import java.util.List;

import edu.brown.cs.food.DetailedRestaurant;
import edu.brown.cs.food.Restaurant;
import edu.brown.cs.food.RestaurantFinder;
import edu.brown.cs.map.BoundingBox;
import edu.brown.cs.map.LatLng;
import edu.brown.cs.map.Route;
import edu.brown.cs.map.RouteFinder;

public class MiddleMan {
  private RestaurantFinder food;
  private RouteFinder map;

  public MiddleMan(RestaurantFinder food, RouteFinder map) {
    this.food = food;
    this.map = map;
  }

  protected Route getRoute(LatLng curLoc, String destination) {
    return map.getRoute(curLoc, destination);
  }


  public List<Restaurant> getRestaurants(LatLng curLoc, String destination, int minutes) {
    Route route = getRoute(curLoc, destination);

    BoundingBox bb = null;
    List<Restaurant> restaurants = findRestaurants(bb);

    return restaurants;
  }

  protected List<Restaurant> findRestaurants(BoundingBox bb) {
    return food.findRestaurants(bb);
  }

  public DetailedRestaurant getRestaurantDetails(String id) {
    return food.detailedRestaurantForID(id);
  }

  public int getExtraTime(LatLng loc, String waypoint, String destination) {
    return map.extraTime(loc, waypoint, destination);
  }
}
