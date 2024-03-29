package edu.brown.cs.maps;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import edu.brown.cs.map.BoundingBox;
import edu.brown.cs.map.GoogleRouteFinder;
import edu.brown.cs.map.LatLng;
import edu.brown.cs.map.Route;
import edu.brown.cs.map.RouteFinder;

public class GoogleRouteTest {

  private LatLng seattle = new LatLng(47.6097, -122.3331);
  private LatLng portland = new LatLng(45.52, -122.6819);
  private LatLng wooley = new LatLng(41.829807041, -71.401411071);

  private Route portland2Seattle =
      (new GoogleRouteFinder()).getRoute(portland, "Seattle, WA");
  private Route portland2Providence =
      (new GoogleRouteFinder()).getRoute(portland,
          "Providence, RI");

  @Before
  public void setUp() {

  }

  @Test
  public void onePointRoute() {
    GoogleRouteFinder grf = new GoogleRouteFinder();
    String city = "Seattle, WA";
    Route r = grf.getRoute(city, city);
    assertEquals(r.routeTime(), 0);
    assertEquals(r.locIn(0), r.locIn(40));
  }

  @Test
  public void testDistanceFrom() {
    LatLng kelso = new LatLng(46.161537, -122.892357);
    LatLng guessGMapsPoint = new LatLng(46.161296, -122.902708);

    assertEquals(portland2Seattle.distanceFrom(kelso), .5, .05);
    assertEquals(portland2Seattle.distanceFrom(kelso),
        kelso.distanceFrom(guessGMapsPoint), .05);
  }

  @Test
  public void testGetBoundingBox() {
    BoundingBox bb1 = portland2Seattle.getBoundingBox(40, 60);
    BoundingBox bb2 = portland2Providence.getBoundingBox(400, 500);
  }

  @Test
  public void testGetLocIn() {
    RouteFinder grf = new GoogleRouteFinder();
    LatLng loc = portland2Seattle.locIn(20);
    assertEquals(20, grf.timeToLoc(portland, loc), 3);

    loc = portland2Seattle.locIn(100);
    assertEquals(100, grf.timeToLoc(portland, loc), 3);

    loc = portland2Providence.locIn(400);
    assertEquals(400, grf.timeToLoc(portland, loc), 15);
  }
}
