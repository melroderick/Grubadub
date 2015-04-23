var app = app || {};

app.ListView = Backbone.View.extend({
	filterSortedRestaurants: function() {
		if (true) { // filtering all restaurants lower than 3 stars
			var filterPredicate = function (r) {
				return r.get('rating') >= 3;
			}
		}

		if (true) { // sorting by stars
			var sortScorer = function (r) {
				return -r.get('rating');
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

			var restaurants = this.filterSortedRestaurants().models;

			var html = template({ restaurants: restaurants, currentLoc: app.currentLoc });

			$(this.el).html(html);
			callback(this);
		}.bind(this));
	}
});