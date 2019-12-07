function getPersonAge(birthDate) {
	var birthYear = new Date(birthDate).getFullYear();
	var currentYear = new Date().getFullYear();
	var partyAge = currentYear - birthYear + 1;
	if (partyAge < 0) {
		return "";
	}
	return "<span class='green'> (" + partyAge + ") " + multiLang.age;
}
var listGender = [];
var mapGender = {};
$(document).ready(
		function() {
			$("#jqxNotificationNested").jqxNotification({
				width : "100%",
				appendContainer : "#container",
				opacity : 0.9,
				autoClose : true,
				template : "info"
			});
			mapGender = {
				"M" : multiLang.male,
				"F" : multiLang.female
			};
			listGender = [ {
				value : "M",
				label : multiLang.male
			}, {
				value : "F",
				label : multiLang.female
			} ];
			var contextMenu = $("#contextMenu").jqxMenu({
				theme : theme,
				width : 200,
				autoOpenPopup : false,
				mode : "popup"
			});
			contextMenu.on("itemclick", function(event) {
				var args = event.args;
				var itemId = $(args).attr("id");
				switch (itemId) {
				case "viewPersonDetails":
					var rowIndexSelected = $("#jqxgrid").jqxGrid(
							"getSelectedRowindex");
					var rowData = $("#jqxgrid").jqxGrid("getrowdata",
							rowIndexSelected);
					if (rowIndexSelected == -1) {
						rowIndexSelected = gridSelecting
								.jqxGrid("getSelectedRowindex");
						rowData = gridSelecting.jqxGrid("getrowdata",
								rowIndexSelected);
					}
					var partyId = rowData.partyId;
					window.location.href = "ViewPersonDetails?partyId="
							+ partyId;
					break;
				// case "editPersonDetails":
				// var rowIndexSelected =
				// $("#jqxgrid").jqxGrid("getSelectedRowindex");
				// var rowData = $("#jqxgrid").jqxGrid("getrowdata",
				// rowIndexSelected);
				// if (rowIndexSelected == -1) {
				// rowIndexSelected =
				// gridSelecting.jqxGrid("getSelectedRowindex");
				// rowData = gridSelecting.jqxGrid("getrowdata",
				// rowIndexSelected);
				// }
				// var partyId = rowData.partyId;
				// window.location.href = "AddNewContactFamily?partyId=" +
				// partyId;
				// break;
				// case "addNewMember":
				// var rowIndexSelected =
				// $("#jqxgrid").jqxGrid("getSelectedRowindex");
				// var rowData = $("#jqxgrid").jqxGrid("getrowdata",
				// rowIndexSelected);
				// if (rowIndexSelected == -1) {
				// rowIndexSelected =
				// gridSelecting.jqxGrid("getSelectedRowindex");
				// rowData = gridSelecting.jqxGrid("getrowdata",
				// rowIndexSelected);
				// }
				// var familyId = rowData.familyId;
				// window.location.href = "AddNewContactFamily?familyId=" +
				// familyId;
				// break;
				// case "addMember":
				// $("#jqxwindowPerson").jqxWindow("open");
				// break;
				// case "deleteMember":
				// var rowIndexSelected =
				// $("#jqxgrid").jqxGrid("getSelectedRowindex");
				// var rowData = $("#jqxgrid").jqxGrid("getrowdata",
				// rowIndexSelected);
				// if (rowIndexSelected == -1) {
				// rowIndexSelected =
				// gridSelecting.jqxGrid("getSelectedRowindex");
				// rowData = gridSelecting.jqxGrid("getrowdata",
				// rowIndexSelected);
				// gridSelecting.jqxGrid("deleterow", rowData.uid);
				// }
				// break;
				default:
					break;
				}
			});
			contextMenu.on("shown", function() {
				var rowIndexSelected = $("#jqxgrid").jqxGrid(
						"getSelectedRowindex");
				var rowData = $("#jqxgrid").jqxGrid("getrowdata",
						rowIndexSelected);
				if (rowIndexSelected == -1) {
					contextMenu.jqxMenu("disable", "addNewMember", true);
					contextMenu.jqxMenu("disable", "addMember", true);
					contextMenu.jqxMenu("disable", "deleteMember", false);
				} else {
					contextMenu.jqxMenu("disable", "addNewMember", false);
					contextMenu.jqxMenu("disable", "addMember", false);
					contextMenu.jqxMenu("disable", "deleteMember", true);
				}
			});
			$("body").on("click", function() {
				if (contextMenu) {
					contextMenu.jqxMenu("close");
				}
			});
		});
function deleteMember(rowData) {
	var result;
	var success = true;
	$.ajax({
		url : "removeMemberInFamily",
		type : "POST",
		data : {
			familyId : rowData.familyId,
			partyId : rowData.partyId,
			roleTypeIdFrom : rowData.roleTypeIdFrom
		},
		async : false,
		success : function(res) {
			result = res;
		}
	}).done(function() {
		$("#jqxNotificationNested").jqxNotification("closeLast");
		if (result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]) {
			success = false;
			$("#jqxNotificationNested").jqxNotification({
				template : "error"
			});
			$("#notificationContentNested").text(multiLang.updateError);
			$("#jqxNotificationNested").jqxNotification("open");
		} else {
			$("#jqxNotificationNested").jqxNotification({
				template : "info"
			});
			$("#notificationContentNested").text(multiLang.updateSuccess);
			$("#jqxNotificationNested").jqxNotification("open");
		}
	});
	return success;
}
var isBusinesesMode = false;