package edu.brown.cs.food;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import edu.brown.cs.map.BoundingBox;
import edu.brown.cs.map.LatLng;

public class YelpRestaurantFinderTest {
  YelpRestaurantFinder yelp = new YelpRestaurantFinder();

  @Test
  public void testFindRestaurants() {
    BoundingBox bb = new BoundingBox(
        new LatLng(37.803, -122.271),
        new LatLng(37.804, -122.270));
    List<Restaurant> restaurants = yelp.findRestaurants(bb);
    System.out.println(restaurants.size());
    for(Restaurant r : restaurants) {
      System.out.println(r.getName());
    }
  }

  @Test
  public void testDetailedRestaurantForID() {
    DetailedRestaurant dRest = yelp.detailedRestaurantForID(
        "golden-lotus-vegetarian-restaurant-oakland");
    assertEquals("golden-lotus-vegetarian-restaurant-oakland", dRest.getId());
    assertTrue(dRest.getReviews().size() > 0);
  }
}
