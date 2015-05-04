var app = app || {};

app.DetailView = Backbone.View.extend({
	render: function(callback) {
		var draw = function() {
			app.getTemplate("restaurants/detail", function(file) {
				var template = _.template(file);
				var html = template({ restaurant: this.restaurant, ror: this.restaurantOnRoute, currentLoc: app.currentLoc });

				$(this.el).html(html);
				callback(this);
				// the smooth zoom function
				function smoothZoom (map, max, cnt) {
			    if (cnt >= max) {
	          return;
	        } else {
		        z = google.maps.event.addListener(map, 'zoom_changed', function(event){
		            google.maps.event.removeListener(z);
		            smoothZoom(map, max, cnt + 1);
		        });
		        setTimeout(function(){map.setZoom(cnt)}, 80);
			    }
				} 
				var rLatLng = this.restaurantOnRoute.get("latLng");
				var latLng = new google.maps.LatLng(rLatLng.lat, rLatLng.lng);
				app.map.panTo(latLng);
				smoothZoom(app.map, 15, app.map.getZoom());
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