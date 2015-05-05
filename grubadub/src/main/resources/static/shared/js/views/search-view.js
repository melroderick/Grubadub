var app = app || {};

app.SearchView = Backbone.View.extend({
	events: {
		"click a#find-curr-location": "getCurrentLoc",
		"click a#manual-time": "showManualTimeOption",
		"click div.manual-time-input a.btn": "getWithCustomTime",
		"click #time-options .btn-row a.btn": "getWithBtnTime",
		"keyup input": "toggleBtnsIfNeeded",
		"change input": "toggleBtnsIfNeeded",
		"blur input": "adjustTimeOptions"
	},

	render: function(callback) {
		app.getTemplate("pages/search", function(file) {
			var template = _.template(file);
			$(this.el).html(template());
			callback(this);

			var start = document.getElementById('curr-location');
			var end = document.getElementById('destination-field');
			var options = { types: ['geocode'] };
			app.autocompleteStart = new google.maps.places.Autocomplete(start, options);
			app.autocompleteEnd = new google.maps.places.Autocomplete(end, options);

			google.maps.event.addListener(app.autocompleteStart, 'place_changed', this.adjustTimeOptions.bind(this));
			google.maps.event.addListener(app.autocompleteEnd, 'place_changed', this.adjustTimeOptions.bind(this));

			if (app.userStart) {
				$("#curr-location").val(app.userStart);
			}

			if (app.userDestination) {
				$("#destination-field").val(app.userDestination);
			}

			this.toggleBtnsIfNeeded();
		}.bind(this));
		if (typeof app.directionsDisplay !== 'undefined') {
			app.directionsDisplay.setMap(null);
		}
		if (typeof app.markers !== 'undefined') {
			for(var i = 0; i < app.markers.length; i++) {
				app.markers[i].setMap(null);
			}
		}
	},

	getCurrentLoc: function(e) {
		e.preventDefault();

		$("#find-curr-location").html('<i class="fa fa-spinner fa-spin"></i>');
		$("#find-curr-location").addClass("unclickable");

		this.refreshLoc();
	},

	refreshLoc: function() {
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
					msg = results[1].formatted_address;
				} else {
					msg = p.coords.latitude + ", " + p.coords.longitude
				}
				$("#curr-location").val(msg);

				this.toggleBtnsIfNeeded();
				this.adjustTimeOptions();
			}.bind(this));
		}.bind(this));
	},

	showManualTimeOption: function(e) {
		e.preventDefault();

		$("#time-options > p").remove();
		$("#time-options > div.manual-time-input").show();
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

	adjustTimeOptions: function() {
		var origin = $("#curr-location").val();
		var destination = $("#destination-field").val();

		if ((origin != undefined && origin.length > 0) &&
			(destination != undefined && destination.length > 0)) {
			var query = {
				origin: origin,
				destination: destination,
				travelMode: google.maps.TravelMode.DRIVING
			};

			app.directionsService.route(query, function(result, status) {
				if (status == google.maps.DirectionsStatus.OK) {
					var legs = result.routes[0].legs;

					var time = 0;
					for (var i=0; i<legs.length; i++) {
						time += legs[i].duration.value;
					}
					time /= 60; // now in minutes

					var mins = Math.round(time);
					var hours = Math.ceil(time / 60);

					if (time <= 30) {
						$("#btn-2").html("10 mins");
						$("#btn-2").attr("data-time", "10");

						$("#btn-3").html("20 mins");
						$("#btn-3").attr("data-time", "20");

						$("#btn-4").html("At destination");
						$("#btn-4").attr("data-time", mins);
					} else if (hours <= 2) {
						$("#btn-2").html("15 mins");
						$("#btn-2").attr("data-time", "15");

						$("#btn-3").html("30 mins");
						$("#btn-3").attr("data-time", "30");

						$("#btn-4").html("At destination");
						$("#btn-4").attr("data-time", mins);
					} else {
						$("#btn-2").html("30 mins");
						$("#btn-2").attr("data-time", "30");

						$("#btn-3").html("1 hour");
						$("#btn-3").attr("data-time", "60");

						$("#btn-4").html("2 hours");
						$("#btn-4").attr("data-time", "120");
					}
				}
			});
		}
	},

	getWithCustomTime: function(e) {
		e.preventDefault();

		var hours = parseInt($("input#manual-hours").val());
		var mins = parseInt($("input#manual-mins").val());
		var time = (hours * 60) + mins;

		this.getResults(time);
	},

	getWithBtnTime: function(e) {
		e.preventDefault();

		var time = parseInt($(e.currentTarget).attr('data-time'));

		this.getResults(time);
	},

	getResults: function(time) {
		find = function() {
			app.foundRestaurants = new app.Restaurants();
			app.userStart = $("#curr-location").val();
			app.foundRestaurants.lat = app.currentLoc.lat;
			app.foundRestaurants.lng = app.currentLoc.lng;
			app.userDestination = $("#destination-field").val();
			app.foundRestaurants.destination = app.userDestination;
			app.foundRestaurants.time = time;
			app.foundRestaurants.fetch({
				success: function() {
					app.resultsView = undefined;
					app.router.navigate("results", { trigger: true });
				},
				error: function(collection, response, opts) {
					alert(response.responseJSON.error);

					this.render(function() {});

					app.router.navigate("", { trigger: true });
				}.bind(this)
			});

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
	}
});