<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<@jqGridMinimumLib/>

<style>
	.expired {
		background-color: #eeeeee !important;
    	cursor: not-allowed;
	}
	.expired.jqx-widget-olbius .expired.jqx-grid-cell-selected-olbius, .jqx-widget-olbius .expired.jqx-grid-cell-hover-olbius {
		background-color: #eeeeee !important;
		cursor: not-allowed;
	}
</style>

<div id="container"></div>
<div id="jqxgridProductPrices"></div>


<div id="jqxNotificationPrice">
	<div id="notificationContentPrice"></div>
</div>

<div id='contextMenu' style="display:none">
	<ul>
		<li id="update"><i class="fa-pencil-square-o open-sans"></i>&nbsp;&nbsp;${StringUtil.wrapString(uiLabelMap.CommonUpdate)}</li>
		<li id="delete"><i class="fa-times open-sans"></i>&nbsp;&nbsp;${StringUtil.wrapString(uiLabelMap.BSExpired)}</li>
	</ul>
</div>

<#if !UpdateMode?exists>
	<#assign UpdateMode = "true" />
</#if>
	
<#if !hasOlbPermission("MODULE", "PRODPRICE_EDIT", "")>
	<#assign UpdateMode = "false" />
</#if>


<#if UpdateMode!="false">
	<#include "component://basesales/webapp/basesales/product/popup/addProductPrice.ftl"/>
</#if>
<#assign localeStr = "vi" />
<#if locale != "vi">
<#assign localeStr = "en" />
</#if>
<#assign productPriceTypes = delegator.findList("ProductPriceType", null, null, null, null, false) />
<#assign currencyUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "CURRENCY_MEASURE"), null, null, null, false) />
<#assign quantityUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false) />
<script>
	var productPriceTypes = [<#if productPriceTypes?exists><#list productPriceTypes as item>{
		productPriceTypeId: '${item.productPriceTypeId?if_exists}',
		description: "${StringUtil.wrapString(item.get("description", locale))}"
	},</#list></#if>];
	var mapProductPriceType = {<#if productPriceTypes?exists><#list productPriceTypes as item>
		"${item.productPriceTypeId?if_exists}": "${StringUtil.wrapString(item.get("description", locale))}",
	</#list></#if>};
	var currencyUoms = [<#if currencyUoms?exists><#list currencyUoms as item>{
		uomId: '${item.uomId?if_exists}',
		description: "${StringUtil.wrapString(item.get("description", locale))}"
	},</#list></#if>];
	var mapCurrencyUom = {<#if currencyUoms?exists><#list currencyUoms as item>
	"${item.uomId?if_exists}": "${StringUtil.wrapString(item.get("description", locale))}",
	</#list></#if>};
	var quantityUoms = [<#if quantityUoms?exists><#list quantityUoms as item>{
    	uomId: '${item.uomId?if_exists}',
    	description: "${StringUtil.wrapString(item.get("description", locale))}"
    },</#list></#if>];
	var mapQuantityUom = {<#if quantityUoms?exists><#list quantityUoms as item>
		"${item.uomId?if_exists}": "${StringUtil.wrapString(item.get("description", locale))}",
	</#list></#if>};
</script>
<script>
	var productIdParam = "${parameters.productId?if_exists}";
	$(document).ready(function() {
		ProductPrice.init();
		$("#divProductId").text("${(product.productCode)?if_exists}");
	});
	var gridSelecting = $("#jqxgridProductPrices");
	var ProductPrice = (function() {
		var grid = $("#jqxgridProductPrices");
		
		var initJqxElements = function() {
			var source =
			{
			    datatype: 'json',
			    url: 'getListPricesOfProduct?productId=${parameters.productId?if_exists}',
		        async: false,
			    datafields:
			    [
					{ name: 'productId', type: 'string'},
					{ name: 'productCode', type: 'string'},
					{ name: 'productPriceTypeId', type: 'string'},
					{ name: 'productPricePurposeId', type: 'string'},
					{ name: 'currencyUomId', type: 'string'},
					{ name: 'productStoreGroupId', type: 'string'},
					{ name: 'price', type: 'number'},
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
			    addrow: function (rowid, rowdata, position, commit) {
			    	if (locale=='vi') {
			    		rowdata.price = rowdata.price.toLocaleString(locale);
					}
			    	commit(DataAccess.execute({
						url: 'createProductPriceCustom',
						data: rowdata}, ProductPrice.notify));
		        },
		        updaterow: function (rowid, newdata, commit) {
		        	if (typeof (newdata.fromDate) == 'object') {
		        		var originFromDate = newdata.fromDate.time;
		        		newdata.fromDate = originFromDate;
					}
		        	if (typeof (newdata.thruDate) == 'object') {
		        		newdata.thruDate?newdata.thruDate=new Date(newdata.thruDate).getTime():newdata.thruDate;
		        	}
		        	if (locale=='vi') {
		        		newdata.price = newdata.price.toLocaleString(locale);
					}
		        	commit(DataAccess.execute({
						url: 'updateProductPriceCustom',
						data: newdata}, ProductPrice.notify));
		        }
			};
			var dataAdapter = new $.jqx.dataAdapter(source);
			grid.jqxGrid({
				localization: getLocalization(),
			    width: '100%',
			    autoheight: true,
			    theme: 'olbius',
			    source: dataAdapter,
			    sortable: true,
			    pagesize: 6,
			    pagesizeoptions: [6, 10, 15, 20, 25, 50, 100],
			    editable: false,
			    columnsresize: true,
				pageable: true,
			    selectionmode: 'singlerow',
			    <#if UpdateMode!="false" || (enableRenderToolbar?exists && enableRenderToolbar)>
			    showtoolbar: true,
			    rendertoolbar: rendertoolbar,
			    </#if>
			    columns: [
						{text: '${uiLabelMap.BPOSequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, cellclassname: cellclassname, width: 50,
						    cellsrenderer: function (row, column, value) {
						        return '<div style=margin:4px;>' + (row + 1) + '</div>';
						    }
						},
						{ text: '${StringUtil.wrapString(uiLabelMap.ProductPriceType)}', datafield: 'productPriceTypeId', columntype: 'dropdownlist', filtertype: 'checkedlist', cellclassname: cellclassname, 
							cellsrenderer: function(row, colum, value){
							   value?value=mapProductPriceType[value]:value;
						       return '<span>' + value + '</span>';
						   	}
						},
						{ text: '${StringUtil.wrapString(uiLabelMap.ProductPrice)}', datafield: 'price', columntype: 'numberinput', cellclassname: cellclassname, width: 150,
							cellsrenderer: function (row, column, value) {
						        return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
						    }
						},
						{ text: '${StringUtil.wrapString(uiLabelMap.ProductCurrencyUom)}', datafield: 'currencyUomId', columntype: 'dropdownlist', filtertype: 'checkedlist', cellclassname: cellclassname, width: 130,
							cellsrenderer: function(row, colum, value){
							   value?value=mapCurrencyUom[value]:value;
							   return '<span>' + value + '</span>';
						   	}
						},
						{ text: '${StringUtil.wrapString(uiLabelMap.DmsQuantityUomId)}', datafield: 'termUomId', columntype: 'dropdownlist', filtertype: 'checkedlist', cellclassname: cellclassname, width: 130,
							cellsrenderer: function(row, colum, value){
							   value?value=mapQuantityUom[value]:value;
							   return '<span>' + value + '</span>';
						   	}
						},
						{ text: '${uiLabelMap.DmsFromDate}', datafield: 'fromDate', cellclassname: cellclassname, width: 150,
							cellsrenderer: function(row, colum, value){
								value.time?value=new Date(value.time).toTimeOlbius():value;
								return '<span>' + value + '</span>';
						   	}
						},
						{ text: '${uiLabelMap.DmsThruDate}', datafield: 'thruDate', cellclassname: cellclassname, width: 150,
							cellsrenderer: function(row, colum, value){
								value.time?value=new Date(value.time).toTimeOlbius():value;
								return '<span>' + value + '</span>';
						   	}
						}]
			});
			function rendertoolbar(toolbar) {
				var container = $("<div style='margin: 17px 4px 0px 0px;' class='pull-right'></div>");
		        var aTag = $("<a style='cursor: pointer;'><i class='fa-plus open-sans'></i>${StringUtil.wrapString(uiLabelMap.CommonAddNew)}</a>");
		        var titleProperty = $("<h4 style='color: #4383b4;'><#if customTitleProperties?exists>${StringUtil.wrapString(customTitleProperties)}<#else>${StringUtil.wrapString(uiLabelMap.ListProductPrice)}</#if></h4>");
		        toolbar.append(container);
		        toolbar.append(titleProperty);
		        <#if UpdateMode!="false">
		        container.append(aTag);
		        aTag.click(function() {
		        	AddProductPrice.open('${parameters.productId?if_exists}', grid);
				});
				</#if>
			}
			<#if UpdateMode!="false">
			$("#jqxNotificationPrice").jqxNotification({ width: "100%", appendContainer: "#container",
				opacity: 0.9, autoClose: true, template: "info" });
			$("#contextMenu").jqxMenu({ theme: 'olbius', width: 200, autoOpenPopup: false, mode: 'popup', theme: theme});
			Grid.addContextMenuHoverStyle(grid, $("#contextMenu"));
			</#if>
		};
		var handleEvents = function() {
			<#if UpdateMode!="false">
			grid.on('contextmenu', function () {
		        return false;
		    });
			grid.on('rowclick', function (event) {
		        if (event.args.rightclick) {
		        	grid.jqxGrid('selectrow', event.args.rowindex);
		            var scrollTop = $(window).scrollTop();
		            var scrollLeft = $(window).scrollLeft();
		            $('#contextMenu').jqxMenu('open', parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop);
		            return false;
		        }
		    });
		    $('body').on('click', function() {
				$("#contextMenu").jqxMenu('close');
			});
			$("#contextMenu").on('itemclick', function (event) {
		        var args = event.args;
		        var itemId = $(args).attr('id');
		        switch (itemId) {
				case "delete":
					if (gridSelecting) {
						var rowIndexSelected = gridSelecting.jqxGrid('getSelectedRowindex');
						var rowData = gridSelecting.jqxGrid('getrowdata', rowIndexSelected);
						gridSelecting.jqxGrid('setcellvaluebyid', rowData.uid, "thruDate", new Date());
					}
					break;
				case "update":
					if (gridSelecting) {
						var rowIndexSelected = gridSelecting.jqxGrid('getSelectedRowindex');
						var rowData = gridSelecting.jqxGrid('getrowdata', rowIndexSelected);
						AddProductPrice.setValue(rowData);
						AddProductPrice.open();
					}
					break;
				default:
					break;
				}
			});
			$("#contextMenu").on('shown', function () {
				if (gridSelecting) {
					var rowIndexSelected = gridSelecting.jqxGrid('getSelectedRowindex');
					var rowData = gridSelecting.jqxGrid('getrowdata', rowIndexSelected);
					if (rowData.thruDate) {
						var thruDate = rowData.thruDate.time;
						var nowDate = new Date().getTime();
						if (thruDate < nowDate) {
							$("#contextMenu").jqxMenu('disable', 'delete', true);
							$("#contextMenu").jqxMenu('disable', 'update', true);
						} else {
							$("#contextMenu").jqxMenu('disable', 'delete', false);
							$("#contextMenu").jqxMenu('disable', 'update', false);
						}
					} else {
						$("#contextMenu").jqxMenu('disable', 'delete', false);
						$("#contextMenu").jqxMenu('disable', 'update', false);
					}
				}
			});
			$($(".breadcrumb").children()[2]).html("<span class='divider'><i class='icon-angle-right'></i></span>${StringUtil.wrapString(uiLabelMap.ListProductPrice)}");
			</#if>
		};
		var cellclassname = function (row, column, value, data) {
			if (data.thruDate) {
				var thruDate = data.thruDate.time;
				var nowDate = new Date().getTime();
				if (thruDate < nowDate) {
					return "expired";
				}
			}
	        return "";
	    };
		var notify = function(res) {
			$('#jqxNotificationPrice').jqxNotification('closeLast');
			if(res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]){
				$("#jqxNotificationPrice").jqxNotification({ template: 'error'});
				$("#notificationContentPrice").text(multiLang.updateError);
				$("#jqxNotificationPrice").jqxNotification("open");
			}else {
				$("#jqxNotificationPrice").jqxNotification({ template: 'info'});
				$("#notificationContentPrice").text(multiLang.updateSuccess);
				$("#jqxNotificationPrice").jqxNotification("open");
				if (gridSelecting) {
					setTimeout(function() {
						gridSelecting.jqxGrid('updatebounddata');
					}, 10);
				}
			}
		};
		return {
			init: function() {
				initJqxElements();
				handleEvents();
			},
			notify: notify
		};
	})();
</script>