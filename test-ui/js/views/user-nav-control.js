var app = app || {};

app.UserNavControl = Backbone.View.extend({
	events: {
		"mouseover": "showDropdown",
		"mouseout": "hideDropdown",
		"click .logout": "logout"
	},

	render: function(callback) {
		app.getTemplate("users/navcontrol", function(file) {
			var template = _.template(file, { user: this.user });
			$(this.el).html(template);

			callback(this);

			$(this.el).find("ul").width($(this.el).width());
		}.bind(this));
	},

	showDropdown: function() {
		$(this.el).find("ul").css("visibility", "visible");
	},

	hideDropdown: function() {
		$(this.el).find("ul").css("visibility", "hidden");
	},

	logout: function() {
		app.currentUser = undefined;
		$.removeCookie('authorization_token');

		this.close();
		app.router.navigate("login", { trigger: true });
	}
});