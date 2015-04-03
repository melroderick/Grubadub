var app = app || {};

app.ListView = Backbone.View.extend({
	render: function(callback) {
		app.getTemplate("restaurants/list", function(file) {
			var template = _.template(file);
			var html = template({ restaurants: this.restaurants.models });

			$(this.el).html(html);
			callback(this);
		}.bind(this));
	}
});