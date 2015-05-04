var app = app || {};

app.ListView = Backbone.View.extend({
	events: {
		"click li": "selectRestaurantRoute",
		"mouseover li": "hoverRestaurant"
	},

	selectRestaurantRoute: function(e) {
		var index = $(e.currentTarget).index();
		app.restaurantOnRoute = this.sortedRestaurants[index];
	},

	hoverRestaurant: function(e) {
		var index = $(e.currentTarget).index();

		this.resultsView.restaurantHovered(index);
	},

	render: function(callback) {
		app.getTemplate("restaurants/list", function(file) {
			var template = _.template(file);

			var html = template({ restaurants: this.sortedRestaurants, currentLoc: app.currentLoc });

			$(this.el).html(html);
			callback(this);

			this.delegateEvents();
		}.bind(this));
	}
});