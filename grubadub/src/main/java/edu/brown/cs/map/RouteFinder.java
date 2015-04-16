package edu.brown.cs.map;

public interface RouteFinder {

  /** Gets a route between a start location and an end andress
   * @param start The start location of the route.
   * @param address The human-readable address of the route.
   * @return A Route object giving the route between start and address.
   */
  Route getRoute(LatLng start, String address);

  /** Gets the additional time accrued by adding a waypoint.
   * @param start The start location.
   * @param waypoint The waypoint address.
   * @param destination The final destination address.
   * @return The time for the route including the waypoint subtracted by the time
   * for the route directly from start to destination.
   */
  int extraTime(LatLng start, String waypoint, String destination);

  /** The time of the route between two locations.
   * @param start The start location.
   * @param end The end location.
   * @return The time of the route between start and end.
   */
  int timeToLoc(LatLng start, LatLng end);
}
