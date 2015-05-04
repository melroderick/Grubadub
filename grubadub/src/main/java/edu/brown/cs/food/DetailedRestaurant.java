package edu.brown.cs.food;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.common.collect.ImmutableList;

import edu.brown.cs.map.LatLng;

public class DetailedRestaurant extends Restaurant {

  private final List<Review> reviews;
  private final String url;

  public DetailedRestaurant(String id, String name, List<String> categories,
      LatLng latLng, float rating, int review_count,
      String address, String city, String image_url,
      List<Review> reviews, String phoneNumber, String url, String phone) {
    super(id, name, categories, latLng, rating, review_count, address, city, image_url, phone);
    this.reviews = ImmutableList.copyOf(reviews);
    this.url = url;
  }

  public DetailedRestaurant(JSONObject jsonRestaurant) {
    super(jsonRestaurant);

    this.url = jsonRestaurant.get("url").toString();

    reviews = new ArrayList<Review>();
    JSONArray jsonReviews = (JSONArray) jsonRestaurant.get("reviews");
    for (int i = 0; i < jsonReviews.size(); i++) {
      Review r = new Review((JSONObject) jsonReviews.get(i));
      reviews.add(r);
    }
  }

  public List<Review> getReviews() {
    return reviews;
  }

  public String getUrl() {
    return url;
  }
}
