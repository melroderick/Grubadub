package edu.brown.cs.map;

public class BoundingBox {
  private final LatLng sw;
  private final LatLng ne;

  public BoundingBox(LatLng sw, LatLng ne) {
    this.sw = sw;
    this.ne = ne;
  }

  public LatLng getSW() {
    return sw;
  }

  public LatLng getNE() {
    return ne;
  }
}
