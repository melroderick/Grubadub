var app = app || {};

app.SearchView = Backbone.View.extend({
	events: {
		"click a#find-curr-location": "getCurrentLoc",
		"click a.time-btn": "getResults"
	},

	render: function(callback) {
		app.getTemplate("pages/search", function(file) {
			var template = _.template(file);
			$(this.el).html(template());
			
			callback(this);
		}.bind(this));
	},

	getCurrentLoc: function(e) {
		e.preventDefault();

		$("#find-curr-location").html('<i class="fa fa-spinner fa-spin"></i>');
		$("#find-curr-location").addClass("unclickable");

		this.refreshLoc();
		this.refreshInterval = window.setInterval(this.refreshLoc, 5000);
	},

	refreshLoc: function() {
		navigator.geolocation.getCurrentPosition(function(p) {
			var loc = {
				lat: p.coords.latitude,
				lng: p.coords.longitude
			};
			app.currentLoc = loc;

			var latLng = new google.maps.LatLng(loc.lat, loc.lng);
			app.geocoder.geocode({'latLng': latLng}, function(results, status) {
				$("#find-curr-location").addClass("loc-found");
				$("#time-options.disabled").removeClass("disabled");

				var msg;
				if (status == google.maps.GeocoderStatus.OK && results[1]) {
					msg = "Near: " + results[1].formatted_address;
				} else {
					msg = p.coords.latitude + ", " + p.coords.longitude
				}
				$("#find-curr-location").html(msg);
			});
		});
	},

	getResults: function(e) {
		e.preventDefault();

		if (app.currentLoc) {
			$(this.el).html('<p class="loading"><i class="fa fa-spinner fa-spin"></i></p>');

			app.foundRestaurants = new app.Restaurants();
			app.foundRestaurants.lat = app.currentLoc.lat;
			app.foundRestaurants.lng = app.currentLoc.lng;
			app.foundRestaurants.destination = $("input[name=destination]").val();
			app.foundRestaurants.time = parseInt($(e.currentTarget).attr('data-time'))
			app.foundRestaurants.fetch({success: function() {
				app.router.navigate("results", { trigger: true });
			}});
		}
	},

	beforeClose: function() {
		console.log("stopping interval");
		window.clearInterval(this.refreshInterval);
	}
});