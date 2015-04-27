var app = app || {};

app.Restaurants = Backbone.Collection.extend({
	model: app.Restaurant,
	
	url: function() {
		var url = '/restaurants?lat=' + this.lat
		+ '&lng=' + this.lng 
		+ '&destination=' + encodeURIComponent(this.destination)
		+ '&time=' + this.time;
		console.log(url);
		return url;
	}
});