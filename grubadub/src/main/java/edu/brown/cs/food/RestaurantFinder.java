package edu.brown.cs.food;

import java.util.List;

import edu.brown.cs.map.BoundingBox;

public interface RestaurantFinder {
  
  /**
   * Returns a {@code List} of {@code Restaurant}s contained in
   * a specified bounding box.
   *
   * @param bb  the {@code BoundingBox} of the search.
   * @return a {@code List} of {@code Restaurant}s contained in
   * a specified bounding box.
   */
  List<Restaurant> findRestaurants(BoundingBox bb);
  
  /**
   * Returns a {@code DetailedRestaurant}s with a specified
   * unique String id.
   *
   * @param id  the {@code String} identifier of the {@code Restaurant}
   * @return a {@code DetailedRestaurant}s with a specified
   * unique String id.
   */
  DetailedRestaurant detailedRestaurantForID(String id);
}
