var app = app || {};

var cachedFiles = {};
app.getTemplate = function(file, handler) {
	if (cachedFiles[file]) {
		handler(cachedFiles[file]);
	} else {
		$.ajax({
			url: "/shared/js/templates/" + file + ".html",
			method: "GET",
			ignorePrefilter: true,
		}).done(function(data) {
			cachedFiles[file] = data;
			handler(data);
		});
	}
}

app.asyncForEach = function(array, each, callback) {
	var completed = 0;

	var done = function() {
		completed ++;
		callbackIfDone();
	};

	var callbackIfDone = function() {
		if (completed == array.length) {
			callback();
		}
	};

	for (var i=0; i<array.length; i++) {
		each(array[i], done);
	}
}

Backbone.View.prototype.close = function(nextView) {
	if (this.beforeClose) {
		this.beforeClose(nextView);
	}

	this.remove();
	this.unbind();
}

function _distanceBetweenLatLngs(l1, l2) {
	var R = 3958.76; // Radius of the earth in miles
	var dLat = _toRadians(l2.lat-l1.lat);  // deg2rad below
	var dLon = _toRadians(l2.lng-l1.lng); 
	var a = Math.sin(dLat/2) * Math.sin(dLat/2) +
			Math.cos(_toRadians(l1.lat)) * Math.cos(_toRadians(l2.lat)) * 
			Math.sin(dLon/2) * Math.sin(dLon/2);
	var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
	var d = R * c; // Distance in miles
	
	return _roundNumber(d);
}

function _toRadians(deg) {
	return deg * (Math.PI/180);
}

function _roundNumber(num) {
	if (num < 0.1) {
		return "< 0.1";
	} else {
		return Math.round(num*10)/10;
	}
}

function _formatTime(time) {
	var toReturn = "";

	var mins = time % 60;
	time -= mins;

	if (time > 0) {
		var hours = time / 60;
		toReturn += hours;
		toReturn += "h ";
	}

	toReturn += mins;
	toReturn += "m";

	return toReturn;
}

function _containsQuery(string, query) {
	return query != "" && string.toLowerCase().indexOf(query.toLowerCase()) !== -1;
}

function _highlightQueries(string, queries) {
	var query = queries[0];

	var lowerString = string.toLowerCase();
	var lowerQuery = query.toLowerCase();

	var index = lowerString.indexOf(lowerQuery)

	if (lowerQuery == "" || index == -1) {
		return string;
	} else {
		var len = lowerQuery.length;

		var firstPart = string.substring(0, index);
		var highlighted = string.substring(index, index + len);
		var rest = string.substring(index + len, string.length);

		return firstPart + '<span class="query-highlighted">' + highlighted + '</span>' + _highlightQueries(rest, queries);
	}
}

$(function() {
	app.geocoder = new google.maps.Geocoder();
	app.directionsService = new google.maps.DirectionsService();

	// Initialize the map
	if (desktop) {
		app.map = new google.maps.Map(document.getElementById("map-canvas"),
			{
				zoom: 14,
				// Center on Brown's Campus
				center: new google.maps.LatLng(41.8262, -71.4032)
			}
		);
		
		app.directionsDisplay = new google.maps.DirectionsRenderer();
		app.directionsDisplay.setMap(app.map);
	}
});