package edu.brown.cs.food;

public class Review {
  private final String id;
  private final float rating;
  private final String excerpt;
  private final String userName;

  public Review(String id, float rating, String excerpt, String userName) {
    this.id = id;
    this.rating = rating;
    this.excerpt = excerpt;
    this.userName = userName;
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
