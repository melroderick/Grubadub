var directionsDisplay;
var directionsService = new google.maps.DirectionsService();
var map;
var start, end;

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
function geocode(address) {
  var geocoder = new google.maps.Geocoder();
  geocoder.geocode({'address':address}, function(result, status) {
    if (status == google.maps.GeocoderStatus.OK) {
      var res = result[0].geometry.location;
      var loc = new google.maps.LatLng(res.lat(), res.lng());
      end = loc;
      calculateRoute();
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
    }
    else {
      console.log("fail");
    }
  });
}

function showPosition(position) {
  start = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);
  initialize();
}