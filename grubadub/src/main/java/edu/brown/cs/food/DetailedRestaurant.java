package edu.brown.cs.food;

import java.util.List;

import com.google.common.collect.ImmutableList;

import edu.brown.cs.map.LatLng;

public class DetailedRestaurant extends Restaurant {

  private final List<Review> reviews;
  private final String phoneNumber;
  private final String url;

  public DetailedRestaurant(String id, String name, List<String> categories,
      LatLng latLng, float rating, String address,
      List<Review> reviews, String phoneNumber, String url) {
    super(id, name, categories, latLng, rating, address);
    this.reviews = ImmutableList.copyOf(reviews);
    this.phoneNumber = phoneNumber;
    this.url = url;
  }

  public List<Review> getReviews() {
    return reviews;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public String getUrl() {
    return url;
  }
}
