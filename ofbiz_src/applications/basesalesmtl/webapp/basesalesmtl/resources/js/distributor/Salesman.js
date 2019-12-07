if (typeof (Salesman) == "undefined") {
	var Salesman = (function($) {
		var initJqxElements = function() {
			$("#jqxwindowViewListSalesman").jqxWindow({ theme: "olbius", cancelButton: $("#cancelViewListSalesman"),
				width: 750, maxWidth: 1845, minHeight: 310, height: 450, resizable: false,  isModal: true, autoOpen: false, modalOpacity: 0.7
			});
			$("#jqxNotificationSalesman").jqxNotification({ width: 250, position: "top-right",
				opacity: 0.9, autoClose: true, template: "info" });
		};
		var handleEvents = function() {
			$("#jqxwindowViewListSalesman").on("close", function (event) {
				$('#jqxgridViewListSalesman').jqxGrid('clear');
				$('#jqxgridAddSalesman').jqxGrid('clear');
				$('#jqxgridViewListSalesman').jqxGrid('clearselection');
				$('#jqxgridAddSalesman').jqxGrid('clearselection');
			});
			$("#saveListSalesman").click(function() {
				var data = AddSalesman.getValue();
				if (_.isEmpty(data)) {
					bootbox.alert(BSNotSalesmanSelected);
				} else {
					for ( var x in data) {
						$("#jqxgridViewListSalesman").jqxGrid('addrow', null, data[x]);
					}
					Salesman.notify({});
					AddSalesman.close();
				}
			});
		};
		var render = function(partyId) {
			var source =
	        {
	            datatype: "json",
	            datafields:
	            [{ name: "partyId", type: "string" },
				{ name: "partyCode", type: "string" },
				{ name: "firstName", type: "string" },
				{ name: "middleName", type: "string" },
				{ name: "lastName", type: "string" },
				{ name: "department", type: "string" }],
	            url: "getSalesmanAssigned?partyId=" + partyId + "&roleTypeIdFrom=" + roleTypeIdFrom + "&partyRelationshipTypeId=" + partyRelationshipTypeId,
	            async: false,
	            addrow: function (rowid, rowdata, position, commit) {
	                commit(DataAccess.execute({
							url: "assignSalesman",
							data: {partyIdFrom: partyId, partyIdTo: rowdata.partyId, roleTypeIdFrom: roleTypeIdFrom, partyRelationshipTypeId: partyRelationshipTypeId}}));
	            },
	            deleterow: function (rowid, commit) {

					commit(DataAccess.execute({
						url: "unassignSalesman",
						data: {partyIdFrom: partyId, partyIdTo: rowid, roleTypeIdFrom: roleTypeIdFrom, partyRelationshipTypeId: partyRelationshipTypeId}},
						Salesman.notify));
	            },
	            updaterow: function (rowid, newdata, commit) {
					
	                commit(true);
	            },
	            id: "partyId"
	        };
	        var dataAdapter = new $.jqx.dataAdapter(source);
	        $("#jqxgridViewListSalesman").jqxGrid({
	            source: dataAdapter,
	            localization: getLocalization(),
	            showfilterrow: true,
	            filterable: true,
				width: "100%",
	            height: "90%",
	            pagesize: 10,
	            pageable: true,
	            theme: "olbius",
	            sortable: true,
	            selectionmode: "singlerow",
	            columns: [
						{text: multiLang.EmployeeId, datafield: "partyCode", width: 150},
						{text: multiLang.DmsPartyLastName, datafield: "lastName", width: 150},
						{text: multiLang.DmsPartyMiddleName, datafield: "middleName", width: 150},
						{text: multiLang.DmsPartyFirstName, datafield: "firstName"},
						{text: multiLang.CommonDepartment, datafield: "department", width: 150}],
	            handlekeyboardnavigation: function (event) {
		                var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
		                if (key == 70 && event.ctrlKey) {
		                	$("#jqxgridViewListSalesman").jqxGrid("clearfilters");
							return true;
		                }
					}
	        });
		};
		var open = function() {
			Salesman.render($("#jqxwindowViewListSalesman").data("partyId"));
			var wtmp = window;
			var tmpwidth = $("#jqxwindowViewListSalesman").jqxWindow("width");
	        $("#jqxwindowViewListSalesman").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 70 }});
	        $("#jqxwindowViewListSalesman").jqxWindow("open");
		};
		var notify = function(res) {
			$("#jqxNotificationSalesman").jqxNotification("closeLast");
			if(res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]){
				$("#jqxNotificationSalesman").jqxNotification({ template: "error"});
				$("#notificationContentSalesman").text(multiLang.updateError);
				$("#jqxNotificationSalesman").jqxNotification("open");
			}else {
				$("#jqxNotificationSalesman").jqxNotification({ template: "info"});
				$("#notificationContentSalesman").text(multiLang.updateSuccess);
				$("#jqxNotificationSalesman").jqxNotification("open");
				$('#jqxgridViewListSalesman').jqxGrid('clearselection');
				$('#jqxgridAddSalesman').jqxGrid('clearselection');
			}
		};
		var _delete = function() {
			var rowIndexEditing = $("#jqxgridViewListSalesman").jqxGrid("getSelectedRowindex");
			if (rowIndexEditing != -1) {
                bootbox.dialog(uiLabelMap.ConfirmDelete,
                    [
                        {"label": uiLabelMap.wgcancel,
                            "icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
                            "callback": function() {bootbox.hideAll();}
                        },
                        {"label": uiLabelMap.wgok,
                            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
                            "callback": function() {
                                var partyId = $("#jqxgridViewListSalesman").jqxGrid("getcellvalue", rowIndexEditing, "partyId" );
                                $("#jqxgridViewListSalesman").jqxGrid("deleterow", partyId);
                            }
                        }
                    ]
                );
			} else {
				bootbox.alert(BSNotSalesmanSelected);
			}
		};
		return {
			init: function() {
				initJqxElements();
				handleEvents();
				AddSalesman.init();
			},
			open: open,
			notify: notify,
			render: render,
			_delete: _delete
		};
	})(jQuery);
}
if (typeof (AddSalesman) == "undefined") {
	var AddSalesman = (function($) {
		var initJqxElements = function() {
			$("#jqxwindowAddSalesman").jqxWindow({ theme: "olbius", cancelButton: $("#cancelAddSalesman"),
				width: 750, maxWidth: 1845, minHeight: 310, height: 450, resizable: false,  isModal: true, autoOpen: false, modalOpacity: 0.7
			});
		};
		var render = function(partyId, supervisorId) {
			var source =
	        {
	            datatype: "json",
	            datafields:
	            [{ name: "partyId", type: "string" },
				{ name: "partyCode", type: "string" },
				{ name: "partyIdTo", type: "string" },
				{ name: "firstName", type: "string" },
				{ name: "middleName", type: "string" },
				{ name: "lastName", type: "string" }],
	            url: "getSalesmanAvailable?partyId=" + partyId + "&roleTypeIdFrom=" + roleTypeIdFrom + "&partyRelationshipTypeId=" + partyRelationshipTypeId + "&supervisorId=" + supervisorId,
	            async: false,
	            id: "partyId"
	        };
	        var dataAdapter = new $.jqx.dataAdapter(source);
	        $("#jqxgridAddSalesman").jqxGrid({
	            source: dataAdapter,
	            localization: getLocalization(),
	            showfilterrow: true,
	            filterable: true,
				width: "100%",
	            height: "90%",
	            pagesize: 10,
	            pageable: true,
	            theme: "olbius",
	            sortable: true,
	            selectionmode: "checkbox",
	            columns: [
						{text: multiLang.EmployeeId, datafield: "partyCode", width: 150},
						{text: multiLang.DmsPartyLastName, datafield: "lastName", width: 150},
						{text: multiLang.DmsPartyMiddleName, datafield: "middleName", width: 150},
						{text: multiLang.DmsPartyFirstName, datafield: "firstName"},
						{text: multiLang.CommonDepartment, datafield: "partyIdTo", width: 150}],
	            handlekeyboardnavigation: function (event) {
		                var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
		                if (key == 70 && event.ctrlKey) {
		                	$("#jqxgridAddSalesman").jqxGrid("clearfilters");
							return true;
		                }
					}
	        });
		};
		var open = function() {
			AddSalesman.render($("#jqxwindowViewListSalesman").data("partyId"), $("#jqxwindowViewListSalesman").data("supervisorId"));
			var wtmp = window;
			var tmpwidth = $("#jqxwindowAddSalesman").jqxWindow("width");
	        $("#jqxwindowAddSalesman").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 70 }});
	        $("#jqxwindowAddSalesman").jqxWindow("open");
		};
		var close = function() {
			$('#jqxgridViewListSalesman').jqxGrid('updatebounddata');
			$('#jqxgridAddSalesman').jqxGrid('updatebounddata');
			$("#jqxwindowAddSalesman").jqxWindow("close");
		};
		var getValue = function() {
			var value = new Array();
			var rowindexes = $('#jqxgridAddSalesman').jqxGrid('getselectedrowindexes');
			for ( var x in rowindexes) {
				value.push($('#jqxgridAddSalesman').jqxGrid('getrowdata', rowindexes[x]));
			}
			return value;
		};
		return {
			init: function() {
				initJqxElements();
			},
			open: open,
			close: close,
			render: render,
			getValue: getValue
		};
	})(jQuery);
}