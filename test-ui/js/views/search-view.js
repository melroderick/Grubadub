var app = app || {};

app.SearchView = Backbone.View.extend({
	render: function(callback) {
		app.getTemplate("pages/search", function(file) {
			var template = _.template(file, {  });
			$(this.el).html(template);
			
			callback(this);
		}.bind(this));
	}
});