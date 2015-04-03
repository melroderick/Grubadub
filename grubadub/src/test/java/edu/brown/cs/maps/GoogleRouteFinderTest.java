package edu.brown.cs.maps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import edu.brown.cs.map.GoogleRoute;
import edu.brown.cs.map.GoogleRouteFinder;
import edu.brown.cs.map.LatLng;

public class GoogleRouteFinderTest {

  private GoogleRouteFinder grf;

  @Before
  public void setUp() throws Exception {
    grf = new GoogleRouteFinder();
  }

  @Test
  public void testGetRoute() {
    LatLng providence = new LatLng(41.8236, -71.4222);
    GoogleRoute r = grf.getRoute(providence, "Boston, MA");
    assertTrue(r != null);
    assertTrue(r.distanceFrom(providence) > 0);
    assertTrue(r.locIn(10) != null);
    assertTrue(r.pointsAlong(1, 10) != null);
    assertTrue(r.routeTime() > 30 && r.routeTime() < 90);
  }

  @Test
  public void testExtraTime() {
    // Times without traffic
    int gMapsSea2Port = 161;
    int gMapsSea2Bellevue2Port = 186;
    LatLng seattle = new LatLng(47.6097, -122.3331);
    assertEquals(
        gMapsSea2Port - gMapsSea2Bellevue2Port,
        grf.extraTime(seattle, "Bellevue, WA", "Portland, OR"),
        5);


  }

}
