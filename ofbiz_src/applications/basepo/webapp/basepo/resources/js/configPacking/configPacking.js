$(function() {
	configPackingJs.initOther();
	configPackingJs.bindingDataToJqxGirdProductList();
	$("#dropDownButtonContentproductIdContainGrid").css("margin-top", "5px");
});
var configPackingJs = (function() {

	var bindingDataToJqxGirdProductList = function() {
		var  datafields =[ {
			name : "productId",
			type : "string"
		}, {
			name : "productName",
			type : "string"
		}, {
			name : "productCode",
			type : "string"
		} ];
		       
		var columns = [ {
			text : POProductId,
			datafield : "productId",
			width : "200",
			hidden : true
		}, {
			text : POProductId,
			datafield : "productCode",
			width : "200"
		}, {
			text : POProductName,
			datafield : "productName"
		} ];
		var config = {
			width: '100%', 
	   		virtualmode: true,
	   		showtoolbar: false,
	   		pageable: true,
	   		sortable: true,
	        filterable: true,	      
	        editable: true,
	        rowsheight: 26,
	        rowdetails: false,
	        useUrl: true,
	        url: 'JQGetPOListProducts',                
	        source: {pagesize: 10}
	  	};
	  	Grid.initGrid(config, datafields, columns, null, $("#jqxgridProduct"));
	}

	var initOther = function() {
		initElement();
		initValidateForm();
		initEvent();
	};
	var initElement = function() {
		$("#uomFromId1").jqxDropDownList({
			source : listUoms,
			width : 218,
			displayMember : "description",
			valueMember : "uomId",
			theme : theme,
			placeHolder : filterchoosestring
		});
		$("#uomFromIdBaseProduct").jqxDropDownList({
			source : listUoms,
			width : 218,
			displayMember : "description",
			valueMember : "uomId",
			theme : theme,
			placeHolder : filterchoosestring
		});
		$("#quantityConvert1").jqxNumberInput({
			inputMode : "simple",
			spinButtons : true,
			theme : theme,
			width : 218,
			decimalDigits : 0,
			min : 1,
			decimal : 1
		});
		$("#fromDate1").jqxDateTimeInput({
			theme : theme,
			width : 218
		});
		$("#thruDate1").jqxDateTimeInput({
			theme : theme,
			width : 218,
			allowNullDate : true,
			value : null
		});
		$("#productIdContainGrid").jqxDropDownButton({
			width : 218,
			theme : theme,
			dropDownWidth : 600
		});
		$("#productIdContainGrid").jqxDropDownButton("setContent",
				filterchoosestring);

		$("#alterpopupWindow").jqxWindow({
			width : 500,
			theme : theme,
			height : 270,
			resizable : false,
			isModal : true,
			autoOpen : false,
			cancelButton : $("#alterCancel"),
			modalOpacity : 0.7
		});
	};

	var initEvent = function() {
		$("#alterpopupWindow").on("close", function() {
			$("#alterpopupWindow").jqxValidator("hide");
		});
		$("#uomFromId1").on(
				"change",
				function(event) {
					var args = event.args;
					if (args) {
						var index = args.index;
						var item = args.item;
						var value = item.value;
						for ( var x in listUoms) {
							$("#uomFromIdBaseProduct").jqxDropDownList(
									"enableItem", listUoms[x].uomId);
						}
						if (value) {
							$("#uomFromIdBaseProduct").jqxDropDownList(
									"disableItem", value);
						}
					}
				});
		$("#uomFromIdBaseProduct").on(
				"change",
				function(event) {
					var args = event.args;
					if (args) {
						var index = args.index;
						var item = args.item;
						var value = item.value;
						for ( var x in listUoms) {
							$("#uomFromId1").jqxDropDownList("enableItem",
									listUoms[x].uomId);
						}
						if (value) {
							$("#uomFromId1").jqxDropDownList("disableItem",
									value);
						}
					}
				});
		var productIdByData = "";
		$("#jqxgridProduct")
				.on(
						"rowselect",
						function(event) {
							productIdByData = "";
							var args = event.args;
							var row = $("#jqxgridProduct").jqxGrid(
									"getrowdata", args.rowindex);
							var dropDownContent = "<div style=\"position: relative; margin-left: 3px;\">"
									+ row["productName"]
									+ "</div>";
							$("#productIdContainGrid").jqxDropDownButton(
									"setContent", dropDownContent);
							productIdByData = row["productId"];
						});

		$("#alterSave").click(function() {
			if ($("#alterpopupWindow").jqxValidator("validate")) {
				var row = {};
				row.uomFromId = $("#uomFromId1").val();
				row.uomToId = $("#uomFromIdBaseProduct").val();
				row.quantityConvert = $("#quantityConvert1").val();
				row.thruDate = $("#thruDate1").jqxDateTimeInput("getDate");
				row.fromDate = $("#fromDate1").jqxDateTimeInput("getDate");
				row.productId = productIdByData;
				$("#jqxgrid").jqxGrid("addRow", null, row, "first");
				$("#alterpopupWindow").jqxWindow("close");
			}
		});
	};

	var initValidateForm = function() {
		$("#alterpopupWindow").jqxValidator({
			rules : [ {
				input : "#uomFromId1",
				message : POCheckIsEmptyCreateLocationFacility,
				action : "change",
				position : "bottom",
				rule : function(input, commit) {
					var value = $("#uomFromId1").val();
					if (value) {
						return true;
					}
					return false;
				}
			}, {
				input : "#uomFromIdBaseProduct",
				message : POCheckIsEmptyCreateLocationFacility,
				action : "change",
				position : "bottom",
				rule : function(input, commit) {
					var value = $("#uomFromIdBaseProduct").val();
					if (value) {
						return true;
					}
					return false;
				}
			}, {
				input : "#quantityConvert1",
				message : POCheckIsEmptyCreateLocationFacility,
				action : "valueChanged",
				position : "bottom",
				rule : function(input, commit) {
					var value = $("#quantityConvert1").val();
					if (value > 0) {
						return true;
					}
					return false;
				}
			}, {
				input : "#productIdContainGrid",
				message : POCheckIsEmptyCreateLocationFacility,
				action : "close",
				position : "bottom",
				rule : function(input, commit) {
					var value = $("#productIdContainGrid").val();
					if (value != filterchoosestring) {
						return true;
					}
					return false;
				}
			} ],
			scroll : false
		});
	};
	return {
		initValidateForm : initValidateForm,
		initOther : initOther,
		bindingDataToJqxGirdProductList: bindingDataToJqxGirdProductList
	};
}());
