var app = app || {};

app.EmailSuggestionList = Backbone.View.extend({
	events: {
		"mouseover li": "mouseSelect",
		"click li.selected": "enter"
	},

	initialize: function() {
		this.users = new app.Users();
		this.selected = null;
	},

	render: function(callback) {
		app.getTemplate("users/email-list", function(file) {
			var template = _.template(file, { users: this.users.models, query: this.users.query });
			$(this.el).html(template);

			callback(this);
		}.bind(this));
	},

	query: function(email) {
		this.users.query = email;
		this.users.fetch({reset: true, success: this.show.bind(this)});
	},

	selectionChanged: function(change) {
		if ((this.selected == 0 && change == -1) || (this.selected == this.users.length - 1 && change == 1)) {
			return;
		}

		if (this.selected == null) {
			if (change == 1) {
				// down arrow
				this.selected = 0;
			} else {
				// up arrow
				this.selected = this.users.length - 1;
			}
		} else {
			this.selected = this.selected + change;
		}

		this.updateSelected();
	},

	mouseSelect: function(e) {
		this.selected = $(e.currentTarget).index();
		this.updateSelected();
	},

	updateSelected: function() {
		$(this.el).find("li.selected").removeClass("selected");
		$(this.el).find("li").eq(this.selected).addClass("selected");
	},

	enter: function(e) {
		if (e) {
			e.preventDefault();
		}

		if (this.users.length == 0) {
			return;
		}

		if (!this.selected) {
			this.selected = 0;
			this.updateSelected();
		}

		var email = this.users.at(this.selected).get('email');
		this.parent.setEmail(email);
	},

	show: function() {
		this.parent.setLoading(false);

		if (this.users.length > 0) {
			this.render(function() {
				$(this.el).parent().css("display", "block");
			}.bind(this));
		} else {
			this.hide();
		}
	},

	hide: function() {
		window.setTimeout(function() {
			this.selected = null;
			$(this.el).parent().css("display", "none");

			this.parent.validateEmail();
		}.bind(this), 95);
	}
});