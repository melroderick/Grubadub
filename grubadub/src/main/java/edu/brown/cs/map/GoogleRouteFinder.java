package edu.brown.cs.map;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsRoute;

public class GoogleRouteFinder implements RouteFinder {

  private static final String apiKey = "AIzaSyCeMcDjaJsg2gDGfXGOn3GRFv1ippD6Pqw";

  private static final GeoApiContext context = new GeoApiContext()
      .setApiKey(apiKey);

  @Override
  public Route getRoute(LatLng start, String address) {
    com.google.maps.model.LatLng gLoc = new com.google.maps.model.LatLng(
        start.getLat(), start.getLng());

    DirectionsRoute[] routes = null;
    try {
      routes = DirectionsApi.newRequest(context)
          .origin(gLoc)
          .destination(address)
          .await();
    } catch (Exception e) {
      return null;
    }

    return new GoogleRoute(routes);
  }

  @Override
  public int extraTime(LatLng start, String waypoint, String destination) {
    com.google.maps.model.LatLng gLoc = new com.google.maps.model.LatLng(
        start.getLat(), start.getLng());

    DirectionsRoute detour = null;
    try {
      DirectionsRoute[] detourArray = DirectionsApi.newRequest(context)
          .origin(gLoc)
          .waypoints(waypoint)
          .destination(destination)
          .await();
      detour = detourArray[0];
    } catch (Exception e) {
      System.out.println("detour error");
      return -1;
    }

    DirectionsRoute direct = null;
    try {
      DirectionsRoute[] directArray = DirectionsApi.newRequest(context)
          .origin(gLoc).destination(destination).await();
      direct = directArray[0];
    } catch (Exception e) {
      System.out.println("direct error");
      return -1;
    }

    assert direct.legs.length == 1;
    assert detour.legs.length == 2;
    DirectionsLeg l1 = detour.legs[0];
    DirectionsLeg l2 = detour.legs[1];

    int t1 = (int) l1.duration.inSeconds / 60;
    int t2 = (int) l2.duration.inSeconds / 60;

    System.out.println(t1);
    System.out.println(t2);
    System.out.println((int) direct.legs[0].duration.inSeconds / 60);

    long detourTime = l1.duration.inSeconds + l2.duration.inSeconds;
    long directTime = direct.legs[0].duration.inSeconds;

    return (int) (detourTime - directTime) / 60;
  }

  @Override
  public int timeToLoc(LatLng start, LatLng end) {
    // TODO Auto-generated method stub
    return 0;
  }

}
