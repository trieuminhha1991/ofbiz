
<#assign productFeatureTypes = Static["com.olbius.basepo.product.ProductUtils"].getProductFeatureTypes(delegator) />

<#assign listQuantityUom = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false) />
<#assign listWeightUom = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "WEIGHT_MEASURE"), null, null, null, false) />

<style type="text/css">
	li#refesh {
		border-bottom: 1px solid #ddd;
	}
</style>

<script type="text/javascript">
	var listQuantityUom = [
	<#if listQuantityUom?exists>
		<#list listQuantityUom as item>
		{	uomId: "${item.uomId?if_exists}", 
			description: "${StringUtil.wrapString(item.get("description", locale))}"
		},
		</#list>
	</#if>
	];
	var mapQuantityUom = {<#if listQuantityUom?exists><#list listQuantityUom as item>
		"${item.uomId?if_exists}": "${StringUtil.wrapString(item.get("description", locale))}",
	</#list></#if>};
	
	var mapProductFeatureType = {<#if productFeatureTypes?exists><#list productFeatureTypes as item>
		"${item.productFeatureTypeId?if_exists}": "${StringUtil.wrapString(item.get("description", locale))}",
	</#list></#if>};
	var productFeatureTypes = [<#if productFeatureTypes?exists><#list productFeatureTypes as item>"${item.productFeatureTypeId?if_exists}",
    </#list></#if>];
    
    var productStateData = [
    	{itemId: "NORMAL", description: "${StringUtil.wrapString(uiLabelMap.BSNormal)}"},
    	{itemId: "DISCONTINUE_SALES", description: "${StringUtil.wrapString(uiLabelMap.BSDiscountinueSales)}"},
    	{itemId: "DISCONTINUE_PURCHASE", description: "${StringUtil.wrapString(uiLabelMap.BSDiscountinuePurchase)}"},
    ];
    
    var gridDetailSelected;
    
    var cellClassProdList = function (row, columnfield, value) {
 		var data = $('#jqxgridProdList').jqxGrid('getrowdata', row);
 		if (typeof(data) != 'undefined') {
 			var salesDiscontinuationDate = data.salesDiscontinuationDate;
 			var purchaseDiscontinuationDate = data.purchaseDiscontinuationDate;
 			var nowTimestamp = new Date();
 			if (OlbCore.isNotEmpty(salesDiscontinuationDate)) {
 				if (nowTimestamp > salesDiscontinuationDate) {
 					return "background-cancel";
 				}
 			} else if (OlbCore.isNotEmpty(purchaseDiscontinuationDate)) {
 				if (nowTimestamp > purchaseDiscontinuationDate) {
 					return "background-cancel";
 				}
 			}
 		}
    }
    
    var filterObjData = new Object();
</script>

<#--
{ name: 'brandName', type: 'string' },
{ name: 'taxCatalogs', type: 'string' },
{ name: 'productWeight', type: 'number' },
{ name: 'weightUomId', type: 'string' },
-->
<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord) {
	var grid = $($(parentElement).children()[0]);
	$(grid).attr('id','jqxgridDetail'+index);
	var sourceGridDetail = {
		localdata: datarecord.rowDetail,
		datatype: 'local',
		datafields: [
			{ name: 'productId', type: 'string' },
			{ name: 'productCode', type: 'string' },
			{ name: 'primaryProductCategoryId', type: 'string' },
			{ name: 'productName', type: 'string' },
			{ name: 'quantityUomId', type: 'string' },
			{ name: 'longDescription', type: 'string' },
			{ name: 'isVirtual', type: 'string' },
		]
    };
	var columns = [
			{text: '${uiLabelMap.BPOSequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
				cellsrenderer: function (row, column, value) {
					return '<div style=margin:4px;>' + (row + 1) + '</div>';
				}
			},
			{ text: '${StringUtil.wrapString(uiLabelMap.ProductProductId)}', datafield: 'productCode', width: 150,
				cellsrenderer: function(row, colum, value){
					var productId = grid.jqxGrid('getcellvalue', row, 'productId');
					var link = 'viewProduct?productId=' + productId;
					return '<div style=\"margin:4px\"><a href=\"' + link + '\">' + value + '</a></div>';
				}
			},
			{ text: '${StringUtil.wrapString(uiLabelMap.ProductProductName)}', datafield: 'productName', minWidth: 200 },
			{ text: '${StringUtil.wrapString(uiLabelMap.DmsDescription)}', datafield: 'longDescription', width: 250 }
		];
	for (var x in productFeatureTypes) {
		sourceGridDetail.datafields.push({ name: productFeatureTypes[x], type: 'string'});
		columns.push({ text: mapProductFeatureType[productFeatureTypes[x]], datafield: productFeatureTypes[x], width: 200 });
	}
	var dataAdapterGridDetail = new $.jqx.dataAdapter(sourceGridDetail);
	
	grid.jqxGrid({
		localization: getLocalization(),
		width: '98%',
		height: '92%',
		theme: theme,
		source: dataAdapterGridDetail,
		sortable: true,
		pagesize: 5,
		columnsresize: true,
		enabletooltips: true,
		pageable: true,
		selectionmode: 'singlerow',
		columns: columns
	});
	grid.on('rowclick', function (event) {
		if (event.args.rightclick) {
			grid.jqxGrid('selectrow', event.args.rowindex);
			var scrollTop = $(window).scrollTop();
			var scrollLeft = $(window).scrollLeft();
			$('#contextMenu').jqxMenu('open', parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop);
			gridDetailSelected = grid;
			$('#jqxgridProdList').jqxGrid('clearselection');
			return false;
		}
	});
}"/>

<#assign dataField="[
			{ name: 'productId', type: 'string' },
			{ name: 'productCode', type: 'string' },
			{ name: 'numChild', type: 'number' },
			{ name: 'primaryProductCategoryId', type: 'string' },
			{ name: 'productName', type: 'string' },
			{ name: 'quantityUomId', type: 'string' },
			{ name: 'longDescription', type: 'string' },
			{ name: 'isVirtual', type: 'string' },
			{ name: 'rowDetail', type: 'string' },
			{ name: 'salesDiscontinuationDate', type: 'date', other: 'Timestamp'},
			{ name: 'purchaseDiscontinuationDate', type: 'date', other: 'Timestamp'},
			{ name: 'productState', type: 'string' },
			{ name: 'idSKU', type: 'string' },
		]"/>
<#--
{ name: 'internalName', type: 'string' },
{ name: 'brandName', type: 'string' },
{ name: 'productFeatureTaste', type: 'string' },
{ name: 'productFeatureSize', type: 'string' },
{ name: 'productFeatureColor', type: 'string' },
{ name: 'taxCatalogs', type: 'string' },
{ text: '${StringUtil.wrapString(uiLabelMap.DmsInternalName)}', datafield: 'internalName', width: 150 },
{ text: '${StringUtil.wrapString(uiLabelMap.DmsDescription)}', datafield: 'longDescription', width: 200 },
{ text: '${StringUtil.wrapString(uiLabelMap.DmsProductTaxCatalogs)}', datafield: 'taxCatalogs', width: 150, sortable: false, filterable: false }
-->
<#assign columnlist="
			{ text: '${uiLabelMap.BPOSequenceId}', datafield: '', width: 50, sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, cellClassName: cellClassProdList,
				cellsrenderer: function (row, column, value) {
					return '<div style=margin:4px;>' + (row + 1) + '</div>';
				}
			},
			{ text: '${StringUtil.wrapString(uiLabelMap.ProductProductId)}', datafield: 'productCode', width: 120, cellClassName: cellClassProdList,
				cellsrenderer: function(row, colum, value){
					var productId = $('#jqxgridProdList').jqxGrid('getcellvalue', row, 'productId');
					var link = 'viewProduct?productId=' + productId;
					return '<div style=\"margin:4px\"><a href=\"' + link + '\">' + value + '</a></div>';
				}
			},
			{ text: '${StringUtil.wrapString(uiLabelMap.BSUPC)}', datafield: 'idSKU', width: 240, cellClassName: cellClassProdList, sortable: false},
			{ text: '${StringUtil.wrapString(uiLabelMap.BSNumChild)}', datafield: 'numChild', width: 60, sortable: false, filterable: false, cellClassName: cellClassProdList,
				cellsrenderer: function(row, colum, value) {
					if (typeof(value) != 'undefined' && value != null && value > 0) {
						return '<span>' + value + '</span>';
					} else {
						return '<span>-</span>';
					}
				}
			},
			{ text: '${StringUtil.wrapString(uiLabelMap.BSPrimaryProductCategory)}', width: 120, datafield: 'primaryProductCategoryId', cellClassName: cellClassProdList},
			{ text: '${StringUtil.wrapString(uiLabelMap.ProductProductName)}', datafield: 'productName', minwidth: 120, cellClassName: cellClassProdList},
			{ text: '${StringUtil.wrapString(uiLabelMap.DmsQuantityUomId)}', datafield: 'quantityUomId', width: 80, columntype: 'dropdownlist', filtertype: 'checkedlist', cellClassName: cellClassProdList,
				cellsrenderer: function(row, colum, value){
					value?value=mapQuantityUom[value]:value;
					return '<span>' + value + '</span>';
				}, createfilterwidget: function (column, htmlElement, editor) {
					editor.jqxDropDownList({ autoDropDownHeight: true, source: listQuantityUom, displayMember: 'uomId', valueMember: 'uomId' ,
						renderer: function (index, label, value) {
							if (index == 0) {
								return value;
							}
							return mapQuantityUom[value];
						}
					});
				}
			},
			{ text: '${uiLabelMap.BSState}', dataField: 'productState', width: 140, cellClassName: cellClassProdList, filtertype: 'list', sortable: false, 
				cellsrenderer: function(row, column, value){
					var returnValue = '';
					var data = $('#jqxgridProdList').jqxGrid('getrowdata', row);
					if (data) {
						var nowTimestamp = new Date();
						if (OlbCore.isNotEmpty(data.salesDiscontinuationDate) && nowTimestamp > data.salesDiscontinuationDate) {
							returnValue += '${uiLabelMap.BSDiscountinueSales}';
						}
						if (OlbCore.isNotEmpty(data.purchaseDiscontinuationDate) && nowTimestamp > data.purchaseDiscontinuationDate) {
							if (returnValue != '') returnValue += ', ';
							returnValue += '${uiLabelMap.BSDiscountinuePurchase}';
						}
						return '<span title=' + returnValue +'>' + returnValue + '</span>';
					}
			 	}, 
			 	createfilterwidget: function (column, columnElement, widget) {
			 		if (productStateData.length > 0) {
						var filterDataAdapter = new $.jqx.dataAdapter(productStateData, {
							autoBind: true
						});
						var records = filterDataAdapter.records;
						widget.jqxDropDownList({source: records, displayMember: 'itemId', valueMember: 'itemId',
							renderer: function(index, label, value){
								if (productStateData.length > 0) {
									for(var i = 0; i < productStateData.length; i++){
										if(productStateData[i].itemId == value){
											return '<span>' + productStateData[i].description + '</span>';
										}
									}
								}
								return value;
							}
						});
						widget.jqxDropDownList('selectedIndex', 0);
					}
	   			}
			},
			{text: '${uiLabelMap.BSPurchaseDiscontinuationDate}', dataField: 'purchaseDiscontinuationDate', width: 140, cellsformat: 'dd/MM/yyyy', filtertype:'range', cellClassName: cellClassProdList,
				cellsrenderer: function(row, colum, value) {
					return \"<span>\" + jOlbUtil.dateTime.formatFullDate(value) + \"</span>\";
				}
			},
			{text: '${uiLabelMap.BSSalesDiscontinuationDate}', dataField: 'salesDiscontinuationDate', width: 140, cellsformat: 'dd/MM/yyyy', filtertype:'range', cellClassName: cellClassProdList,
				cellsrenderer: function(row, colum, value) {
					return \"<span>\" + jOlbUtil.dateTime.formatFullDate(value) + \"</span>\";
				}
			},
		"/>

<#if hasOlbPermission("MODULE", "PRODUCTPO", "CREATE")>
	<#assign tmpCreateUrl = "icon-plus open-sans@${uiLabelMap.CommonAddNew}@newProduct"/>
	<#--<#include "component://basepo/webapp/basepo/product/popup/addNewVariantProduct.ftl"/>-->
</#if>

<#assign customcontrol1 = "fa fa-file-excel-o@@javascript: void(0);@exportExcel()">
<@jqGrid id="jqxgridProdList" filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" editrefresh="true"
	showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="false" 
	url="jqxGeneralServicer?sname=JQGetPOListProducts" viewSize="15"
	customcontrol1="${tmpCreateUrl?if_exists}" selectionmode="multiplerowsextended" enabletooltips="true"
	initrowdetails="true" initrowdetailsDetail=initrowdetailsDetail rowdetailsheight="203"
	contextMenuId="contextMenu" mouseRightMenu="true" customcontrol1=customcontrol1 isSaveFormData="true" formData="filterObjData"/>

<#--
<#if security.hasEntityPermission("ECOMMERCE", "_CREATE", session)>
	<#include "component://baseecommerce/webapp/baseecommerce/backend/content/listComment.ftl"/>
	<div id="contextMenu" style="display:none;">
		<ul>
			<li id="view"><i class="icon-eye-open"></i>&nbsp;&nbsp;${uiLabelMap.BSPreview}</li>
			<li id="viewComments"><i class="fa-comment-o"></i>&nbsp;&nbsp;${uiLabelMap.BSViewComments}</li>
			<li id="viewContent"><i class="icon-eye-open"></i>&nbsp;&nbsp;${uiLabelMap.BSViewContent}</li>
			<li id="addContent"><i class="icon-plus"></i>&nbsp;&nbsp;${uiLabelMap.BSAddContent}</li>
			<li id="viewProductDetails"><i class="icon-eye-open"></i>&nbsp;&nbsp;${uiLabelMap.ViewProductDetails}</li>
		</ul>
	</div>
<#else>
</#if>
-->

<div id="contextMenu" style="display:none;">
	<ul>
		<li id="viewProductDetails_newtab"><i class="fa fa-folder-open-o"></i>${StringUtil.wrapString(uiLabelMap.BSViewDetailInNewTab)}</li>
		<li id="viewProductDetails"><i class="fa fa-folder-open-o"></i>${StringUtil.wrapString(uiLabelMap.BSViewDetail)}</li>
		<#if hasOlbPermission("MODULE", "PRODUCTPO", "UPDATE")>
			<li id="configProduct"><i class="icon-edit"></i>${StringUtil.wrapString(uiLabelMap.BSConfigProduct)}</li>
		</#if>
		<#if hasOlbPermission("MODULE", "PRODUCTPO", "CREATE")>
			<li id="addSimilarProduct"><i class="icon-plus"></i>${StringUtil.wrapString(uiLabelMap.BSCreateASimilarProduct)}</li>
			<#--<li id="addNewProduct"><i class="icon-plus"></i>${StringUtil.wrapString(uiLabelMap.DmsAddNewProductVariant)}</li>-->
		</#if>
		<li id="refesh"><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
		<#if hasOlbPermission("MODULE", "PRODUCTPO", "UPDATE")>
		<li id="discontSales"><i class="fa fa-ban"></i>${StringUtil.wrapString(uiLabelMap.BSDiscountinueSales)}</li>
		<li id="discontPurchase"><i class="fa fa-ban"></i>${StringUtil.wrapString(uiLabelMap.BSDiscountinuePurchase)}</li>
		<li id="contSales"><i class="fa fa-play"></i>${StringUtil.wrapString(uiLabelMap.BSCountinueSales)}</li>
		<li id="contPurchase"><i class="fa fa-play"></i>${StringUtil.wrapString(uiLabelMap.BSCountinuePurchase)}</li>
		</#if>
	</ul>
</div>

<script type="text/javascript">
	var exportExcel = function(){
		//window.location.href = "exportProductsExcel";
		
		var form = document.createElement("form");
	    form.setAttribute("method", "POST");
	    form.setAttribute("action", "exportProductsExcel");
	    //form.setAttribute("target", "_blank");
	    
	    if (OlbCore.isNotEmpty(filterObjData) && OlbCore.isNotEmpty(filterObjData.data)) {
	    	$.each(filterObjData.data, function(key, value) {
	    		var hiddenField0 = document.createElement("input");
		        hiddenField0.setAttribute("type", "hidden");
		        hiddenField0.setAttribute("name", key);
		        hiddenField0.setAttribute("value", value);
		        form.appendChild(hiddenField0);
	    	});
	    }
        
	    document.body.appendChild(form);
	    form.submit();
	};
	
	$(function(){
		OlbProductList.init();
	});
	var OlbProductList = (function(){
		var init = function(){
			initElement();
			initEvent();
		};
		var initElement = function(){
			jOlbUtil.contextMenu.create($("#contextMenu"));
		};
		
		var changeProductStateList = function(productIds, action) {
			jOlbUtil.confirm.dialog("${StringUtil.wrapString(uiLabelMap.BSAreYouSureYouWantToChange)}", 
				function(){
					$.ajax({
						type: 'POST',
						url: "changeProductStateList",
						data: {
							"action": action,
							"productIds": productIds
						},
						beforeSend: function(){
							$("#loader_page_common").show();
						},
						success: function(data){
							jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
							        	$('#container').empty();
							        	$('#jqxNotification').jqxNotification({ template: 'error'});
							        	$("#jqxNotification").html(errorMessage);
							        	$("#jqxNotification").jqxNotification("open");
							        	return false;
									}, function(){
										$('#container').empty();
							        	$('#jqxNotification').jqxNotification({ template: 'info'});
							        	$("#jqxNotification").html("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
							        	$("#jqxNotification").jqxNotification("open");
							        	
							        	$("#jqxgridProdList").jqxGrid('updatebounddata');
									}
							);
						},
						error: function(data){
							alert("Send request is error");
						},
						complete: function(data){
							$("#loader_page_common").hide();
						},
					});
				}
			);
		};
		
		var initEvent = function(){
			var contextMenu = $("#contextMenu");
			contextMenu.on("itemclick", function (event) {
				var args = event.args;
				var itemId = $(args).attr("id");
				var grid = $("#jqxgridProdList");
				var selectionMode = grid.jqxGrid("selectionmode");
				
				var rowIndexSelected = grid.jqxGrid("getSelectedRowindex");
				var rowData = grid.jqxGrid("getrowdata", rowIndexSelected);
				
				if (rowIndexSelected == -1) {
					rowIndexSelected = gridDetailSelected.jqxGrid("getSelectedRowindex");
					rowData = gridDetailSelected.jqxGrid("getrowdata", rowIndexSelected);
				}
				
				var rowsData = [];
				var productIds = [];
				if (selectionMode == "multiplerowsextended") {
					var rowsIndexSelected = grid.jqxGrid("getselectedrowindexes");
					if (rowsIndexSelected) {
						for (var iRowIndex in rowsIndexSelected) {
							var iRowData = grid.jqxGrid("getrowdata", rowsIndexSelected[iRowIndex]);
							if (iRowData) {
								rowsData.push(iRowData);
								productIds.push(iRowData.productId);
							}
						}
					}
				}
				
				switch (itemId) {
					case "viewProductDetails": {
						if (rowData) {
							window.location.href = "viewProduct?productId=" + rowData.productId;
						}
						break;
					};
					case "viewProductDetails_newtab": {
						if (rowData) {
							var url = "viewProduct?productId=" + rowData.productId;
							var win = window.open(url, '_blank');
							win.focus();
						}
						break;
					};
					case "configProduct": {
						window.open("ConfigProductAndCategories?productId=" + rowData.productId, "_blank");
						break;
					};
					case "addSimilarProduct": {
						if (rowData) {
							window.open("newProduct?productIdOrg=" + rowData.productId, "_blank");
						}
						break;
					};
					case "refesh": { 
		    			$("#jqxgridProdList").jqxGrid('updatebounddata');
		    			break;
		    		};
					case "discontSales": { 
		    			changeProductStateList(productIds, "discontSales");
		    			break;
		    		};
					case "discontPurchase": { 
		    			changeProductStateList(productIds, "discontPurchase");
		    			break;
		    		};
					case "contSales": { 
		    			changeProductStateList(productIds, "contSales");
		    			break;
		    		};
					case "contPurchase": { 
		    			changeProductStateList(productIds, "contPurchase");
		    			break;
		    		};
					default:
						break;
					}
					<#--
					case "addNewProduct": {
						if (rowData) {
							$("#txtProductIdFrom").text(rowData.productName);
							AddProductVariant.open(rowData);
						}
						break;
					};
					case "viewContent": {
						if (rowData) {
							window.location.href = "ListProductContent?productId=" + rowData.productId;
						}
						break;
					};
					case "addContent": {
						if (rowData) {
							window.location.href = "ContentEditorEngine?productId=" + rowData.productId  + "&type=PRODUCT";
						}
						break;
					};
					case "viewComments": {
						CommentTree.load(rowData.productId, "true");
						break;
					};
					case "view": {
						window.open("/baseecommerce/control/product?product_id=" + rowData.productId, "_blank");
						break;
					};
					-->
			});
			
			<#if hasOlbPermission("MODULE", "PRODUCTPO_NEW", "")>
			contextMenu.on("shown", function () {
				var rowIndexSelected = $("#jqxgridProdList").jqxGrid("getSelectedRowindex");
				var rowData = $("#jqxgridProdList").jqxGrid("getrowdata", rowIndexSelected);
				if (rowIndexSelected == -1) {
					rowIndexSelected = gridDetailSelected.jqxGrid("getSelectedRowindex");
					rowData = gridDetailSelected.jqxGrid("getrowdata", rowIndexSelected);
				}
				<#--
				var isVirtual = rowData.isVirtual;
				if (isVirtual == "Y") {
					contextMenu.jqxMenu("disable", "addNewProduct", false);
				} else {
					contextMenu.jqxMenu("disable", "addNewProduct", true);
				}
				-->
			});
			</#if>
			contextMenu.jqxMenu("disable", "viewProductDetails", true);
			<#if hasOlbPermission("MODULE", "PRODUCTPO_VIEW", "")>
				contextMenu.jqxMenu("disable", "viewProductDetails", false);
			</#if>
			
			$("body").on("click", function() {
				if ($("#contextMenu")) {
					contextMenu.jqxMenu("close");
				}
			});
		};
		return {
			init: init
		}
	}());
</script>
