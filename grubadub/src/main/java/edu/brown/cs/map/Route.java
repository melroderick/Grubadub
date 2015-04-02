package edu.brown.cs.map;

import java.util.List;

public interface Route {

  int routeTime();
  double distanceFrom(LatLng loc);
  List<LatLng> pointsAlong(int start, int end, double spacedBy);

}
