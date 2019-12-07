$(function() {
	supplierNewTarget.init();
});

var supplierNewTarget = (function() {
	var productDDB;
	var validatorVAL;
	var init = function() {
		initElement();
		initEvent();
		initElementComplex();
		initValidateForm();
	};

	var initElementComplex = function() {
		var configProduct = {
			useUrl : true,
			root : "results",
			widthButton : 218,
			showdefaultloadelement : false,
			autoshowloadelement : false,
			datafields : [ {
				name : "productId",
				type : "string"
			}, {
				name : "productCode",
				type : "string"
			}, {
				name : "productName",
				type : "string"
			}, ],
			columns : [ {
				text : uiLabelMap.BSProductId,
				dataField : "productCode",
				width : "20%"
			}, {
				text : uiLabelMap.BSProductName,
				dataField : "productName"
			}, ],
			url : "JQListProductBySupplier&supplierId="
					+ $("#supplierId").val(),
			useUtilFunc : true,
			key : "productCode",
			description : [ "productName" ],
			autoCloseDropDown : true,
			filterable : true,
			sortable : true
		};
		productDDB = new OlbDropDownButton($("#productIdBtn"),
				$("#jqxgridProduct"), null, configProduct, []);
	};

	var initElement = function() {
		$("#supplierId").jqxDropDownList({
			source : supplierData,
			theme : theme,
			width : 218,
			displayMember : "description",
			valueMember : "partyId",
			disabled : false,
			placeHolder : uiLabelMap.BSClickToChoose,
			autoDropDownHeight : true
		});
		$("#quantityUomId").jqxDropDownList({
			source : [],
			theme : theme,
			width : 218,
			displayMember : "description",
			valueMember : "uomId",
			disabled : false,
			placeHolder : uiLabelMap.BSClickToChoose,
			autoDropDownHeight : true,
		});
		$("#quantity").jqxNumberInput({
			inputMode : "simple",
			spinMode : "simple",
			groupSeparator : ".",
			min : 1,
			width : 218,
			decimalDigits : 0
		});
		$("#alterpopupWindow").jqxWindow({
			width : 700,
			height : 240,
			resizable : false,
			isModal : true,
			autoOpen : false,
			cancelButton : $("#alterCancel"),
			modalOpacity : 0.7,
			theme : theme
		});
        $("#fromDateDiv").jqxDateTimeInput({height: '25px',width: 218, formatString: 'dd-MM-yyyy HH:mm:ss', allowNullDate: false, value: new Date()});
        $("#thruDateDiv").jqxDateTimeInput({height: '25px',width: 218, formatString: 'dd-MM-yyyy HH:mm:ss', allowNullDate: true, value: null});
	};

	var initEvent = function() {
		$("#supplierId").on(
				"select",
				function(event) {
					var args = event.args;
					if (args) {
						var partyId = args.item.value;
						productDDB.updateSource(
								"jqxGeneralServicer?sname=jqGetListSupplierProductConfig&partyId="
										+ partyId, null, null);
						$("#quantityUomId").jqxDropDownList("clearSelection");
						$("#quantityUomId").jqxDropDownList({
							source : []
						});
					}
				});

		$("#jqxgridProduct").on(
				"rowselect",
				function(event) {
					var row = $("#jqxgridProduct").jqxGrid("getrowdata",
							event.args.rowindex);
					$("#productIdTmp").val(row.productId);
					updateListUomByProduct(row.productId);
				});

		$("#alterSave").on(
				"click",
				function() {
					if (validatorVAL.validate()) {
						var row = {
							partyId : $("#supplierId").val(),
							productId : $("#productIdTmp").val(),
							quantityUomId : $("#quantityUomId").val(),
							quantity : $("#quantity").val(),
							fromDate : $("#fromDateDiv").jqxDateTimeInput('getDate'),
							thruDate : $("#thruDateDiv").jqxDateTimeInput('getDate')
						};
						$("#jqxgridSupplierTargets").jqxGrid("addRow", null,
								row, "first");
						$("#alterpopupWindow").jqxWindow("close");
					}
				});
        $("#alterCancel").on(
            "click",
            function() {
                $("#alterpopupWindow").jqxWindow("close");
            });

		$("#alterpopupWindow").on("open", function() {
			$("#supplierId").jqxDropDownList("clearSelection");
			$("#quantityUomId").jqxDropDownList("clearSelection");
			productDDB.clearAll(true);
			$("#quantityUomId").jqxDropDownList({
				source : []
			});
			$("#quantity").val(1);
		});

		$("#alterpopupWindow").on("close", function() {
			$("#alterpopupWindow").jqxValidator("hide");
		});
	};

	var initValidateForm = function() {
		var extendRules = [
		    {
			    input : "#quantity",
			    message : uiLabelMap.DAQuantityMustBeGreaterThanZero,
			    action : "keyup, blur",
                rule : function(input, commit) {
                    var value = input.val();
                    if (value <= 0)
                        return false;
                    return true;
                }
		    },
		];
		var mapRules = [ {
			input : "#supplierId",
			type : "validInputNotNull"
		}, {
			input : "#productIdBtn",
			type : "validInputNotNull"
		}, {
			input : "#quantityUomId",
			type : "validInputNotNull"
		}, {
            input : "#fromDateDiv",
            type : "validDateTimeInputNotNull"
        }, {
            input : "#fromDateDiv",
            type : "validDateCompareToday"
        }, {
        	input : "#thruDateDiv",
        	type : "validDateTimeInputNotNull"
        }, {
            input : "#thruDateDiv",
            type : "validDateCompareToday"
        }, {
            input : "#fromDateDiv, #thruDateDiv",
            type : "validCompareTwoDate",
            paramId1 : "fromDateDiv",
            paramId2 : "thruDateDiv"
        }, ];
		validatorVAL = new OlbValidator($("#alterpopupWindow"), mapRules,
				extendRules, {
					position : "bottom"
				});
	};

	var updateListUomByProduct = function(productId) {
		$.ajax({
			url : "getListUomByProduct",
			type : "POST",
			data : {
				productId : productId
			},
			dataType : "json"
		}).done(function(data) {
			var uomId = data.uomId;
			var description = data.description;
			var uomData = [ {
				"uomId" : uomId,
				"description" : description
			} ];
			$("#quantityUomId").jqxDropDownList({
				source : uomData,
				theme : theme,
				selectedIndex : 0,
				displayMember : "description",
				valueMember : "uomId",
				disabled : false,
				autoDropDownHeight : true
			});
		});
	}

	return {
		init : init,
		initValidateForm : initValidateForm,
	};
}());