var app = app || {};

app.SearchView = Backbone.View.extend({
	events: {
		"click a#find-curr-location": "getCurrentLoc",
		"click #time-options a.btn": "getResults",
		"keyup input": "toggleBtnsIfNeeded",
		"change input": "toggleBtnsIfNeeded"
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
		this.refreshInterval = window.setInterval(this.refreshLoc.bind(this), 5000);
	},

	refreshLoc: function() {
		var toggle = this.toggleBtnsIfNeeded;

		navigator.geolocation.getCurrentPosition(function(p) {
			var loc = {
				lat: p.coords.latitude,
				lng: p.coords.longitude
			};
			app.currentLoc = loc;

			$("#find-curr-location").html('<i class="fa fa-location-arrow">');

			var latLng = new google.maps.LatLng(loc.lat, loc.lng);
			app.geocoder.geocode({'latLng': latLng}, function(results, status) {
				var msg;
				if (status == google.maps.GeocoderStatus.OK && results[1]) {
					msg = "Near: " + results[1].formatted_address;
				} else {
					msg = p.coords.latitude + ", " + p.coords.longitude
				}
				$("#curr-location").val(msg);
				$("#curr-location").prop("disabled", true);

				toggle();
			});
		});
	},

	toggleBtnsIfNeeded: function() {
		var currVal = $("#curr-location").val();
		var destVal = $("#destination-field").val();

		if ((currVal != undefined && currVal.length > 0) &&
			(destVal != undefined && destVal.length > 0)) {
			$("#time-options.disabled").removeClass("disabled");
		} else {
			$("#time-options").addClass("disabled");
		}
	},

	getResults: function(e) {
		e.preventDefault();

		find = function() {
			app.foundRestaurants = new app.Restaurants();
			app.foundRestaurants.lat = app.currentLoc.lat;
			app.foundRestaurants.lng = app.currentLoc.lng;
			app.foundRestaurants.destination = $("#destination-field").val();
			app.foundRestaurants.time = parseInt($(e.currentTarget).attr('data-time'))
			app.foundRestaurants.fetch({success: function() {
				app.router.navigate("results", { trigger: true });
			}});

			$(this.el).html('<p class="loading"><i class="fa fa-spinner fa-spin"></i></p>');

			if (desktop) {
				var start = new google.maps.LatLng(app.currentLoc.lat, app.currentLoc.lng);
				var request = {
					origin: start,
					destination: app.foundRestaurants.destination,
					travelMode: google.maps.TravelMode.DRIVING
				};
				app.directionsService.route(request, function(res, status) {
					if (status == google.maps.DirectionsStatus.OK) {
						app.directionsDisplay.setMap(app.map);
						app.directionsDisplay.setDirections(res);
					}
				});
			}
		}.bind(this)

		if (!app.currentLoc) {
			app.geocoder.geocode({'address': $("#curr-location").val()}, function(result, status) {
				if (status == google.maps.GeocoderStatus.OK) {
					var res = result[0].geometry.location;
					app.currentLoc = {
						lat: res.lat(),
						lng: res.lng()
					}

					find();
				}
			});
		} else {
			find();
		}
	},

	beforeClose: function() {
		window.clearInterval(this.refreshInterval);
	}
});