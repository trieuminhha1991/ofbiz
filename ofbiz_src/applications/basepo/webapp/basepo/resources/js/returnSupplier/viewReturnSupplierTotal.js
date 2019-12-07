$(function() {
	pageCommonViewReturnSupplier.init();
});

var pageCommonViewReturnSupplier = (function() {
	var init = function() {
		initElement();
		initEvent();
		initElementComplex();
	};

	var initElement = function() {
		loadProductDataSumToJqx(returnItems);
	};

	var initElementComplex = function() {
	};

	var initEvent = function() {
	};

	var loadProductDataSumToJqx = function(valueDataSoure) {
		var cellclassname = function(row, columnfield, value) {
			return "green1";
		};

		var source = {
			datafields : [ { name : "returnId", type : "string" }, 
			{ name : "returnItemSeqId", type : "string" }, 
			{ name : "orderId", type : "string" }, 
			{ name : "productId", type : "string" }, 
			{ name : "productCode", type : "string" }, 
			{ name : "productName", type : "string" }, 
			{ name : "description", type : "string" }, 
			{ name : "returnQuantity", type : "number" }, 
			{ name : "returnAmount", type : "number" }, 
			{ name : "returnPrice", type : "number" 	}, 
			{ name : "returnReasonId", type : "string" }, 
			{ name : "returnTypeId", type : "string" }, 
			{ name : "quantityUomId", type : "string" }, 
			{ name : "weightUomId", type : "string" }, 
			{ name : "expectedItemStatus", type : "string" },
			{ name : "requireAmount", type : "string" }
			],
			localdata : valueDataSoure,
			datatype : "array",
			updaterow : function(rowid, rowdata, commit) {
				$.ajax({
					beforeSend : function() {
					},
					complete : function() {
						commit(true);
						$("#quantity" + rowid).jqxTooltip({ content : "<span style=\"color: green;\">"
									+ uiLabelMap.SaveSucess
									+ "</span>",
							theme : "customtooltip",
							position : "top",
							name : "movieTooltip"
						});
						$("#quantity" + rowid).jqxTooltip("open");
						setTimeout(function() {
							$("#quantity" + rowid).jqxTooltip("destroy");
						}, 3000);
					},
					error : function() {
						commit(false);
						$("#quantity" + rowid).jqxTooltip(
							{
								content : "<span style=\"color: red;\">"
										+ uiLabelMap.SaveNotSucess
										+ "</span>",
								theme : "customtooltip",
								position : "top",
								name : "movieTooltip"
							});
						$("#quantity" + rowid).jqxTooltip("open");
						setTimeout(function() {
							$("#quantity" + rowid)
									.jqxTooltip("destroy");
						}, 3000);
					},
					url : "jqxUpdateReturnItems",
					type : "POST",
					data : {
						returnId : rowdata.returnId,
						returnItemSeqId : rowdata.returnItemSeqId,
						returnQuantity : rowdata.returnQuantity
					},
					dataType : "json",
					success : function(data) {

					}
				});
			},
			deleterow : function(rowid, commit) {
				var dataRecord = $("#listReturnItem").jqxGrid("getrowdata",
						rowid);
				$.ajax({
					beforeSend : function() {
						$("#loader_page_common").show();
					},
					complete : function() {
						$("#loader_page_common").hide();
					},
					url : "removeReturnItemCustom",
					type : "POST",
					data : {
						returnId : dataRecord.returnId,
						returnItemSeqId : dataRecord.returnItemSeqId
					},
					dataType : "json",
					success : function(data) {
					}
				});
				commit(true);
			}
		};
		var dataAdapterProduct = new $.jqx.dataAdapter(source);
		$("#listReturnItem").jqxGrid(
						{ source : dataAdapterProduct,
							localization : getLocalization(),
							filterable : true,
							showfilterrow : true,
							theme : "olbius",
							width : "100%",
							sortable : true,
							enabletooltips : true,
							autoheight : true,
							pageable : true,
							columnsresize : true,
							editable : true,
							columns : [
									{
										text : uiLabelMap.BPOSequenceId,
										sortable : false,
										filterable : false,
										editable : false,
										pinned : true,
										groupable : false,
										draggable : false,
										resizable : false,
										datafield : "",
										columntype : "number",
										width : 50,
										cellsrenderer : function(row, column,
												value) {
											return "<div style=\"margin:4px;\">"
													+ (value + 1) + "</div>";
										},
									},
									{
										text : uiLabelMap.POOrderId,
										datafield : "returnId",
										width : 100,
										editable : false,
										hidden : true
									},
									{
										text : uiLabelMap.POOrderId,
										datafield : "returnItemSeqId",
										width : 100,
										editable : false,
										hidden : true
									},
									{
										text : uiLabelMap.POOrderId,
										datafield : "orderId",
										width : 100,
										editable : false,
										pinned : true,
									},
									{
										text : uiLabelMap.POProductId,
										datafield : "productId",
										width : 130,
										editable : false,
										hidden : true
									},
									{
										text : uiLabelMap.POProductId,
										datafield : "productCode",
										width : 130,
										editable : false,
										pinned : true,
									},
									{
										text : uiLabelMap.DmsProduct,
										dataField : "description",
										align : "left",
										cellsalign : "left",
										editable : false,
									},
									{
										text : uiLabelMap.Unit,
										datafield : "quantityUomId",
										width : "150",
										align : "left",
										editable : false,
										filterable : true,
										cellsalign : "right",
										filtertype : "number",
										cellsrenderer : function(row, column, value) {
											var data = $("#listReturnItem").jqxGrid('getrowdata', row);
											if (data.requireAmount && data.requireAmount == 'Y') {
												value = data.weightUomId;
											}
											if (value) {
												return "<span style=\"text-align: right\">" + getUomDescription(value) + "</span>";
											}
										},
									},
									{
										text : uiLabelMap.BPOQuantity,
										datafield : "returnQuantity",
										width : "150",
										align : "left",
										editable : false,
										filterable : true,
										cellsalign : "right",
										filtertype : "number",
										cellsrenderer : function(row, column, value) {
											var data = $("#listReturnItem").jqxGrid('getrowdata', row);
											if (data.requireAmount && data.requireAmount == 'Y') {
												value = data.returnAmount;
											}
											if (value) {
												var idRow = "quantity" + row;
												return "<span id=" + idRow + " style=\"text-align: right\">" + formatnumber(value) + "</span>";
											}
										},
									},
									{
										text : uiLabelMap.unitPrice,
										datafield : "returnPrice",
										width : "100",
										editable : false,
										filterable : true,
										cellsalign : "right",
										align : "left",
										filtertype : "number",
										cellsrenderer : function(row, column,
												value) {
											if (value) {
												return "<span style=\"text-align: right\">"
														+ formatcurrency(value,
																currencyUomId)
														+ "</span>";
											}
										},
									},
									{
										text : uiLabelMap.POReturnReason,
										datafield : "returnReasonId",
										width : 200,
										editable : false,
										filtertype : "checkedlist",
										cellsrenderer : function(row, column,
												value) {
											if (value) {
												for (var i = 0; i < listReturnReasons.length; i++) {
													if (value == listReturnReasons[i].returnReasonId) {
														return "<span style=\"text-align: left\">"
																+ listReturnReasons[i].description
																+ "</span>";
													}
												}
											}
										},
										createfilterwidget : function(column,
												columnElement, widget) {
											var filterDataAdapter = new $.jqx.dataAdapter(
													listReturnReasons, {
														autoBind : true
													});
											var records = filterDataAdapter.records;
											widget
													.jqxDropDownList({
														source : records,
														displayMember : "returnReasonId",
														valueMember : "returnReasonId",
														dropDownWidth : "auto",
														autoDropDownHeight : "auto",
														renderer : function(
																index, label,
																value) {
															if (listReturnReasons.length > 0) {
																for (var i = 0; i < listReturnReasons.length; i++) {
																	if (listReturnReasons[i].returnReasonId == value) {
																		return "<span>"
																				+ listReturnReasons[i].description
																				+ "</span>";
																	}
																}
															}
															return value;
														}
													});
											widget.jqxDropDownList("checkAll");
										}
									} ]
						});
	}
	return {
		init : init
	};
}());
