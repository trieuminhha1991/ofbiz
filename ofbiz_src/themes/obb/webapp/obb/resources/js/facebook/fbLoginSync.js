var cpr = function() {
	getProfile(jQuery('#fbLogin'));
	jQuery('#fbLogin').on('fb:profile', function(e, response) {
		sessionStorage.setItem('userProfile', JSON.stringify(response));
		window.location = '/baseecommerce/control/newcustomer';
	});
};
var init = function() {
	FB.getLoginStatus(function(response) {
		if (response.status === 'connected') {
			cpr();
		}
	});
	FB.Event.subscribe('auth.login', function(response) {
		if (response.status === 'connected') {
			cpr();
		}
	});
};
if (!FB) {
	jQuery(document).ready(function($) {
		$('#fb-root').on('fb:init', function(e, obj) {
			FB = obj;
			//init();
			$('.fb-login-bt').click(function() {
				login(jQuery(this));
				cpr();
			});
		});
	});
} else {
	//init();
	jQuery(document).ready(function($) {
		$('.fb-login-bt').click(function() {
			login(jQuery(this));
			cpr();
		});
	});
}
