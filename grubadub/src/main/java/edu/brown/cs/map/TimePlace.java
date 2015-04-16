package edu.brown.cs.map;

import edu.brown.cs.kdtree.KDData;

class TimePlace implements KDData {

  private LatLng loc;
  private int timeInSeconds;

  public TimePlace(int timeInSeconds, LatLng loc) {
    this.loc = loc;
    this.timeInSeconds = timeInSeconds;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "TimePlace [loc=" + loc + ", timeInSeconds=" + timeInSeconds + "]";
  }

  public LatLng getLoc() {
    return loc;
  }

  public int timeInSeconds() {
    return timeInSeconds;
  }

  public int timeInMinutes() {
    return timeInSeconds / 60;
  }

  public double distanceFrom(TimePlace o) {
    return loc.distanceFrom(o.getLoc());
  }

  @Override
  public double[] getLocData() {
    return loc.getLocData();
  }

  @Override
  public int getDims() {
    return 2;
  }

}
