package edu.brown.cs.grubadub;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.brown.cs.food.Restaurant;
import edu.brown.cs.food.RestaurantFinder;
import edu.brown.cs.food.YelpRestaurantFinder;
import edu.brown.cs.map.BoundingBox;
import edu.brown.cs.map.GoogleRouteFinder;
import edu.brown.cs.map.LatLng;
import edu.brown.cs.map.RestaurantOnRoute;
import edu.brown.cs.map.Route;
import edu.brown.cs.map.RouteFinder;

public class MiddleManTest {
  private static MiddleMan middleman;

  private static Restaurant chipotle;
  private static Restaurant fiveGuys;
  private static Restaurant subway;
  private static Restaurant soban;

  @BeforeClass
  public static void init() {
    List<String> c = new ArrayList<>();
    c.add("Fast Casual");

    chipotle = new Restaurant("0", "Chipotle", c, new LatLng(0.1, 0.1), 5, 1, "A", "", "+1-415-908-3801");
    fiveGuys = new Restaurant("1", "Five Guys", c, new LatLng(5.0, 5.0), 4, 7, "B", "", "+1-415-908-3801");
    subway = new Restaurant("2", "Subway", c, new LatLng(3.2, 2.7), 3, 9, "C", "", "+1-415-908-3801");
    soban = new Restaurant("3", "Soban", c, new LatLng(1.2, 4.3), 4.3f, 40, "D", "", "+1-415-908-3801");
    Restaurant[] restaurants = new Restaurant[] {chipotle, fiveGuys, subway, soban};


    RestaurantFinder food = new TestRestaurantFinder(restaurants, null);
    RouteFinder map = new TestRouteFinder();
    middleman = new MiddleMan(food, map);
  }

  // Restaurant tests
  @Test
  public void findAllRestraunts() {
    LatLng sw = new LatLng(0.0, 0.0);
    LatLng ne = new LatLng(5.1, 5.1);
    BoundingBox bb = new BoundingBox(sw, ne);
    List<Restaurant> result = middleman.findRestaurants(bb);
    Restaurant[] actual =  result.toArray(new Restaurant[result.size()]);
    Restaurant[] expected = new Restaurant[] { chipotle, fiveGuys, subway, soban };

    Arrays.sort(expected);
    Arrays.sort(actual);

    assertTrue(Arrays.equals(expected, actual));
  }

  @Test
  public void someRestaurantsFound() {
    LatLng sw = new LatLng(1.1, 2.1);
    LatLng ne = new LatLng(3.5, 4.5);
    BoundingBox bb = new BoundingBox(sw, ne);
    List<Restaurant> result = middleman.findRestaurants(bb);
    Restaurant[] actual =  result.toArray(new Restaurant[result.size()]);
    Restaurant[] expected = new Restaurant[] {subway, soban};

    Arrays.sort(expected);
    Arrays.sort(actual);

    assertTrue(Arrays.equals(expected, actual));
  }

  @Test
  public void noRestaurantsFound() {
    LatLng sw = new LatLng(0.0, 0.0);
    LatLng ne = new LatLng(0.05, 0.05);
    BoundingBox bb = new BoundingBox(sw, ne);
    List<Restaurant> result = middleman.findRestaurants(bb);
    Restaurant[] actual =  result.toArray(new Restaurant[result.size()]);
    Restaurant[] expected = new Restaurant[] {};

    Arrays.sort(expected);
    Arrays.sort(actual);
    assertTrue(actual.length == 0);
    assertTrue(Arrays.equals(expected, actual));
  }

  @Test
  public void restaurantSearchExpands() {
    LatLng seattle = new LatLng(47.6097, -122.3331);
    LatLng portland = new LatLng(45.52, -122.6819);
    LatLng wooley = new LatLng(41.829807041, -71.401411071);

    MiddleMan googleMiddleMan = new MiddleMan(new YelpRestaurantFinder(), new GoogleRouteFinder());
    googleMiddleMan.MIN_NUM_RESTAURANTS = 500;
    googleMiddleMan.SEARCH_RADIUS = .001; // small so min num won't be reached

    // Test that middleman stops when it reaches beginning and end of route.
    assertTrue(googleMiddleMan.getRestaurants(seattle, "portland, OR", 30).size() < 500);

    // Test that middleman expands properly.
    googleMiddleMan.SEARCH_RADIUS = 1.0;
    assertTrue(googleMiddleMan.getRestaurants(seattle, "portland, OR", 30).size() > 500);
  }

  @Test
  public void eatTimeExceedsRouteTime() {
    LatLng seattle = new LatLng(47.6097, -122.3331);
    GoogleRouteFinder grf = new GoogleRouteFinder();
    YelpRestaurantFinder yrf = new YelpRestaurantFinder();
    MiddleMan googleMiddleMan = new MiddleMan(yrf, grf);

    Route sea2Port = grf.getRoute(seattle, "Portland, OR");
    int routeTime = sea2Port.routeTime();

    int insaneTime = sea2Port.routeTime() * 3;
    List<RestaurantOnRoute> test = googleMiddleMan.getRestaurants(
        seattle, "Portland, OR", insaneTime);
    List<RestaurantOnRoute> expected = googleMiddleMan.getRestaurants(
        seattle, "Portland, OR", routeTime);
    // TODO: Why is test not subset of expected?

  }

  // Map tests
// TODO: fix map tests
//  @Test
//  public void fullTestFromA() {
//    List<Restaurant> result = middleman.getRestaurants(new LatLng(0.0, 0.0), "a", 1);
//    Restaurant[] actual =  result.toArray(new Restaurant[result.size()]);
//    Restaurant[] expected = new Restaurant[] {subway};
//    assertTrue(Arrays.equals(expected, actual));
//  }
//  @Test
//  public void fullTestFromB() {
//    List<Restaurant> result = middleman.getRestaurants(new LatLng(0.0, 0.0), "b", 2);
//    Restaurant[] actual =  result.toArray(new Restaurant[result.size()]);
//    Restaurant[] expected = new Restaurant[] {subway, fiveGuys};
//    assertTrue(Arrays.equals(expected, actual));
//  }
//
//  @Test
//  public void fullTestFromC() {
//    List<Restaurant> result = middleman.getRestaurants(new LatLng(0.0, 0.0), "c", 3);
//    Restaurant[] actual =  result.toArray(new Restaurant[result.size()]);
//    Restaurant[] expected = new Restaurant[] {soban};
//    assertTrue(Arrays.equals(expected, actual));
//  }
}
