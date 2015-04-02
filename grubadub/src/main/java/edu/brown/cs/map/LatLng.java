package edu.brown.cs.map;

public class LatLng {

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

  public Double eucideanDistance(LatLng point) {
    return Math.sqrt(
        Math.pow(point.getLat() - lat, 2)
        + Math.pow(point.getLng() - lng, 2));
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

  @Override
  public String toString() {
    return lat + ":" + lng;
  }
}
