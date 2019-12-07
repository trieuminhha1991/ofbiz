<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<#assign localeStr = "vi" />
<#if locale != "vi">
<#assign localeStr = "en" />
</#if>
<#assign productPriceTypes = delegator.findList("ProductPriceType", null, null, null, null, false) />
<#assign currencyUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "CURRENCY_MEASURE"), null, null, null, false) />
<#assign quantityUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false) />
<script>
	var locale = "${localeStr}";
	var editable = false;
	<#if hasOlbPermission("MODULE", "PRODPRICE_EDIT", "")>
	editable = true;
	</#if>
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

<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord) {
	var grid = $($(parentElement).children()[0]);
	$(grid).attr('id','jqxgridDetail'+index);
	var sourceGridDetail =
	{
	    datatype: 'json',
	    url: 'getListPricesOfProduct?productId=' + datarecord.productId,
        async: false,
	    datafields:
	    [
			{ name: 'productId', type: 'string'},
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
			{ name: 'fromDate', type: 'date'},
			{ name: 'thruDate', type: 'date'}
	    ],
	    addrow: function (rowid, rowdata, position, commit) {
	    	if (locale=='vi') {
        		newdata.price = newdata.price.toLocaleString(locale);
			}
	    	commit(DataAccess.execute({
				url: 'createProductPrice',
				data: rowdata}, ProductPrice.notify));
        },
        updaterow: function (rowid, newdata, commit) {
        	if (typeof (newdata.fromDate) == 'object') {
				var originFromDate = newdata.fromDate.time;
				newdata.fromDate?newdata.fromDate=new Date(newdata.fromDate.time).toSQLTimeStamp():newdata.fromDate;
				var sec = originFromDate - new Date(newdata.fromDate).getTime();
				if (sec < 100) {
					sec = '0' + sec;
				}
				newdata.fromDate = newdata.fromDate + '.' + sec;
			}
        	if (typeof (newdata.thruDate) == 'object') {
        		newdata.thruDate?newdata.thruDate=new Date(newdata.thruDate).toSQLTimeStamp():newdata.thruDate;
        	}
        	if (locale=='vi') {
        		newdata.price = newdata.price.toLocaleString(locale);
			}
        	commit(DataAccess.execute({
				url: 'updateProductPrice',
				data: newdata}, ProductPrice.notify));
        }
	};
	var dataAdapterGridDetail = new $.jqx.dataAdapter(sourceGridDetail);
	grid.jqxGrid({
		localization: getLocalization(),
	    width: '98%',
	    height: '92%',
	    theme: 'olbius',
	    source: dataAdapterGridDetail,
	    sortable: true,
	    pagesize: 5,
	    editable: false,
	    columnsresize: true,
		pageable: true,
	    selectionmode: 'singlerow',
	    showtoolbar: true,
	    rendertoolbar: rendertoolbar,
	    columns: [
				{text: '${uiLabelMap.BPOSequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
				    cellsrenderer: function (row, column, value) {
				        return '<div style=margin:4px;>' + (row + 1) + '</div>';
				    }
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.ProductPriceType)}', datafield: 'productPriceTypeId', columntype: 'dropdownlist', filtertype: 'checkedlist', width: 150,
					cellsrenderer: function(row, colum, value){
					   value?value=mapProductPriceType[value]:value;
				       return '<span>' + value + '</span>';
				   	}
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.ProductPrice)}', datafield: 'price', columntype: 'numberinput', minwidth: 150,
					cellsrenderer: function (row, column, value) {
				        return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
				    }
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.ProductCurrencyUom)}', datafield: 'currencyUomId', columntype: 'dropdownlist', filtertype: 'checkedlist', width: 150,
					cellsrenderer: function(row, colum, value){
					   value?value=mapCurrencyUom[value]:value;
					       return '<span>' + value + '</span>';
				   	}
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.DmsQuantityUomId)}', datafield: 'termUomId', columntype: 'dropdownlist', filtertype: 'checkedlist', width: 150,
					cellsrenderer: function(row, colum, value){
					   value?value=mapQuantityUom[value]:value;
					       return '<span>' + value + '</span>';
				   	}
				},
				{ text: '${uiLabelMap.DmsFromDate}', datafield: 'fromDate', width: 150,
					cellsrenderer: function(row, colum, value){
					   value.time?value=new Date(value.time).toTimeOlbius():value;
				       return '<span>' + value + '</span>';
				   	}
				},
				{ text: '${uiLabelMap.DmsThruDate}', datafield: 'thruDate', width: 150,
					cellsrenderer: function(row, colum, value){
					   value.time?value=new Date(value.time).toTimeOlbius():value;
				       return '<span>' + value + '</span>';
				   	}
				}]
	});
	grid.on('contextmenu', function () {
        return false;
    });
	grid.on('rowclick', function (event) {
		if (gridSelecting && gridSelecting != grid) {
			gridSelecting.jqxGrid('clearselection');
		}
		gridSelecting = grid;
        if (event.args.rightclick) {
        	grid.jqxGrid('selectrow', event.args.rowindex);
            var scrollTop = $(window).scrollTop();
            var scrollLeft = $(window).scrollLeft();
            $('#contextMenu').jqxMenu('open', parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop);
            return false;
        }
    });
	function rendertoolbar(toolbar) {
		var container = $(\"<div style='margin: 17px 4px 0px 0px;' class='pull-right'></div>\");
        var aTag = $(\"<a style='cursor: pointer;'><i class='fa-plus open-sans'></i>${StringUtil.wrapString(uiLabelMap.CommonAddNew)}</a>\");
        toolbar.append(container);
        container.append(aTag);
        aTag.click(function() {
        	AddProductPrice.open(datarecord.productId, grid);
		});
	}
}"/>
<#assign dataField="[{ name: 'productId', type: 'string'},
				{ name: 'productCode', type: 'string'},
				{ name: 'primaryProductCategoryId', type: 'string'},
				{ name: 'internalName', type: 'string'},
				{ name: 'productName', type: 'string'},
				{ name: 'brandName', type: 'string'},
				{ name: 'productWeight', type: 'number'},
				{ name: 'weightUomId', type: 'string'},
				{ name: 'quantityUomId', type: 'string'},
				{ name: 'description', type: 'string'},
				{ name: 'taxCatalogs', type: 'string'}]"/>

<#assign columnlist="{text: '${uiLabelMap.BPOSequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
				    cellsrenderer: function (row, column, value) {
				        return '<div style=margin:4px;>' + (row + 1) + '</div>';
				    }
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.ProductProductId)}', datafield: 'productCode', width: 200,
					cellsrenderer: function(row, colum, value){
						var productId = $('#jqxgrid').jqxGrid('getcellvalue', row, 'productId');
				        var link = 'viewProduct?productId=' + productId;
				        return '<div style=\"margin:4px\"><a href=\"' + link + '\">' + value + '</a></div>';
					}
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.ProductProductName)}', datafield: 'productName', width: 200 },
				{ text: '${StringUtil.wrapString(uiLabelMap.DmsInternalName)}', datafield: 'internalName', width: 200 },
				{ text: '${StringUtil.wrapString(uiLabelMap.DmsDescription)}', datafield: 'description', minwidth: 250 },
				{ text: '${StringUtil.wrapString(uiLabelMap.DmsProductTaxCatalogs)}', datafield: 'taxCatalogs', width: 150, sortable: false, filterable: false }"/>

<@jqGrid filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true"
		showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="false"
		url="jqxGeneralServicer?sname=JQGetListProductsPrices"
		initrowdetails="true" initrowdetailsDetail=initrowdetailsDetail rowdetailsheight="203"/>

<div id="jqxNotificationPrice">
	<div id="notificationContentPrice"></div>
</div>

<div id='contextMenu' style="display:none">
	<ul>
		<li id="update"><i class="fa-pencil-square-o open-sans"></i>&nbsp;&nbsp;${StringUtil.wrapString(uiLabelMap.CommonUpdate)}</li>
		<li id="delete"><i class="fa-minus-circle open-sans"></i>&nbsp;&nbsp;${StringUtil.wrapString(uiLabelMap.BSExpired)}</li>
	</ul>
</div>
<#include "popup/addProductPrice.ftl"/>
<script>
	$(document).ready(function() {
		ProductPrice.init();
	});
	var gridSelecting;
	var ProductPrice = (function() {
		var initJqxElements = function() {
			$("#jqxNotificationPrice").jqxNotification({ width: "100%", appendContainer: "#container",
				opacity: 0.9, autoClose: true, template: "info" });
			
			$("#contextMenu").jqxMenu({ theme: 'olbius', width: 200, autoOpenPopup: false, mode: 'popup', theme: theme});
		};
		var handlerEvent = function() {
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
						if (rowData.thruDate) {
							var thruDate = rowData.thruDate.time;
							var nowDate = new Date().getTime();
							if (thruDate < nowDate) {
								gridSelecting.jqxGrid('setcellvaluebyid', rowData.uid, "thruDate", null);
							} else {
								gridSelecting.jqxGrid('setcellvaluebyid', rowData.uid, "thruDate", new Date());
							}
						} else {
							gridSelecting.jqxGrid('setcellvaluebyid', rowData.uid, "thruDate", new Date());
						}
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
							$("#delete").html("<i class='fa-check open-sans'></i>&nbsp;&nbsp;${StringUtil.wrapString(uiLabelMap.BSUnexpired)}");
						} else {
							$("#delete").html("<i class='fa-minus-circle open-sans'></i>&nbsp;&nbsp;${StringUtil.wrapString(uiLabelMap.BSExpired)}");
						}
					}
				}
			});
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
				handlerEvent();
			},
			notify: notify
		};
	})();
</script>