var app = app || {};

var Router = Backbone.Router.extend({
	routes: {
		"": "search",
		"list": "list-restaurants"
	},

	showView: function(selector, view) {
		if (this.currentView) {
			this.currentView.close();
		}

		this.currentView = view;
		view.render(function(v) {
			$(selector).html(v.el);
		});
	}
});

app.router = new Router();

app.router.on('route:search', function() {
	document.title = 'Grubadub';
	
	app.currentLoc = null;
	app.foundRestaurants = null;

	var searchView = new app.SearchView();
	app.router.showView("#main-wrapper", searchView);
});

app.router.on('route:list-restaurants', function() {
	document.title = "Restaurants";

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

	var listView = new app.ListView();
	listView.restaurants = app.foundRestaurants;

	app.router.showView("#main-wrapper", listView);
});

app.router.on('route:error', function() {
	alert("Error 404!");
});

Backbone.history.start();