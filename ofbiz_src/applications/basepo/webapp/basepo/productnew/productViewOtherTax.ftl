<@jqGridMinimumLib/>
<@jqOlbCoreLib hasGrid=true hasCore=true hasValidator=true/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtextarea.js"></script>
<script>
	var productId = "${parameters.productId}";
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.AreYouSureRemove = "${StringUtil.wrapString(uiLabelMap.AreYouSureRemove)}";
	uiLabelMap.RemoveSuccess = "${StringUtil.wrapString(uiLabelMap.RemoveSuccess)}";
	<#assign types = delegator.findList("Enumeration", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("enumTypeId", "TAX_CATEGORY"), null, null, null, false)/>
	var taxTypes = [];
	<#list types as item>
		var type = {};
		type['enumId'] = "${item.enumId}";
		type['description'] = "${item.description?if_exists}";
		taxTypes.push(type);
	</#list>
	<#assign tmpAdd = "false"/>
	<#assign mouseRightMenu = "false"/>
<#if hasOlbPermission("MODULE", "PRODUCTPO_CREATE_OTHERTAX", "CREATE")>
	<#assign tmpAdd = "true"/>
</#if>
<#if hasOlbPermission("MODULE", "PRODUCTPO_DELETE_OTHERTAX", "UPDATE")>
	<#assign mouseRightMenu = "true"/>
</#if>
</script>
<div id="othertax-tab" class="tab-pane<#if activeTab?exists && activeTab == "othertax-tab"> active</#if>">
	<div class="row-fluid">
			<#assign dataField="[{ name: 'productCategoryId', type: 'string'},
					{ name: 'categoryName', type: 'string'},
					{ name: 'taxPercentage', type: 'number'},
					{ name: 'fromDate', type: 'date', other: 'Timestamp'},
		 			{ name: 'thruDate', type: 'date', other: 'Timestamp'},
					{ name: 'description', type: 'string'},
					]"/>


			<#assign columnlist="{ text: '${uiLabelMap.CategoryId}', dataField: 'productCategoryId', width: 300, },
						{ text: '${uiLabelMap.CategoryName}', dataField: 'categoryName', width: 300, 
							
						},
					 	{ text: '${uiLabelMap.BPTaxPercentage}', dataField: 'taxPercentage', width: 150 ,
					 		cellsrenderer: function(row, column, value){
				   				var str = '<div class=\"text-right\">';
								str += value + '%';
								str += '</div>';
								return str;
							},
						},
					 	{ text: '${uiLabelMap.BPFromDate}', dataField: 'fromDate', editable: false, align: 'left', width: 150, 
					 	filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
						},
						{ text: '${uiLabelMap.BPThruDate}', dataField: 'thruDate', editable: false, align: 'left', width: 150, 
						filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right', 
						},
						{ text: '${uiLabelMap.Description}', dataField: 'description', width: 400 },
					 "/>
					
			<@jqGrid id="jqxGridOtherTax" filtersimplemode="true" alternativeAddPopup="alterpopupWindow" addType="popup"  
			dataField=dataField columnlist=columnlist
			editable="true"  clearfilteringbutton="true" showtoolbar="true" addrow=tmpAdd editrefresh ="true"
	 		url="jqxGeneralServicer?sname=JQGetListOtherTax&productId=${parameters.productId?if_exists}" 
	 		viewSize="15" filterable="true" filtersimplemode="true"
			mouseRightMenu=mouseRightMenu contextMenuId="contextMenu" />
	</div>
</div>
<div id="contextMenu" style="display:none;">
	<ul>
		<li id='contextMenu_remove' action="removeOtherTax"> <i class="red fa fa-trash" ></i>${uiLabelMap.RemoveOtherTax}</li>
		<li id='contextMenu_refresh' action="refresh"><i class="fa fa-refresh"></i>${uiLabelMap.Refresh}</li>
 
	</ul>
</div>
<script type="text/javascript" src="/logresources/js/util/DateUtil.js"></script>
<script type="text/javascript" src="/poresources/js/product/contextMenuOtherTax.js"></script>
<#include "popup/addOtherTax.ftl"/>