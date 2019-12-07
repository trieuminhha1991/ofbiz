<#assign localeStr = "VI" />

<#if locale = "en">
    <#assign localeStr = "EN" />
<#elseif locale= "en_US">
	<#assign localeStr = "EN" />
</#if>
<script>
	var locale = '${locale}';
	$(document).ready(function () {
		locale == "vi_VN"?locale="vi":locale=locale;
	});
	
	<#assign listRoleType = delegator.findList("RoleType", null, null, null, null, false) />
	var mapRoleType = {
		<#if listRoleType?exists>
			<#list listRoleType as item>
				"${item.roleTypeId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
			</#list>
		</#if>	
	};
	
	<#assign listFacility = delegator.findList("Facility", null, null, null, null, false) />
	var mapFacility = {
		<#if listFacility?exists>
			<#list listFacility as item>
				"${item.facilityId?if_exists}": "${StringUtil.wrapString(item.get("facilityName", locale)?if_exists)}",
			</#list>
		</#if>	
	};
	
	<#assign listStatusItem = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "DELI_ENTRY_STATUS"), null, null, null, false) />
	var mapStatusItem = {
		<#if listStatusItem?exists>
			<#list listStatusItem as item>
				"${item.statusId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
			</#list>
		</#if>	
	};
</script>
<div id="detailItems">
	<#assign dataFieldVehicle="[
					{ name: 'deliveryEntryId', type: 'string'},
					{ name: 'fullName', type: 'string' },
					{ name: 'roleTypeId', type: 'string' },
					{ name: 'facilityId', type: 'string' },
					{ name: 'statusId', type: 'string' },
					{ name: 'fromDate', type: 'date', other: 'Timestamp'},
					{ name: 'thruDate', type: 'date', other: 'Timestamp'},
				]"/>
	
	<#assign columngroups = "
					 { text: '${uiLabelMap.DatetimeDelivery}', align: 'center', name: 'deliveryTime' },
				"/>
	<#assign columnlistVehicle="  
					{  
					    text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
					    groupable: false, draggable: false, resizable: false,
					    datafield: '', columntype: 'number', width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (value + 1) + '</div>';
					    }
					},
					{ text: '${uiLabelMap.DeliveryEntryId}', datafield: 'deliveryEntryId', align: 'left', width: 180, pinned: true},
					{ text: '${uiLabelMap.LogShipper}', datafield: 'fullName', align: 'left', width: 200,},
					{ text: '${uiLabelMap.BLRoles}', datafield: 'roleTypeId', align: 'left', width: 150,
						cellsrenderer: function(row, colum, value) {
			        		return '<span>'+ mapRoleType[value] +'</span>';
						}
					}, 
					{ text: '${uiLabelMap.FromDate}', dataField: 'fromDate', cellsformat: 'dd/MM/yyyy', filtertype:'range', width: 150, columngroup: 'deliveryTime',
	                },
	                { text: '${uiLabelMap.ThruDate}', dataField: 'thruDate', cellsformat: 'dd/MM/yyyy', filtertype:'range', width: 150, columngroup: 'deliveryTime',
	                },
	                { text: '${uiLabelMap.FacilityFrom}', datafield: 'facilityId', align: 'left', minwidth: 150,
						cellsrenderer: function(row, colum, value) {
			        		return '<span>'+ mapFacility[value] +'</span>';
						}
					},
					{ text: '${uiLabelMap.LogShipmentStatus}', datafield: 'statusId', align: 'left', width: 200,
						cellsrenderer: function(row, colum, value) {
			        		return '<span>'+ mapStatusItem[value] +'</span>';
						}
					},
				"/> 
	
	<@jqGrid filtersimplemode="true" filterable="true" dataField=dataFieldVehicle columnlist=columnlistVehicle editable="false" showtoolbar="true"
		url="jqxGeneralServicer?sname=JQGetDeliveryPartyRole" customTitleProperties="BLListStatisticsDeliveryParty" id="jqxgridDeliveryPartyRole"
			columngrouplist = columngroups mouseRightMenu="true" contextMenuId="menuSummary"
	/> 
</div>
<div id='menuSummary' style="display:none;">
	<ul>
	    <li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	</ul>
	</div>
	<script>
	$(document).ready(function (){
		$("#menuSummary").jqxMenu({ width: 220, autoOpenPopup: false, mode: 'popup', theme: theme});
	});
	$("#menuSummary").on('itemclick', function (event) {
		var tmpStr = $.trim($(args).text());
		if(tmpStr == "${StringUtil.wrapString(uiLabelMap.BSRefresh)}"){
			$('#jqxgridDeliveryPartyRole').jqxGrid('updatebounddata');
		}
	});
</script>