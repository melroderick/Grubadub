package edu.brown.cs.grubadub;

import java.util.List;

import edu.brown.cs.map.BoundingBox;
import edu.brown.cs.map.LatLng;
import edu.brown.cs.map.Route;

public class TestRoute implements Route {

  @Override
  public int routeTime() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public double distanceFrom(LatLng loc) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public LatLng locIn(int minutes) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<LatLng> pointsAlong(int start, int end) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public BoundingBox getBoundingBox(int start, int end) {
    // TODO Auto-generated method stub
    return null;
  }
}
