package edu.brown.cs.food;

import java.util.List;

import com.google.common.collect.ImmutableList;

import edu.brown.cs.map.LatLng;

public class Restaurant {
  private final String id;
  private final String name;
  private final List<String> categories;
  private final LatLng latLng;
  private final float rating;
  private final String address;
  
  public Restaurant(String id, String name,
      List<String> categories, LatLng latLng,
      float rating, String address) {
    this.id = id;
    this.name = name;
    this.categories = ImmutableList.copyOf(categories);
    this.latLng = latLng;
    this.rating = rating;
    this.address = address;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public List<String> getCategories() {
    return categories;
  }

  public LatLng getLatLng() {
    return latLng;
  }

  public float getRating() {
    return rating;
  }

  public String getAddress() {
    return address;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    } else if (obj instanceof Restaurant) {
      Restaurant r = (Restaurant) obj;
      return equals(r);
    } else {
      return false;
    }
  }

  public boolean equals(Restaurant r) {
    return this.id.equals(r.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
