//var data = DataAccess.getData({
//				url: "loadFriends",
//				data: {userLoginPartyId: userLoginPartyId},
//				source: "listFriends"});
//
//DataAccess.execute({
//	url: "cancelFriend",
//	data: {
//		partyId: partyId,
//		fromDate: fromDate,
//		userLoginPartyId: userLoginPartyId}
//	}, FriendRequestLayer.reloadDataTable);

if (typeof (DataAccess) == "undefined") {
	var DataAccess = (function($) {
		var getData = function(parameters) {
			var result;
			$.ajax({
				url: parameters.url?parameters.url:"",
				type: "POST",
				data: parameters.data?parameters.data:"",
				async: false,
				dataType: "json",
				success: function() {}
			}).done(function(res) {
				if (parameters.source == "*") {
					result = res;
				} else {
					result = res[parameters.source?parameters.source:""];
				}
			});
			return result;
		};
		var execute = function(parameters, callback) {
			var result;
			$.ajax({
				url: parameters.url?parameters.url:"",
				type: parameters.type?parameters.type:"POST",
				data: parameters.data?parameters.data:"",
				async: false,
				dataType: parameters.dataType?parameters.dataType:"json",
			}).done(function(res) {
				if(res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]){
					result = false;
				} else {
					result = true;
				}
				if (typeof (callback) == "function") {
					callback(res);
				}
			});
			return result;
		};
		var executeAsync = function(parameters, callback) {
			var result;
			$.ajax({
				url: parameters.url?parameters.url:"",
				type: parameters.type?parameters.type:"POST",
				data: parameters.data?parameters.data:"",
				dataType: parameters.dataType?parameters.dataType:"json",
			}).done(function(res) {
				if(res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]) {
					result = false;
				} else {
					result = true;
				}
				if (typeof (callback) == "function") {
					callback(res);
				}
			});
			return result;
		};
		var uploadFile = function(file) {
			var result;
			var form_data= new FormData();
			form_data.append("uploadedFile", file);
			$.ajax({
				url: "jackrabbitUploadFile",
				type: "POST",
				data: form_data,
				cache : false,
				contentType : false,
				processData : false,
				async: false,
			}).done(function(res) {
				result = res["path"];
			});
			return result;
		};
		return {
			getData: getData,
			execute: execute,
			executeAsync: executeAsync,
			uploadFile: uploadFile
		};
	})(jQuery);
}
if (typeof (LocalUtil) == "undefined") {
	var LocalUtil = (function() {
		var getValueSelectedJqxComboBox = function(input) {
			var items = input.jqxComboBox('getSelectedItems');
			var values = new Array();
			for ( var x in items) {
				values.push(items[x].value);
			}
			return values;
		};
		var getPartyName = function (partyId) {
			if (partyId) {
				return DataAccess.getData({
					url: "getPartyName",
					data: {partyId: partyId},
					source: "partyName"});
			}
		};
		var setBreadcrumb = function(value) {
			$($(".breadcrumb").children()[2]).html("<span class='divider'><i class='icon-angle-right'></i></span>" + value);
		};
		return {
			getValueSelectedJqxComboBox: getValueSelectedJqxComboBox,
			getPartyName: getPartyName,
			setBreadcrumb: setBreadcrumb
		}
	})();
}
function hasWhiteSpace(s) {
	return /\s/g.test(s);
}
$('body').ajaxStart(function() {
			$('body').css({'cursor':'progress'});
		}).ajaxStop(function() {
			$('body').css({'cursor':'default'});
		}).ajaxError(function( event, jqxhr, settings, thrownError ) {
			console.log(thrownError);
		});