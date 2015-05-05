var app = app || {};

app.ResultsView = Backbone.View.extend({
	events: {
		"change #sort-box select": "sortResults",
		"keyup #search-box": "search",
		"change #search-box": "search",
	},

	filterSortRestaurants: function() {
		this.tags = this.searchQuery.toLowerCase().replace(/[^a-z\d\s]/g, "").split(" ");

		var tags = this.tags;
		var filterPredicate = function (r) {
			tags: for (var i in tags) {
				var tag = tags[i];
				if (tag === "") {
					continue;
				}

				if (_containsQuery(r.get('name'), tag)) {
					continue;
				}

				if (_containsQuery(r.get('city'), tag)) {
					continue;
				}

				for (var c in r.get('categories')) {
					var category = r.get('categories')[c];
					if (_containsQuery(category, tag)) {
						continue tags;
					}
				}

				return false;
			}
			
			return true;
		};

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
		var filteredList;
		if (this.searchQuery != "") {
			filteredList = this.restaurants.filter(filterPredicate);
		} else {
			filteredList = this.restaurants.models;
		}

		var sortedList = _.sortBy(filteredList, sortScorer);
		var newRestaurants = new app.Restaurants(_.first(sortedList, 15));
		return newRestaurants;
	},

	sortResults: function() {
		this.sortType = $("#sort-box select").val();
		this.renderList();

		if (desktop) {
			this.updateMapBounds();
		}
	},

	search: function() {
		this.searchQuery = $("#search-box").val();
		this.renderList();

		this.renderList();

		if (desktop) {
			this.updateMapBounds();
		}
	},

	selectRestaurantRoute: function(e) {
		var index = $(e.currentTarget).index();
		app.restaurantOnRoute = this.sortedRestaurants[index];
	},

	updateMapBounds: function(e) {
		if (typeof this.sortedRestaurants !== 'undefined'
				&& this.sortedRestaurants.length > 0) {
			app.bounds = new google.maps.LatLngBounds();
			this.sortedRestaurants.forEach(function (r) {	
				var latLng = new google.maps.LatLng(r.get('latLng').lat,
																						r.get('latLng').lng);
				app.bounds.extend(latLng);
			});
			app.map.fitBounds(app.bounds);
			var zoom = app.map.getZoom();
			zoom = (zoom > 15) ? 15 : zoom;
			app.map.setZoom(zoom);
		}
	},

	restaurantHovered: function(index) {
		if (desktop) {
			this.infowindow.close();
			this.infowindow = new google.maps.InfoWindow();
			var r = this.sortedRestaurants[index];
			this.infowindow.setContent('<b>' + r.get('name') + '</b><br>' + r.get('address'));
			this.infowindow.open(app.map, app.markers[index]);
		}
	},

	showPins: function() {
		if (desktop) {
			if (typeof app.markers !== 'undefined') {
				this.drawMarkers(null);
			}
			app.markers = [];
			app.bounds = new google.maps.LatLngBounds();
			this.infowindow = new google.maps.InfoWindow();
			var marker;
			this.sortedRestaurants.forEach(function (r) {
				var latLng = new google.maps.LatLng(r.get('latLng').lat,
																						r.get('latLng').lng);
				app.bounds.extend(latLng);
				marker = new google.maps.Marker({
					position: latLng,
					map: app.map
				});
				app.markers.push(marker);
				google.maps.event.addListener(marker, 'click', (function(marker, r) {
					return function() {
						this.infowindow.setContent('<b>' + r.get('name') + '</b><br>' + r.get('address'));
						this.infowindow.open(app.map, marker);

						app.restaurantOnRoute = r;
						app.router.navigate("restaurants/" + r.get('id'), {trigger: true});
					}.bind(this);
				}.bind(this))(marker, r));
			}.bind(this));
			this.drawMarkers(app.map);
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

	renderList: function() {
		this.sortedRestaurants = this.filterSortRestaurants().models;

		this.listView.sortedRestaurants = this.sortedRestaurants;
		this.listView.tags = this.tags;

		this.listView.render(function(v) {
			$(this.el).find("ol.restaurant-list").html(v.el);
		}.bind(this));

		this.showPins();
	},

	render: function(callback) {
		app.getTemplate("pages/results", function(file) {
			var template = _.template(file);

			app.bounds = new google.maps.LatLngBounds();
			this.sortedRestaurants = this.filterSortRestaurants().models;

			var html = template({ restaurants: this.sortedRestaurants, currentLoc: app.currentLoc });
			$(this.el).html(html);

			callback(this);

			this.listView = new app.ListView();
			this.listView.resultsView = this;
			this.renderList();

			$("#search-box").val(this.searchQuery);

			if (desktop) {
				$('#sort-box').affix({
					offset: {
						top: 0
					},
					target: "#main-wrapper"
				});
			}

			if (desktop) {
				$('#sort-box').on('affix.bs.affix', function() {
					$('ol.restaurant-list').css('padding-top', $("#sort-box").outerHeight());
				});

				$('#sort-box').on('affix-top.bs.affix', function() {
					$('ol.restaurant-list').css('padding-top', 0);
				});
			}
		}.bind(this));
	},

	beforeClose: function() {
		if (desktop) {
			this.updateMapBounds();
		}
		
		this.listView.close();
	},
});