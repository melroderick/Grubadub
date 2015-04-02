package edu.brown.cs.map;

public interface RouteFinder {
  Route getRoute(LatLng start, String address);
  int extraTime(LatLng start, String waypoint, String destination);
  int timeToLoc(LatLng start, LatLng end);
}
