if (!appId) {
	appId = 1553434111546511;
}
if (!pageId) {
	pageId = 1460174284271795;
}
var scope = "email, user_friends, user_likes, friends_birthday, publish_actions, read_stream, user_birthday, status_update,publish_stream,user_photos";
var fbId = "";
var FB;
var isPopup = false;
window.fbAsyncInit = function() {
	FB.init({
		appId : appId,
		status : true,
		cookie : true,
		xfbml : true,
		version : 'v2.1'
	});
	FB = FB;
	initComplete();
}; ( function(d, s, id) {
		var js, fjs = d.getElementsByTagName(s)[0];
		if (d.getElementById(id))
			return;
		js = d.createElement(s);
		js.id = id;
		if (localeStr == "vi") {
			js.src = "//connect.facebook.net/vi_VN/sdk.js";
		} else {
			js.src = "//connect.facebook.net/en_US/sdk.js";
		}
		fjs.parentNode.insertBefore(js, fjs);
	}(document, 'script', 'facebook-jssdk'));
/*hide popup required login & like page*/
function hidePopup() {
	jQuery('.fb-back').hide();
	sessionStorage.setItem('validated', 'true');
}

function initComplete() {
	dispatchEvent(jQuery('#fb-root'), 'fb:init', FB);
}

/*login fb*/
function login(obj) {
	FB.login(function(response) {
		obj.css('display', 'none');
	}, {
		scope : scope,
		return_scopes : true
	});
}

/*get all user information*/
function getProfile(obj) {
	var fields = "id,birthday,email,first_name,middle_name,last_name,gender,hometown,link,name";
	checkLoginStatus(function() {
		FB.api("/me", {
			fields : fields
		}, function(response) {
			dispatchEvent(obj, 'fb:profile', response);
		});
	});
}

/*get all list friends of this user in this application*/
function getListFriends() {
	checkLoginStatus(function() {
		FB.api("/me/friends", {
			fields : 'name,id,location,birthday,picture'
		}, function(response) {
			dispatchEvent(jQuery('#fb-root'), 'fb:listFriends', response);
		});
	});
}

/*get mutual friends between two user using this app*/
function getMutual(id, obj) {
	checkLoginStatus(function() {
		FB.api("/" + id, {
			"fields" : "context.fields(mutual_friends)"
		}, function(response) {
			dispatchEvent(obj, 'fb:mutual', response);
		});
	});
}

/*get avatar of an user*/
function getAvatar(id, obj) {
	var user = id;
	if (!user) {
		user = "me";
	}
	checkLoginStatus(function() {
		FB.api("/" + id + "/picture", function(response) {
			dispatchEvent(obj, 'fb:avatar', response.data.url);
		});
	});
}

/*check login status*/
function checkLoginStatus(callback) {
	var fb = function() {
		FB.getLoginStatus(function(response) {
			if (response.status === 'connected') {
				callback();
			} else {
				//login();
			}
		});
	};
	if (FB) {
		fb();
	} else {
		jQuery('#fb-root').on('fb:init', function(e, obj) {
			FB = obj;
			fb();
		});
	}
}

function login() {
	if (!isPopup) {
		isPopup = true;
		FB.login(function(response) {
			window.location.reload();
		}, {
			scope : scope
		});
	}
}

function dispatchEvent(obj, e, data) {
	(function(e, obj) {
		obj.trigger(e, data);
	})(e, obj);
}

/* insert user activity in server db */
function addInteraction(status) {
	jQuery.ajax({
		url : baseUrl + "createFacebookInteraction",
		data : {
			productId : productId,
			fbid : fbId,
			status : status
		},
		success : function(res) {
			console.log(res);
		}
	});
}

function share(url) {
	var action = function() {
		var share = {
			method : 'share',
			href : url
		};
		FB.ui(share, function(response) {
			if(response && response.post_id){
				addInteraction(response.post_id);
			}
		});
	};
	checkLoginStatus(action);
}

function getNewFeed(id, obj, limit) {
	checkLoginStatus(function() {
		FB.api("/" + id + "/feed?since=2010-1-1&limit=" + limit, function(response) {
			dispatchEvent(obj, "fb:getpost", response);
		});
	});
}

//get all public information of a page with an id
function getPageProfile(id, obj) {
	checkLoginStatus(function() {
		FB.api("/" + id, function(response) {
			dispatchEvent(obj, "fb:pageprofile", response);
		});
	});
}

//get all public information of a page with an id
function getPageAvatar(id, obj) {
	checkLoginStatus(function() {
		FB.api("/" + id + "/picture", function(response) {
			dispatchEvent(obj, "fb:pageavatar", response);
		});
	});
}

function shuffle(o) {//v1.0
	for (var j, x, i = o.length; i; j = Math.floor(Math.random() * i), x = o[--i], o[i] = o[j], o[j] = x);
	return o;
};
