var app = app || {};

var Router = Backbone.Router.extend({
	routes: {
		"": "search",
		"results": "list-restaurants"
	},

	showView: function(selector, view) {
		if (this.currentView) {
			this.currentView.close();
		}

		this.currentView = view;
		view.render(function(v) {
			$(selector).html(v.el);
		});
	}
});

app.router = new Router();

app.router.on('route:search', function() {
	document.title = 'Grubadub';
	
	app.foundRestaurants = null;

	var searchView = new app.SearchView();
	app.router.showView("#main-wrapper", searchView);
});

app.router.on('route:list-restaurants', function() {
	alert("eyo");

	var guideview = new app.GuideView();
	guideview.guide = new app.Guide({id: id});

	guideview.guide.fetch({success: function() {
		guideview.guide.sections = new app.Sections(guideview.guide.get('sections'));

		document.title = guideview.guide.get('name');

		app.router.showView("#main-wrapper", guideview);
	}});
});

app.router.on('route:error', function() {
	alert("Error 404!");
});

Backbone.history.start();