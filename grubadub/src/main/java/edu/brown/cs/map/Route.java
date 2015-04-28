package edu.brown.cs.map;

import java.util.List;

public interface Route {

  /** Gets expected travel time.
   * @return Expected travel time in minutes.
   */
  int routeTime();

  /** Gets a location's approximate distance off the route.
   * @param loc The location to check
   * @return The location's approximate distance off the route in miles.
   */
  double distanceFrom(LatLng loc);

  TimePlace nearestTimePlace(LatLng loc);

  /** The expected location in the specified time in the future.
   * @param minutes The number of minutes in the future to find
   * the location for.
   * @return The LatLng representing the expected location for the route
   * "minutes" minutes in the future.
   */
  LatLng locIn(int minutes);

  /** A list of points representing the route's approximate path between
   *  two future times.
   * @param start The number of minutes until the route will reach the
   * first point in the returned list.
   * @param end The number of minutes in the future until the route will
   * reach the last point in the returned list.
   * @return A list of points representing the route's approximate path
   * between start and end minutes in the future. If end is greater than
   * the total expected route time, returns points between start and
   * expected route time. There is no guarantee that points will be evenly
   * spaced.
   */
  List<LatLng> pointsAlong(int start, int end);



  BoundingBox getBoundingBox(int start, int end);
  BoundingBox getBoundingBox(int start, int end, double radius);

  BoundingBox getBoundingBox(int minutes, double radius);
}
