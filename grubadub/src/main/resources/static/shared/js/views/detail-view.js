var app = app || {};

app.DetailView = Backbone.View.extend({
	render: function(callback) {
		app.getTemplate("restaurants/detail", function(file) {
			var template = _.template(file);
			var html = template({ restaurant: this.restaurant, ror: this.restaurantOnRoute, currentLoc: app.currentLoc });

			$(this.el).html(html);
			callback(this);
		}.bind(this));
	}
});