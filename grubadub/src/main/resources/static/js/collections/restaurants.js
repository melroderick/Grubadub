var app = app || {};

app.Restaurants = Backbone.Collection.extend({
	model: app.Restaurant,
	
	url: function() {
		return '/restaurants';
	}
});