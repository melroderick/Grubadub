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

		navigator.geolocation.getCurrentPosition(function(p) {
			app.currentLoc = {
				lat: p.coords.latitude,
				lng: p.coords.longitude
			};

			$("#find-curr-location").addClass("loc-found");
			$("#time-options.disabled").removeClass("disabled");

			// TODO: do better to reverse geocode coordinates into human readable format
			$("#find-curr-location").html(p.coords.latitude + ", " + p.coords.longitude);
		})
	},

	getResults: function(e) {
		e.preventDefault();

		if (app.currentLoc) {
			app.foundRestaurants = new app.Restaurants();
			app.foundRestaurants.lat = app.currentLoc.lat;
			app.foundRestaurants.lng = app.currentLoc.lng;
			app.foundRestaurants.destination = $("input[name=destination]").val();
			app.foundRestaurants.time = parseInt($(e.currentTarget).attr('data-time'))
			app.foundRestaurants.fetch({success: function() {
				app.router.navigate("results", { trigger: true });
			}});
		}
	}

});