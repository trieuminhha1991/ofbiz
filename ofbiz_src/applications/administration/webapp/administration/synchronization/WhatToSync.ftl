<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<#include "popup/addEntityToSync.ftl"/>

<#assign dataField = "[{ name: 'entitySyncId', type: 'string' },
					{ name: 'entityGroupId', type: 'string' },
					{ name: 'entityGroupName', type: 'string' },
					{ name: 'applEnumId', type: 'string' },
					{ name: 'entityOrPackage', type: 'string' }]"/>

<#assign columnlist = "{ text: '${StringUtil.wrapString(uiLabelMap.entitySyncId)}', dataField: 'entitySyncId', width: 200, editable: false },
					{ text: '${StringUtil.wrapString(uiLabelMap.entityOrPackage)}', datafield: 'entityOrPackage', width: 350 },
					{ text: '${StringUtil.wrapString(uiLabelMap.entityGroupId)}', datafield: 'entityGroupId', width: 200 },
					{ text: '${StringUtil.wrapString(uiLabelMap.entityGroupName)}', datafield: 'entityGroupName', minwidth: 250 }"/>

<#assign isServer = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("administration.properties", "common.config.isServer")>

<#if isServer=="Y">
	<#assign customTitleProperties = uiLabelMap.ADWhatToSync + " (Server)" />
	<#else>
	<#assign customTitleProperties = uiLabelMap.ADWhatToSync + " (Client)"/>
</#if>

<@jqGrid id="jqxgridWhatToSync" addrow="true" deleterow="true" clearfilteringbutton="true" editable="false" alternativeAddPopup="addEntityToSync"
	columnlist=columnlist dataField=dataField customTitleProperties=customTitleProperties
	showtoolbar="true" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="true" addType="popup"
	url="jqxGeneralServicer?sname=JQGetListEntityGroupEntrySync&isServer=isServer"
	removeUrl="jqxGeneralServicer?sname=deleteEntityGroupEntry&jqaction=D" deleteColumn="entityGroupId;entityOrPackage"	
	createUrl="jqxGeneralServicer?sname=createEntityGroupEntries&jqaction=C" addColumns="entityGroupId;entityOrPackage(java.util.List);applEnumId"/>
<script>
	$(document).ready(function() {
		AddEntityToSync.init();
	});
</script>