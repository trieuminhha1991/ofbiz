<script type="text/javascript" src="/delys/images/js/import/miscUtil.js"></script>
<script type="text/javascript" src="/delys/images/js/import/progressing.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>

<#assign dataField="[{ name: 'targetHeaderId', type: 'string'},
					{ name: 'targetItemSeqId', type: 'string'},
					{ name: 'productId', type: 'string'},
					{ name: 'productCategoryId', type: 'string'},
					{ name: 'quantity', type: 'number'}
					]"/>
<#assign columnlist="
					{text: '${uiLabelMap.SequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (row + 1) + '</div>';
					    }
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.ProductProductId)}', datafield: 'productId', align: 'center', width: 220, editable: false},
					{ text: '${StringUtil.wrapString(uiLabelMap.DAInternalName)}', datafield: '1', align: 'center', minwidth: 250, editable: false,
						cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + 0 + '</div>';
					    }
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.productCategoryId)}', datafield: 'productCategoryId', align: 'center', width: 280, editable: false,
						cellsrenderer: function (row, column, value) {
							value?value=mapProductCategory[value]:value;
					        return '<div style=margin:4px;>' + value + '</div>';
					    }
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.quantity)}', datafield: 'quantity', align: 'center', columntype:'numberinput', cellsalign: 'right', filtertype: 'number', width: 250,
						cellsrenderer: function(row, colum, value){
							return '<span class=\"text-right\">' + value.toLocaleString('${locale}') + '</span>';
						},validation: function (cell, value) {
							if (value >= 0) {
								return true;
							}
							return { result: false, message: '${uiLabelMap.QuantityNotValid}' };
						}
					}
					"/>
<@jqGrid filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" viewSize="15"
	showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="true" editrefresh="true"
	url="jqxGeneralServicer?sname=JQGetListTargetItem&targetHeaderId=${parameters.targetHeaderId?if_exists}"
	updateUrl="jqxGeneralServicer?sname=updateTargetItem&jqaction=U"
	editColumns="targetHeaderId;targetItemSeqId;quantity(java.math.BigDecimal)"
	/>
					
<#assign listProductCategory = delegator.findList("ProductCategory", null, null, null, null, false) />

<script>
	var listProductCategory = [
	                       <#if listProductCategory?exists>
		                        <#list listProductCategory as item>
		                        {
		                        	productCategoryId: '${item.productCategoryId?if_exists}',
		                        	categoryName: "${StringUtil.wrapString(item.categoryName)}"
		                        },
		                        </#list>
	                       </#if>
	                       ];
	var mapProductCategory = {
		<#if listProductCategory?exists>
            <#list listProductCategory as item>
            	'${item.productCategoryId?if_exists}': "${StringUtil.wrapString(item.categoryName)}",
            </#list>
       </#if>
	};
</script>