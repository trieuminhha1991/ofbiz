if (typeof (GeneralInfo) == "undefined") {
	var GeneralInfo = (function() {
		var supplierGrid;
		var initJqxElements = function() {
			var initEmployeeDrDGrid = function(dropdown, grid, width){
				var datafields =
				[
					{ name: "partyId", type: "string" },
					{ name: "partyCode", type: "string" },
					{ name: "groupName", type: "string" },
					{ name: "infoString", type: "string" },
					{ name: "contactNumber", type: "string" },
					{ name: "addressDetail", type: "string" }
				];
				var columns =
				[
					{ text: uiLabelMap.POSupplierId, datafield: "partyCode", width: 150 },
					{ text: uiLabelMap.POSupplierName, datafield: "groupName", width: 250 },
					{ text: uiLabelMap.BPOAddress1, datafield: "addressDetail", width: 250 },
					{ text: uiLabelMap.POEmailAddr, datafield: "infoString", width: 200 },
					{ text: uiLabelMap.POTelecomNumber, datafield: "contactNumber", width: 200 }
				];
				GridUtils.initDropDownButton({url: "jqGetListPartySupplier", filterable: true, showfilterrow: true,
					width: 700, source: {pagesize: 5}, closeOnSelect: "Y",
							handlekeyboardnavigation: function (event) {
								var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
								if (key == 70 && event.ctrlKey) {
									supplierGrid.jqxGrid("clearfilters");
									return true;
								}
							}, clearOnClose: false, dropdown: {width: 300}}, datafields, columns, null, grid, dropdown, "partyId", "groupName");
			};
			initEmployeeDrDGrid($("#supplierDropdown"), supplierGrid, 700);
			
			$("#shipAfterDate").jqxDateTimeInput({ width : 250, formatString : "dd/MM/yyyy HH:mm" });
			$("#shipAfterDate").jqxDateTimeInput("clear");

			$("#shipBeforeDate").jqxDateTimeInput({ width : 250, formatString : "dd/MM/yyyy HH:mm" });
			$("#shipBeforeDate").jqxDateTimeInput("clear");
			
			$("#jqxRadioButtonOnePO").jqxRadioButton({ width : 50, height : 25, checked : true });
			$("#jqxRadioButtonMultiPO").jqxRadioButton({ width : 50, height : 25 });
			
			$("#jqxDropDownFacility").jqxDropDownList({ source : facilityData, theme : theme, width : 300, displayMember : "description",
				valueMember : "facilityId", disabled : false, placeHolder : uiLabelMap.BSClickToChoose, selectedIndex : 0 });
			
			setTimeout(function() {
				supplierGrid.jqxGrid({ selectedrowindex: 0});
			}, 10);
		};
		var handleEvents = function() {
			$("#jqxRadioButtonOnePO").on("change", function(event) {
				$("#jqxDropDownFacility").jqxDropDownList({
					disabled : false
				});
				$("#labelFacility").addClass("asterisk");
				$("#labelFacility").html(uiLabelMap.ReceiveToFacility);
				
				if ($("#faId").hasClass("hide")) {
					$("#faId").removeClass("hide");
				}
			});
			$("#jqxRadioButtonMultiPO").on("change", function(event) {
				$("#jqxDropDownFacility").jqxDropDownList({
					disabled : true
				});
				$("#labelFacility").removeClass("asterisk");
				$("#labelFacility").html(uiLabelMap.ReceiveToFacility + "&nbsp;&nbsp;&nbsp;");
				
				if (!$("#faId").hasClass("hide")) {
					$("#faId").addClass("hide");
				}
			});
			
			supplierGrid.on("rowselect", function (event) {
				var args = event.args;
				var rowBoundIndex = args.rowindex;
				var rowData = args.row;
				if (rowData.partyId) {
					var adapter = mainGrid.jqxGrid("source");
					if (adapter) {
						adapter.url = "jqxGeneralServicer?sname=jqGetListSupplierProducts&partyId=" + rowData.partyId;
						adapter._source.url = adapter.url;
						mainGrid.jqxGrid("source", adapter);
					}
				}
			});
		};
		var initValidator = function() {
			$("#initPurchaseInfo").jqxValidator({
				rules : [ {
					input : "#supplierDropdown",
					message : uiLabelMap.FieldRequired,
					action : "close",
					rule : function(input, commit) {
						var value = input.jqxDropDownButton("val").trim();
						if (value) {
							return true;
						}
						return false;
					}
				}, {
					input : "#shipAfterDate",
					message : uiLabelMap.FieldRequired,
					action : "valueChanged",
					rule : function(input, commit) {
						if (input.jqxDateTimeInput("getDate")) {
							return true;
						}
						return false;
					}
				}, {
					input : "#shipBeforeDate",
					message : uiLabelMap.FieldRequired,
					action : "valueChanged",
					rule : function(input, commit) {
						if (input.jqxDateTimeInput("getDate")) {
							return true;
						}
						return false;
					}
				}, {
					input : "#shipAfterDate",
					message : uiLabelMap.CannotBeforeNow,
					action : "valueChanged",
					rule : function(input, commit) {
						if (input.jqxDateTimeInput("getDate") <= new Date()) {
							return false;
						}
						return true;
					}
				}, {
					input : "#shipBeforeDate",
					message : uiLabelMap.CannotBeforeNow,
					action : "valueChanged",
					rule : function(input, commit) {
						if (input.jqxDateTimeInput("getDate") <= new Date()) {
							return false;
						}
						return true;
					}
				}, {
					input : "#shipAfterDate",
					message : uiLabelMap.CanNotAfterShipBeforeDate2,
					action : "valueChanged",
					rule : function(input, commit) {
						var shipAfterDate = input.jqxDateTimeInput("getDate");
						var shipBeforeDate = $("#shipBeforeDate")
								.jqxDateTimeInput("getDate");
						if (shipBeforeDate && (shipBeforeDate <= shipAfterDate)) {
							return false;
						}
						return true;
					}
				}, {
					input : "#shipBeforeDate",
					message : uiLabelMap.CanNotBeforeShipAfterDate2,
					action : "valueChanged",
					rule : function(input, commit) {
						var shipAfterDate = $("#shipAfterDate")
								.jqxDateTimeInput("getDate");
						var shipBeforeDate = input.jqxDateTimeInput("getDate");
						if (shipAfterDate && (shipBeforeDate <= shipAfterDate)) {
							return false;
						}
						return true;
					}
				} ],
				position : "topcenter"
			});
		};
		var validator = function() {
			return $("#initPurchaseInfo").jqxValidator("validate");
		};
		var getValue = function() {
			var value = new Object();
			value.supplierId = Grid.getDropDownValue($("#supplierDropdown")).trim();
			value.supplierName = $("#supplierDropdown").val().trim();
			value.multiPO = $("#jqxRadioButtonMultiPO").jqxRadioButton("val");
			value.facilityId = $("#jqxDropDownFacility").jqxDropDownList("val");
			value.facilityName = $("#jqxDropDownFacility").jqxDropDownList("getSelectedItem").label;
			value.shipAfterDate = $("#shipAfterDate").jqxDateTimeInput("getDate");
			value.shipBeforeDate = $("#shipBeforeDate").jqxDateTimeInput("getDate");
			return value;
		};
		return {
			init: function() {
				supplierGrid = $("#supplierListGrid");
				initJqxElements();
				handleEvents();
				initValidator();
			},
			getValue: getValue,
			validator: validator
		};
	})();
}