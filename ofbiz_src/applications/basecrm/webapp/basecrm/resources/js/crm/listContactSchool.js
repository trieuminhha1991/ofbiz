$(document).ready(
		function() {
			$("#jqxNotificationNestedSchool").jqxNotification({
				width : "100%",
				appendContainer : "#container",
				opacity : 0.9,
				autoClose : true,
				template : "info"
			});
			if (getAllCookie().checkContainValue("newContact")) {
				deleteCookie("newContact");
				$("#jqxNotificationNestedSchool").jqxNotification({
					template : "info"
				});
				$("#notificationContentNestedSchool")
						.text(multiLang.addSuccess);
				$("#jqxNotificationNestedSchool").jqxNotification("open");
			}
			if (getAllCookie().checkContainValue("updateContact")) {
				deleteCookie("updateContact");
				$("#jqxNotificationNestedSchool").jqxNotification({
					template : "info"
				});
				$("#notificationContentNestedSchool").text(
						multiLang.updateSuccess);
				$("#jqxNotificationNestedSchool").jqxNotification("open");
			}
			$("#contextMenuSchool").jqxMenu({
				theme : "olbius",
				width : 200,
				autoOpenPopup : false,
				mode : "popup",
				theme : theme
			});
			$("#contextMenuSchool").on(
					"itemclick",
					function(event) {
						var args = event.args;
						var rowindex = $("#ListContactSchool").jqxGrid(
								"getselectedrowindex");
						var tmpKey = $.trim($(args).text());
						if (tmpKey == refresh) {
							$("#ListContactSchool").jqxGrid("updatebounddata");
						} else if (tmpKey == viewDetail) {
							var rowIndexSelected = $("#ListContactSchool")
									.jqxGrid("getSelectedRowindex");
							var rowData = $("#ListContactSchool").jqxGrid(
									"getrowdata", rowIndexSelected);
							var partyId = rowData.partyId;
							window.location.href = "ViewSchoolDetails?partyId="
									+ partyId;
						} else if (tmpKey == edit) {
							var rowIndexSelected = $("#ListContactSchool")
									.jqxGrid("getSelectedRowindex");
							var rowData = $("#ListContactSchool").jqxGrid(
									"getrowdata", rowIndexSelected);
							var partyId = rowData.partyId;
							window.location.href = "EditContactSchool?partyId="
									+ partyId;
						}
					});
		});