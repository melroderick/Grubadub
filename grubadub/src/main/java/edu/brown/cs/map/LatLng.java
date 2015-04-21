package edu.brown.cs.map;

import edu.brown.cs.kdtree.KDData;


public class LatLng implements KDData {

  public static final double EARTH_RADIUS_IN_MILES = 3959.0;

  private final Double lat;
  private final Double lng;

  /**
   * Constructs a latitude and longitude object.
   * @param lat the latitude.
   * @param lon the longitude.
   */
  public LatLng(Double lat, Double lng) {
    this.lat = lat;
    this.lng = lng;
  }

  // Convert from Google LatLng to ours.
  protected LatLng(com.google.maps.model.LatLng gLoc) {
    this.lat = gLoc.lat;
    this.lng = gLoc.lng;
  }

  /**
   * Returns the latitude.
   * @return returns the double representing the latitude.
   */
  public Double getLat() {
    return lat;
  }

  /**
   * Returns the longitude.
   * @return returns the double representing the longitude.
   */
  public Double getLng() {
    return lng;
  }

  public double distanceFrom(LatLng o) {
    if (equals(o)) {
      return 0;
    }

    double lat1 = Math.toRadians(getLat());
    double lng1 = Math.toRadians(getLng());
    double lat2 = Math.toRadians(o.getLat());
    double lng2 = Math.toRadians(o.getLng());

    return EARTH_RADIUS_IN_MILES * Math.acos(
        Math.sin(lat1) * Math.sin(lat2)
        + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lng2 - lng1));
  }

  public Double euclideanDistance(LatLng o) {
    return Math.sqrt(
        Math.pow(o.getLat() - lat, 2)
        + Math.pow(o.getLng() - lng, 2));
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    } else if (obj instanceof LatLng) {
      LatLng latLng = (LatLng) obj;
      return this.lat.equals(latLng.lat)
          && this.lng.equals(latLng.lng);
    } else {
      return false;
    }
  }


  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "LatLng [lat=" + lat + ", lng=" + lng + "]";
  }

  @Override
  public double[] getLocData() {
    return new double[]{lat, lng};
  }

  @Override
  public int getDims() {
    return 2;
  }
}
