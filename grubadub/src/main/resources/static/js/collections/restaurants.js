var app = app || {};

app.Restaurants = Backbone.Collection.extend({
	model: app.Restaurant,
	
	url: function() {
		return '/restaurants?lat=' + this.lat
		+ '&lng=' + this.lng 
		+ '&destination=' + encodeURIComponent(this.destination)
		+ '&time=' + this.time;
	}
});