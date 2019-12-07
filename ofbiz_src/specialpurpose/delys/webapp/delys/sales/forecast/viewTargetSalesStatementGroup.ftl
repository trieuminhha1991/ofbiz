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
	
	<@jqxTreeGrid id="treeGrid" localData=localData dataFields=dataFields columnList=columnList columnGroups=columnGroups idKey="salesId" idKeyParent="parentId" 
		rootDataName="children" parentRow=true columnsResize=true width="100%" height="500px" editable="false" selectionMode="multiplecellsadvanced" theme="olbius"/>
	<div style="position:relative">
		<div id="info_loader" style="overflow: hidden; position: fixed; display: none; left: 50%; top: 50%; z-index: 900;" class="jqx-rc-all jqx-rc-all-olbius">
			<div style="z-index: 99999; position: relative; width: auto; height: 33px; padding: 5px; font-family: verdana; font-size: 12px; color: #767676; border-color: #898989; border-width: 1px; border-style: solid; background: #f6f6f6; border-collapse: collapse;" class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
				<div style="float: left;">
					<div style="float: left; overflow: hidden; width: 32px; height: 32px;" class="jqx-grid-load"></div>
					<span style="margin-top: 10px; float: left; display: block; margin-left: 5px;">${uiLabelMap.DALoading}...</span>
				</div>
			</div>
		</div>
	</div>
	
	<script src="/delys/images/js/bootbox.min.js"></script>
	<script type="text/javascript">
		function approveTotalStatement() {
			bootbox.confirm("${uiLabelMap.DAAreYouSureAccept}", function(result) {
				if(result) {
					jQuery.ajax({
			            url: 'approveTargetSalesStatementGroup',
			            async: true,
			            type: 'POST',
			            data: {
			            	"customTimePeriodId": "${customTimePeriodId?if_exists}",
			            	"salesTypeId": "${salesTypeId?if_exists}",
			            },
			            beforeSend: function () {
							$("#info_loader").show();
						},
			            success: function (data) {
			            	window.location.href = "targetSalesStatementList";
			                if (data.thisRequestUri == "json") {
			            		var errorMessage = "";
						        if (data._ERROR_MESSAGE_LIST_ != null) {
						        	for (var i = 0; i < data._ERROR_MESSAGE_LIST_.length; i++) {
						        		errorMessage += "<p><b>${uiLabelMap.DAErrorUper}</b>: " + data._ERROR_MESSAGE_LIST_[i] + "</p>";
						        	}
						        }
						        if (data._ERROR_MESSAGE_ != null) {
						        	errorMessage += "<p><b>${uiLabelMap.DAErrorUper}</b>: " + data._ERROR_MESSAGE_ + "</p>";
						        }
						        if (errorMessage != "") {
						        	$('#container').empty();
						        	$('#jqxNotification').jqxNotification({ template: 'info'});
						        	$("#jqxNotification").html(errorMessage);
						        	$("#jqxNotification").jqxNotification("open");
						        } else {
						        	$('#container').empty();
						        	$('#jqxNotification').jqxNotification({ template: 'info'});
						        	$("#jqxNotification").html("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
						        	$("#jqxNotification").jqxNotification("open");
						        }
						        return false;
			            	} else {
			            		
			            	}
			            },
			            error: function (e) {
			            },
			            complete: function() {
					        $("#info_loader").hide();
					    }
			        });
				}
			});
		}
		function cancelTotalStatement() {
			bootbox.confirm("${uiLabelMap.DAAreYouSureCancelNotAccept}", function(result) {
				if(result) {
					jQuery.ajax({
			            url: 'cancelTargetSalesStatementGroup',
			            async: true,
			            type: 'POST',
			            data: {
			            	"customTimePeriodId": "${customTimePeriodId?if_exists}",
			            	"salesTypeId": "${salesTypeId?if_exists}",
			            },
			            beforeSend: function () {
							$("#info_loader").show();
						},
			            success: function (data) {
			                if (data.thisRequestUri == "json") {
			            		var errorMessage = "";
						        if (data._ERROR_MESSAGE_LIST_ != null) {
						        	for (var i = 0; i < data._ERROR_MESSAGE_LIST_.length; i++) {
						        		errorMessage += "<p><b>${uiLabelMap.DAErrorUper}</b>: " + data._ERROR_MESSAGE_LIST_[i] + "</p>";
						        	}
						        }
						        if (data._ERROR_MESSAGE_ != null) {
						        	errorMessage += "<p><b>${uiLabelMap.DAErrorUper}</b>: " + data._ERROR_MESSAGE_ + "</p>";
						        }
						        if (errorMessage != "") {
						        	$('#container').empty();
						        	$('#jqxNotification').jqxNotification({ template: 'info'});
						        	$("#jqxNotification").html(errorMessage);
						        	$("#jqxNotification").jqxNotification("open");
						        } else {
						        	$('#container').empty();
						        	$('#jqxNotification').jqxNotification({ template: 'info'});
						        	$("#jqxNotification").html("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
						        	$("#jqxNotification").jqxNotification("open");
						        }
						        return false;
			            	} else {
			            		
			            	}
			            },
			            error: function (e) {
			            },
			            complete: function() {
					        $("#info_loader").hide();
					    }
			        });
				}
			});
		}
	</script>
<#else>
	${uiLabelMap.DADataNotFound}
</#if>