package edu.brown.cs.grubadub;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import spark.ExceptionHandler;
import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.brown.cs.food.DetailedRestaurant;
import edu.brown.cs.food.RestaurantFinder;
import edu.brown.cs.food.YelpRestaurantFinder;
import edu.brown.cs.map.GoogleRouteFinder;
import edu.brown.cs.map.LatLng;
import edu.brown.cs.map.RestaurantOnRoute;
import edu.brown.cs.map.RouteFinder;
import freemarker.template.Configuration;

public final class Main {

  private static final String[] MOBILE_USER_AGENTS = {"Android", "Blackberry",
    "iPhone", "Blazer 3.0", "Opera Mini"};

  public static void main(String[] args) {
    GsonBuilder builder = new GsonBuilder();
    builder.registerTypeAdapter(RestaurantOnRoute.class,
        new RestaurantOnRoute.RoRSerializer());
    GSON = builder.create();

    new Main(args).run();
  }

  private String[] args;
  private static Gson GSON;
  private MiddleMan middleman;

  private Main(String[] args) {
    this.args = args;
  }

  private void run() {
    RestaurantFinder food = new YelpRestaurantFinder();
    RouteFinder map = new GoogleRouteFinder();
    middleman = new MiddleMan(food, map);

    runSparkServer();
  }

  private static FreeMarkerEngine createEngine() {
    Configuration config = new Configuration();
    File templates = new File("src/main/resources/spark/template/freemarker");
    try {
      config.setDirectoryForTemplateLoading(templates);
    } catch (IOException ioe) {
      System.out.printf("ERROR: Unable use %s for template loading.%n",
          templates);
      System.exit(1);
    }
    return new FreeMarkerEngine(config);
  }

  private void runSparkServer() {
    Spark.externalStaticFileLocation("src/main/resources/static");
    Spark.exception(Exception.class, new ExceptionPrinter());

    FreeMarkerEngine freeMarker = createEngine();

    // Setup Spark Routes
    Spark.get("/", new MainHandler(), freeMarker);
    Spark.get("/desktop", new DesktopHandler(), freeMarker);
    Spark.get("/mobile", new MobileHandler(), freeMarker);
    Spark.get("/restaurants", new RestaurantHandler());
    Spark.get("/time", new TimeHandler());
    Spark.get("/details", new DetailHandler());
  }

  /**
   * Handles the main '/' route, including serving desktop or mobile depending
   * on user agent.
   *
   * @author Max
   */
  private class MainHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      String ua = req.userAgent();

      if (userAgentIsMobile(ua)) {
        return new ModelAndView(null, "mobile.ftl");
      } else {
        return new ModelAndView(null, "desktop.ftl");
      }
    }
  }

  private class MobileHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      return new ModelAndView(null, "mobile.ftl");
    }
  }

  private class DesktopHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      return new ModelAndView(null, "desktop.ftl");
    }
  }

  private static boolean userAgentIsMobile(String ua) {
    for (int i = 0; i < MOBILE_USER_AGENTS.length; i++) {
      if (ua.indexOf(MOBILE_USER_AGENTS[i]) >= 0) {
        return true;
      }
    }

    return false;
  }

  private class RestaurantHandler implements Route {
    @Override
    public Object handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      Double lat = Double.parseDouble(qm.value("lat"));
      Double lng = Double.parseDouble(qm.value("lng"));
      LatLng loc = new LatLng(lat, lng);

      String destination = qm.value("destination");
      int time = Integer.parseInt(qm.value("time"));

      List<RestaurantOnRoute> restaurants = middleman.getRestaurants(loc,
          destination, time);

      res.type("text/json");
      return GSON.toJson(restaurants);
    }
  }

  private class TimeHandler implements Route {
    @Override
    public Object handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      Double lat = Double.parseDouble(qm.value("lat"));
      Double lng = Double.parseDouble(qm.value("lng"));
      LatLng loc = new LatLng(lat, lng);

      String waypoint = qm.value("waypoint");
      String destination = qm.value("destination");

      int extraTime = middleman.getExtraTime(loc, waypoint, destination);

      Map<String, Object> variables = new ImmutableMap.Builder<String, Object>()
          .put("time", extraTime).build();

      res.type("text/json");
      return GSON.toJson(variables);
    }
  }

  private class DetailHandler implements Route {
    @Override
    public Object handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      String id = qm.value("id");

      DetailedRestaurant detailed = middleman.getRestaurantDetails(id);

      res.type("text/json");
      return GSON.toJson(detailed);
    }
  }

  /**
   * Prints exceptions to the browser when debugging the GUI.
   *
   * @author Michael Gillett
   */
  private static class ExceptionPrinter implements ExceptionHandler {
    private static final int ERROR_CODE = 500;

    @Override
    public void handle(Exception e, Request req, Response res) {
      res.status(ERROR_CODE);
      StringWriter stacktrace = new StringWriter();
      try (PrintWriter pw = new PrintWriter(stacktrace)) {
        pw.println("<pre>");
        e.printStackTrace(pw);
        pw.println("</pre>");
      }
      res.body(stacktrace.toString());
    }
  }
}
