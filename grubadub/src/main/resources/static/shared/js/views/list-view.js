var app = app || {};

app.ListView = Backbone.View.extend({
	events: {
		"click ol.restaurant-list > li": "selectRestaurantRoute",
		"change #sort-box > select": "sortResults"
	},

	filterSortRestaurants: function(filter, type) {
		if (filter) { // filtering all restaurants lower than 3 stars
			var filterPredicate = function (r) {
				return r.get('rating') >= 3;
			}
		}

		var rating_gain = -1;
		var off_route_gain = 5;
		var time_gain = 0;
		var review_count_gain = -0.01;

		var sortScorer;
		switch(type) {
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
		var newRestaurants = new app.Restaurants(_.first(sortedList, 50));
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

	render: function(callback) {
		if (this.shouldFilter == undefined) {
			this.shouldFilter = true;
		}

		if (this.sortType == undefined) {
			this.sortType = "special";
		}

		app.getTemplate("restaurants/list", function(file) {
			var template = _.template(file);

			this.sortedRestaurants = this.filterSortRestaurants(this.shouldFilter, this.sortType).models;

			var html = template({ restaurants: this.sortedRestaurants, currentLoc: app.currentLoc });

			$(this.el).html(html);
			callback(this);

			$('#sort-box').affix({
				offset: {
					top: 50
				}
			});

			$('#sort-box').on('affix.bs.affix', function() {
				$('ol.restaurant-list').css('padding-top', $("#sort-box").outerHeight());
			});

			$('#sort-box').on('affix-top.bs.affix', function() {
				$('ol.restaurant-list').css('padding-top', 0);
			});
		}.bind(this));
	}
});