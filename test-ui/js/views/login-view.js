var app = app || {};

app.LoginView = Backbone.View.extend({
	render: function(callback) {
		app.getTemplate("pages/login", function(file) {
			var template = _.template(file, {  });
			$(this.el).html(template);
			
			callback(this);
		}.bind(this));
	}
});