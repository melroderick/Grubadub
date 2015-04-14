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
			$("#find-curr-location").html(p.coords.latitude + ", " + p.coords.longitude);
		})
	},

	getResults: function(e) {
		e.preventDefault();

		if (app.currentLoc) {
			var test = [
				{
					name: "Chipotle",
					rating: 4.5,
					address: "215 Thayer Street, Providence RI",
					latLng: {
						lat: 41.8298,
						lng: -71.4014
					}
				},
				{
					name: "Baja's",
					rating: 5.0,
					address: "215 Thayer Street, Providence RI",
					latLng: {
						lat: 41.8298,
						lng: -71.4016
					}
				},
				{
					name: "Paragon",
					rating: 4.0,
					address: "215 Thayer Street, Providence RI",
					latLng: {
						lat: 41.8294,
						lng: -71.4012
					}
				}
			];

			app.foundRestaurants = new app.Restaurants(test);

			app.router.navigate("list", { trigger: true });
		}
	}

});