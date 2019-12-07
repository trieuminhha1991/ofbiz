$(function() {
	pageCommonReturnNew.init();
	Loading.hide("process-loading-css");
});

var pageCommonReturnNew = (function() {
	$.jqx.theme = "olbius";
	var theme = $.jqx.theme;
	var curDate = new Date();
	var orderDDB;
	var validatorVAL;
	var init = function() {
		initElement();
		initEvent();
		initElementComplex();
		initValidateForm();
	};

	var initElement = function() {
		$("#toPartyId").jqxDropDownList({
			source : supplierData,
			theme : theme,
			width : "100%",
			displayMember : "description",
			autoDropDownHeight : true,
			valueMember : "partyId",
			disabled : false,
			placeHolder : uiLabelMap.BSClickToChoose
		});
		$("#entryDate").jqxDateTimeInput({
			formatString : "dd/MM/yyyy HH:mm",
			width : "100%",
			showFooter : true,
			allowNullDate : false,
			value : null
		});
		$("#currencyUomId").jqxDropDownList({
			source : [],
			disabled : true,
			theme : theme,
			width : "100%",
			placeHolder : uiLabelMap.BSClickToChoose,
			autoDropDownHeight : true
		});
	};

	var initElementComplex = function() {
		var configPO = {
			useUrl : true,
			root : "results",
			width : 400,
			widthButton : "100%",
			showdefaultloadelement : false,
			autoshowloadelement : false,
			datafields : [ {
				name : "orderId",
				type : "string"
			}, {
				name : "orderDate",
				type : "date",
				other : "Timestamp"
			} ],
			columns : [
					{
						text : uiLabelMap.BPOSequenceId,
						sortable : false,
						filterable : false,
						editable : false,
						groupable : false,
						draggable : false,
						resizable : false,
						datafield : "",
						columntype : "number",
						width : 50,
						cellsrenderer : function(row, column, value) {
							return "<div style="margin:4px;">" + (value + 1)
									+ "</div>";
						}
					}, {
						text : uiLabelMap.POOrderId,
						datafield : "orderId",
						editable : false,
						width : 120
					}, {
						text : uiLabelMap.POOrderDate,
						dataField : "orderDate",
						align : "left",
						cellsalign : "left",
						filterable : true,
						editable : false,
						cellsformat : "dd/MM/yyyy",
						filtertype : "range"
					} ],
			url : "JQGetListPOBySupplier&supplierId=" + $("#toPartyId").val(),
			useUtilFunc : true,
			key : "orderId",
			description : [],
			autoCloseDropDown : true,
			filterable : true,
			sortable : true,
		};
		orderDDB = new OlbDropDownButton($("#orderHeaderBtn"),
				$("#orderHeaderGrid"), null, configPO, []);
	};

	var initEvent = function() {
		$("#toPartyId")
				.on(
						"select",
						function(event) {
							var args = event.args;
							if (args) {
								var value = args.item.value;
								orderDDB.updateSource(
										"jqxGeneralServicer?sname=JQGetListPOBySupplier&supplierId="
												+ value, null, null);
								updateUomBySupplier(value);
								orderDDB.clearAll();
								$("#jqxgridProduct").jqxGrid("source")._source.url = "jqxGeneralServicer?sname=JQListProductByOrder&orderId=";
								$("#jqxgridProduct").jqxGrid("updatebounddata");
								$("#orderHeaderId").val("");
							}
						});

		$("#orderHeaderGrid").on(
				"rowselect",
				function(event) {
					var args = event.args;
					var row = $("#orderHeaderGrid").jqxGrid("getrowdata",
							args.rowindex);
					$("#orderHeaderId").val(row.orderId);
				});

		$("#createReturn")
				.on(
						"click",
						function() {
							if (validatorVAL.validate()) {
								var supplier = $("#toPartyId").val();
								var currencyUomId = $("#currencyUomId").val();
								var entryDate = $("#entryDate")
										.jqxDateTimeInput("getDate").getTime();
								var rowindexes = $("#jqxgridProduct").jqxGrid(
										"getselectedrowindexes");
								if (rowindexes.length > 0) {
									var orderItems = [];
									for (var i = 0; i < rowindexes.length; i++) {
										var row = $("#jqxgridProduct").jqxGrid(
												"getrowdata", rowindexes[i]);
										if (row.quantity > 0) {
											orderItems.push(row);
										}
									}

									bootbox
											.dialog(
													""
															+ uiLabelMap.BPOAreYouSureYouWantCreate
															+ "?",
													[
															{
																"label" : uiLabelMap.wgcancel,
																"icon" : "fa fa-remove",
																"class" : "btn  btn-danger form-action-button pull-right",
																"callback" : function() {
																	bootbox
																			.hideAll();
																}
															},
															{
																"label" : uiLabelMap.wgok,
																"icon" : "fa-check",
																"class" : "btn btn-primary form-action-button pull-right",
																"callback" : function() {
																	createNewReturnSupplier(
																			supplier,
																			currencyUomId,
																			// entryDate,
																			JSON
																					.stringify(orderItems));
																}
															} ]);
								} else {
									jOlbUtil.alert
											.error(uiLabelMap.BSYouNotYetChooseProduct);
								}
							}
						});
	};

	var updateUomBySupplier = function(partyId) {
		$.ajax({
			url : "getProductBySupplier",
			type : "POST",
			data : {
				supplier : partyId
			},
			dataType : "json",
			success : function(data) {

			}
		}).done(function(data) {
			var currencyUomId = data.currencyUomId;
			var currencyCombo = [ {
				currencyUomId : currencyUomId
			} ];
			$("#currencyUomId").jqxDropDownList({
				source : currencyCombo,
				theme : theme,
				selectedIndex : 0,
				displayMember : "currencyUomId",
				valueMember : "currencyUomId",
				disabled : false,
				autoDropDownHeight : true
			});
		});
	};

	var createNewReturnSupplier = function(supplier, currencyUomId,
			orderItems) {
		$
				.ajax(
						{
							beforeSend : function() {
								$("#loader_page_common").show();
							},
							complete : function() {
								$("#loader_page_common").hide();
							},
							url : "createNewReturnSupplierJson",
							type : "POST",
							data : {
								returnHeaderTypeId : "VENDOR_RETURN",
								statusId : "SUP_RETURN_REQUESTED",
								toPartyId : supplier,
								currencyUomId : currencyUomId,
								needsInventoryReceive : "N",
								orderItems : orderItems
							},
							dataType : "json",
							success : function(data) {
								if (!data._ERROR_MESSAGE_) {
									window.location.href = "viewGeneralReturnSupplier?returnId="
											+ data.returnId;
								}
							}
						}).done(function(data) {

				});
	};

	var initValidateForm = function() {
		var mapRules = [ {
			input : "#toPartyId",
			type : "validInputNotNull"
		}, {
			input : "#currencyUomId",
			type : "validInputNotNull"
		}, {
			input : "#orderHeaderBtn",
			type : "validInputNotNull"
		}, {
			input : "#entryDate",
			type : "validDateTimeInputNotNull"
		}, {
			input : "#entryDate",
			type : "validDateTimeCompareToday"
		}, ];
		validatorVAL = new OlbValidator($("#newReturnSupplier"), mapRules,
				null, {
					position : "bottom"
				});
	};

	return {
		init : init,
		initValidateForm : initValidateForm,
	};
}());