package edu.brown.cs.map;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;

import edu.brown.cs.maps.kdtree.KDTree;

public class GoogleRoute implements Route {

  private DirectionsRoute gRoute;
  private DirectionsLeg gLeg;
  private List<DirectionsStep> gSteps;

  private int routeTime;

  private List<LatLng> polylinePoints;
  private List<LatLng> detailedPolylinePoints;
  private List<LatLng> pointsAlong;
  private KDTree<LatLng> kdt;

  public GoogleRoute(DirectionsRoute[] routes) {
    if (routes.length >= 1) {
      gRoute = routes[0];
      assert gRoute.legs.length == 1;
      gLeg = gRoute.legs[0];

      routeTime = (int) gLeg.duration.inSeconds / 60;

      polylinePoints =
          gRoute.overviewPolyline.decodePath().stream()
          .map(p -> new LatLng(p))
          .collect(Collectors.toList());

      gSteps = Lists.newArrayList(gLeg.steps);

      detailedPolylinePoints = new ArrayList<>();
      for (DirectionsStep step : gSteps) {
        List<com.google.maps.model.LatLng> gStepPoints =
            step.polyline.decodePath();

        List<LatLng> stepPoints =
            gStepPoints.stream()
            .map(p -> new LatLng(p))
            .collect(Collectors.toList());

        detailedPolylinePoints.addAll(stepPoints);
      }

      pointsAlong = fillIn(detailedPolylinePoints, .1);
      kdt = new KDTree<>(pointsAlong);
    }
  }

  private List<LatLng> fillIn(List<LatLng> points, double maxDist) {

    // Because of spherical/LatLng issues, not super precise for filling
    // big gaps between points.

    LatLng p1, p2;
    double dLat, dLng;
    double dist;
    int numNewPoints;
    List<LatLng> newList = new ArrayList<>();
    for (int i = 0 ; i < points.size() - 1; i++) {
      p1 = points.get(i);
      p2 = points.get(i + 1);
      newList.add(p1);

      dist = p1.distanceFrom(p2);
      numNewPoints = (int) (dist / maxDist);


      for (int j = 1; j <= numNewPoints; j++) {
        dLat = (p2.getLat() - p1.getLat()) / (numNewPoints + 1);
        dLng = (p2.getLng() - p1.getLng()) / (numNewPoints + 1);

        LatLng newPoint = new LatLng(
            p1.getLat() + j * dLat,
            p1.getLng() + j * dLng);
        newList.add(newPoint);
      }
    }

    LatLng lastPoint = points.get(points.size() - 1);
    newList.add(lastPoint);

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
    /*List<Double> distances = pointsAlong.stream()
        .map(p -> p.distanceFrom(loc))
        .collect(Collectors.toList());
    double naive = Collections.min(distances);
    return naive;*/

    LatLng nn = kdt.nearestNeighbor(loc);
    return nn.distanceFrom(loc);
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

}
