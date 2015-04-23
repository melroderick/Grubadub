package edu.brown.cs.maps;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import edu.brown.cs.map.LatLng;

public class LatLngTest {

  @Before
  public void setUp() throws Exception {

  }

  @Test
  public void moveNorthSouthTest() {
    LatLng loc = new LatLng(47.61, 122.33);
    assertEquals(loc.moveNorth(0.0), loc);

    LatLng north = loc.moveNorth(100.0);
    assertEquals(north.distanceFrom(loc), 100.0, .001);

    LatLng closeToOriginal = north.moveSouth(100.0);
    assertEquals(loc.distanceFrom(closeToOriginal), 0, .001);
  }

  @Test
  public void moveEastWestTest() {
    LatLng loc = new LatLng(47.61, 122.33);
    assertEquals(loc.moveWest(0.0), loc);

    LatLng west = loc.moveWest(100.0);
    assertEquals(west.distanceFrom(loc), 100.0, .01);

    LatLng closeToOriginal = west.moveEast(100.0);
    assertEquals(loc.distanceFrom(closeToOriginal), 0, .01);
  }

}
