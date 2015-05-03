var app = app || {};

app.ListView = Backbone.View.extend({
	events: {
		"click ol.restaurant-list > li": "selectRestaurantRoute",
		"mouseover ol.restaurant-list > li": "hoverRestaurant",
		"change #sort-box > select": "sortResults",
	},

	filterSortRestaurants: function() {
		var searchQueryLower = this.searchQuery.toLowerCase();
		var filterPredicate = function (r) {
			if (r.get('name').toLowerCase().indexOf(searchQueryLower) !== -1) {
				return true;
			}
			for (var i in r.get('categories')) {
				var category = r.get('categories')[i];
				if (category.toLowerCase().indexOf(searchQueryLower) !== -1) {
					return true;
				}
			}
			return false;
		}

		var rating_gain = -1;
		var off_route_gain = 5;
		var time_gain = 0;
		var review_count_gain = -0.01;

		var sortScorer;
		switch(this.sortType) {
			case "special":
				sortScorer = function (r) {
					return rating_gain * r.get('rating') +
					off_route_gain * r.get('distFromRoute') + 
					time_gain * r.get('timeToRestaurant') + 
					review_count_gain * r.get('review_count') * r.get('rating');
				}
				break;
			case "rating":
				sortScorer = function (r) {
					return -r.get('rating');
				}
				break;
			case "time":
				sortScorer = function (r) {
					return r.get('timeToRestaurant');
				}
				break;
			default:
				break;
		}

		// filter, sort, and return first 50
		var filteredList = this.restaurants.filter(filterPredicate);
		var sortedList = _.sortBy(filteredList, sortScorer);
		var newRestaurants = new app.Restaurants(_.first(sortedList, 15));
		return newRestaurants;
	},

	sortResults: function() {
		this.sortType = $("#sort-box > select").val();

		this.render(function() {
			$("#sort-box > select").val(this.sortType);
		}.bind(this));
	},

	selectRestaurantRoute: function(e) {
		var index = $(e.currentTarget).index();
		app.restaurantOnRoute = this.sortedRestaurants[index];
	},

	hoverRestaurant: function(e) {
		var index = $(e.currentTarget).index();
		var restaurant = this.sortedRestaurants[index];

		if (desktop) {
			this.infowindow.close();
			this.infowindow = new google.maps.InfoWindow();
			this.infowindow.setContent(this.sortedRestaurants[index].get('name'));
		  this.infowindow.open(app.map, app.markers[index]);
		}
	},

	drawMarkers: function(map) {
		for(var i = 0; i < app.markers.length; i++) {
			app.markers[i].setMap(map);
		}
	},

	initialize: function() {
		this.searchQuery = "";
		this.sortType = "special";
	},

	render: function(callback) {
		app.getTemplate("restaurants/list", function(file) {
			var template = _.template(file);

			this.sortedRestaurants = this.filterSortRestaurants().models;

			// Show restaurant pins on map
			if (desktop) {
				if (typeof app.markers !== 'undefined') {
					this.drawMarkers(null);
				}
				app.markers = [];
				this.infowindow = new google.maps.InfoWindow();
			  var marker;
			  /*var icon = {
			  	url: "/shared/img/marker.png",
			  	size: new google.maps.Size(162, 249),
			  	scaledSize: new google.maps.Size(20, 32),
			  	anchor: new google.maps.Point(10, 32)
			  }*/
			  this.sortedRestaurants.forEach(function (r) {
			  	marker = new google.maps.Marker({
			  	position: new google.maps.LatLng(r.get('latLng').lat,
			  																	 r.get('latLng').lng),
		        map: app.map,
		        //icon: icon
		      });
		      app.markers.push(marker);
		      google.maps.event.addListener(marker, 'click', (function(marker, r) {
		        return function() {
		          this.infowindow.setContent(r.get('name'));
		          this.infowindow.open(app.map, marker);

		          app.restaurantOnRoute = r;
		          app.router.navigate("restaurants/" + r.get('id'), {trigger: true});
		        }.bind(this);
		      }.bind(this))(marker, r));
			  }.bind(this));
			  this.drawMarkers(app.map);
			}


			var html = template({ restaurants: this.sortedRestaurants, currentLoc: app.currentLoc });

			$(this.el).html(html);
			callback(this);

			if (desktop) {
				$('#sort-box').affix({
					offset: {
						top: 0
					},
					target: "#main-wrapper"
				});
			} else {
				$('#sort-box').affix({
					offset: {
						top: 50
					}
				});
			}

			$('#sort-box').on('affix.bs.affix', function() {
				$('ol.restaurant-list').css('padding-top', $("#sort-box").outerHeight());
			});

			$('#sort-box').on('affix-top.bs.affix', function() {
				$('ol.restaurant-list').css('padding-top', 0);
			});
		}.bind(this));
	}
});