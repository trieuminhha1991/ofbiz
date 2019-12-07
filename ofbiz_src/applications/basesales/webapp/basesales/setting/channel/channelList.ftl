<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>

<#assign dataField = "[{name: 'enumId', type: 'string'}, 
	{name: 'enumCode', type: 'string'},
	{name: 'description', type: 'string'},
	{name: 'sequenceId', type: 'number'}
]"/>

<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.BSSalesChannelTypeCode)}', dataField: 'enumId', width: '25%', editable: false, }, 
	{text: '${StringUtil.wrapString(uiLabelMap.BSChannelCode)}', dataField: 'enumCode', width: '25%'},
	{text: '${StringUtil.wrapString(uiLabelMap.BSDescription)}', dataField: 'description', width: '25%'},
	{text: '${StringUtil.wrapString(uiLabelMap.BSSequenceNum)}', dataField: 'sequenceId', cellsalign: 'right', width: '25%'},
"/>

<@jqGrid id="jqxgrid" addrow="true" clearfilteringbutton="true" editable="true" alternativeAddPopup="alterpopupWindow1" 
	columnlist=columnlist dataField=dataField viewSize="25" showtoolbar="true" filtersimplemode="true" 
	showstatusbar="false" isShowTitleProperty="true" addType="popup" mouseRightMenu="false"
	url="jqxGeneralServicer?sname=getListChannel" 
	createUrl="jqxGeneralServicer?sname=createChannel&jqaction=C" addColumns="enumId;enumCode;sequenceId;description"
	updateUrl="jqxGeneralServicer?jqaction=U&sname=editChannel" editColumns="enumId;enumCode;sequenceId;description"
/> 

<script>
	var productIdd = '${productStoreId?if_exists}';
	var choose = "${StringUtil.wrapString(uiLabelMap.BSPleaseChoose)}";
	var successK = '${StringUtil.wrapString(uiLabelMap.BSSuccessK)}';
	var requiredField = '${StringUtil.wrapString(uiLabelMap.BSRequiredField)}';
	var checkSpecialCharacter = '${uiLabelMap.BSThisFieldMustNotByContainSpecialCharacter}';
</script>
<@jqOlbCoreLib hasValidator=true/>
<#include "channelNewPopup.ftl">
