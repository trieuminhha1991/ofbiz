var Assigned = (function() {
	var initElement = function() {
		initEmployee($('#ListEmployee'));
	};
	var initEmployee = function(element) {
		element.on('change', function(event) {
			var args = event.args;
			if (args) {
				var index = args.index;
				var item = args.item;
				var label = item.label;
				var value = item.value;
				changeSourceGrid(value);
			}
		});
		var sourceWard = {
			datatype : "json",
			datafields : [ {
				name : 'partyId'
			}, {
				name : 'partyDetail'
			} ],
			url : "getCallCenterEmployee"
		};
		var dataAdapterWard = new $.jqx.dataAdapter(sourceWard);
		element.jqxComboBox({
			selectedIndex : 0,
			source : dataAdapterWard,
			theme : "olbius",
			placeHolder : "",
			displayMember : "partyDetail",
			valueMember : "partyId",
			width : "300px",
			height : 25,
			'autoDropDownHeight' : true
		});
	};
	var changeSourceGrid = function(partyId) {
		var currentSource = $('#ListResourceAssigned').jqxGrid('source');
		if (currentSource) {
			currentSource._source.url = "jqxGeneralServicer?sname=JQGetListContactFamilyByCampaign&partyId="
					+ partyId + "&campaignId=" + campaignId;
			$('#ListResourceAssigned').jqxGrid({
				source : currentSource
			});
			$('#ListResourceAssigned').jqxGrid({
				enabletooltips : true
			});
		}
	};
	var bindTabClick = function() {
		$('#AssignResourceTab').on('shown.bs.tab', function(e) {
			var href = $(e.target).attr('href');
			switch (href) {
			case "#assignedContact":
				var obj = $("#ListResourceAssigned");
				if (!obj.data('init')) {
					obj.data('init', true);
					initGridListResourceAssigned();
					changeSourceGrid($('#ListEmployee').val());
				}
				break;
			}
		});
	};
	var bindEvent = function() {

	};
	var initContextEmployee = function(data) {
		var contextmenu = $('#assignedMenu');
		var element = $('#assignedMenu ul');
		for ( var x in data) {
			var obj = $('<li></li>');
			obj.attr('id', 'Employee-' + data[x].partyId);
			obj.attr('data-id', data[x].partyId);
			obj.html(data[x].partyFullName);
			element.append(obj);
		}
		var unAssigne = $('<li></li>');
		unAssigne.attr('id', 'unAssigne');
		unAssigne.attr('data-id', 'unAssigne');
		unAssigne.html(uiLabelMap.unAssigne);
		element.append(unAssigne);
		contextmenu.jqxMenu({
			theme : 'olbius',
			width : 230,
			autoOpenPopup : false,
			mode : 'popup'
		});

		contextmenu.on('shown', function() {
			for ( var x in data) {
				contextmenu.jqxMenu('disable', 'Employee-' + data[x].partyId,
						false);
			}
			contextmenu.jqxMenu('disable', 'Employee-'
					+ $("#ListEmployee").val(), true);
		});

		contextmenu.on('itemclick', function(event) {
			var args = event.args;
			var itemId = $(args).data('id');
			assignContactToParty(itemId);
		});
	};
	var assignContactToParty = function(partyId) {
		if (campaignId) {
			var contacts = getContactSelected();
			var time = Assign.getFromThruDate();
			var obj = {
				marketingCampaignId : campaignId,
				fromDate : time.fromDate,
				thruDate : time.thruDate,
				partyId : partyId,
				contacts : JSON.stringify(contacts)
			};
			var url = "assignContactToParty";
			if (partyId == "unAssigne") {
				url = "unassignContactToParty";
				obj.partyId = $('#ListEmployee').val();
			}
			$.ajax({
				url : url,
				data : obj,
				type : "POST",
				success : function(res) {
					// Loading.hide('loadingMacro');
					assignSuccess(res);
				},
				beforeSend : function() {
					// Loading.show('loadingMacro');
				},
			});
		}
	};
	var assignSuccess = function(res) {
		if (!res["_ERROR_MESSAGE_"] && !res["_ERROR_MESSAGE_LIST_"]) {
			$('#ListResourceAssigned').jqxGrid('updatebounddata');
			$('#ListResourceAssigned').jqxGrid('clearSelection');
			$("#notifyAssign").notify(uiLabelMap.assignSuccess, {
				position : "left",
				className : "success"
			});
		} else {
			$("#notifyAssign").notify(uiLabelMap.assignError, {
				position : "left",
				className : "error"
			});
		}
	};
	var getContactSelected = function() {
		var all = $('#ListResourceAssigned').jqxGrid('getselectedrowindexes');
		var contacts = [];
		for ( var x in all) {
			var obj = $('#ListResourceAssigned').jqxGrid('getrowdata', all[x]);
			if (obj) {
				contacts.push({
					partyId : obj['partyId'],
					fromDate : obj['fromDate']
				});
			}
		}
		return contacts;
	};
	var init = function() {
		initElement();
		bindEvent();
		bindTabClick();
	};
	return {
		init : init,
		initContextEmployee : initContextEmployee
	};
})();
$(document).ready(function() {
	Assigned.init();
});
