var app = app || {};

app.SectionEditView = Backbone.View.extend({
	tagName: "li",

	events: {
		"click a.delete-section": "removeSection",
		"change input.section-name": "nameChange",
		"keyup input.section-name": "nameTyped",
		"keydown input.section-email": "keypress",
		"keyup input.section-email": "emailChange",
		"blur input.section-email": "hideSuggestions"
	},

	render: function(callback) {
		app.getTemplate("sections/edit", function(file) {
			var template = _.template(file, { section: this.section });
			$(this.el).html(template);

			this.suggestionList = new app.EmailSuggestionList();
			this.suggestionList.parent = this;

			this.suggestionList.render(function(v) {
				$(this.el).find(".email-list").html(v.el);

				callback(this);
				$('input.section-name, input.section-email').tipsy({trigger: 'manual', gravity: 'e'});
			}.bind(this));
		}.bind(this));
	},

	beforeClose: function() {
		this.suggestionList.close();
	},

	sectionMoved: function() {
		var index = $(this.el).index();
		this.section.set('index', index);
	},

	removeSection: function(e) {
		e.preventDefault();
		
		this.parentView.removeSection(this);
	},

	nameChange: function() {
		var name = $(this.el).find("input.section-name").val();
		this.section.set('name', name);
	},

	nameTyped: function() {
		$(this.el).find("input.section-name").tipsy("hide");
	},

	emailChange: function(e) {
		var code = e.keyCode;
		if (code == 38 || code == 40 || code == 13) {
			return;
		}

		$(this.el).find("input.section-email").tipsy("hide");
		this.emailLastChanged = new Date();

		var email = $(this.el).find("input.section-email").val();
		this.section.set('email', email);

		if (email != "") {
			this.setLoading(true);

			window.setTimeout(function() {
				if ((new Date() - this.emailLastChanged) >= 300) {
					this.suggestionList.query(email);
				}
			}.bind(this), 300);
		} else {
			this.setLoading(false);
		}
	},

	keypress: function(e) {
		var code = e.keyCode;

		switch(code) {
		case 38:
			// up arrow
			e.preventDefault();
			this.suggestionList.selectionChanged(-1);
			break;
		case 40:
			// down arrow
			e.preventDefault();
			this.suggestionList.selectionChanged(1);
			break;
		case 13:
			// enter
			e.preventDefault();
			this.suggestionList.enter();
			break;
		}
	},

	setEmail: function(email) {
		$(this.el).find("input.section-email").val(email);
		this.section.set('email', email);

		$(this.el).find("input.section-email").blur();
		$(this.el).next().find("input.section-name").focus();
	},

	validateEmail: function() {
		if (this.section.get('email') == "") {
			// this.section.emailValid = false;
			this.setEmailValid(false);
			return;
		}

		var user = new app.User({email: this.section.get('email')});

		user.fetch({success: function() {
			this.setEmailValid(user.get('exists'));
		}.bind(this)});
	},

	setEmailValid: function(valid) {
		this.section.emailValid = valid;

		if (valid) {
			$(this.el).find("input.section-email").addClass("confirmed");
		} else {
			$(this.el).find("input.section-email").removeClass("confirmed");
		}
	},

	setLoading: function(loading) {
		if (this.loading != loading) {
			this.loading = loading;

			if (loading) {
				$(this.el).find(".section-email").addClass("loading");
			} else {
				$(this.el).find(".section-email").removeClass("loading");
			}
		}
	},

	hideSuggestions: function(e) {
		this.suggestionList.hide();
	}
});