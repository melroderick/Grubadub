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
	$("#back-btn").hide();

	app.router.routesHit = 0;

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
		$("#back-btn").show();
		$("#back-btn").unbind("click");
		$("#back-btn").click(function(e) {
			e.preventDefault();

			app.router.navigate("", {trigger: true});
		});

		var listView = new app.ListView();
		listView.restaurants = app.foundRestaurants;

		app.router.showView("#main-wrapper", listView);

		// Show restaurant pins on map
		if (desktop) {
			var infowindow = new google.maps.InfoWindow();
		  var marker;
		  app.foundRestaurants.forEach(function (r) {
		  	marker = new google.maps.Marker({
		  		position: new google.maps.LatLng(r.get('latLng').lat,
		  																		 r.get('latLng').lng),
	        map: app.map
	      });
	      google.maps.event.addListener(marker, 'click', (function(marker, r) {
	        return function() {
	          infowindow.setContent(r.get('name'));
	          infowindow.open(app.map, marker);

	          app.restaurantOnRoute = r;
	          app.router.navigate("restaurants/" + r.get('id'), {trigger: true});
	        }
	      })(marker, r));
		  });
		}
	}
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