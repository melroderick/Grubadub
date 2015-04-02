var app = app || {};

app.ListView = Backbone.View.extend({
	events: {
		"click a.first-guide": "showform",
		"click #main.hidden": "hideform"
	},

	render: function(callback) {
		var mine = new app.Guides();
		mine.context = "mine";
		mine.comparator = function(item) {
			return item.get('created_date');
		};

		var shared = new app.Guides();
		shared.context = "shared";
		shared.comparator = function(item) {
			return item.get('edited_date');
		};

		var _render = function() {
			app.getTemplate("guides/list", function(file) {
				var template = _.template(file, { mine: mine.models, shared: shared.models });
				$(this.el).html(template);

				this.newGuide = new app.NewGuideView();

				this.newGuide.render(function(v) {
					$(this.el).find("#creation-wrapper").html(v.el);

					callback(this);
				}.bind(this));
			}.bind(this));
		}.bind(this);

		mine.fetch({success: function() {
			mine.finished = true;
			if (shared.finished) {
				_render();
			}
		}});

		shared.fetch({success: function() {
			shared.finished = true;
			if (mine.finished) {
				_render();
			}
		}});
	},

	beforeClose: function() {
		this.newGuide.close();
	},

	showform: function(e) {
		this.newGuide.show(e);
	},

	hideform: function(e) {
		this.newGuide.hide(e);
	}
});