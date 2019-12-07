<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<@jqGridMinimumLib/>

<#assign UpdateMode = "false" />
<#assign customTitleProperties = "${uiLabelMap.BSBasePrice}"/>
<#assign enableRenderToolbar = true/>

<#assign listProductPriceTypes = delegator.findList("ProductPriceType", null, null, null, null, false)!/>
<script type="text/javascript">
	var productPriceTypesData = [
	<#if listProductPriceTypes?exists>
		<#list listProductPriceTypes as item>
		{	productPriceTypeId: '${item.productPriceTypeId?if_exists}',
			description: "${StringUtil.wrapString(item.get("description", locale))}"
		},
		</#list>
	</#if>
	];
	var productPriceTypeMap = {
	<#if listProductPriceTypes?exists>
		<#list listProductPriceTypes as item>"${item.productPriceTypeId?if_exists}": "${StringUtil.wrapString(item.get("description", locale))}",</#list>
	</#if>
	};
	
	var cellClassProdPriceNor = function(row, columnfield, value) {
 		var data = $('#jqxgridProductPrices').jqxGrid('getrowdata', row);
 		var returnValue = "";
 		if (typeof(data) != 'undefined') {
 			var now = new Date();
			if (data.thruDate != null && data.thruDate < now) {
				return "background-cancel";
			} else if (data.fromDate >= now) {
				return "background-prepare";
			}
 		}
    }
</script>

<div id="jqxgridProductPrices"></div>

<#--
<#assign permitUpdate = false>
<#if hasOlbPermission("MODULE", "PRODPRICE_EDIT", "")><#assign permitUpdate = true></#if>
<#assign contextMenuItemIdPriceNor = "ctxmnupricenor">
<div id='contextMenu_${contextMenuItemIdPriceNor}' style="display:none">
	<ul>
		<li id="${contextMenuItemIdPriceNor}_update"><i class="fa-pencil-square-o open-sans"></i>${StringUtil.wrapString(uiLabelMap.CommonUpdate)}</li>
		<li id="${contextMenuItemIdPriceNor}_delete"><i class="fa-times open-sans"></i>${StringUtil.wrapString(uiLabelMap.BSExpired)}</li>
	</ul>
</div>
-->

<#--<#include "component://basesales/webapp/basesales/product/popup/addProductPrice.ftl"/>-->

<#--
<#assign localeStr = "vi" />
<#if locale != "vi">
<#assign localeStr = "en" />
</#if>
-->

<script type="text/javascript">
	$(function(){
		OlbProdViewPriceNormal.init();
	});
	var OlbProdViewPriceNormal = (function(){
		var init = function(){
			initElementComplex();
		};
		var initElementComplex = function(){
			var configGridProductPrices = {
				datafields: [
					{ name: 'productId', type: 'string'},
					{ name: 'productCode', type: 'string'},
					{ name: 'productPriceTypeId', type: 'string'},
					{ name: 'productPricePurposeId', type: 'string'},
					{ name: 'currencyUomId', type: 'string'},
					{ name: 'productStoreGroupId', type: 'string'},
					{ name: 'price', type: 'number'},
					{ name: 'taxAmount', type: 'number'},
					{ name: 'termUomId', type: 'string'},
					{ name: 'customPriceCalcService', type: 'string'},
					{ name: 'priceWithoutTax', type: 'number'},
					{ name: 'priceWithTax', type: 'number'},
					{ name: 'taxAmount', type: 'number'},
					{ name: 'taxPercentage', type: 'number'},
					{ name: 'taxAuthPartyId', type: 'string'},
					{ name: 'taxAuthGeoId', type: 'string'},
					{ name: 'taxInPrice', type: 'string'},
					{ name: 'fromDate', type: 'date', other: 'Timestamp'},
					{ name: 'thruDate', type: 'date', other: 'Timestamp'}
				],
				columns: [
					{text: '${uiLabelMap.BPOSequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, cellclassname: cellClassProdPriceNor, width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (row + 1) + '</div>';
					    }
					},
					{text: '${StringUtil.wrapString(uiLabelMap.ProductPriceType)}', datafield: 'productPriceTypeId', minWidth: 120, columntype: 'dropdownlist', filtertype: 'checkedlist', cellclassname: cellClassProdPriceNor, 
						cellsrenderer: function(row, colum, value) {
							var returnValue = value;
							if (value) returnValue = productPriceTypeMap[value];
					       	return '<span>' + returnValue + '</span>';
					   	}
					},
					{text: '${StringUtil.wrapString(uiLabelMap.ProductPrice)} ${uiLabelMap.BSBeforeVAT}', datafield: 'price', width: 120, columntype: 'numberinput', cellclassname: cellClassProdPriceNor,
						cellsrenderer: function (row, column, value) {
					        if (OlbCore.isNotEmpty(value)) {
					        	return '<div class="text-right">' + value.toLocaleString(locale) + '</div>';
				        	} else {
				        		return '<div class="text-right"></div>';
				        	}
					    }
					},
					{text: '${StringUtil.wrapString(uiLabelMap.ProductPrice)} ${StringUtil.wrapString(uiLabelMap.BSTax)}', datafield: 'taxAmount', width: 120, columntype: 'numberinput', cellclassname: cellClassProdPriceNor,
						cellsrenderer: function (row, column, value) {
							if (OlbCore.isNotEmpty(value)) {
					        	return '<div class="text-right">' + value.toLocaleString(locale) + '</div>';
				        	} else {
				        		return '<div class="text-right"></div>';
				        	}
					    }
					},
					{text: '${StringUtil.wrapString(uiLabelMap.ProductPrice)} ${uiLabelMap.BSAfterVAT}', datafield: 'priceTotal', width: 120, columntype: 'numberinput', cellclassname: cellClassProdPriceNor,
						cellsrenderer: function (row, column, value) {
							var resultValue;
							var data = $('#jqxgridProductPrices').jqxGrid('getrowdata', row);
							if (data) {
								resultValue = data.price;
								if (data.taxAmount) resultValue += data.taxAmount;
							}
					        if (OlbCore.isNotEmpty(resultValue)) {
								return '<div class="text-right">' + resultValue.toLocaleString(locale) + '</div>';
							} else {
								return '<div class="text-right"></div>';
							}
					    }
					},
					{text: '${StringUtil.wrapString(uiLabelMap.BSCurrencyUomId)}', datafield: 'currencyUomId', width: 100, columntype: 'dropdownlist', filtertype: 'checkedlist', cellclassname: cellClassProdPriceNor},
					{text: '${StringUtil.wrapString(uiLabelMap.BSProductPackingUomId)}', datafield: 'termUomId', width: 110, columntype: 'dropdownlist', filtertype: 'checkedlist', cellclassname: cellClassProdPriceNor,
						cellsrenderer: function(row, colum, value) {
						   	var returnValue = value;
							if (value) returnValue = quantityUomMap[value];
					       	return '<span>' + returnValue + '</span>';
					   	}
					},
					{text: '${uiLabelMap.BSFromDate}', datafield: 'fromDate', width: 150, cellsformat: 'dd/MM/yyyy - HH:mm:ss', filtertype: 'range', cellclassname: cellClassProdPriceNor,
						cellsrenderer: function(row, colum, value) {
							return '<span>' + jOlbUtil.dateTime.formatFullDate(value) + '</span>';
						}
					},
					{text: '${uiLabelMap.BSThruDate}', datafield: 'thruDate', width: 150, cellsformat: 'dd/MM/yyyy - HH:mm:ss', filtertype: 'range', cellclassname: cellClassProdPriceNor,
						cellsrenderer: function(row, colum, value) {
							return '<span>' + jOlbUtil.dateTime.formatFullDate(value) + '</span>';
						}
					}
				],
				width: '100%',
				height: 200,
				sortable: true,
				filterable: false,
				pageable: true,
				pagesize: 6,
			    pagesizeoptions: [6, 10, 15, 20, 25, 50, 100],
				showfilterrow: false,
				useUtilFunc: false,
				useUrl: true,
				url: 'jqxGeneralServicer?sname=JQGetListProdPriceByProduct&productId=${parameters.productId?if_exists}',
				showdefaultloadelement:true,
				autoshowloadelement:true,
			    selectionmode: 'singlerow',
				showtoolbar:true,
				rendertoolbarconfig: {
					titleProperty: "${StringUtil.wrapString(uiLabelMap.BSBasePrice)}",
				},
			};
			new OlbGrid($("#jqxgridProductPrices"), null, configGridProductPrices, []);
		};
		return {
			init: init
		}
	}());
</script>