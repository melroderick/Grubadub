package edu.brown.cs.grubadub;

import java.util.Arrays;
import java.util.List;

import edu.brown.cs.map.BoundingBox;
import edu.brown.cs.map.LatLng;
import edu.brown.cs.map.Route;
import edu.brown.cs.map.TimePlace;

public class TestRoute implements Route {

  private int totalTime;
  private LatLng[] points; // Each point is one minutes apart

  public TestRoute(int totalTime, LatLng[] points) {
    this.totalTime = totalTime;
    this.points = points;
  }
  @Override
  public int routeTime() {
    return totalTime;
  }

  @Override
  public double distanceFrom(LatLng loc) {
    double min = Double.MAX_VALUE;
    for(LatLng p : points) {
      double dist = p.distanceFrom(loc);
      min = (min > dist) ? dist : min;
    }
    return min;
  }

  @Override
  public LatLng locIn(int minutes) {
    return points[minutes - 1];
  }

  @Override
  public List<LatLng> pointsAlong(int start, int end) {
    LatLng[] subpoints = Arrays.copyOfRange(points, start - 1, end - 1);
    return Arrays.asList(subpoints);
  }

  @Override
  public BoundingBox getBoundingBox(int start, int end) {
    LatLng point = points[start - 1];
    LatLng sw = new LatLng(point.getLat() - 1.5, point.getLng() - 1.5);
    LatLng ne = new LatLng(point.getLat() + 1.5, point.getLng() + 1.5);
    return new BoundingBox(sw, ne);
  }
  @Override
  public TimePlace nearestTimePlace(LatLng loc) {
    // TODO Auto-generated method stub
    return null;
  }
  @Override
  public BoundingBox getBoundingBox(int start, int end, double radius) {
    // TODO Auto-generated method stub
    return null;
  }
}
