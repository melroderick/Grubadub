package edu.brown.cs.map;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import edu.brown.cs.food.Restaurant;

public class RestaurantOnRoute {

  private final Restaurant restaurant;
  private final int timeToRestaurant;
  private final double distFromRoute;

  /**
   * Constructs a RestaurantOnRoute from a Restaurant and a Route.
   *
   * @param rest
   *          The restaurant.
   * @param route
   *          The route. Used to find the restaurant's distance from the route
   *          in miles and in how long the route takes to reach its closest
   *          location to the restaurant.
   */
  public RestaurantOnRoute(Restaurant rest, Route route) {
    this(rest, route.nearestTimePlace(rest.getLatLng()));
  }

  private RestaurantOnRoute(Restaurant rest, TimePlace tp) {
    this.restaurant = rest;

    timeToRestaurant = tp.timeInMinutes();
    distFromRoute = tp.distanceFrom(rest.getLatLng());
  }

  public Restaurant getRestaurant() {
    return restaurant; // This is cool because Restaurant is immutable
  }

  /**
   * Gets the time in minutes to the location on the route nearest to the
   * restaurant.
   *
   * @return
   */
  public int getTimeToRestaurant() {
    return timeToRestaurant;
  }

  /**
   * Gets the distance from the route in miles.
   * 
   * @return
   */
  public double getDistFromRoute() {
    return distFromRoute;
  }

  /**
   * Class that serializes RestaurantOnRoute into JSON using Google's GSON
   * library.
   *
   * @author Max
   */
  public static class RoRSerializer implements
      JsonSerializer<RestaurantOnRoute> {
    @Override
    public JsonElement serialize(RestaurantOnRoute ror, Type t,
        JsonSerializationContext context) {
      JsonObject obj = new JsonObject();

      obj.addProperty("id", ror.getRestaurant().getId());
      obj.addProperty("name", ror.getRestaurant().getName());
      obj.addProperty("rating", ror.getRestaurant().getRating());
      obj.addProperty("address", ror.getRestaurant().getAddress());
      obj.addProperty("image_url", ror.getRestaurant().getImage_url());
      obj.addProperty("timeToRestaurant", ror.getTimeToRestaurant());
      obj.addProperty("distFromRoute", ror.getDistFromRoute());

      obj.add("categories",
          context.serialize(ror.getRestaurant().getCategories()));
      obj.add("latLng", context.serialize(ror.getRestaurant().getLatLng()));

      return obj;
    }
  }

}
