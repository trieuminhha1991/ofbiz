<script type="text/javascript">
	var productQuantitiesMap = {};
	var productUomesMap = {};
	var localData = [];
	var orderItem = {};
	var listQuantityUomIdByProduct = [];
	<#if orderItemSGList?exists && orderItemSGList?size &gt; 0>
		<#assign prodCatalogId = orderItemSGList[0].prodCatalogId?default("")/>
		<#assign defaultShipBeforeDate = orderItemSGList[0].shipBeforeDate?default("")/>
		<#assign defaultShipAfterDate = orderItemSGList[0].shipAfterDate?default("")/>
		<#list orderItemSGList as orderItem>
			<#if (orderItem.productId?exists) && (!(orderItem.isPromo?exists) || orderItem.isPromo?string == "N")>
				<#assign productGe = delegator.findOne("Product",{"productId" : "${orderItem.productId}"},false) !>
				orderItem = {};
				orderItem.orderId = "${orderItem.orderId}";
				orderItem.orderItemSeqId = "${orderItem.orderItemSeqId}";
				orderItem.shipGroupSeqId = "${orderItem.shipGroupSeqId}";
				orderItem.productId = "${orderItem.productId}";
				orderItem.productCode = "${productGe.productCode?if_exists}";
				orderItem.productName = "${orderItem.itemDescription?if_exists}";
				orderItem.quantity = "${orderItem.quantity}";
				orderItem.isPromo = "${orderItem.isPromo}";
				orderItem.unitPrice = "${orderItem.unitPrice}";
				<#if orderItem.alternativeQuantity?exists>
					productQuantitiesMap["${orderItem.productId}"] = ${orderItem.alternativeQuantity};
					orderItem.quantity = "${orderItem.alternativeQuantity}";
				</#if>
				<#if orderItem.quantityUomId?exists>
					productUomesMap["${orderItem.productId}"] = "${orderItem.quantityUomId}";
					orderItem.quantityUomId = "${orderItem.quantityUomId}";
				</#if>
				<#if orderItem.alternativeUnitPrice?exists>
					orderItem.unitPrice = "${orderItem.alternativeUnitPrice}";
				</#if>
				<#assign product = delegator.findOne("Product", {"productId" : orderItem.get("productId")}, false)!/>
				<#assign quantityUom = delegator.findOne("Uom", {"uomId" : product.get("quantityUomId")}, false)!/>
				<#if quantityUom?exists>
					listQuantityUomIdByProduct.push({"description" : "${StringUtil.wrapString(quantityUom.getString("description"))}", "uomId" : "${StringUtil.wrapString(quantityUom.getString("uomId"))}"});
					orderItem.quantityUomId = "${StringUtil.wrapString(quantityUom.getString("uomId"))}";
				</#if>
				localData.push(orderItem);
			</#if>
		</#list>
	</#if>
	
	<#assign uomList = delegator.findByAnd("Uom", {"uomTypeId" : "PRODUCT_PACKING"}, null, false)/>
	var uomData = [<#if uomList?exists><#list uomList as uomItem>{
		uomId: "${uomItem.uomId}",
		description: "${StringUtil.wrapString(uomItem.get("description", locale))}"
	},</#list></#if>];
	
	<#assign uomList = delegator.findList("Uom", null, null, null, null, false)>
	
	var cellclass = function (row, columnfield, value) {
 		var data = $("#jqxEditSO").jqxGrid("getrowdata", row);
 		if (data.isPromo != undefined && data.isPromo != null && "Y" == data.isPromo) {
 			return "background-promo";
 		}
	}
</script>

<div id="container" style="background-color: transparent; overflow: auto;"></div>
<div id="jqxNotification" style="margin-bottom:5px">
    <div id="notification">
    </div>
</div>

<div class="row-fluid">
	<div id="jqxEditSO"></div>
</div>

<div class="row-fluid margin-between-block">
	<div class="pull-right form-window-content-custom">
		<button id="alterSaveEdit" class="btn btn-primary form-action-button"><i class="fa-check"></i> ${uiLabelMap.CommonSave}</button>
		<button id="alterCancelEdit" class="btn btn-danger form-action-button"><i class="fa-remove"></i> ${uiLabelMap.BSExit}</button>
	</div>
</div>

<div style="position:relative">
	<div id="loader_page_common" style="overflow: hidden; position: fixed; display: none; left: 50%; top: 50%; z-index: 900;" class="jqx-rc-all jqx-rc-all-olbius">
		<div style="z-index: 99999; position: relative; width: auto; height: 33px; padding: 5px; font-family: verdana; font-size: 12px; color: #767676; border-color: #898989; border-width: 1px; border-style: solid; background: #f6f6f6; border-collapse: collapse;" class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
			<div style="float: left;">
				<div style="float: left; overflow: hidden; width: 32px; height: 32px;" class="jqx-grid-load"></div>
				<span style="margin-top: 10px; float: left; display: block; margin-left: 5px;">${uiLabelMap.BSLoading}...</span>
			</div>
		</div>
	</div>
</div>

<div id="contextMenu" style="display:none">
	<ul>
		<li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
		<li><i class="fa fa-trash red"></i>${StringUtil.wrapString(uiLabelMap.BSDeleteOrderItem)}</li>
	</ul>
</div>

<#include "orderEditAddItemsPopup.ftl"/>

<@jqGridMinimumLib />
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<@jqOlbCoreLib hasGrid=true/>
<script>
	$(function(){
		pageCommonEditSO.init();
	});
	var pageCommonEditSO = (function(){
		var init = function(){
			initElement();
			initElementAdvance();
			initEvent();
		};
		var initElement = function(){
			var tmpWidth = "100%";
			$("#container").width(tmpWidth);
			$("#jqxNotification").jqxNotification({ 
				icon: { width: 25, height: 25, url: "/aceadmin/assets/images/info.jpg"}, 
				width: tmpWidth, 
				appendContainer: "#container", 
				opacity: 1, autoClose: true, template: "success" 
			});
			$("#contextMenu").jqxMenu({ width: 180, autoOpenPopup: false, mode: "popup", theme: theme });
		};
		var initElementAdvance = function(){
			var datafields = [
				{ name: "orderId", type: "string" },
				{ name: "orderItemSeqId", type: "string" },
				{ name: "shipGroupSeqId", type: "string" },
				{ name: "productId", type: "string" },
				{ name: "productCode", type: "string" },
				{ name: "productName", type: "string" },
				{ name: "quantityUomId", type: "string"},
				{ name: "packingUomIds", type: "string"},
				{ name: "productPackingUomId", type: "string"},
				{ name: "quantity", type: "number", formatter: "integer"},
				{ name: "packingUomId", type: "string"}, 
				{ name: "isPromo", type: "string"},
				{ name: "unitPrice", type: "number", formatter: "float"}
			];
			var columns = [
				{ text: "${uiLabelMap.SequenceId}", sortable: false, filterable: false, editable: false,
					groupable: false, draggable: false, resizable: false,
					datafield: "", columntype: "number", width: 50,
					cellsrenderer: function (row, column, value) {
						return "<div style=margin:4px;>" + (value + 1) + "</div>";
					}
				},
				{ text: "${StringUtil.wrapString(uiLabelMap.BSShipGroupSeqId)}", dataField: "shipGroupSeqId", width: "16%", editable:false, cellclassname: cellclass,hidden: true },
				{ text: "${StringUtil.wrapString(uiLabelMap.BSProductId)}", dataField: "productId", width: "16%", editable:false, cellclassname: cellclass, hidden: true },
				{ text: "${StringUtil.wrapString(uiLabelMap.BSProductId)}", dataField: "productCode", width: 150, editable:false, cellclassname: cellclass },
				{ text: "${StringUtil.wrapString(uiLabelMap.BSProductName)}", dataField: "productName", minwidth: 250, editable: false, cellclassname: cellclass },
				{ text: "${uiLabelMap.Unit}", datafield: "quantityUomId", width: 100, editable: false, filtertype: "checkedlist",
					cellsrenderer: function (row, column, value){
						for (var i = 0; i < uomData.length; i ++){
							if (uomData[i].uomId == value){
								return "<span style=\"text-align: right\">" + uomData[i].description +"</span>";
							}
						}
					}, createfilterwidget: function (column, columnElement, widget) {
						var filterDataAdapter = new $.jqx.dataAdapter(uomData, {
							autoBind: true
						});
						var records = filterDataAdapter.records;
						widget.jqxDropDownList({source: records, displayMember: "uomId", valueMember: "uomId", dropDownWidth: "auto", autoDropDownHeight: "auto",
							renderer: function(index, label, value){
								if (uomData.length > 0) {
									for(var i = 0; i < uomData.length; i++){
										if(uomData[i].uomId == value){
											return "<span>" + uomData[i].description + "</span>";
										}
									}
								}
								return value;
							}
						});
					}
				},
				{ text: "${StringUtil.wrapString(uiLabelMap.UnitPrice)}", dataField: "unitPrice", width: 150, editable: false, filtertype: "number", columntype: "numberinput",
					cellsalign: "right", cellsformat: "c", 
					createeditor: function (row, cellvalue, editor) {
						editor.jqxNumberInput({ inputMode: "simple", spinMode: "simple", groupSeparator: ".", min:0, decimalDigits: 2 });
					}
				},
				{ text: "${StringUtil.wrapString(uiLabelMap.BSQuantity)}", dataField: "quantity", width: 150, cellsalign: "right", filtertype: "number", sortable:false, 
					cellclassname: cellclass, columntype: "numberinput",
					validation: function (cell, value) {
						if (value <= 0) {
							return {result: false, message: "${uiLabelMap.BSQuantityMustBeGreaterThanZero}"};
						}
						return true;
					}, createeditor: function (row, cellvalue, editor) {
						editor.jqxNumberInput({ decimalDigits: 0, digits: 9 });
					}
				}];
			var configProductList = {
				showdefaultloadelement: false,
				autoshowloadelement: false,
				dropDownHorizontalAlignment: "right",
				datafields: datafields,
				columns: columns,
				useUrl: false,
				clearfilteringbutton: false,
				editable: true,
				alternativeAddPopup: "alterpopupWindow",
				pageable: true,
				pagesize: 15,
				showtoolbar: false,
				editmode: "click",
				selectionmode: "multiplecellsadvanced	",
				width: "100%",
				bindresize: false,
				groupable: false,
				localization: getLocalization(),
				showtoolbar: true,
				showdefaultloadelement: true,
				autoshowloadelement: true,
				virtualmode: false,
				rendertoolbar: function (toolbar) {
					toolbar.html("");
					<#assign id = "jqxEditSO"/>
					<#assign addType = "popup"/>
					<#assign alternativeAddPopup="alterpopupWindow"/>
					var grid = $("#${id}");
					var me = this;
					<#if titleProperty?has_content && (!customTitleProperties?exists || customTitleProperties == "")>
						<@renderJqxTitle titlePropertyTmp=titleProperty id=id/>
						<#elseif customTitleProperties?exists && customTitleProperties != "">
						<@renderJqxTitle titlePropertyTmp=customTitleProperties id=id/>
					</#if>
					toolbar.append(jqxheader);
					var container = $("#toolbarButtonContainer${id}");
					var maincontainer = $("#toolbarcontainer${id}");
					Grid.createAddRowButton(
						grid, container, "${uiLabelMap.accAddNewRow}", {
							type: "${addType}",
							container: $("#${alternativeAddPopup}"),
							<#if addType != "popup">
							<#if addinitvalue !="">
								data: {${primaryColumn}: "${addinitvalue}"}
								<#else>
								data: ${primaryColumn}
								</#if>  
							</#if>
						}
					);
				},
				contextMenu: "contextMenu",
			};
			new OlbGrid($("#jqxEditSO"), localData, configProductList, []);
		};
		function simulateKeyPress(character) {
			jQuery.event.trigger({ type : "keypress", which : 50});
		}
		var initEvent = function(){
			$("#alterCancelEdit").on("click", function(){
				window.open("viewDetailPO?orderId=${parameters.orderId?if_exists}", "_self");
			});
			$("#alterSaveEdit").on("click", function(){
				bootbox.dialog("${uiLabelMap.AreYouSureUpdate}", 
				[{"label": "${uiLabelMap.CommonCancel}", 
					"icon": "fa fa-remove", "class": "btn  btn-danger form-action-button pull-right",
					"callback": function() {bootbox.hideAll();}
				}, 
				{"label": "${uiLabelMap.OK}",
					"icon": "fa-check", "class": "btn btn-primary form-action-button pull-right",
					"callback": function() {
						var rowIndex = $("#jqxEditSO").jqxGrid("getselectedrowindex");
						if (OlbCore.isNotEmpty(rowIndex)) {
							$("#jqxEditSO").jqxGrid("endcelledit", rowIndex, "quantity", true, true);
						}
						var dataMap = {
							orderId: "${orderHeader.orderId}",
						};
						var data = $("#jqxEditSO").jqxGrid("getrows");
						if (typeof(data) == "undefined") {
							alert("Error check data");
						}
						var listProd = [];
						for (var i = 0; i < data.length; i++) {
							var dataItem = data[i];
							if (OlbCore.isNotEmpty(dataItem.quantity) && OlbCore.isNotEmpty(dataItem.productId)) {
								if (parseInt(dataItem.quantity) > 0) {
									var prodItem = {
										shipGroupSeqId: dataItem.shipGroupSeqId,
										orderItemSeqId: dataItem.orderItemSeqId,
										unitPrice: dataItem.unitPrice,
										productId: dataItem.productId,
										quantityUomId: dataItem.quantityUomId,
										quantity: dataItem.quantity
									};
									listProd.push(prodItem);
								}
								<#--
								else {
									var messageError = "<i class=\"fa-times-circle open-sans icon-modal-alert-danger\"></i> <span class=\"message-content-alert-danger\">${uiLabelMap.BSQuantityMustBeGreaterThanZero}!</span>";
									bootbox.dialog(messageError, [{
										"label" : "OK",
										"class" : "btn-mini btn-primary width60px",
									}]);
									return;
								}
								-->
							}
						}
						if (listProd.length > 0) {
							dataMap.listProd = JSON.stringify(listProd);
							
							$.ajax({
								type: "POST",
								url: "processEditSalesOrder",
								data: dataMap,
								beforeSend: function(){
									$("#loader_page_common").show();
								},
								success: function(data){
									processResultInitOrder(data);
								},
								error: function(data){
									alert("Send request is error");
								},
								complete: function(data){
									$("#loader_page_common").hide();
								},
							});
						} else {
							var messageError = "<i class=\"fa-times-circle open-sans icon-modal-alert-danger\"></i> <span class=\"message-content-alert-danger\">${uiLabelMap.BSYouNotYetChooseProduct}!</span>";
							bootbox.dialog(messageError, [{
								"label" : "OK",
								"class" : "btn-mini btn-primary width60px",
							}]);
							return false;
						}
					}
				}]);
			});
			
			$("#contextMenu").on("itemclick", function (event) {
				var args = event.args;
				var rowindex = $("#jqxEditSO").jqxGrid("getselectedrowindex");
				var tmpKey = $.trim($(args).text());
				if (tmpKey == "${StringUtil.wrapString(uiLabelMap.BSRefresh)}") {
					$("#jqxEditSO").jqxGrid("updatebounddata");
				} else if (tmpKey == "${StringUtil.wrapString(uiLabelMap.BSDeleteOrderItem)}") {
					var messageConfirm = "";
					var dataRows = $("#jqxEditSO").jqxGrid("getrows");
					if (dataRows != undefined && dataRows.length == 1) {
						messageConfirm += "<b>${uiLabelMap.BSThisOrderHasOnlyOneOrderItemIfDeleteThisOrderItemThen}</b><br/>";
					}
					messageConfirm += "${uiLabelMap.BSAreYouSureYouWantToCancelThisOrderItem}";
					bootbox.dialog(messageConfirm, [
					{"label": "${StringUtil.wrapString(uiLabelMap.wgcancel)}", 
						"icon": "fa fa-remove", "class": "btn  btn-danger form-action-button pull-right",
						"callback": function() {bootbox.hideAll();}
					}, 
					{"label": "${StringUtil.wrapString(uiLabelMap.wgok)}",
						"icon": "fa-check", "class": "btn btn-primary form-action-button pull-right",
						"callback": function() {
							var data = $("#jqxEditSO").jqxGrid("getrowdata", rowindex);
							if (data != undefined && data != null) {
								var orderId = data.orderId;
								$.ajax({
									type: "POST",
									url: "cancelOrderItemSales",
									data: {
										orderId: orderId,
										orderItemSeqId: data.orderItemSeqId,
										shipGroupSeqId: data.shipGroupSeqId,
									},
									beforeSend: function(){
										$("#loader_page_common").show();
									},
									success: function(data){
										processResultInitOrder(data);
										window.location.reload();
									},
									error: function(data){
										alert("Send request is error");
									},
									complete: function(data){
										$("#loader_page_common").hide();
									}
								});
							}
						}
					}
					]);
				}
			});
		};
		var processResultInitOrder = function(data){
			if (data.thisRequestUri == "json") {
				var errorMessage = "";
				if (data._ERROR_MESSAGE_LIST_ != null) {
					for (var i = 0; i < data._ERROR_MESSAGE_LIST_.length; i++) {
						errorMessage += "<p><b>${uiLabelMap.BSErrorUper}</b>: " + data._ERROR_MESSAGE_LIST_[i] + "</p>";
					}
				}
				if (data._ERROR_MESSAGE_ != null) {
					errorMessage += "<p><b>${uiLabelMap.BSErrorUper}</b>: " + data._ERROR_MESSAGE_ + "</p>";
				}
				if (errorMessage != "") {
					$("#container").empty();
					$("#jqxNotification").jqxNotification({ template: "error"});
					$("#jqxNotification").html(errorMessage);
					$("#jqxNotification").jqxNotification("open");
				} else {
					$("#container").empty();
					$("#jqxNotification").jqxNotification({ template: "info"});
					$("#jqxNotification").html("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
					$("#jqxNotification").jqxNotification("open");
					return true;
				}
				return false;
			} else {
				$("#container").empty();
				$("#jqxNotification").jqxNotification({ template: "info"});
				$("#jqxNotification").html("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
				$("#jqxNotification").jqxNotification("open");
				return true;
			}
		};
		return {
			init: init
		};
	}());
</script>