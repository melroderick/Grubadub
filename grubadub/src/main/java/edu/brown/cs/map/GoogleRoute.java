package edu.brown.cs.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;

import edu.brown.cs.kdtree.KDTree;

class GoogleRoute implements Route {

  private DirectionsRoute gRoute;
  private DirectionsLeg gLeg;
  private List<DirectionsStep> gSteps;

  private int routeTime;

  private List<LatLng> polylinePoints;

  private List<TimePlace> detailedTimePlaces;
  private List<TimePlace> filledInDetailedTimePlaces;

  private KDTree<TimePlace> kdt;

  public GoogleRoute(DirectionsRoute[] routes) {
    if (routes.length >= 1) {
      gRoute = routes[0];
      assert gRoute.legs.length == 1;
      gLeg = gRoute.legs[0];

      routeTime = (int) gLeg.duration.inSeconds / 60;

      polylinePoints = gRoute.overviewPolyline.decodePath().stream()
          .map(p -> new LatLng(p))
          .collect(Collectors.toList());

      gSteps = Lists.newArrayList(gLeg.steps);

      detailedTimePlaces = new ArrayList<>();

      int currTime = 0;
      for (DirectionsStep step : gSteps) {
        List<com.google.maps.model.LatLng> gStepPoints = step.polyline
            .decodePath();

        List<LatLng> stepPoints = gStepPoints.stream()
            .map(p -> new LatLng(p))
            .collect(Collectors.toList());

        int stepTime = (int) step.duration.inSeconds;
        detailedTimePlaces.addAll(
            buildTimePlaces(currTime, stepTime, stepPoints));

        currTime += stepTime;
      }

      List<Integer> times = detailedTimePlaces.stream().map(tp -> tp.timeInSeconds()).collect(Collectors.toList());
      // System.out.println("Detailed time places good: " + Ordering.natural().isOrdered(times));

      filledInDetailedTimePlaces = fillIn(detailedTimePlaces, .1);

      times = filledInDetailedTimePlaces.stream().map(tp -> tp.timeInSeconds()).collect(Collectors.toList());
      // System.out.println("Filled in detailed time places good: " + Ordering.natural().isOrdered(times));
      // kdt = new KDTree<>(filledInDetailedTimePlaces);
      kdt = new KDTree<>(detailedTimePlaces);
    }
  }

  private List<TimePlace> buildTimePlaces(int startTime, int pathTime,
      List<LatLng> path) {
    double pathDist = pathDistance(path);

    List<TimePlace> timePlaces = new ArrayList<>();
    timePlaces.add(new TimePlace(startTime, path.get(0)));

    double distSoFar = 0.0;
    for (int i = 0; i < path.size() - 1; i++) {
      distSoFar += path.get(i).distanceFrom(path.get(i + 1));
      double percentDone = distSoFar / pathDist;

      timePlaces.add(new TimePlace(
          startTime + (int) (percentDone * pathTime),
          path.get(i + 1)));
    }

    return timePlaces;
  }

  private double pathDistance(List<LatLng> pathPoints) {
    double dist = 0.0;
    for (int i = 0; i < pathPoints.size() - 1; i++) {
      dist += pathPoints.get(i).distanceFrom(pathPoints.get(i + 1));
    }

    return dist;
  }

  public List<TimePlace> fillIn(List<TimePlace> timePlaces, double maxDist) {
    List<TimePlace> newList = new ArrayList<>();
    for (int i = 0; i < timePlaces.size() - 1; i++) {
      TimePlace tp1 = timePlaces.get(i);
      TimePlace tp2 = timePlaces.get(i + 1);
      newList.add(tp1);

      double dist = tp1.distanceFrom(tp2);
      int numNewPoints = (int) (dist / maxDist);

      for (int j = 1; j <= numNewPoints; j++) {
        double dLat = (tp2.getLoc().getLat() - tp1.getLoc().getLat())
            / (numNewPoints + 1);
        double dLng = (tp2.getLoc().getLng() - tp1.getLoc().getLng())
            / (numNewPoints + 1);
        double dTime = tp2.timeInSeconds() - tp1.timeInSeconds()
            / (numNewPoints + 1);

        LatLng newLoc = new LatLng(
            tp1.getLoc().getLat() + j * dLat,
            tp1.getLoc().getLng() + j * dLng);
        int newTime = (int) (tp1.timeInSeconds() + j * dTime);
        newList.add(new TimePlace(newTime, newLoc));
      }
    }

    return newList;

  }

  @Override
  public int routeTime() {
    // TODO: Update for traffic?
    // See comment on durationInTraffic
    // https://github.com/googlemaps/google-maps-services-java/
    // blob/master/src/main/java/com/google/maps/model/DirectionsLeg.java

    return routeTime;
  }

  @Override
  public double distanceFrom(LatLng loc) {
    /* Naive implementation
     * List<Double> distances = pointsAlong.stream()
     * .map(p -> p.distanceFrom(loc))
     * .collect(Collectors.toList());
     * double naive = Collections.min(distances);
     * return naive;
     */

    TimePlace dummy = new TimePlace(-1, loc);

    LatLng nn = kdt.nearestNeighbor(dummy).getLoc();
    return nn.distanceFrom(loc);
  }

  @Override
  public LatLng locIn(int minutes) {
    for (TimePlace tp : detailedTimePlaces) {
      if (tp.timeInMinutes() >= minutes) {
        return tp.getLoc();
      }
    }

    return detailedTimePlaces.get(detailedTimePlaces.size() - 1).getLoc();
  }

  @Override
  public BoundingBox getBoundingBox(int minutes, double radius) {
    LatLng loc = locIn(minutes);

    LatLng sw = loc.moveSouth(radius).moveWest(radius);
    LatLng ne = loc.moveNorth(radius).moveEast(radius);

    return new BoundingBox(sw, ne);
  }

  @Override
  public List<LatLng> pointsAlong(int start, int end) {
    if (start < 0) {
      throw new IllegalArgumentException("Start time is less than 0");
    }
    if (start >= end) {
      throw new IllegalArgumentException("Start time must be less than end time");
    }

    int startSeconds = start * 60;
    int endSeconds = end * 60;

    List<LatLng> pointsAlong = detailedTimePlaces.stream()
        .filter(tp -> {
          int time = tp.timeInSeconds();
          return time >= startSeconds && time <= endSeconds;})

        .map(tp -> tp.getLoc())
        .collect(Collectors.toList());

    return pointsAlong;
  }

  @Override
  public BoundingBox getBoundingBox(int start, int end) {
    List<LatLng> points = pointsAlong(start, end);

    List<Double> lats = points.stream()
        .map(p -> p.getLat())
        .collect(Collectors.toList());
    double minLat = Collections.min(lats);
    double maxLat = Collections.max(lats);

    List<Double> lngs = points.stream()
        .map(p -> p.getLng())
        .collect(Collectors.toList());
    double minLng = Collections.min(lngs);
    double maxLng = Collections.max(lngs);

    LatLng sw = new LatLng(minLat, minLng);
    LatLng ne = new LatLng(maxLat, maxLng);

    return new BoundingBox(sw, ne);
  }

  @Override
  public BoundingBox getBoundingBox(int start, int end, double radius) {
    BoundingBox bb = getBoundingBox(start, end);

    LatLng expandedSW = bb.getSW().moveSouth(radius).moveWest(radius);
    LatLng expandedNE = bb.getNE().moveNorth(radius).moveEast(radius);

    return new BoundingBox(expandedSW, expandedNE);
  }

  @Override
  public TimePlace nearestTimePlace(LatLng loc) {
    TimePlace dummy = new TimePlace(-1, loc);

    return kdt.nearestNeighbor(dummy);
  }

}
