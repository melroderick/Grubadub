package edu.brown.cs.grubadub;

import java.util.List;

import edu.brown.cs.food.DetailedRestaurant;
import edu.brown.cs.food.Restaurant;
import edu.brown.cs.food.RestaurantFinder;
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

  public List<Restaurant> getRestaurants(LatLng curLoc, String destination, int minutes) {
    Route route = map.getRoute(curLoc, destination);
    List<Restaurant> restaurants = food.findRestaurants(null);

    return restaurants;
  }

  private DetailedRestaurant getRestaurantDetails(String id) {
    return null;
  }

  private int extraTime(LatLng curLoc, String waypoint, String destination) {
    return -1;
  }
}
