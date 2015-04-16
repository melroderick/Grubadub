package edu.brown.cs.maps;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import edu.brown.cs.map.GoogleRouteFinder;
import edu.brown.cs.map.LatLng;
import edu.brown.cs.map.Route;

public class GoogleRouteTest {

  private Route portland2Seatttle;
  private Route portland2Providence;

  @Before
  public void setUp() {
    LatLng seattle = new LatLng(47.6097, -122.3331);
    LatLng portland = new LatLng(45.52, -122.6819);
    portland2Seatttle = (new GoogleRouteFinder()).getRoute(portland,
        "Seattle, WA");
    portland2Providence = (new GoogleRouteFinder()).getRoute(portland,
        "Providence, RI");
  }

  @Test
  public void testDistanceFrom() {
    LatLng kelso = new LatLng(46.161537, -122.892357);
    LatLng guessGMapsPoint = new LatLng(46.161296, -122.902708);

    assertEquals(portland2Seatttle.distanceFrom(kelso), .5, .05);
    assertEquals(portland2Seatttle.distanceFrom(kelso),
        kelso.distanceFrom(guessGMapsPoint), .05);
  }

  /*
   * @Test public void testDistanceFromTime() { Long t1 = System.nanoTime();
   * LatLng kelso = new LatLng(46.161537, -122.892357);
   * System.out.println(port2Prov.distanceFrom(kelso));
   * System.out.println((System.nanoTime() - t1) / 1000000000.0); }
   */

}
