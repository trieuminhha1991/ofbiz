
<#assign dataField = "[
			{name: 'partyTypeId', type: 'string'}, 
			{name: 'description', type: 'string'}
		]"/>
<#assign columnlist = "
			{text: '${StringUtil.wrapString(uiLabelMap.BSCustomerTypeId)}', dataField: 'partyTypeId', width: '20%'}, 
			{text: '${StringUtil.wrapString(uiLabelMap.BSCustomerTypeName)}', dataField: 'description'},
		"/>

<#assign tmpCreate = "false"/>
<#assign tmpDelete = "false"/>
<#if hasOlbPermission("MODULE", "SALES_CUSTOMERTYPE_NEW", "")><#assign tmpCreate = "true"/></#if>
<#if hasOlbPermission("MODULE", "SALES_CUSTOMERTYPE_DELETE", "")><#assign tmpDelete = "true"/></#if>

<@jqGrid id="jqxgridCustomerType" clearfilteringbutton="true" editable="false" alternativeAddPopup="alterpopupWindowCustomerTypeNew" 
		columnlist=columnlist dataField=dataField viewSize="25" showtoolbar="true" filtersimplemode="true" 
		showstatusbar="false" isShowTitleProperty="true" addType="popup" mouseRightMenu="false"
		url="jqxGeneralServicer?sname=JQGetListCustomerType" 
		addrow=tmpCreate createUrl="jqxGeneralServicer?sname=createCustomerType&jqaction=C" addColumns="partyTypeId;description"
		deleterow=tmpDelete removeUrl="jqxGeneralServicer?sname=deleteCustomerType&jqaction=C" deleteColumn="partyTypeId" 
	/>

<@jqOlbCoreLib hasValidator=true/>
<#include "customerTypeNewPopup.ftl">