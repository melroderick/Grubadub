var app = app || {};

app.User = Backbone.Model.extend({
	urlRoot: '/users',

	url: function() {
		if (this.context == 'me') {
			return this.urlRoot + '/me';
		} else {
			return this.urlRoot + '/exists/' + this.get('email');
		}
	},

	initialize: function() {
		// console.log("New guide");
	}
});