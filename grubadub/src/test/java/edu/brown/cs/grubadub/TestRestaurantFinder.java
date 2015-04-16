package edu.brown.cs.grubadub;

import java.util.ArrayList;
import java.util.List;

import edu.brown.cs.food.DetailedRestaurant;
import edu.brown.cs.food.Restaurant;
import edu.brown.cs.food.RestaurantFinder;
import edu.brown.cs.map.BoundingBox;

public class TestRestaurantFinder implements RestaurantFinder {
  private Restaurant[] restaurants;
  private DetailedRestaurant[] details;

  public TestRestaurantFinder(
      Restaurant[] seedRestaurants,
      DetailedRestaurant[] seedDetails) {
     restaurants = seedRestaurants;
     details = seedDetails;
  }

  @Override
  public List<Restaurant> findRestaurants(BoundingBox bb, int offset) {
    List<Restaurant> found = new ArrayList<>();

    for (Restaurant r : restaurants) {
      if (bb.contains(r.getLatLng())) {
        found.add(r);
      }
    }
    return found;
  }

  @Override
  public DetailedRestaurant detailedRestaurantForID(String id) {
    for (DetailedRestaurant d : details) {
      if (d.getId().equals(id)) {
        return d;
      }
    }
    throw new IllegalArgumentException("Restaurant ID not found.");
  }
}
