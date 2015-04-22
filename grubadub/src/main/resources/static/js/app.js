var app = app || {};

var cachedFiles = {};
app.getTemplate = function(file, handler) {
	if (cachedFiles[file]) {
		handler(cachedFiles[file]);
	} else {
		$.ajax({
			url: "/js/templates/" + file + ".html",
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

Backbone.View.prototype.close = function() {
	if (this.beforeClose) {
		this.beforeClose();
	}

	this.remove();
	this.unbind();
}

function _distanceBetweenLatLngs(l1, l2) {
	var R = 3958.76; // Radius of the earth in miles
	var dLat = toRadians(l2.lat-l1.lat);  // deg2rad below
	var dLon = toRadians(l2.lng-l1.lng); 
	var a = Math.sin(dLat/2) * Math.sin(dLat/2) +
			Math.cos(toRadians(l1.lat)) * Math.cos(toRadians(l2.lat)) * 
			Math.sin(dLon/2) * Math.sin(dLon/2);
	var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
	var d = R * c; // Distance in km
	
	if (d == 0) {
		return "< 0.1";
	} else {
		return Math.round(d*10)/10;
	}
}

function _filterSortedRestaurants(restaurants) {
	if (true) { // filtering all restaurants lower than 3 stars
		var filterPredicate = function (r) {
			return r.attributes.rating >= 3;
		}
	}

	if (true) { // sorting by stars
		var sortScorer = function (r) {
			return -r.attributes.rating;
		}
	}

	return new app.Restaurants(_.sortBy(restaurants.filter(filterPredicate), sortScorer));
}

function toRadians(deg) {
	return deg * (Math.PI/180);
}

$(function() {

});