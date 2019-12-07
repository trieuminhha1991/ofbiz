if (typeof (UserInGroup) == "undefined") {
	var UserInGroup = (function() {
		var jqxwindow, jqxgrid;
		var initJqxElements = function() {
			jqxwindow.jqxWindow({
				theme: theme, width: 950, maxWidth: 1000, height: 485, resizable: false, isModal: true, autoOpen: false,
				cancelButton: $("#btnCancelUserInGroup"), modalOpacity: 0.7
			});
			
			var datafield = [
				{ name: "userLoginId", type: "string" },
				{ name: "partyId", type: "string" },
				{ name: "partyCode", type: "string" },
				{ name: "partyName", type: "string" }
			];
			var columnlist = [
				{ text: multiLang.userLoginId, datafield: "userLoginId", width: 250 },
				{ text: multiLang.EmployeeId, dataField: "partyCode", width: 250 },
				{ text: multiLang.EmployeeName, dataField: "partyName", minwidth: 300 }
			];
			var config = {
				showfilterrow: true,
				filterable: true,
				editable: false,
				width: "100%",
				height: 250,
				pageable: true,
				sortable: true,
				virtualmode : true,
				autoheight: true,
				selectionmode: "singlerow",
				url: "",
				source: {
					pagesize: 10
				}
			};
			Grid.initGrid(config, datafield, columnlist, null, jqxgrid);
			
			var contextmenu = $("#contextMenuUserInGroup").jqxMenu({ theme: theme, width: 150, autoOpenPopup: false, mode: "popup", popupZIndex: 999999 });
			jqxgrid.on("contextmenu", function () {
				return false;
			});
			jqxgrid.on("rowclick", function (event) {
				if (event.args.rightclick) {
					jqxgrid.jqxGrid("selectrow", event.args.rowindex);
					var scrollTop = $(window).scrollTop();
					var scrollLeft = $(window).scrollLeft();
					contextmenu.jqxMenu("open", parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop);
					return false;
				}
			});
			contextmenu.on("itemclick", function (event) {
				var args = event.args;
				var itemId = $(args).attr("id");
				switch (itemId) {
				case "deleteUserInGroup":
					var rowIndexSelected = jqxgrid.jqxGrid("getSelectedRowindex");
					DataAccess.executeAsync({
						url: "deleteUserInGroup",
						data: {
							partyId: jqxgrid.jqxGrid("getcellvalue", rowIndexSelected, "partyId"),
							groupId: UserInGroup.groupId}
						}, UserInGroup.notify);
					break;
				default:
					break;
				}
			});
		};
		var groupId = function() {
			return jqxwindow.data("partyId");
		};
		var loadGrid = function(partyId) {
			var adapter = jqxgrid.jqxGrid("source");
			if (adapter) {
				adapter.url = "jqxGeneralServicer?sname=JQGetListUserInGroup&partyId=" + partyId;
				adapter._source.url = adapter.url;
				jqxgrid.jqxGrid("source", adapter);
			}
		};
		var open = function(partyId) {
			if (partyId) {
				loadGrid(partyId);
				jqxwindow.data("partyId", partyId);
				var wtmp = window;
				var tmpwidth = jqxwindow.jqxWindow("width");
				jqxwindow.jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 70 }});
				jqxwindow.jqxWindow("open");
			}
		};
		var notify = function(res) {
			if(res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]){
				Grid.renderMessage(jqxgrid.attr("id"), multiLang.updateError, {
					autoClose : true,
					template : "error",
					opacity : 0.9
				});
			}else {
				Grid.renderMessage(jqxgrid.attr("id"), multiLang.updateSuccess, {
					autoClose : true,
					template : "info",
					opacity : 0.9
				});
				jqxgrid.jqxGrid("updatebounddata");
			}
		};
		return {
			init: function() {
				jqxwindow = $("#jqxwindowUserInGroup");
				jqxgrid = $("#jqxgridUserInGroup");
				initJqxElements();
				AddUserToGroup.init();
			},
			open: open,
			groupId: groupId,
			notify: notify
		};
	})();
}