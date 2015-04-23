package edu.brown.cs.map;

import edu.brown.cs.food.Restaurant;

public class RestaurantOnRoute {

  private final Restaurant r;
  private final int timeToRestaurant;
  private final double distFromRoute;

  /** Constructs a RestaurantOnRoute from a Restaurant and a Route.
   * @param rest The restaurant.
   * @param route The route. Used to find the restaurant's distance from
   * the route in miles and in how long the route takes to reach
   * its closest location to the restaurant.
   */
  public RestaurantOnRoute(Restaurant rest, Route route) {
    this(rest, route.nearestTimePlace(rest.getLatLng()));
  }

  private RestaurantOnRoute(Restaurant rest, TimePlace tp) {
    this.r = rest;

    timeToRestaurant = tp.timeInMinutes();
    distFromRoute = tp.distanceFrom(rest.getLatLng());
  }

  public Restaurant getRestaurant() {
    return r; // This is cool because Restaurant is immutable
  }

  /** Gets the time in minutes to the location on the route
   * nearest to the restaurant.
   * @return
   */
  public int getTimeToRestaurant() {
    return timeToRestaurant;
  }

  public double getDistFromRoute() {
    return distFromRoute;
  }

}
