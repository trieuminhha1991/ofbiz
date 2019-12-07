$(document).ready(function() {
	SearchEngine.init();
});
if (typeof (SearchEngine) == "undefined") {
	var SearchEngine = (function() {
		var currentFocus = 0;
		var delaySearch = 0;
		var searchCondition = [ "fullName" ];
		var formatData = function(data) {
			var combobox = $("#searchCustomerInput");
			if (combobox.jqxComboBox('searchString') != undefined) {
				var val = combobox.jqxComboBox('searchString');
				if (val.charAt(0) == "#") {
					val = val.toUpperCase();
				}
				val = val.replace(/[#!$@]/gi, '');
				data.searchKey = val;
				data.conditions = processSearchInput();
				return data;
			}
		};
		var processSearchInput = function() {
			var search = $("#searchCustomerInput");
			var val = search.jqxComboBox('searchString');
			if (val) {
				var first = val.charAt(0);
				switch (first) {
				case "!":
					searchCondition = [ "agreementId" ];
					break;
				case "#":
					searchCondition = [ "partyId" ];
					break;
				case "$":
					searchCondition = [ "contactNumber" ];
					break;
				case "@":
				default:
					searchCondition = [ "fullName" ];
					break;
				}
				;
			}
			return JSON.stringify(searchCondition);
		};
		var getSearchCondition = function() {
			return {
				conditions : JSON.stringify(searchCondition)
			};
		};
		var setSearchCondition = function(condition) {
			searchCondition = condition;
		};
		var navigateContainer = function() {
			$(window).bind('keydown', 'f2', function(e) {
				focusSearch();
			});
		};
		var focusSearch = function() {
			$('#searchCustomerInput').jqxComboBox('focus');
		};
		var renderSearchResult = function(index, description, value) {
			if (!value || !value.partyIdFrom) {
				return label.NotFound;
			}
			var first = value.firstName ? capitalizeFirstLetter(value.firstName)
					: "";
			var middle = value.middleName ? capitalizeFirstLetter(value.middleName)
					: "";
			var last = value.lastName ? capitalizeFirstLetter(value.lastName)
					: "";
			var group = value.groupName ? capitalizeFirstLetter(value.groupName)
					: "";
			var type = value.partyTypeIdFrom;
			var role = value.roleTypeIdFrom;
			var str = "";
			var code = value.partyCode ? value.partyCode : value.partyIdFrom;
			if (type && type == "PERSON") {
				str += "<p><b>" + code + " - " + last + ' ' + middle + ' '
						+ first;
				if (role) {
					str += " - (" + role + ")";
				}
				str += "</b></p>";
			} else {
				str += "<p><b>" + code + " - " + group;
				if (role) {
					str += " - (" + role + ")";
				}
				str += "</b></p>";
			}
			if (value.contactNumber && value.contactNumber.length) {
				str += "<p><b><i class='fa fa-phone'></i></b>";
				for ( var x in value.contactNumber) {
					var z = false;
					for ( var y in value.contactNumber[x]) {
						if (y == 'PRIMARY_PHONE' || y == 'PRIMARY_HOME'
								|| y == 'PHONE_SHIPPING' || y == 'PHONE_WORK'
								|| y == "PHONE_MOBILE" || y == 'PHONE_HOME') {
							var obj = value.contactNumber[x][y];
							var ll = label[y];
							str += "<span><b>" + ll + "</b> : " + obj
									+ "</span>";
							var z = true;
							break;
						}
					}
					if (z) {
						break;
					}
				}
				str += "</p>";
			}
			if (value.infoString) {
				str += "<p><b><i class='fa fa-envelope-o'></i></b>";
				for ( var x in value.infoString) {
					var z = false;
					for ( var y in value.infoString[x]) {
						var obj = value.infoString[x][y];
						var ll = label[y] ? label[y] : y;
						str += "<span><b>" + label + "</b> : " + obj
								+ "</span>";
						var z = true;
						break;
					}
					if (z) {
						break;
					}
				}
				str += "</p>";
			}
			str += value.address ? "<p><b><i class='fa fa-map-marker'></i></b><span>"
					+ value.address + "</span></p>"
					: "";
			return str;
		};
		function capitalizeFirstLetter(string) {
			return string.charAt(0).toUpperCase() + string.slice(1);
		}
		var checkQuerySearchChange = function() {
			var searchInput = $("#searchCustomerInput");
			searchInput.on('change', function(e) {
				var val = searchInput.jqxComboBox('val');
				clearTimeout(delaySearch);
				delaySearch = setTimeout(function() {
					if (val.partyIdFrom) {
						CreateContact.clearAllForm();
						CookieLayer.setCurrentParty(val.partyIdFrom,
								val.roleTypeIdFrom);
						CreateMode = false;
						Processor.renderUser();
					}
				}, 500);
			});
		};
		var checkQuerySearchFocus = function() {
			var searchInput = $("#searchCustomerInput input[type='textarea']");
			var container = $("#searchCustomerInput");
			searchInput.on('focus', function() {
				var val = searchInput.val();
				if (val) {
					container.jqxComboBox('open');
				}
			});
		};
		var init = function() {
			hideSidebar();
			initComboboxsearchCustomerInput();
			checkQuerySearchChange();
			issueObj.checkAccessWithCustomer()
			// navigateContainer();
			var time2 = setTimeout(function() {
				checkQuerySearchFocus();
				clearTimeout(time2);
			}, 1000);
		};
		return {
			init : init,
			formatData : formatData,
			getSearchCondition : getSearchCondition,
			setSearchCondition : setSearchCondition,
			renderSearchResult : renderSearchResult,
			focusSearch : focusSearch
		};
	})();
}
if (typeof (CookieLayer) == "undefined") {
	var CookieLayer = (function() {
		var setCurrentParty = function(partyId, partyRole) {
			if (partyId != undefined) {
				if (typeof (currentParty) != undefined) {
					currentParty.partyId = partyId;
					currentParty.partyRole = partyRole;
				}
				var windowGuid = Utils.getWindowGUID();
				sessionStorage.setItem('currentPartyId' + windowGuid, partyId);
				sessionStorage.setItem('currentPartyRole' + windowGuid,
						partyRole);
				issueObj.checkAccessWithCustomer();
			}
		};
		var getCurrentParty = function() {
			var windowGuid = Utils.getWindowGUID();
			var currentParty = {
				partyId : sessionStorage.getItem('currentPartyId' + windowGuid),
				partyRole : sessionStorage.getItem('currentPartyRole'
						+ windowGuid)
			};
			return currentParty;
		};
		return {
			setCurrentParty : setCurrentParty,
			getCurrentParty : getCurrentParty,
		};
	})();
}