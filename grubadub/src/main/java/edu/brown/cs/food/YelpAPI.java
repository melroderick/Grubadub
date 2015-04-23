package edu.brown.cs.food;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

/**
 * Code sample for accessing the Yelp API V2.
 * 
 * This program demonstrates the capability of the Yelp API version 2.0 by using the Search API to
 * query for businesses by a search term and location, and the Business API to query additional
 * information about the top result from the search query.
 * 
 * <p>
 * See <a href="http://www.yelp.com/developers/documentation">Yelp Documentation</a> for more info.
 * 
 */
public class YelpAPI {

  private static final String API_HOST = "api.yelp.com";
  private static final String SEARCH_PATH = "/v2/search";
  private static final String BUSINESS_PATH = "/v2/business";

  /*
   * Update OAuth credentials below from the Yelp Developers API site:
   * http://www.yelp.com/developers/getting_started/api_access
   */
  private static final String CONSUMER_KEY = "thxLKGzFRaZtqS0ctlhycg";
  private static final String CONSUMER_SECRET = "DuGuawOpZowGngXPaIlQn5237PM";
  private static final String TOKEN = "yvXv-QZ9KWRmf8VaUEzgqOXNE7IdGxEh";
  private static final String TOKEN_SECRET = "WRYWFHWGve4tqWKBwBbNqzc1Gnk";

  OAuthService service;
  Token accessToken;

  /**
   * Setup the Yelp API OAuth credentials.
   * 
   * @param consumerKey Consumer key
   * @param consumerSecret Consumer secret
   * @param token Token
   * @param tokenSecret Token secret
   */
  public YelpAPI() {
    this.service =
        new ServiceBuilder().provider(TwoStepOAuth.class).apiKey(CONSUMER_KEY)
            .apiSecret(CONSUMER_SECRET).build();
    this.accessToken = new Token(TOKEN, TOKEN_SECRET);
  }

  /**
   * Creates and sends a request to the Search API by term and location.
   * <p>
   * See <a href="http://www.yelp.com/developers/documentation/v2/search_api">Yelp Search API V2</a>
   * for more info.
   * 
   * @param sw_latitude the south-west latitude of the bounds
   * @param sw_longitude   the south-west longitude of the bounds
   * @param ne_latitude  the north-east latitude of the bounds
   * @param ne_longitude   the north-east longitude of the bounds
   * @return <tt>String</tt> JSON Response
   */
  public String searchForRestaurantsByBounds(
      double sw_latitude, double sw_longitude,
      double ne_latitude, double ne_longitude,
      int offset) {
    OAuthRequest request = createOAuthRequest(SEARCH_PATH);
    request.addQuerystringParameter("category_filter", "restaurants");
    request.addQuerystringParameter("bounds",
        String.format("%s,%s|%s,%s",
            sw_latitude, sw_longitude, ne_latitude, ne_longitude));
    request.addQuerystringParameter("offset", Integer.toString(offset));
    return sendRequestAndGetResponse(request);
  }

  /**
   * Creates and sends a request to the Business API by business ID.
   * <p>
   * See <a href="http://www.yelp.com/developers/documentation/v2/business">Yelp Business API V2</a>
   * for more info.
   * 
   * @param businessID <tt>String</tt> business ID of the requested business
   * @return <tt>String</tt> JSON Response
   */
  public String searchByBusinessId(String businessID) {
    OAuthRequest request = createOAuthRequest(BUSINESS_PATH + "/" + businessID);
    return sendRequestAndGetResponse(request);
  }

  /**
   * Creates and returns an {@link OAuthRequest} based on the API endpoint specified.
   * 
   * @param path API endpoint to be queried
   * @return <tt>OAuthRequest</tt>
   */
  private OAuthRequest createOAuthRequest(String path) {
    OAuthRequest request = new OAuthRequest(Verb.GET, "http://" + API_HOST + path);
    return request;
  }

  /**
   * Sends an {@link OAuthRequest} and returns the {@link Response} body.
   * 
   * @param request {@link OAuthRequest} corresponding to the API request
   * @return <tt>String</tt> body of API response
   */
  private String sendRequestAndGetResponse(OAuthRequest request) {
//    System.out.println("Querying " + request.getCompleteUrl() + " ...");
    this.service.signRequest(this.accessToken, request);
    Response response = request.send();
    return response.getBody();
  }
}
