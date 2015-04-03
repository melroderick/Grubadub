package edu.brown.cs.map;

public interface RouteFinder {

  /** Gets a route between a start location and an end andress
   * @param start The start location of the route.
   * @param address The human-readable address of the route.
   * @return A Route object giving the route between start and address.
   */
  Route getRoute(LatLng start, String address);

  int extraTime(LatLng start, String waypoint, String destination);
  int timeToLoc(LatLng start, LatLng end);
}
