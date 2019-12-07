$(document).ready(function () {
	SalesDepartment.init();
});
if (typeof (SalesDepartment) == "undefined") {
	var SalesDepartment = (function() {
		var mainGrid, contextMenu;
		var initJqxElements = function() {
			contextMenu.jqxMenu({ theme: "olbius", width: 200, autoOpenPopup: false, mode: "popup" });
			Grid.addContextMenuHoverStyle(mainGrid, contextMenu);
		};
		var handleEvents = function() {
			mainGrid.on("contextmenu", function () {
                return false;
            });
            mainGrid.on("rowClick", function (event) {
                var args = event.args;
                if (args.originalEvent.button == 2) {
                    var scrollTop = $(window).scrollTop();
                    var scrollLeft = $(window).scrollLeft();
                    contextMenu.jqxMenu("open", parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop);
                    return false;
                }
            });
            contextMenu.on("itemclick", function (event) {
                var args = event.args;
                var itemId = $(args).attr("id");
		        switch (itemId) {
				case "listAddress":
						var selection = mainGrid.jqxTreeGrid("getSelection");
		                var partyId = selection[0].partyId;
		                $("#txtPartyId").val(partyId);
		                PrimaryAddress.open(partyId);
					break;
				default:
					break;
				}
            });
		};
		var renderTree = function() {
			var source = {
					dataType: "json",
					dataFields: [
		               { name: "partyId", type: "string" },
		               { name: "partyCode", type: "string" },
		               { name: "partyIdFrom", type: "string" },
		               { name: "partyName", type: "string" },
		               { name: "postalAddress", type: "string"},
		               { name: "contactMechId", type: "string"},
		               { name: "totalEmployee", type: "number" },                   
		               { name: "comments", type: "string" },                   
		               { name: "expanded",type: "bool"},
		           ],
		           hierarchy:
		           {
		               keyDataField: { name: "partyId" },
		               parentDataField: { name: "partyIdFrom" }
		           },
		           id: "partyId",
		           url: "getOrganizationUnit?partyId=" + rootPartyId,
		           root: "listReturn"
			};
			var dataAdapter = new $.jqx.dataAdapter(source);
			mainGrid.jqxTreeGrid({
				width: "100%",
				source: source,
		        sortable: true,
		        theme: "olbius",
		        columnsResize: true,
		        columns: [
			          		{text: uiLabelMap.OrgUnitName, datafield: "partyName", minWidth: 400,},			
			          		{text: uiLabelMap.OrgUnitId, datafield: "partyCode", width: 300},
			          		{text: uiLabelMap.NumEmployees, datafield: "totalEmployee", width: 200, cellsalign: "right"}
			          	]
			});
		};
		return {
			init: function() {
				mainGrid = $("#treePartyGroupGrid");
				contextMenu = $("#contextMenu");
				renderTree();
				initJqxElements();
				handleEvents();
			}
		}
	})();
}