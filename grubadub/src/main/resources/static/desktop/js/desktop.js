var directionsDisplay;
var directionsService = new google.maps.DirectionsService();
var map;
var start = null , end = null;
var route;
var restaurants = [];

$(document).ready(function () {
  $("#location").click(function() {
    getLocation();
  });
});

function getLocation() {
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(showPosition);
  } else {
      x.innerHTML = "Geolocation is not supported by this browser.";
  }
}
function initialize() {
  directionsDisplay = new google.maps.DirectionsRenderer();

  var mapOptions = {
    zoom: 14,
    center: start
    //mapTypeId: google.maps.MapTypeId.TERRAIN
  }
  end = geocode($("#destination").val());
  map = new google.maps.Map(document.getElementById("map-canvas"), mapOptions);




  directionsDisplay.setMap(map);


  var bounds = new google.maps.LatLngBounds();
  bounds.extend(start);
  bounds.extend(end);
  map.fitBounds(bounds);

  /*setMarkers(map, sites);
  infowindow = new google.maps.InfoWindow({
    content: "loading..."
  });*/
}

function getRestaurants() {
  var data = {lat: start.lat(), lng: start.lng(), destination: $("#destination").val(), time: 40};
  $.get("/restaurants", data, function(json) {
    restaurants = json;
    console.log(restaurants.length);
    var infowindow = new google.maps.InfoWindow();
    var marker, i;
    for (var i = 0; i < 50; i++) {
      var r = restaurants[i];
      marker = new google.maps.Marker({
        position: new google.maps.LatLng(r.latLng.lat, r.latLng.lng),
        map: map
      });
      google.maps.event.addListener(marker, 'click', (function(marker, i) {
        return function() {
          infowindow.setContent(restaurants[i].name + "<b>"
            +restaurants[i].address + "</b>");
          infowindow.open(map, marker);
        }
      })(marker, i));
    }

  });
}

function canRoute() {
  return start !== null && end !== null;
}

function geocode(address) {
  var geocoder = new google.maps.Geocoder();
  geocoder.geocode({'address':address}, function(result, status) {
    if (status == google.maps.GeocoderStatus.OK) {
      var res = result[0].geometry.location;
      var loc = new google.maps.LatLng(res.lat(), res.lng());
      end = loc;
      calculateRoute();
      getRestaurants();
    }
  });
}
function calculateRoute() {
  var request = {
    origin: start,
    destination: end,
    travelMode: google.maps.TravelMode.DRIVING
  };
  directionsService.route(request, function(response, status) {
    if (status == google.maps.DirectionsStatus.OK) {
      directionsDisplay.setDirections(response);
      route = response.routes[0].legs[0];
    }
  });
}

function showPosition(position) {
  start = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);
  initialize();
}
