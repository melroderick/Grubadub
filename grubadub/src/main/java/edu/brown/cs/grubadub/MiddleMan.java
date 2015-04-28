package edu.brown.cs.grubadub;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import edu.brown.cs.food.DetailedRestaurant;
import edu.brown.cs.food.Restaurant;
import edu.brown.cs.food.RestaurantFinder;
import edu.brown.cs.map.BoundingBox;
import edu.brown.cs.map.LatLng;
import edu.brown.cs.map.RestaurantOnRoute;
import edu.brown.cs.map.Route;
import edu.brown.cs.map.RouteFinder;

public class MiddleMan {

  public static double SEARCH_RADIUS = 1.0;
  public static int MIN_NUM_RESTAURANTS = 5;

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
    int minuteRadius = getMinuteRadius(minutes);
    int startMinutes = Math.max(0, minutes - minuteRadius);
    int endMinutes = minutes + minuteRadius;

    BoundingBox bb = route.getBoundingBox(
        startMinutes, endMinutes, SEARCH_RADIUS);
    Set<RestaurantOnRoute> restaurants = getRestaurantsOnRoute(route, bb);

    // Will be entered if there aren't a sufficient number of restaurants
    // near the route. Adds restaurants earlier and later initial bounding
    // box, until there are a sufficient number of restaurants.
    while (restaurants.size() < MIN_NUM_RESTAURANTS
        && startMinutes > 0
        && endMinutes < route.routeTime()) {
      startMinutes = Math.max(0, startMinutes - minuteRadius);
      endMinutes += minuteRadius;

      BoundingBox bb1 = route.getBoundingBox(
          startMinutes, startMinutes + minuteRadius, SEARCH_RADIUS);
      BoundingBox bb2 = route.getBoundingBox(
          endMinutes - minuteRadius, endMinutes, SEARCH_RADIUS);

      restaurants.addAll(getRestaurantsOnRoute(route, bb1));
      restaurants.addAll(getRestaurantsOnRoute(route, bb2));
    }

    return new ArrayList<RestaurantOnRoute>(restaurants);
  }

  private Set<RestaurantOnRoute> getRestaurantsOnRoute(
      Route route, BoundingBox bb) {
    // Find all restaurants within the bounding box
    List<Restaurant> restaurants = findRestaurants(bb);

    System.out.println(restaurants.stream().filter(r -> r.getLatLng() == null).collect(Collectors.toList()));

    Set<RestaurantOnRoute> restaurantsOnRoute = restaurants.stream()
        .filter(r -> r.getLatLng() != null)
        .map(r -> new RestaurantOnRoute(r, route))
        .filter(r -> r.getDistFromRoute() < SEARCH_RADIUS)
        .collect(Collectors.toSet());

    return restaurantsOnRoute;
  }

  private int getMinuteRadius(int minutes) {
    int minimumRadius = 15;
    return Math.min(minimumRadius, (int) (Math.sqrt(minutes) * 2.5));
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
