app.factory('Notification', ['$rootScope',
function(root) {
	var self = this;
	self.init = function() {
		notification.register(iosConfig).then(function(deviceToken) {
		});
	};
	self.register = function() {
		root.$on('$cordovaPush:notificationReceived', function(event, res) {

		});
	};
	self.remove = function() {
		notification.unregister(options).then(function(result) {
		});
	};
	return self;
}]);