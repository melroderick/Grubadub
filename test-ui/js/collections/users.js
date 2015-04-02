var app = app || {};

app.Users = Backbone.Collection.extend({
	model: app.User,
	
	url: function() {
		return '/users/find/' + this.query;
	}
});