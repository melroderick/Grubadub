package edu.brown.cs.grubadub;

import edu.brown.cs.map.LatLng;
import edu.brown.cs.map.Route;
import edu.brown.cs.map.RouteFinder;

public class TestRouteFinder implements RouteFinder {

  private LatLng[] points;

  public TestRouteFinder() {
    LatLng a = new LatLng(4.0, 2.0);
    LatLng b = new LatLng(4.5, 4.0);
    LatLng c = new LatLng(1.0, 5.0);
    points = new LatLng[] {a, b, c};
  }
  @Override
  public Route getRoute(LatLng start, String address) {
    return new TestRoute(points.length, points);
  }

  @Override
  public int extraTime(LatLng start, String waypoint, String destination) {
    return -1;
  }

  @Override
  public int timeToLoc(LatLng start, LatLng end) {
    return (int) (start.distanceFrom(end) * 10);
  }

  @Override
  public Route getRoute(String startAddress, String destinationAddress) {
    return new TestRoute(points.length, points);
  }
}
