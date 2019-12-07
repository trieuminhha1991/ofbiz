<#assign localeStr = "VI" />

<#if locale = "en">
    <#assign localeStr = "EN" />
<#elseif locale= "en_US">
	<#assign localeStr = "EN" />
</#if>
<script>
<#assign listStatusItem = delegator.findList("StatusItem", null, null, null, null, false) />

var listStatusItem = [
						<#if listStatusItem?exists>
							<#list listStatusItem as item>
								{
									statusId: "${item.statusId?if_exists}",
									description: "${StringUtil.wrapString(item.get("description", locale)?if_exists)}"
								},
							</#list>
						</#if>
	                      ];
var mapStatusItem = {
					<#if listStatusItem?exists>
						<#list listStatusItem as item>
							"${item.statusId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
						</#list>
					</#if>	
				};
var locale = '${locale}';
$(document).ready(function () {
	locale == "vi_VN"?locale="vi":locale=locale;
});
</script>
<div id="detailItems">
	<#assign dataFieldVehicle="[
					{ name: 'fixedAssetId', type: 'string'},
					{ name: 'fixedAssetTypeId', type: 'string' },
					{ name: 'fixedAssetName', type: 'string'},
					{ name: 'fromDate', type: 'date', other: 'Timestamp'},
					{ name: 'thruDate', type: 'date', other: 'Timestamp'},
					{ name: 'statusIdFix', type: 'string'},
				]"/>
	<#assign columnlistVehicle="  
					{
					    text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
					    groupable: false, draggable: false, resizable: false,
					    datafield: '', columntype: 'number', width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (value + 1) + '</div>';
					    }
					},
					{ text: '${uiLabelMap.VehicleId}', datafield: 'fixedAssetId', align: 'left', width: 180, pinned: true},
					{ text: '${uiLabelMap.VehicleName}', datafield: 'fixedAssetName', align: 'left'},
					{ text: '${uiLabelMap.BLEffectiveDate}', dataField: 'fromDate', cellsformat: 'dd/MM/yyyy', filtertype:'range', width: 150,
	                },
	                { text: '${uiLabelMap.BLExpiryDate}', dataField: 'thruDate', cellsformat: 'dd/MM/yyyy', filtertype:'range', width: 150,
	                },
	                {text: '${uiLabelMap.Status}', datafield: 'statusIdFix', width: '150', 
	                	cellsrenderer: function(row, colum, value) {
			        		return '<span>'+ mapStatusItem[value] +'</span>';
						}
	                },
				"/> 
	
	<@jqGrid filtersimplemode="true" filterable="true" dataField=dataFieldVehicle columnlist=columnlistVehicle editable="false" showtoolbar="true"
		url="jqxGeneralServicer?sname=JQGetFixedAssetVehicles" customTitleProperties="ListVehicles" id="jqxgridVehiclesFixAsset"
	/> 
</div>
