$(function() {
	OlbPromoNewInfo.init();
});
var OlbPromoNewInfo = (function() {
	var supplierDDB;
	//var supplierGRID;
	var supplierDataArr = [];
	var validatorVAL;

	var init = function() {
		initElement();
		initElementComplex();
		initValidateForm();
		initEvent();
	};
	var initElement = function() {
		jOlbUtil.input.create("#productPromoId", {
			maxLength : 20
		});
		jOlbUtil.input.create("#promoName", {
			maxLength : 100
		});
		jOlbUtil.dateTimeInput.create("#fromDate", {
			width : "100%",
			allowNullDate : true,
			value : null
		});
		jOlbUtil.dateTimeInput.create("#thruDate", {
			width : "100%",
			allowNullDate : true,
			value : null
		});
		/*
		 * jOlbUtil.numberInput.create("#useLimitPerOrder", {width: "100%",
		 * spinButtons:true, decimalDigits: 0, min: 0});
		 * jOlbUtil.numberInput.create("#useLimitPerCustomer", {width: "100%",
		 * spinButtons:true, decimalDigits: 0, min: 0});
		 * jOlbUtil.numberInput.create("#useLimitPerPromotion", {width: "100%",
		 * spinButtons:true, decimalDigits: 0, min: 0});
		 */

		$("#promoName").jqxInput("focus");
		/*
		 * if (typeof(dataPromoNew.useLimitPerOrder) != "undefined")
		 * jOlbUtil.numberInput.val("#useLimitPerOrder",
		 * dataPromoNew.useLimitPerOrder); else
		 * jOlbUtil.numberInput.clear("#useLimitPerOrder"); if
		 * (typeof(dataPromoNew.useLimitPerCustomer) != "undefined")
		 * jOlbUtil.numberInput.val("#useLimitPerCustomer",
		 * dataPromoNew.useLimitPerCustomer); else
		 * jOlbUtil.numberInput.clear("#useLimitPerCustomer"); if
		 * (typeof(dataPromoNew.useLimitPerPromotion) != "undefined")
		 * jOlbUtil.numberInput.val("#useLimitPerPromotion",
		 * dataPromoNew.useLimitPerPromotion); else
		 * jOlbUtil.numberInput.clear("#useLimitPerPromotion");
		 */

		if (typeof (dataPromoNew.productPromoId) != "undefined") {
			$("#productPromoId").jqxInput("val", dataPromoNew.productPromoId);
			$("#productPromoId").jqxInput({
				disabled : true
			});
			if (typeof (dataPromoNew.productPromoName) != "undefined") {
				$("#promoName").jqxInput("val", dataPromoNew.productPromoName);
			}
		}
		if (typeof (dataPromoNew.fromDate) != "undefined")
			$("#fromDate").jqxDateTimeInput("setDate", dataPromoNew.fromDate);
		if (typeof (dataPromoNew.thruDate) != "undefined")
			$("#thruDate").jqxDateTimeInput("setDate", dataPromoNew.thruDate);

		/* change to allow select only one supplier
		jOlbUtil.windowPopup.create($("#alterpopupWindowSupplier"), {
			width : 960,
			height : 450,
			cancelButton : $("#wn_ps_alterCancel")
		}); 
		 */
	};
	var initElementComplex = function() {
		var configSupplier = {
			widthButton: '100%',
			dropDownHorizontalAlignment: 'right',
			showdefaultloadelement: false,
			autoshowloadelement: false,
			datafields: [
	            {name: 'partyId', type: 'string'}, 
	            {name: 'partyCode', type: 'string'}, 
	            {name: 'groupName', type: 'string'}, 
            ],
			columns: [
				{text: uiLabelMap.POSupplierId, datafield: 'partyCode', width: '30%'},
				{text: uiLabelMap.POSupplierName, datafield: 'groupName', width: '70%'},
			],
			useUrl: true,
			useUtilFunc: true,
			url: 'jqGetListSupplierSimple',
			
			key: 'partyId',
			keyCode: 'partyCode',
			description: ['groupName'],
			autoCloseDropDown: true,
			filterable: true,
			sortable: true,
		};
		supplierDDB = new OlbDropDownButton($("#supplierIds"), $("#supplierGrid"), null, configSupplier, []);
		
		/* change to allow select only one supplier
		var configSupplier = {
			width : "100%",
			placeHolder : uiLabelMap.BSClickToChoose,
			useUrl : true,
			url : "jqxGeneralServicer?sname=jqGetListPartySupplier",
			key : "partyId",
			value : "groupName",
			dropDownHeight : 200,
			multiSelect : true,
		}
		supplierCBB = new OlbComboBox($("#supplierIds"), null, configSupplier, listSupplierSelected);
		*/
		/* change to allow select only one supplier
		var configGridSupplier = {
			datafields : [ {
				name : "partyId",
				type : "string"
			}, {
				name : "groupName",
				type : "string"
			}, {
				name : "preferredCurrencyUomId",
				type : "string"
			}, ],
			columns : [ {
				text : uiLabelMap.POSupplierId,
				dataField : "partyId",
				width : "20%",
				editable : false
			}, {
				text : uiLabelMap.POSupplierName,
				dataField : "groupName",
				width : "55%"
			}, {
				text : uiLabelMap.BSDefaultCurrencyUomId,
				dataField : "preferredCurrencyUomId"
			}, ],
			width : "100%",
			height : "auto",
			sortable : true,
			filterable : true,
			pageable : true,
			pagesize : 10,
			showfilterrow : true,
			useUtilFunc : true,
			useUrl : true,
			url : "jqGetListPartySupplier",
			groupable : false,
			showdefaultloadelement : true,
			autoshowloadelement : true,
			selectionmode : "checkbox",
		};
		supplierGRID = new OlbGrid($("#jqxgridSupplier"), null,
				configGridSupplier, []);
		*/

	};
	var initEvent = function() {
		/* change to allow select only one supplier
		$("#btnShowSupplierList").on("click", function() {
			$("#alterpopupWindowSupplier").jqxWindow("open");
		});
		 */
		/* change to allow select only one supplier
		supplierGRID.on("bindingComplete", function() {
			supplierDataArr = [];
		});
		supplierGRID
				.on(
						"rowselect",
						function(event) {
							var args = event.args;
							var rowBoundIndex = args.rowindex;
							if (Object.prototype.toString.call(rowBoundIndex) === "[object Array]") {
								for (var i = 0; i < rowBoundIndex.length; i++) {
									processDataRowSelect(rowBoundIndex[i]);
								}
							} else {
								processDataRowSelect(rowBoundIndex);
							}
						});
		supplierGRID.on("rowunselect", function(event) {
			var args = event.args;
			var rowBoundIndex = args.rowindex;
			if (rowBoundIndex == -9999) {
				supplierGRID.clearSelection();
				supplierDataArr = [];
			} else {
				var data = $("#jqxgridSupplier").jqxGrid("getrowdata",
						rowBoundIndex);
				if (data) {
					var index = supplierDataArr.indexOf(data.partyId);
					if (index > -1) {
						supplierDataArr.splice(index, 1);
					}
				}
			}
		});

		var processDataRowSelect = function(rowIndex) {
			var rowData = $("#jqxgridSupplier").jqxGrid("getrowdata", rowIndex);
			if (rowData) {
				var idStr = rowData.partyId;
				var index = supplierDataArr.indexOf(idStr);
				if (index < 0) {
					supplierDataArr.push(idStr);
				}
			}
		};
		*/
		
		/* change to allow select only one supplier
		$("#wn_ps_alterSave").on("click", function() {
			if (supplierCBB) {
				supplierCBB.clearAll();
				supplierCBB.selectItem(supplierDataArr);
			}
			$("#alterpopupWindowSupplier").jqxWindow("close");
		});
		 */
	};

	var initValidateForm = function() {
		validatorVAL = new OlbValidator($("#initPromotionEntry"), [ {
			input : "#productPromoId",
			type : "validCannotSpecialCharactor"
		}, {
			input : "#promoName",
			type : "validInputNotNull"
		}, {
			input : "#supplierIds",
			type : "validObjectNotNull",
			objType : "dropDownButton"
		}, {
			input : "#fromDate",
			type : "validDateTimeInputNotNull"
		}, {
			input : "#fromDate",
			type : "validDateCompareToday"
		}, {
			input : "#fromDate, #thruDate",
			type : "validCompareTwoDate",
			paramId1 : "fromDate",
			paramId2 : "thruDate"
		}, ]);
	};
	var clearNumberInput = function(element) {
		jOlbUtil.numberInput.clear(element);
	};
	var getValidator = function() {
		return validatorVAL;
	}
	var getValue = function(){
		var dataMap = {};
		dataMap.productPromoId = $("#productPromoId").val();
		dataMap.promoName = $("#promoName").val();
		var fromDate = $("#fromDate").jqxDateTimeInput("getDate") != null ? $("#fromDate").jqxDateTimeInput("getDate").getTime() : "";
		var thruDate = $("#thruDate").jqxDateTimeInput("getDate") != null ? $("#thruDate").jqxDateTimeInput("getDate").getTime() : "";
		dataMap.fromDate = fromDate;
		dataMap.thruDate = thruDate;
		dataMap.promoText = $("#promoText").val();
		//dataMap.useLimitPerOrder = $("#useLimitPerOrder").val();
		//dataMap.useLimitPerCustomer = $("#useLimitPerCustomer").val();
		//dataMap.useLimitPerPromotion = $("#useLimitPerPromotion").val();
		
		return dataMap;
	}
	var getSupplierIds = function(){
		// change to allow select only one supplier
		// $("#supplierIds").jqxComboBox("getSelectedItems")
		return [supplierDDB.getValue()];
	}
	return {
		init : init,
		clearNumberInput : clearNumberInput,
		getValidator : getValidator,
		getValue: getValue,
		getSupplierIds: getSupplierIds,
	};
}());