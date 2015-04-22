package edu.brown.cs.food;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    List<Restaurant> restaurants = yelp.findRestaurants(bb, 20, 0);
    boolean containsGoldenLotus = false;
    for (Restaurant r : restaurants) {
      wellFormedRestaurant(r);
      if (r.getId().equals("golden-lotus-vegetarian-restaurant-oakland")) {
        containsGoldenLotus = true;
      }
    }
    assertTrue(containsGoldenLotus);

    List<Restaurant> restaurants2 = yelp.findRestaurants(bb, 20, 20);
    assertTrue(restaurants2.size() == 0);
  }

  @Test
  public void testFindRestaurantsWithOffset() {
    BoundingBox bb = new BoundingBox(
        new LatLng(41.7, -71.3),
        new LatLng(41.9, -71.5));
    List<Restaurant> restaurants = yelp.findRestaurants(bb, 100, 0);
    assert(restaurants.size() > 95);
    testUniqueRestaurants(restaurants);
    for (Restaurant r : restaurants) {
      wellFormedRestaurant(r);
    }

    List<Restaurant> restaurants2 = yelp.findRestaurants(bb, 100, 100);
    assertTrue(restaurants2.size() > 95);
    for (Restaurant r : restaurants2) {
      wellFormedRestaurant(r);
    }

    List<Restaurant> restaurants3 = yelp.findRestaurants(bb, 100, 200);
    assertTrue(restaurants3.size() > 95);
    for (Restaurant r : restaurants3) {
      wellFormedRestaurant(r);
    }

    List<Restaurant> allRestaurants = new ArrayList<Restaurant>(restaurants);
    allRestaurants.addAll(restaurants2);
    allRestaurants.addAll(restaurants3);
    testUniqueRestaurants(allRestaurants);
  }

  @Test
  public void testFindManyRestaurants() {
    BoundingBox bb = new BoundingBox(
        new LatLng(41.7, -71.3),
        new LatLng(41.9, -71.5));
    List<Restaurant> restaurants = yelp.findRestaurants(bb, 1000, 0);
    assert(restaurants.size() > 995);
    testUniqueRestaurants(restaurants);
    for (Restaurant r : restaurants) {
      wellFormedRestaurant(r);
    }
  }

  @Test
  public void testDetailedRestaurantForID1() {
    DetailedRestaurant dRest = yelp.detailedRestaurantForID(
        "golden-lotus-vegetarian-restaurant-oakland");
    assertEquals("golden-lotus-vegetarian-restaurant-oakland", dRest.getId());
    assertTrue(dRest.getReviews().size() > 0);
    wellFormedRestaurant(dRest);
  }

  @Test
  public void testDetailedRestaurantForID2() {
    DetailedRestaurant dRest = yelp.detailedRestaurantForID(
        "nice-slice-providence");
    assertEquals("nice-slice-providence", dRest.getId());
    assertTrue(dRest.getReviews().size() > 0);
    wellFormedRestaurant(dRest);
  }

  @Test
  public void testDetailedRestaurantForID3() {
    DetailedRestaurant dRest = yelp.detailedRestaurantForID(
        "saturn-cafe-berkeley");
    assertEquals("saturn-cafe-berkeley", dRest.getId());
    assertTrue(dRest.getReviews().size() > 0);
    wellFormedRestaurant(dRest);
  }

  static void wellFormedRestaurant(Restaurant r) {
    assertTrue(r.getId() != null && r.getId().length() > 0);
    assertTrue(r.getName() != null && r.getName().length() > 0);
    if (r.getImage_url() != null) {
      assertTrue(r.getImage_url().length() > 0);
    }
    assertTrue(r.getCategories() != null
        && r.getCategories().size() > 0
        && r.getCategories().get(0).length() > 1);
    assertTrue(r.getRating() >= 0 && r.getRating() <= 5);
    assertTrue(r.getAddress() != null && r.getAddress().length() > 0);
  }

  static void wellFormedDetailedRestaurant(DetailedRestaurant dr) {
    wellFormedRestaurant(dr);
    assertTrue(dr.getUrl() != null && dr.getUrl().length() > 0);
    assertTrue(dr.getPhoneNumber() != null && dr.getPhoneNumber().length() > 0);
    assertTrue(dr.getReviews() != null);
    for (Review rev : dr.getReviews()) {
      wellFormedReview(rev);
    }
  }

  static void wellFormedReview(Review rev) {
    assertTrue(rev.getId() != null && rev.getId().length() > 0);
    assertTrue(rev.getRating() >= 0 && rev.getRating() <= 5);
    assertTrue(rev.getUserName() != null && rev.getUserName().length() > 0);
    assertTrue(rev.getExcerpt() != null && rev.getExcerpt().length() > 0);
  }

  static void testUniqueRestaurants(List<Restaurant> restaurants) {
    Set<String> ids = new HashSet<String>();
    for (Restaurant r : restaurants) {
      assert(!ids.contains(r.getId()));
      ids.add(r.getId());
    }
  }
}
