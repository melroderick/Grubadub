var app = app || {};

app.ListView = Backbone.View.extend({
	filterSortRestaurants: function() {
		if (true) { // filtering all restaurants lower than 3 stars
			var filterPredicate = function (r) {
				return r.get('rating') >= 3;
			}
		}

		var rating_gain = -1;
		var off_route_gain = 5;
		var time_gain = -0.1;
		var review_count_gain = -0.01;
		if (true) { // sorting by grubadub ranking
			var sortScorer = function (r) {
				return rating_gain * r.get('rating') +
				off_route_gain * r.get('distFromRoute') + 
				time_gain * r.get('timeToRestaurant') + 
				review_count_gain * r.get('review_count') * r.get('rating');
			}
		} else if (true) { // sorting by stars
			var sortScorer = function (r) {
				return -r.get('rating');
			}
		} else if (true) { // sorting by time
			var sortScorer = function (r) {
				return -r.get('timeToRestaurant');
			}
		}

		var newRestaurants = new app.Restaurants(this.restaurants.filter(filterPredicate));
		newRestaurants.comparator = sortScorer;
		newRestaurants.sort();

		return newRestaurants;
	},

	render: function(callback) {
		app.getTemplate("restaurants/list", function(file) {
			var template = _.template(file);

			var restaurants = this.filterSortRestaurants().models;

			var html = template({ restaurants: restaurants, currentLoc: app.currentLoc });

			$(this.el).html(html);
			callback(this);
		}.bind(this));
	}
});