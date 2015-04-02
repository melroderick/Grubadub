var app = app || {};

app.SectionShowView = Backbone.View.extend({
	events: {
		"click a.approve": "approveSection",
		"click a.disapprove": "disapproveSection"
	},

	render: function(callback) {
		app.getTemplate("sections/show", function(file) {
			var timeAgo = moment(this.section.get('edited_date')).fromNow();
			var template = _.template(file, { section: this.section, guideMine: this.guideMine, hideText: this.hideText, timeAgo: timeAgo });
			$(this.el).html(template);

			callback(this);

			$(this.el).find("p.user-name i").tipsy({gravity: 's'});

			if (this.section.get('mine')) {
				$(this.el).find("div.section-text").editable({
					inlineMode: false,
					autosave: true,
					autosaveInterval: 2000,
					beforeSaveCallback: this.beforeSave.bind(this),
					saveURL: "/sections/" + this.section.get('id'),
					afterSaveCallback: this.afterSave.bind(this),
					buttons: ["bold", "italic", "underline", "strikeThrough",  "fontSize", "color", "align", "insertOrderedList", "insertUnorderedList", "outdent", "indent", "createLink", "insertImage", "undo", "redo", "save"]
				});
			}

			if (this.guideMine) {
				$(this.el).find('div.approve-box a').tipsy({gravity: 's'})
			}

			if ((!this.hideText && !this.guideMine) || this.section.get('mine')) {
				$(this.el).find('div.approved-status i').tipsy({gravity: 's'})
			}

			window.setInterval(this.updateEditedText.bind(this), 60000)
		}.bind(this));
	},

	beforeSave: function() {
		this.updateEditedText("saving...");
	},

	afterSave: function() {
		this.section.set('edited_date', new Date());
		window.setTimeout(this.updateEditedText.bind(this), 350);
	},

	updateEditedText: function(text) {
		if (!text) {
			text = "last edited " + moment(this.section.get('edited_date')).fromNow();
		}

		$(this.el).find(".edited-date").html(text);
	},

	setApproved: function(e, approved) {
		e.preventDefault();

		this.section.setApproved(approved, function() {
			$(this.el).find("div.approve-box a.selected").removeClass("selected");

			var klass = approved ? "approve" : "disapprove";
			$(this.el).find("div.approve-box a." + klass).addClass("selected");
		}.bind(this));
	},

	approveSection: function(e) {
		this.setApproved(e, true);
	},

	disapproveSection: function(e) {
		this.setApproved(e, false);
	}
});