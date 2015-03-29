package edu.brown.cs.food;

import org.json.simple.JSONObject;

public class Review {
  private final String id;
  private final int rating;
  private final String excerpt;
  private final String userName;

  public Review(String id, int rating, String excerpt, String userName) {
    this.id = id;
    this.rating = rating;
    this.excerpt = excerpt;
    this.userName = userName;
  }
  
  public Review(JSONObject jsonReview) {
    this.id = jsonReview.get("id").toString();
    this.rating = Integer.parseInt(jsonReview.get("rating").toString());
    this.excerpt = jsonReview.get("excerpt").toString();
    JSONObject user = (JSONObject) jsonReview.get("user");
    this.userName = user.get("name").toString();
  }

  public String getId() {
    return id;
  }

  public float getRating() {
    return rating;
  }

  public String getExcerpt() {
    return excerpt;
  }

  public String getUserName() {
    return userName;
  }
}
