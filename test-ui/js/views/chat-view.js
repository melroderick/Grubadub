var app = app || {};

app.ChatView = Backbone.View.extend({
	events: {
		"submit form": "sendMessage"
	},

	initialize: function() {
		if (!app.socket) {
			app.socket = io.connect('/');
		}
	},

	render: function(callback) {
		app.getTemplate("guides/chat", function(file) {
			var template = _.template(file, { guide: this.guide });
			$(this.el).html(template);

			callback(this);

			this.adjustHeight();

			app.socket.emit('enter', { id: this.guide.id, token: $.cookie('authorization_token') });
			app.socket.on('existing users', this.loadUsers.bind(this));
			app.socket.on('user entered', this.userEntered.bind(this));
			app.socket.on('user left', this.userLeft.bind(this));
			app.socket.on('new message', this.messageReceived.bind(this));
		}.bind(this));
	},

	loadUsers: function(users) {
		users.forEach(this.appendUser.bind(this));
		this.adjustHeight();
	},

	userEntered: function(user) {
		this.appendUser(user);
		this.adjustHeight();
	},

	userLeft: function(user) {
		$(this.el).find("#" + user.id).remove();
		this.adjustHeight();
	},

	appendUser: function(user) {
		$(this.el).find(".online-users ul").append('<li id="' + user.id + '">' + user.fullName + "</li>");
	},

	adjustHeight: function() {
		$(this.el).find(".messages-list").css("top", $(this.el).find(".online-users").outerHeight());
	},

	messageReceived: function(data) {
		$(this.el).find(".messages-list").append('<li class="' + data.klass + '"><strong>' + data.user.fullName + '</strong>' + data.message + '</li>');

		$(this.el).find(".messages-list").animate({ scrollTop: $(this.el).find(".messages-list")[0].scrollHeight}, 100);
	},

	sendMessage: function(e) {
		e.preventDefault();

		var message = $(this.el).find("input[type=text]").val();

		if (message != "") {
			app.socket.emit('message', { message: message });
			this.messageReceived({ user: app.currentUser.toJSON(), message: message, klass: 'mine' });

			$(this.el).find("input[type=text]").val('');
		}
	},

	beforeClose: function() {
		app.socket.emit('leave');
	}
});