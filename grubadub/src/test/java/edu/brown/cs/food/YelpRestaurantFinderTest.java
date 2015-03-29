package edu.brown.cs.food;

import static org.junit.Assert.*;

import org.junit.Test;

public class YelpRestaurantFinderTest {
  YelpRestaurantFinder yelp = new YelpRestaurantFinder();
  @Test
  public void test() {
    DetailedRestaurant dRest = yelp.detailedRestaurantForID(
        "golden-lotus-vegetarian-restaurant-oakland");
    assertEquals("golden-lotus-vegetarian-restaurant-oakland", dRest.getId());
    assertTrue(dRest.getReviews().size() > 0);
  }
}
