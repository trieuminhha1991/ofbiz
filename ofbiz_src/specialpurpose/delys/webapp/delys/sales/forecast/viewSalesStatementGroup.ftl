<#-- jqxTreeGrid / Editing / Property Editor -->
<style type="text/css">
	#contenttreeGrid {
		width:100% !important;
	}
	.background-root {
  		color: #DD0806 !important;
  		font-weight:bold;
	}
	.background-one {
		background: #FF99CC !important;
		font-weight:bold;
	}
	.background-two {
  		background: #FFCC99 !important;
  		font-weight:bold;
  		color: #0000FF !important;
	}
	.background-three {
  		background: #E6B8B7 !important;
  		font-weight:bold;
  		color: #DD0806 !important;
	}
	.background-four {
  		background: #CCFFFF !important;
  		font-weight:bold;
	}
	.background-five {
  		background: #FFFF99 !important;
	}
</style>
<script type="text/javascript">
	var cellClass = function (row, dataField, cellText, rowData) {
        var levelValue = rowData["level"];
        if (levelValue == 0) {
        	return "background-root";
        } else if (levelValue == 1) {
        	return "background-one";
        } else if (levelValue == 2) {
        	return "background-two";
        } else if (levelValue == 3) {
        	return "background-three";
        } else if (levelValue == 4) {
        	return "background-four";
        } else if (levelValue == 5) {
        	return "background-five";
        }
    }
</script>
<#if catalogId?exists && catalogId?has_content && listData?exists>
	<#include "macroTreeGrid.ftl"/>
	<#assign localData = "["/>
				<#list listData as iData>
	                <#assign localData = localData + "{"/>
	                	<#list iData.entrySet() as iMap>
	                		<#assign localData = localData + "'${iMap.key}' : '${iMap.value?default('-')}', "/>
	                	</#list>
	                <#assign localData = localData + "},"/>
				</#list>
	        <#assign localData = localData + "]"/>
	<#assign dataFields = "["/>
					<#assign dataFields = dataFields + "{ name: 'salesId', type: 'string' },{ name: 'partyId', type: 'string' },{ name: 'fullName', type: 'string' },{ name: 'parentId', type: 'string' },{ name: 'level', type: 'string' },{ name: 'recordChildIds', type: 'string'},">
					<#list columListData as columItem>
						<#assign dataFields = dataFields + "{ name: '${columItem.columnId}_target', type: 'string' },{ name: '${columItem.columnId}_actual', type: 'string' },{ name: '${columItem.columnId}_percent', type: 'string' },"/>
	                </#list>
			<#assign dataFields = dataFields + "]">
	<#assign columnList = "["/>
					<#assign columnList = columnList + "{ text: 'PartyId', dataField: 'partyId', width: '20 %', editable: false, cellClassName: cellClass, pinned: true},"/>
					<#list columListData as columnItem>
						<#assign columnList = columnList + "{ text: '${uiLabelMap.DATarget}', dataField: '${columnItem.columnId}_target', width: '10 %', columnGroup: '${columnItem.columnId}', cellClassName: cellClass, cellsalign: 'right'}, "/>
						<#assign columnList = columnList + "{ text: '${uiLabelMap.DAActual}', dataField: '${columnItem.columnId}_actual', width: '10 %', columnGroup: '${columnItem.columnId}', cellClassName: cellClass, cellsalign: 'right'}, "/>
						<#assign columnList = columnList + "{ text: '${uiLabelMap.DAPercent}', dataField: '${columnItem.columnId}_percent', width: '10 %', columnGroup: '${columnItem.columnId}', cellClassName: cellClass, cellsalign: 'right', cellsformat: 'p2'}, "/>
					</#list>
			<#assign columnList = columnList + "]">
	<#assign columnGroups = "["/>
					<#list columListData as columnItem>
						<#assign columnGroups = columnGroups + "{ text: '${columnItem.columnName}', name:'${columnItem.columnId}', columnGroup: '${columnItem.columnGroupId}'},"/>
					</#list>
					<#list columnGroupListData as columnGroupItem>
						<#assign columnGroups = columnGroups + "{ text: '${columnGroupItem.columnGroupName}', name: '${columnGroupItem.columnGroupId}'},"/>
					</#list>
	        <#assign columnGroups = columnGroups + "]">
	
	<@jqxTreeGrid id="treeGrid" localData=localData dataFields=dataFields columnList=columnList columnGroups=columnGroups idKey="salesId" idKeyParent="parentId" 
		rootDataName="children" parentRow=true columnsResize=true width="100%" height="450px" editable="false" selectionMode="multiplecellsadvanced" theme="olbius"/>
<#else>
	${uiLabelMap.DADataNotFound}
</#if>