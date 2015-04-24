package edu.brown.cs.grubadub;

import java.util.ArrayList;
import java.util.List;

import edu.brown.cs.food.DetailedRestaurant;
import edu.brown.cs.food.Restaurant;
import edu.brown.cs.food.RestaurantFinder;
import edu.brown.cs.map.BoundingBox;
import edu.brown.cs.map.LatLng;
import edu.brown.cs.map.RestaurantOnRoute;
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

  public List<RestaurantOnRoute> getRestaurants(LatLng curLoc,
      String destination, int minutes) {

    // Get the route from the current location to the destination
    Route route = getRoute(curLoc, destination);
    // Get the Bounding box from the route where the user will be in 'minutes'
    BoundingBox bb = route.getBoundingBox(minutes, minutes + 10);
    // Find all restaurants within the bounding box
    List<Restaurant> restaurants = findRestaurants(bb);

    List<RestaurantOnRoute> restaurantsOnRoute = new ArrayList<RestaurantOnRoute>(
        restaurants.size());
    for (Restaurant r : restaurants) {
      RestaurantOnRoute restaurantOnRoute = new RestaurantOnRoute(r, route);
      restaurantsOnRoute.add(restaurantOnRoute);
    }

    return restaurantsOnRoute;
  }

  final static int NUMBER_OF_YELP_RESULTS = 1000;

  protected List<Restaurant> findRestaurants(BoundingBox bb) {
    // TODO: update to allow GUI to request
    // with number of results and/or with offset
    return food.findRestaurants(bb, NUMBER_OF_YELP_RESULTS, 0);
  }

  public DetailedRestaurant getRestaurantDetails(String id) {
    return food.detailedRestaurantForID(id);
  }

  public int getExtraTime(LatLng loc, String waypoint, String destination) {
    return map.extraTime(loc, waypoint, destination);
  }
}
