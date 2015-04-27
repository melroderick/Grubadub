var app = app || {};

var Router = Backbone.Router.extend({
	routes: {
		"": "search",
		"results": "list-restaurants",
		"restaurants/:id": "show-restaurant"
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

	if (app.foundRestaurants == null) {
		app.router.navigate("", { trigger: true });
	} else {
		var listView = new app.ListView();
		listView.restaurants = app.foundRestaurants;

		app.router.showView("#main-wrapper", listView);
	}
});

app.router.on('route:show-restaurant', function(id) {
	var restaurant = new app.Restaurant({id: id});

	restaurant.fetch({success: function() {
		var detailView = new app.DetailView();
		detailView.restaurant = restaurant;

		document.title = restaurant.get('name');

		app.router.showView("#main-wrapper", detailView);
	}});
});

app.router.on('route:error', function() {
	alert("Error 404!");
});

Backbone.history.start();