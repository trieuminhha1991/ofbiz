$(document).ready(function() {
	PrimaryAddress.init();
});
if (typeof (PrimaryAddress) == "undefined") {
	var PrimaryAddress = (function() {
		var mainGrid, contextMenu, partyId;
		var initJqxElements = function() {
			$("#jqxwindowListPrimaryAddress").jqxWindow({
				theme: "olbius", width: 950, height: 480, maxWidth: 1845, resizable: false, isModal: true, autoOpen: false,
				cancelButton: $("#btnCloseWindow"), modalOpacity: 0.7
			});
			$("#jqxNotification").jqxNotification({ opacity: 0.9, autoClose: true, template: "info" });
			contextMenu.jqxMenu({ theme: "olbius", width: 200, autoOpenPopup: false, mode: "popup", popupZIndex: 98001 });
			render();
			Grid.addContextMenuHoverStyle(mainGrid, contextMenu);
		};
		var render = function() {
			var datafield =  [
        		{ name: "partyId", type: "string"},
      			{ name: "contactMechId", type: "string"},
      			{ name: "contactMechPurposeTypeId", type: "string"},
      			{ name: "fromDate", type: "date"},
      			{ name: "toName", type: "string"},
      			{ name: "attnName", type: "string"},
      			{ name: "address1", type: "string"},
      			{ name: "city", type: "string"},
      			{ name: "stateProvinceGeoId", type: "string"},
      			{ name: "stateProvinceGeoName", type: "string"},
      			{ name: "postalCode", type: "string"},
      			{ name: "countryGeoId", type: "string"},
      			{ name: "countryGeoName", type: "string"},
      			{ name: "districtGeoId", type: "string"},
      			{ name: "districtGeoName", type: "string"},
      			{ name: "wardGeoId", type: "string"},
      			{ name: "wardGeoName", type: "string"}
        	];
        	var columnlist = [
	  			{ text: multiLang.BSContactMechId, dataField: "contactMechId", width: 150},
	  			{ text: multiLang.BSWard, datafield: "wardGeoName", width: 200 },
	  			{ text: multiLang.BSCounty, datafield: "districtGeoName", width: 200 },
	  			{ text: multiLang.BSStateProvince, datafield: "stateProvinceGeoName", width: 200 },
	  			{ text: multiLang.BSCountry, datafield: "countryGeoName" }
        	];
        	var config = {
	  			showfilterrow: true,
	  			filterable: true,
	  			editable: false,
	  			width: "100%",
	  			height: 350,
	  			pagesize: 10,
	  			pageable: true,
	  			sortable: true,
	  			enabletooltips: true,
	  			autoheight: false,
	  			selectionmode: "singlerow",
	  			url: "",
        		source: {pagesize: 5, id: "contactMechId"}
        	};
        	Grid.initGrid(config, datafield, columnlist, null, mainGrid);
		};
		var handleEvents = function() {
			$("#addAddress").click(function() {
				AddPrimaryAddress.open();
			});
			mainGrid.on("contextmenu", function () {
                return false;
            });
            mainGrid.on('rowclick', function (event) {
                if (event.args.rightclick) {
                	mainGrid.jqxGrid('selectrow', event.args.rowindex);
                    var scrollTop = $(window).scrollTop();
                    var scrollLeft = $(window).scrollLeft();
                    contextMenu.jqxMenu('open', parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop);
                    return false;
                }
            });
            contextMenu.on("itemclick", function (event) {
                var args = event.args;
                var itemId = $(args).attr("id");
		        switch (itemId) {
				case "mnAdrEdit":
					var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
					var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
					AddPrimaryAddress.open(rowData);
					break;
				case "mnAdrDelete":
					var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
					var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
					AddPrimaryAddress._delete(rowData);
					break;
				default:
					break;
				}
            });
		};
		var open = function(partyId) {
			if (partyId) {
				partyId = partyId;
				var adapter = mainGrid.jqxGrid("source");
				if(adapter){
					adapter.url = "jqxGeneralServicer?sname=JQGetPartyAddress&contactMechPurposeTypeId=PRIMARY_LOCATION&partyId=" + partyId;
					adapter._source.url = "jqxGeneralServicer?sname=JQGetPartyAddress&contactMechPurposeTypeId=PRIMARY_LOCATION&partyId=" + partyId;
					mainGrid.jqxGrid("source", adapter);
				}
				var wtmp = window;
		    	var tmpwidth = $("#jqxwindowListPrimaryAddress").jqxWindow("width");
		        $("#jqxwindowListPrimaryAddress").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 40 }});
		    	$("#jqxwindowListPrimaryAddress").jqxWindow("open");
			}
		};
		var notify = function(res) {
			$("#jqxNotification").jqxNotification("closeLast");
			if(res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]){
				var errormes = "";
				res["_ERROR_MESSAGE_"]?errormes=res["_ERROR_MESSAGE_"]:errormes=res["_ERROR_MESSAGE_LIST_"];
				$("#jqxNotification").jqxNotification({ template: "error"});
		      	$("#notificationContent").text(errormes);
		      	$("#jqxNotification").jqxNotification("open");
			}else {
				$("#jqxNotification").jqxNotification({ template: "info"});
		      	$("#notificationContent").text(multiLang.updateSuccess);
		      	$("#jqxNotification").jqxNotification("open");
		      	mainGrid.jqxGrid("updatebounddata");
			}
		};
		return {
			init: function() {
				mainGrid = $("#jqxgridPrimaryAddress");
				contextMenu = $("#contextMenuAddress");
				initJqxElements();
				handleEvents();
			},
			open: open,
			notify: notify
		}
	})();
}