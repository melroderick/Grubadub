var app = app || {};

var Router = Backbone.Router.extend({
	routes: {
		"": "search",
		"results": "list-restaurants",
		"restaurants/:id": "show-restaurant"
	},

	showView: function(selector, view) {
		if (this.currentView) {
			this.currentView.close(view);
		}

		this.currentView = view;
		view.render(function(v) {
			$(selector).html(v.el);
		});
	}
});

app.router = new Router();

app.router.on('route:search', function() {
	$("#back-btn").hide();

	app.router.routesHit = 0;

	document.title = 'Grubadub';
	
	app.currentLoc = null;
	app.foundRestaurants = null;

	var searchView = new app.SearchView();
	searchView.shouldClearMap = true;
	app.router.showView("#main-wrapper", searchView);
});

app.router.on('route:list-restaurants', function() {
	document.title = "Restaurants";

	if (app.foundRestaurants == null) {
		app.router.navigate("", { trigger: true });
		return;
	}

	$("#back-btn").show();
	$("#back-btn").unbind("click");
	$("#back-btn").click(function(e) {
		e.preventDefault();

		app.router.navigate("", {trigger: true});
	});

	var resultsView = new app.ResultsView();

	if (app.resultsView == undefined) {
		resultsView.restaurants = app.foundRestaurants;
	} else {
		resultsView.restaurants = app.resultsView.restaurants;
		resultsView.searchQuery = app.resultsView.searchQuery;
		resultsView.sortType = app.resultsView.sortType;
	}

	app.resultsView = resultsView;
	app.router.showView("#main-wrapper", resultsView);
});

app.router.on('route:show-restaurant', function(id) {
	if (app.foundRestaurants == null) {
		app.router.navigate("", { trigger: true });
	} else {
		$("#back-btn").show();
		$("#back-btn").unbind("click");
		$("#back-btn").click(function(e) {
			e.preventDefault();

			app.router.navigate("results", {trigger: true});
		});

		var restaurant = new app.Restaurant({id: id});

		restaurant.fetch({success: function() {
			var detailView = new app.DetailView();
			detailView.restaurant = restaurant;

			if (app.restaurantOnRoute) {
				detailView.restaurantOnRoute = app.restaurantOnRoute;
			}

			document.title = restaurant.get('name');

			app.router.showView("#main-wrapper", detailView);
		}});
	}
});

app.router.on('route:error', function() {
	alert("Error 404!");
});

Backbone.history.start();