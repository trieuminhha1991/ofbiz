<style type="text/css">
	//#pagerjqxgridSupplierProduct {display:none}
	.jqx-widget.jqx-widget-bootstrap {color: #393939; border-radius:0}
	.jqx-widget.jqx-widget-bootstrap [id^="columntable"]>div>div>div>span {
		font-weight: 400 !important;
	}
</style>

<#assign permitUpdatePurPrice = false>
<#if hasOlbPermission("MODULE", "PRODUCTPO_EDIT_PURPRICE", "")><#assign permitUpdatePurPrice = true></#if>
<script type="text/javascript">
	<#assign uomList = delegator.findByAnd("Uom", {"uomTypeId" : "PRODUCT_PACKING"}, null, false)/>
	var uomData = [
	<#if uomList?exists>
		<#list uomList as uomItem>
		{	uomId: '${uomItem.uomId}',
			description: '${StringUtil.wrapString(uomItem.get("description", locale)?if_exists)}'
		},
		</#list>
	</#if>
	];
	
	var cellClassNameEditPurPrice = <#if permitUpdatePurPrice>'background-prepare'<#else>null</#if>;
</script>

<#assign priceDecimalDigits = 2>
<div class="row-fluid">
	<div class="span12">
		<#assign dataFieldSupplierProduct = "[
					{name: 'productId', type: 'string'},
					{name: 'productCode', type: 'string'},
					{name: 'partyId', type: 'string'},
					{name: 'partyCode', type: 'string'},
					{name: 'groupName', type: 'string'},
					{name: 'supplierPrefOrderId', type: 'string'},
					{name: 'minimumOrderQuantity', type: 'number'},
					{name: 'currencyUomId', type: 'string'},
					{name: 'lastPrice', type: 'number'},
					{name: 'shippingPrice', type: 'number'},
					{name: 'quantityUomId', type: 'string'},
					{name: 'supplierProductId', type: 'string'},
					{name: 'availableFromDate', type: 'date', other: 'Timestamp'},
					{name: 'availableThruDate', type: 'date', other: 'Timestamp'},
					{name: 'canDropShip', type: 'string'},
					{name: 'internalName', type: 'string'},
					{name: 'groupName', type: 'string'},
					{name: 'comments', type: 'string'}
				]"/>
		<#assign columnlistSupplierProduct = "
					{text: '${uiLabelMap.BSSupplier}', dataField: 'groupName', editable: false, 
						cellsrenderer: function(row, column, value) {
					 		var str = '<div class=\"innerGridCellContent\">';
					 		var data = $('#jqxgridSupplierProduct').jqxGrid('getrowdata', row);
					 		if (!value && data) {
						 		str += '(' + data.partyCode + ')';
					 		} else {
								str += value;
							}
							str += '</div>';
							return str;
					 	}
					}, 
					{text: '${uiLabelMap.BSMinQty}', dataField: 'minimumOrderQuantity', width: 100, cellsalign: 'right', editable: false, 
						cellsrenderer: function(row, column, value) {
					 		var str = '<div class=\"innerGridCellContent align-right\">';
					 		var data = $('#jqxgridSupplierProduct').jqxGrid('getrowdata', row);
					 		if (typeof(data) != 'undefined') {
						 		str += formatnumber(value);
					 		} else {
								str += value;
							}
							str += '</div>';
							return str;
					 	}
					}, 
					{text: '${uiLabelMap.BSPrice} (T)', dataField: 'lastPrice', width: 140, cellsalign: 'right', columntype: 'numberinput', cellsformat: 'd', cellClassName: cellClassNameEditPurPrice, 
						cellsrenderer: function(row, column, value) {
					 		var str = '<div class=\"innerGridCellContent align-right\">';
					 		var data = $('#jqxgridSupplierProduct').jqxGrid('getrowdata', row);
					 		if (typeof(data) != 'undefined') {
						 		str += formatcurrency(value, data.currencyUomId, null, ${priceDecimalDigits});
					 		} else {
								str += value;
							}
							str += '</div>';
							return str;
					 	},
					 	validation: function (cell, value) {
							if (value < 0) {
								return {result: false, message: '${uiLabelMap.BSQuantityMustBeGreaterThanZero}'};
							}
							return true;
						},
						createeditor: function (row, cellvalue, editor) {
							editor.jqxNumberInput({spinButtons: false, digits: 8, decimalDigits: ${priceDecimalDigits}, allowNull: true, min: 0, width: '100%', theme: OlbCore.theme});
							setTimeout(function(){
								var locale = '${locale}';
								if (locale == 'vi') editor.jqxNumberInput({decimalSeparator: ',', groupSeparator: '.'});
							}, 50);
						}
					}, 
					{text: '${uiLabelMap.BSProductPackingUomId}', dataField: 'quantityUomId', width: 120, editable: false, 
						cellsrenderer: function(row, column, value){
					 		var returnVal = '<div class=\"innerGridCellContent align-center\">';
				   			for (var i = 0 ; i < uomData.length; i++){
								if (value == uomData[i].uomId){
									returnVal += uomData[i].description + '</div>';
			   						return returnVal;
								}
							}
				   			returnVal += value + '</div>';
			   				return returnVal;
						},
					}, 
					{text: '${uiLabelMap.BSFromDate}', dataField: 'availableFromDate', width: 160, cellsformat: 'dd/MM/yyyy', filtertype:'range', editable: false,
						cellsrenderer: function(row, colum, value) {
							return '<div class=\"innerGridCellContent\">' + jOlbUtil.dateTime.formatFullDate(value) + '</div>';
						}
					},
				"/>
		<div id="jqxgridSupplierProduct"></div>
		<#if permitUpdatePurPrice>
		<a id="btnAddSupplierProduct" href="javascript:void(0)"><i class="fa fa-plus"></i></a>
		<a id="btnSubtractSupplierProduct" href="javascript:void(0)"><i class="fa fa-minus"></i></a>
		</#if>
	</div>
</div>

<#if permitUpdatePurPrice>
<#include "productNewSupplierPopup.ftl"/>
</#if>

<#assign updateMode = false/>
<#if product?exists>
	<#assign updateMode = true/>
</#if>
<#if !copyMode?exists><#assign copyMode = false/></#if>

<script type="text/javascript">
	$(function(){
		OlbSupplierProductNew.init();
		
		<#if copyMode>
			var data = $("#jqxgridSupplierProduct").jqxGrid("getboundrows");
			if (data) {
				for (var i = 0; i < data.length; i++) {
					var dataItem = data[i];
					if (dataItem != window) {
						var rowBoundIndex = $('#jqxgridSupplierProduct').jqxGrid('getrowboundindexbyid', data.uid);
						$('#jqxgridSupplierProduct').jqxGrid('setcellvalue', rowBoundIndex, 'productId', "");
						$('#jqxgridSupplierProduct').jqxGrid('setcellvalue', rowBoundIndex, 'productCode', "");
						$('#jqxgridSupplierProduct').jqxGrid('setcellvalue', rowBoundIndex, 'supplierProductId', "");
					}
				}
			}
		</#if>
	});
	var OlbSupplierProductNew = (function(){
		var init = function() {
			//AddSupplier.init();
			initElementComplex();
			initEvent();
		};
		var initElementComplex = function(){
			var configGridProduct = {
				theme: 'bootstrap',
				datafields: ${dataFieldSupplierProduct},
				columns: [${columnlistSupplierProduct}],
				width: '100%',
				height: 200,
				sortable: false,
				filterable: false,
				editable: <#if permitUpdatePurPrice>true<#else>false</#if>,
				pageable: false,
				pagesize: 10,
				showfilterrow: false,
				useUtilFunc: false,
				<#if updateMode>
				root: 'results',
				useUrl: true,
				url: 'jqxGeneralServicer?sname=JQGetListSupplierProduct&productId=${product.productId?if_exists}&isActive=Y&hasFuture=Y&pagesize=0', 
				<#else>
				useUrl: false,
				url: '',
				</#if>
				groupable: false,
				showdefaultloadelement:true,
				autoshowloadelement:true,
				selectionmode:<#if permitUpdatePurPrice>'singlerow'<#else>'none'</#if>,
				virtualmode: false,
			};
			new OlbGrid($("#jqxgridSupplierProduct"), null, configGridProduct, []);
			
			<#--
			var configGridProduct = {
				theme: 'bootstrap',
				datafields: ${dataFieldSupplierProduct},
				columns: [${columnlistSupplierProduct}],
				width: '100%',
				height: 200,
				sortable: false,
				filterable: false,
				editable: <#if permitUpdatePurPrice>true<#else>false</#if>,
				pageable: false,
				pagesize: 10,
				showfilterrow: false,
				useUtilFunc: false,
				<#if updateMode>
				root: 'listSuppliers',
				useUrl: true,
				url: 'getListSuppliersOfProduct', //jqxGeneralServicer?sname=JQListSalesOrder | getListSuppliersOfProduct
				<#if !copyMode>
				dataMap: {"productId": "${product.productId?if_exists}", "checkActive": "Y"},
				</#if>
				<#else>
				useUrl: false,
				url: '',
				</#if>
				groupable: false,
				showdefaultloadelement:true,
				autoshowloadelement:true,
				selectionmode:<#if permitUpdatePurPrice>'singlerow'<#else>'none'</#if>,
				virtualmode: false,
			};
			-->
		};
		var initEvent = function(){
			<#if permitUpdatePurPrice>
			$("#btnAddSupplierProduct").on("click", function(){
				$("#alterpopupWindowSupplierProductNew").jqxWindow("open");
			});
			$("#btnSubtractSupplierProduct").on("click", function(){
				var rowIndex = $("#jqxgridSupplierProduct").jqxGrid('getselectedrowindex');
				if (rowIndex == null || rowIndex < 0) {
					jOlbUtil.alert.error("${uiLabelMap.BSYouNotYetChooseRow}");
				} else {
					var rowData = $("#jqxgridSupplierProduct").jqxGrid('getrowdata', rowIndex);
					if (rowData) {
						$("#jqxgridSupplierProduct").jqxGrid('deleterow', rowData.uid);
					}
				}
			});
			</#if>
		};
		var getValue = function() {
			var data = $("#jqxgridSupplierProduct").jqxGrid('getboundrows');
			if (data) {
				var listDataResult = new Array();
				for (var i = 0; i < data.length; i++) {
					var dataItem = data[i];
					if (dataItem != window) {
						var itemMap = {
							productId: dataItem.productId,
							partyId: dataItem.partyId,
							supplierPrefOrderId: dataItem.supplierPrefOrderId,
							minimumOrderQuantity: dataItem.minimumOrderQuantity,
							currencyUomId: dataItem.currencyUomId,
							lastPrice: dataItem.lastPrice,
							shippingPrice: dataItem.shippingPrice,
							quantityUomId: dataItem.quantityUomId,
							supplierProductId: dataItem.supplierProductId,
							canDropShip: dataItem.canDropShip,
							comments: dataItem.comments,
						};
						
						var availableFromDate;
						var fromDateObject = dataItem.availableFromDate;
						if (OlbCore.isNotEmpty(fromDateObject)) {
							if (Object.prototype.toString.call(fromDateObject) === "[object Date]" || !isNaN(fromDateObject)) {
								availableFromDate = (new Date(fromDateObject)).getTime();
							} else if (typeof fromDateObject == "object") {
								availableFromDate = fromDateObject.time;
							} else if (typeof fromDateObject == "number") {
								availableFromDate = fromDateObject;
							}
						}
						
						var availableThruDate;
						var thruDateObject = dataItem.availableThruDate;
						if (OlbCore.isNotEmpty(thruDateObject)) {
							if (Object.prototype.toString.call(thruDateObject) === "[object Date]" || !isNaN(thruDateObject)) {
								availableThruDate = (new Date(thruDateObject)).getTime();
							} else if (typeof thruDateObject == "object") {
								availableThruDate = thruDateObject.time;
							} else if (typeof thruDateObject == "number") {
								availableThruDate = thruDateObject;
							}
						}
						
						itemMap.availableFromDate = availableFromDate;
						itemMap.availableThruDate = availableThruDate;
						
						listDataResult.push(itemMap);
					}
				};
				<#--
				for (var x in data) {
					if (data[x].availableFromDate) {
						data[x].availableFromDate = data[x].availableFromDate.time?data[x].availableFromDate.time:data[x].availableFromDate;
					}
					if (data[x].availableThruDate) {
						data[x].availableThruDate = data[x].availableThruDate.time?data[x].availableThruDate.time:data[x].availableThruDate;
					}
				}
				-->
				return JSON.stringify(listDataResult);
			}
			return "";
		};
		return {
			init: init,
			getValue: getValue,
		}
	}());
</script>