var app = app || {};

app.Guide = Backbone.Model.extend({
	urlRoot: '/guides',
	
	initialize: function() {
		// console.log("New guide");
	},

	create: function(callback) {
		this.set('sections', JSON.stringify(this.sections.toJSON()));

		this.save(null, {success:function(guide, response) {
			callback(response.id);
		}});
	},

	validate: function(attrs, options) {
		var err = [];

		if (!attrs.name || attrs.name.length == 0) {
			err.push({ selector: "input.guide-name", msg: "Enter a name for your study guide." });
		}

		if (this.sections.length == 0) {
			err.push({ selector: "a#new-section", msg: "Please enter at least one section." })
		} else {
			this.sections.forEach(function(section) {
				if (!section.isValid()) {
					err.push({ index: section.get('index'), errors: section.validationError });
				}
			});
		}

		if (err.length > 0) {
			return err;
		}
	}
});