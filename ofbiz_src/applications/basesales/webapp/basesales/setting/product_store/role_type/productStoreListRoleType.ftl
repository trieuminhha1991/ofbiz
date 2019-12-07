<script type="text/javascript">
	var cellClassProdStoreRole = function (row, columnfield, value) {
 		var data = $('#jqxProdStoreRole').jqxGrid('getrowdata', row);
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

<#assign listConditions = [Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("roleTypeGroupId", "SALES_PRODSTORE_ROLE")]/>
<#assign listConditions = listConditions + [Static["org.ofbiz.entity.util.EntityUtil"].getFilterByDateExpr()]/>
<#assign listRoleType = delegator.findList("RoleTypeGroupMemberDetail", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(listConditions) , null, ["sequenceNum"], null, false)!>
<#assign partyTypeList = delegator.findList("PartyType", null , null, orderBy, null, false)!>
<script type="text/javascript">
	var roleTypeData = [
	<#if listRoleType?exists>
		<#list listRoleType as roleTypeItem>
		{	roleTypeId: "${roleTypeItem.roleTypeId}",
			description: "${StringUtil.wrapString(roleTypeItem.get("description", locale))}"
		},
		</#list>
	</#if>
	];
	var partyTypeList = [
	    <#list partyTypeList as partyTypeL>
	    {	partyTypeId : "${partyTypeL.partyTypeId}",
	    	description: "${StringUtil.wrapString(partyTypeL.description)}"
	    },
	    </#list>
	];
</script>
<style>
	.line-height-25{
		line-height: 25px;
	}
</style>
<#assign dataField = "[
			{name: 'productStoreId', type: 'string'}, 
			{name: 'partyId', type: 'string'}, 
			{name: 'partyCode', type: 'string'}, 
			{name: 'fullName', type: 'string'}, 
			{name: 'roleTypeId', type: 'string'}, 
			{name: 'fromDate', type: 'date', other: 'Timestamp'}, 
			{name: 'thruDate', type: 'date', other: 'Timestamp'}
		]"/>
<#assign columnlist = "
			{text: '${StringUtil.wrapString(uiLabelMap.BSPartyId)}', dataField: 'partyCode', width: '16%', cellClassName: cellClassProdStoreRole}, 
			{text: '${StringUtil.wrapString(uiLabelMap.BSFullName)}', dataField: 'fullName', minWidth: '10%', cellClassName: cellClassProdStoreRole},
			{text: '${StringUtil.wrapString(uiLabelMap.BSRoleType)}', dataField: 'roleTypeId', width: '16%', filtertype: 'checkedlist', cellClassName: cellClassProdStoreRole,
				cellsrenderer: function(row, column, value){
					if (roleTypeData.length > 0) {
						for(var i = 0 ; i < roleTypeData.length; i++){
							if (value == roleTypeData[i].roleTypeId){
								return '<span title = ' + roleTypeData[i].description +'>' + roleTypeData[i].description + '</span>';
							}
						}
					}
					return '<span title=' + value +'>' + value + '</span>';
				}, 
				createfilterwidget: function (column, columnElement, widget) {
					if (roleTypeData.length > 0) {
		   				var filterBoxAdapter2 = new $.jqx.dataAdapter(roleTypeData, {autoBind: true});
		                var uniqueRecords2 = filterBoxAdapter2.records;
		   				widget.jqxDropDownList({ source: uniqueRecords2, displayMember: 'description', valueMember : 'roleTypeId', renderer: function (index, label, value) 
						{
							for(i=0;i < roleTypeData.length; i++){
								if(roleTypeData[i].roleTypeId == value){
									return roleTypeData[i].description;
								}
							}
						    return value;
						}});
						widget.jqxDropDownList('checkAll');
					}
	   			}
			},
			{text: '${StringUtil.wrapString(uiLabelMap.BSFromDate)}', dataField: 'fromDate', width: '14%', cellsformat: 'dd/MM/yyyy', filtertype: 'range', cellClassName: cellClassProdStoreRole}, 
			{text: '${StringUtil.wrapString(uiLabelMap.BSThruDate)}', dataField: 'thruDate', width: '14%', cellsformat: 'dd/MM/yyyy', filtertype: 'range', cellClassName: cellClassProdStoreRole}, 
			{text: '${StringUtil.wrapString(uiLabelMap.BSStatus)}', dataField: 'status', width: '8%', cellClassName: cellClassProdStoreRole,
				cellsrenderer: function(row, column, value){
					var data = $('#jqxProdStoreRole').jqxGrid('getrowdata', row);
					if (data != null && data.thruDate != null && data.thruDate != undefined) {
						var thruDate = new Date(data.thruDate);
						var nowDate = new Date();
						if (thruDate < nowDate) {
							return '<span title=\"${uiLabelMap.BSExpired}\">${uiLabelMap.BSExpired}</span>';
						}
					}
					return '<span></span>';
				}, 
			}, 
		"/>
<#assign contextMenuItemIdRole = "ctxmnupsrolelst">
<#assign customcontrol1 = "">
<#assign permitCreate = false>
<#assign permitDelete = false>
<#if hasOlbPermission("MODULE", "SALES_STOREROLETYPE_NEW", "")>
	<#if ("SMCHANNEL_POS" == productStore.salesMethodChannelEnumId)>
		<#assign customcontrol1 = "icon-plus open-sans@${uiLabelMap.BSAddPosSeller}@javascript: void(0);@OlbAddSellerPos.openWindowAdd()">
	</#if>
	<#assign permitCreate = true>
</#if>
<#if hasOlbPermission("MODULE", "SALES_STOREROLETYPE_DELETE", "")><#assign permitDelete = true></#if>
<@jqGrid id="jqxProdStoreRole" clearfilteringbutton="true" editable="false" alternativeAddPopup="popupProdStoreNewRole" columnlist=columnlist dataField=dataField
		viewSize="10" showtoolbar="true" filtersimplemode="true" showstatusbar="false" addType="popup" addrefresh="true" 
		jqGridMinimumLibEnable="true" deleterow="false" 
		url="jqxGeneralServicer?sname=JQGetListProductStoresRole&productStoreId=${productStore.productStoreId?if_exists}&hasrequest=Y" 
		addrow="${permitCreate?string}" createUrl="jqxGeneralServicer?sname=createProductStoreRoleOlb&jqaction=C" addColumns="productStoreId;partyId;roleTypeId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp)"
		deleterow="${permitDelete?string}" removeUrl="jqxGeneralServicer?jqaction=D&sname=deleteProductStoreRoleOlb" deleteColumn="productStoreId;partyId;roleTypeId;fromDate(java.sql.Timestamp)" 
		mouseRightMenu="true" contextMenuId="contextMenu_${contextMenuItemIdRole}" customcontrol1=customcontrol1 />

<div id='contextMenu_${contextMenuItemIdRole}' style="display:none">
	<ul>
	    <li id="${contextMenuItemIdRole}_refresh"><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
		<#if permitDelete><li id="${contextMenuItemIdRole}_delete"><i class="fa-trash-o open-sans"></i>${StringUtil.wrapString(uiLabelMap.BSDelete)}</li></#if>
	</ul>
</div>
<#include "productStoreRoleNewAddSellerPos.ftl"/>
<#include "productStoreNewRoleType.ftl">

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasComboBox=true hasValidator=true/>
<#--
<script>
	var notEmpty = '${StringUtil.wrapString(uiLabelMap.BSValueIsNotEmpty)}';
	var successK = '${StringUtil.wrapString(uiLabelMap.BSSuccessK)}';
	var partyIdGrid = '${uiLabelMap.BSPartyId}';
	var partyTypeIdGrid = '${uiLabelMap.FormFieldTitle_partyTypeId}';
	var fullNameGrid = '${uiLabelMap.BSFullName}';
	var groupNameGrid = '${uiLabelMap.BSGroupName}';
	var fromDateValidate = '${uiLabelMap.BSFromDateValidate}';
	var clickChoose = "${StringUtil.wrapString(uiLabelMap.DAClickToChoose)}";
</script>
-->
<script type="text/javascript">
	<#if hasOlbPermission("MODULE", "SALES_STOREROLETYPE_NEW", "")>
	$(function(){
		$('body').on("addSellerPosComplete", function(){
			$("#jqxProdStoreRole").jqxGrid("updatebounddata");
		});
	});
	</#if>
	var contextMenuItemIdRole = "${contextMenuItemIdRole}";
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.BSClickToChoose = '${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}';
	uiLabelMap.BSPartyId = '${StringUtil.wrapString(uiLabelMap.BSPartyId)}';
	uiLabelMap.BSFullName = '${StringUtil.wrapString(uiLabelMap.BSFullName)}';
</script>
<script type="text/javascript" src="/salesresources/js/setting/product_store/productStoreListRoleType.js"></script>
<#--<#include "productStoreDeleteRoleType.ftl">-->
