var app = app || {};

app.Restaurant = Backbone.Model.extend({
	urlRoot: '/details',

	url: function() {
		return this.urlRoot + '?id=' + this.get('id');
	},

	googleMapsUrl: function() {
		var iOS = ( navigator.userAgent.match(/(iPad|iPhone|iPod)/g) ? true : false );

		if (iOS) {
			return 'comgooglemaps://?saddr=&directionsmode=driving&daddr='
			+ encodeURIComponent(this.get('address'));
		} else {
			return 'https://www.google.com/maps/dir/'
			+ encodeURIComponent(app.userStart) + '/'
			+ encodeURIComponent(this.get('address')) + '/'
			+ encodeURIComponent(app.userDestination);
		}
	},

	reviewUrl: function(id) {
		return this.get('url') + '?hrid=' + id;
	}
});