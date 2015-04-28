package edu.brown.cs.food;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.common.collect.ImmutableList;

import edu.brown.cs.map.LatLng;

public class Restaurant implements Comparable<Restaurant> {
  private final String id;
  private final String name;
  private final List<String> categories;
  private final LatLng latLng;
  private final float rating;
  private final int review_count;
  private final String address;
  private final String image_url;
  private final String phone;

  public Restaurant(String id, String name, List<String> categories,
      LatLng latLng, float rating, int review_count, String address,
      String image_url, String phone) {
    this.id = id;
    this.name = name;
    this.categories = ImmutableList.copyOf(categories);
    this.latLng = latLng;
    this.rating = rating;
    this.review_count = review_count;
    this.address = address;
    this.image_url = image_url;
    this.phone = phone;
  }

  public Restaurant(JSONObject jsonRestaurant) {
    this.id = jsonRestaurant.get("id").toString();
    this.name = jsonRestaurant.get("name").toString();

    Object jsonImageURL = jsonRestaurant.get("image_url");
    this.image_url = jsonImageURL != null ? jsonImageURL.toString() : null;
    Object jsonPhone = jsonRestaurant.get("display_phone");
    this.phone = jsonPhone != null ? jsonPhone.toString() : null;

    categories = new ArrayList<String>();
    JSONArray jsonCategories = (JSONArray) jsonRestaurant.get("categories");
    if (jsonCategories != null) {
      for (int i = 0; i < jsonCategories.size(); i++) {
        JSONArray jsonCategory = (JSONArray) jsonCategories.get(i);
        categories.add(jsonCategory.get(0).toString());
      }
    }

    JSONObject jsonLocation = (JSONObject) jsonRestaurant.get("location");
    JSONObject jsonCoordinate = (JSONObject) jsonLocation.get("coordinate");
    if (jsonCoordinate != null) {
      this.latLng = new LatLng(Double.parseDouble(jsonCoordinate
          .get("latitude").toString()), Double.parseDouble(jsonCoordinate.get(
          "longitude").toString()));
    } else {
      this.latLng = null;
    }

    @SuppressWarnings("unchecked")
    List<String> addresses = (List<String>) jsonLocation.get("address");
    String streetAddress = (addresses.size() > 0) ? addresses.get(0) + ", "
        : "";

    Object jsonCity = jsonLocation.get("city");
    String city = (jsonCity != null) ? jsonCity.toString() : "";

    Object jsonState_code = jsonLocation.get("state_code");
    String state_code = (jsonState_code != null) ? jsonState_code.toString()
        : "";

    Object jsonPostal_code = jsonLocation.get("postal_code");
    String postal_code = (jsonPostal_code != null) ? jsonPostal_code.toString()
        : "";

    this.address = streetAddress + city + " " + state_code + " " + postal_code;

    this.rating = Float.parseFloat(jsonRestaurant.get("rating").toString());
    this.review_count = Integer.parseInt(jsonRestaurant.get("review_count")
        .toString());
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

  @Override
  public int compareTo(Restaurant r) {
    return id.compareTo(r.id);
  }

  public String getImage_url() {
    return image_url;
  }

  public int getReview_count() {
    return review_count;
  }

  public String getPhone() {
    return phone;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Restaurant [id=" + id + ", name=" + name + ", categories="
        + categories + ", latLng=" + latLng + ", rating=" + rating
        + ", review_count=" + review_count + ", address=" + address
        + ", image_url=" + image_url + ", phone=" + phone + "]";
  }

}
