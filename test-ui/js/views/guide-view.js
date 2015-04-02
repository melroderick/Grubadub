var app = app || {};

app.GuideView = Backbone.View.extend({
	render: function(callback) {
		app.getTemplate("guides/show", function(file) {
			var template = _.template(file, { guide: this.guide });
			$(this.el).html(template);

			this.sectionViews = [];

			app.asyncForEach(this.guide.sections.models, function(section, done) {
				var view = new app.SectionShowView();
				view.section = section;
				view.guideMine = this.guide.get('mine');
				view.hideText = this.guide.get('hideText');

				this.sectionViews.push(view);

				view.render(done);
			}.bind(this), function() {
				this.sectionViews.forEach(function(view) {
					$(this.el).find("#guide").append(view.el);
				}.bind(this));

				this.chatView = new app.ChatView();
				this.chatView.guide = this.guide;

				this.chatView.render(function(v) {
					$(this.el).find(".chat-wrapper").html(v.el);

					callback(this);
				}.bind(this));
			}.bind(this));
		}.bind(this));
	},

	beforeClose: function() {
		this.sectionViews.forEach(function(view) {
			view.close();
		});

		this.chatView.close();
	}
});