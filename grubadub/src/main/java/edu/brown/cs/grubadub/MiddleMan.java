package edu.brown.cs.grubadub;

import java.util.Collections;
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

    // Get the route from the current location to the destination
    Route route = getRoute(curLoc, destination);
    // Get the Bounding box from the route where the user will be in 'minutes'
    BoundingBox bb = route.getBoundingBox(minutes, minutes + 10);
    // Find all restaurants within the bounding box
    List<Restaurant> restaurants = findRestaurants(bb);

    Collections.sort(restaurants, (r1, r2) -> Double.compare(
        curLoc.distanceFrom(r1.getLatLng()),
        curLoc.distanceFrom(r2.getLatLng())));

    return restaurants;
  }

  protected List<Restaurant> findRestaurants(BoundingBox bb) {
    // TODO: update to allow server to request with offset
    return food.findRestaurants(bb, 0);
  }

  public DetailedRestaurant getRestaurantDetails(String id) {
    return food.detailedRestaurantForID(id);
  }

  public int getExtraTime(LatLng loc, String waypoint, String destination) {
    return map.extraTime(loc, waypoint, destination);
  }
}
