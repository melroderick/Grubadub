var app = app || {};

app.Guides = Backbone.Collection.extend({
	model: app.Guide,
	
	url: function() {
		return '/guides/' + this.context;
	}
});