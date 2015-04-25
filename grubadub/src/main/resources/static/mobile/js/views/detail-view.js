var app = app || {};

app.DetailView = Backbone.View.extend({
	render: function(callback) {
		app.getTemplate("restaurants/detail", function(file) {
			console.log(this.restaurant);
			var template = _.template(file);
			var html = template({ restaurant: this.restaurant, currentLoc: app.currentLoc });

			$(this.el).html(html);
			callback(this);
		}.bind(this));
	}
});