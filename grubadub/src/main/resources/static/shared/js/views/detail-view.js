var app = app || {};

app.DetailView = Backbone.View.extend({
	render: function(callback) {
		var draw = function() {
			app.getTemplate("restaurants/detail", function(file) {
				var template = _.template(file);
				var html = template({ restaurant: this.restaurant, ror: this.restaurantOnRoute, currentLoc: app.currentLoc });

				$(this.el).html(html);
				callback(this);
			}.bind(this));
		}.bind(this);

		if (this.restaurantOnRoute.get('timeAdded')) {
			draw();
		} else {
			$.get('/time', {lat: app.currentLoc.lat, lng: app.currentLoc.lng, waypoint: this.restaurant.get('address'), destination: app.userDestination}, function(data) {
				this.restaurantOnRoute.set('timeAdded', data.time);
				draw();
			}.bind(this));
		}
	}
});