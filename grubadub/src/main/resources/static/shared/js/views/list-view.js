var app = app || {};

app.ListView = Backbone.View.extend({
	events: {
		"mousedown li a": "selectRestaurantRoute",
		"click li a": "selectRestaurantRoute",
		"mouseover li": "hoverRestaurant"
	},

	selectRestaurantRoute: function(e) {
		e.preventDefault();

		var index = $(e.currentTarget).parent().index();
		app.restaurantOnRoute = this.sortedRestaurants[index];

		app.router.navigate("restaurants/" + app.restaurantOnRoute.get('id'), { trigger: true });

		return false;
	},

	hoverRestaurant: function(e) {
		var index = $(e.currentTarget).index();

		this.resultsView.restaurantHovered(index);
	},

	render: function(callback) {
		app.getTemplate("restaurants/list", function(file) {
			var template = _.template(file);

			var html = template({ restaurants: this.sortedRestaurants, tags: this.tags });

			$(this.el).html(html);
			callback(this);

			this.delegateEvents();
		}.bind(this));
	}
});