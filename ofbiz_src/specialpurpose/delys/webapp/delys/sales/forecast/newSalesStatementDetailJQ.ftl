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
                		<#assign localData = localData + "'${iMap.key}' : '${iMap.value}', "/>
                	</#list>
                <#assign localData = localData + "},"/>
			</#list>
        <#assign localData = localData + "]"/>
<#assign dataFields = "["/>
				<#assign dataFields = dataFields + "{ name: 'partyId', type: 'string' },{ name: 'fullName', type: 'string' },{ name: 'parentId', type: 'string' },{ name: 'level', type: 'string' },{ name: 'recordChildIds', type: 'string'},">
				<#list columListData as columItem>
					<#assign dataFields = dataFields + "{ name: '${columItem.columnId}', type: 'string' },"/>
                </#list>
		<#assign dataFields = dataFields + "]">
<#assign columnList = "["/>
				<#assign columnList = columnList + "{ text: 'PartyId', dataField: 'partyId', width: '20 %', editable: false, cellClassName: cellClass, pinned: true},"/>
				<#list columListData as columnItem>
					<#assign columnList = columnList + "{ text: '${columnItem.columnName}', dataField: '${columnItem.columnId}', width: '10 %', columnGroup: '${columnItem.columnGroupId}', cellClassName: cellClass, cellsalign: 'right'}, "/>
				</#list>
		<#assign columnList = columnList + "]">
<#assign columnGroups = "["/>
				<#list columnGroupListData as columnGroupItem>
					<#assign columnGroups = columnGroups + "{ text: '${columnGroupItem.columnGroupName}', name: '${columnGroupItem.columnGroupId}'},"/>
				</#list>
        <#assign columnGroups = columnGroups + "]">

<input type="hidden" name="salesStatementTypeId" id="salesStatementTypeId2" value="${salesStatementTypeId?if_exists}"/>
<input type="hidden" name="customTimePeriodId" id="customTimePeriodId2" value="${customTimePeriodId?if_exists}"/>

<@jqxTreeGrid id="treeGrid" localData=localData dataFields=dataFields columnList=columnList columnGroups=columnGroups idKey="partyId" idKeyParent="parentId" 
	rootDataName="children" parentRow=true columnsResize=true width="100%" height="450px" editable="true" selectionMode="multiplecellsadvanced" theme="olbius"/>

<script type="text/javascript">
	function convertStringToList(str) {
		var returnValue = [];
		if (str == undefined || str == null) return returnValue;
		str = str.replace("&#123;", "");
       	str = str.replace("&#125;", "");
       	str = str.split(",");
       	for (var i = 0; i < str.length; i++) {
       		returnValue[i] = str[i].trim();
       	}
		return returnValue;
	}
	
	function setValueForParent(parentId, dataField) {
		var parent = $("#treeGrid").jqxTreeGrid('getRow', parentId);
		var parentRowKey = $("#treeGrid").jqxTreeGrid('getKey', parent);
       	var value = 0;
       	if (parent != undefined && parent.recordChildIds != undefined) {
       		var records2 = convertStringToList(parent.recordChildIds);
	       	if (records2 != undefined && records2.length > 0) {
	           	for (var i = 0; i < records2.length; i++) {
	               	var rowKey = records2[i];
	          	 	var cellValue = $("#treeGrid").jqxTreeGrid('getCellValue', rowKey, dataField);
	          	 	var tmpValue = parseInt(cellValue, 10);
	               	if (!isNaN(tmpValue)) value = value + tmpValue;
	           	}
	           	value = "" + value;
	           	$("#treeGrid").jqxTreeGrid('setCellValue', parentRowKey, dataField, value);
	           	if (parent.parentId) {
	           		setValueForParent(parent.parentId, dataField);
	           	}
	       	}
       	}
	}
	
	$(function(){
		$("#treeGrid").on('cellEndEdit', function (event) {
           	// Update the Location and Size properties and their nested properties.
       		var args = event.args;
           	var row = args.row;
           	var dataField = args.dataField;
           	var keyId = $("#treeGrid").jqxTreeGrid('getKey', row);
           	if (row.level == 5) {
           		// update the parent value when the user changes a nested property, 
               	setValueForParent(row.parentId, dataField);
           	}
       	});
       	
		/*
		var isManual = true;
		var iIndex = 0;
		// cellValueChanged
		$("#treeGrid").on('cellEndEdit', function (event) {
           	// Update the Location and Size properties and their nested properties.
           	if (isManual || iIndex < 2) {
           		var args = event.args;
	           	var row = args.row;
	           	var dataField = args.dataField;
	           	var keyId = $("#treeGrid").jqxTreeGrid('getKey', row);
	           	if (row.level == 5) {
	           		// update the parent value when the user changes a nested property, 
	               	var parent = $("#treeGrid").jqxTreeGrid('getRow', row.parentId);
	               	var parentRowKey = parent.partyId;
	               	var value = 0;
	               	var records2 = convertStringToList(parent.recordChildIds);
	               	if (records2 != undefined && records2.length > 0) {
	                   	for (var i = 0; i < records2.length; i++) {
	                       	var rowKey = records2[i];
	                  	 	var cellValue = $("#treeGrid").jqxTreeGrid('getCellValue', rowKey, dataField);
	                  	 	var tmpValue = parseInt(cellValue, 10);
	                       	if (!isNaN(tmpValue)) value = value + tmpValue;
	                   	}
	               	}
	               	isManual = false;
	               	$("#treeGrid").jqxTreeGrid('setCellValue', parentRowKey, dataField, value);
	               	isManual = true;
	           	} else if (row.level < 5) {
	           		var value = 0;
	           		var records = convertStringToList(row.recordChildIds);
	           		if (records != undefined && records.length > 0) {
	                   	for (var i = 0; i < records.length; i++) {
	                       	var rowKey = records[i];
	                  	 	var cellValue = $("#treeGrid").jqxTreeGrid('getCellValue', rowKey, dataField);
	                  	 	var tmpValue = parseInt(cellValue, 10);
	                       	if (!isNaN(tmpValue)) value = value + tmpValue;
	                   	}
	               	}
	               	isManual = false;
	               	iIndex++;
	               	value = "" + value;
	               	$("#treeGrid").jqxTreeGrid('setCellValue', keyId, dataField, value);
	               	isManual = true;
	           	}
           	} else {
           		iIndex = 0;
           	}
       });*/
	});
	
	function buildDataBeforeSend() {
		var productIds = [
		<#list listProductKey as productKey>
			"${productKey}",
		</#list>
		];
		var rows = $("#treeGrid").jqxTreeGrid("getRows");
		var data = [];
		var index = 0;
		var listParentRoot = new Array();
		if (rows != undefined && rows.length > 0) {
			for (var j = 0; j < rows.length; j++) {
				var row = rows[j];
				if (row != undefined) {
					listParentRoot.push(row.parentId);
				}
			}
			data = data.concat(buildData2(productIds, rows));
		}
		jQuery.ajax({
            url: 'storeSalesStatementJQ',
            type: 'POST',
            data: {
            	"salesStatementTypeId": $("#salesStatementTypeId2").val(),
            	"customTimePeriodId": $("#customTimePeriodId2").val(),
            	"listItems": JSON.stringify(data),
            	"listParentRoot": JSON.stringify(listParentRoot),
            },
            beforeSend: function () {
				$("#info_loader").show();
			},
            success: function (data) {
                //window.location.href = "viewTargetSalesStatementGroup?customTimePeriodId=${customTimePeriodId?if_exists}&salesTypeId=${salesStatementTypeId?if_exists}&statusId=SALES_SM_CREATED";
            	window.location.href = "targetSalesStatementList";
            },
            error: function (e) {
            	//console.log(e);
            },
            complete: function() {
		        $("#info_loader").hide();
		        $("#btnPrevWizard").removeClass("disabled");
				$("#btnNextWizard").removeClass("disabled");
		    }
        });
	}
	
	function buildData2(productIds, rows) {
		var data = new Array();
		for (var i = 0; i < rows.length; i++) {
			var row = rows[i];
			if (row != undefined) {
				var rowMap = {};
				for (var j = 0; j < productIds.length; j++) {
					var productId = productIds[j];
					rowMap[productId] = row[productId];
				}
				rowMap.partyId = row.partyId;
				rowMap.parentId = row.parentId;
				data.push(rowMap);
				var records = row.records;
				if (records != undefined && records.length > 0) {
					data = data.concat(buildData2(productIds, records));
				}
			}
		}
		return data;
	}
</script>
</#if>