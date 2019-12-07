<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>

<#assign dataField="[{ name: 'productId', type: 'string' },
					{ name: 'productCode', type: 'string' },
					{ name: 'primaryProductCategoryId', type: 'string' },
					{ name: 'productName', type: 'string' },
					{ name: 'brandName', type: 'string' },
					{ name: 'productWeight', type: 'number' },
					{ name: 'weightUomId', type: 'string' },
					{ name: 'quantityUomId', type: 'string' },
					{ name: 'description', type: 'string' },
					{ name: 'isVirtual', type: 'string' },
					{ name: 'taxCatalogs', type: 'string' }]"/>

<#assign columnlist="{ text: '${uiLabelMap.DmsSequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
						cellsrenderer: function (row, column, value) {
							return '<div style=margin:4px;>' + (row + 1) + '</div>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.ProductProductId)}', datafield: 'productCode', width: 200 },
					{ text: '${StringUtil.wrapString(uiLabelMap.ProductProductName)}', datafield: 'productName', width: 200 },
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsDescription)}', datafield: 'description', width: 250 },
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsQuantityUomId)}', datafield: 'quantityUomId', columntype: 'dropdownlist', filtertype: 'checkedlist', width: 150,
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
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsProductWeight)}', datafield: 'productWeight', width: 150, filtertype: 'number', cellsalign: 'right',
						cellsrenderer: function (row, column, value) {
							return '<div class=\"text-right\">' + value.toLocaleString('${locale}') + '</div>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsProductWeightUomId)}', datafield: 'weightUomId', columntype: 'dropdownlist', filtertype: 'checkedlist', width: 150,
						cellsrenderer: function(row, colum, value){
							value?value=mapWeightUom[value]:value;
							return '<span>' + value + '</span>';
						}, createfilterwidget: function (column, htmlElement, editor) {
							editor.jqxDropDownList({ autoDropDownHeight: true, source: listWeightUom, displayMember: 'uomId', valueMember: 'uomId',
								renderer: function (index, label, value) {
									if (index == 0) {
										return value;
									}
									return mapWeightUom[value];
								}
							});
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsProductTaxCatalogs)}', datafield: 'taxCatalogs', width: 200, sortable: false, filterable: false }"/>

<@jqGrid filtersimplemode="true" addType="popup" clearfilteringbutton="true" dataField=dataField columnlist=columnlist
	showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="false"
	url="jqxGeneralServicer?sname=JQGetListProducts"
	contextMenuId="contextMenu" mouseRightMenu="true"/>

<#include "component://baseecommerce/webapp/baseecommerce/backend/content/listComment.ftl"/>

<div id="contextMenu" style="display:none;">
	<ul>
		<li id="configProduct"><i class="icon-edit"></i>&nbsp;&nbsp;${uiLabelMap.BSConfigProduct}</li>
		<li id="view"><i class="icon-eye-open"></i>&nbsp;&nbsp;${uiLabelMap.BSPreview}</li>
		<li id="viewComments"><i class="fa-comment-o"></i>&nbsp;&nbsp;${uiLabelMap.BSViewComments}</li>
		<li id="viewContent"><i class="icon-eye-open"></i>&nbsp;&nbsp;${uiLabelMap.BSViewContent}</li>
		<li id="addContent"><i class="icon-plus"></i>&nbsp;&nbsp;${uiLabelMap.BSAddContent}</li>
	</ul>
</div>
		
<#assign listQuantityUom = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false) />
<#assign listWeightUom = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "WEIGHT_MEASURE"), null, null, null, false) />
<script>
	var listQuantityUom = [<#if listQuantityUom?exists><#list listQuantityUom as item>{
		uomId: "${item.uomId?if_exists}",
		description: "${StringUtil.wrapString(item.description)}"
	},</#list></#if>];
	var mapQuantityUom = {<#if listQuantityUom?exists><#list listQuantityUom as item>
		"${item.uomId?if_exists}": "${StringUtil.wrapString(item.description?if_exists)}",
	</#list></#if>};
	var listWeightUom = [<#if listWeightUom?exists><#list listWeightUom as item>{
		uomId: "${item.uomId?if_exists}",
		description: "${StringUtil.wrapString(item.description)}"
	},</#list></#if>];
	var mapWeightUom = {<#if listWeightUom?exists><#list listWeightUom as item>
		"${item.uomId?if_exists}": "${StringUtil.wrapString(item.description?if_exists)}",
	</#list></#if>};
	
	$(document).ready(function() {
		var contextMenu = $("#contextMenu").jqxMenu({ theme: theme, width: 200, height: 140, autoOpenPopup: false, mode: "popup" });
		contextMenu.on("itemclick", function (event) {
			var args = event.args;
			var itemId = $(args).attr("id");
			var rowIndexSelected = $("#jqxgrid").jqxGrid("getSelectedRowindex");
			var rowData = $("#jqxgrid").jqxGrid("getrowdata", rowIndexSelected);
			if (rowIndexSelected == -1) {
				rowIndexSelected = gridSelecting.jqxGrid("getSelectedRowindex");
				rowData = gridSelecting.jqxGrid("getrowdata", rowIndexSelected);
			}
			if (rowData) {
				switch (itemId) {
				case "configProduct":
					window.location.href = "ConfigProductAndCategories?productId=" + rowData.productId;
					break;
				case "viewContent":
					
					window.location.href = "ListProductContent?productId=" + rowData.productId;
					break;
				case "addContent":
					window.location.href = "ContentEditorEngine?productId=" + rowData.productId  + "&type=PRODUCT";
					break;
				case "viewComments":
					CommentTree.load(rowData.productId, "true");
					break;
				case "view":
			    	window.open("/baseecommerce/control/product?product_id=" + rowData.productId, "_blank");
					break;
				default:
					break;
				}
			}
		});
	});
</script>