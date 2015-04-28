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
	},

	// Code taken from: http://stackoverflow.com/questions/14860461/selective-history-back-using-backbone-js
	initialize: function() {
		this.routesHit = 0;
		//keep count of number of routes handled by your application
		Backbone.history.on('route', function() {
			this.routesHit++;
			$("#back-btn").show();
		}, this);
	},

	back: function() {
		if (this.routesHit > 1) {
			//more than one route hit -> user did not land to current page directly
			window.history.back();
			this.routesHit--;
		} else {
			//otherwise go to the home page. Use replaceState if available so
			//the navigation doesn't create an extra history entry
			this.navigate('', { trigger:true, replace:true });
		}
	}
});

app.router = new Router();

app.router.on('route:search', function() {
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
		var listView = new app.ListView();
		listView.restaurants = app.foundRestaurants;

		app.router.showView("#main-wrapper", listView);

		// Show restaurant pins on map
		if (desktop) {
			var infowindow = new google.maps.InfoWindow();
		  var marker, r;
		  app.foundRestaurants.forEach(function (r) {
		  	marker = new google.maps.Marker({
		  		position: new google.maps.LatLng(r.get('latLng').lat,
		  																		 r.get('latLng').lng),
	        map: app.map
	      });
		  });
		  google.maps.event.addListener(marker, 'click', (function(marker, r) {
        return function() {
          infowindow.setContent(r.get('name'));
          console.log("Hey");
          console.log(r.get('name'));
          infowindow.open(map, marker);
          app.router.navigate("restaurants/" + r.get('id'), detailView);
        }
      })(marker, r));
		}
	}
});

app.router.on('route:show-restaurant', function(id) {
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
});

app.router.on('route:error', function() {
	alert("Error 404!");
});

Backbone.history.start();