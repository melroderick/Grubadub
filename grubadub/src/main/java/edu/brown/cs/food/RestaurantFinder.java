package edu.brown.cs.food;

import java.util.List;

import edu.brown.cs.map.BoundingBox;

public interface RestaurantFinder {
  
  /**
   * Returns a {@code List} of {@code Restaurant}s contained in
   * a specified bounding box.
   *
   * @param bb  the {@code BoundingBox} of the search.
   * @param offset  the result offset for the search.
   * i.e. offset = 0 will give you the first results,
   * while offset = 20 will give you the results after
   * the first 20.
   * @return a {@code List} of {@code Restaurant}s contained in
   * a specified bounding box.
   */
  List<Restaurant> findRestaurants(BoundingBox bb, int offset);
  
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
