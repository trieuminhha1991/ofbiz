<#assign localeStr = "VI" />

<#if locale = "en">
    <#assign localeStr = "EN" />
<#elseif locale= "en_US">
	<#assign localeStr = "EN" />
</#if>
<script>
	<#assign weightUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "WEIGHT_MEASURE"), null, null, null, false)>
	var weightUomData = [];
	<#list weightUoms as item>
		var row = {};
		<#assign abbreviation = StringUtil.wrapString(item.get("abbreviation", locale)) />
		row['uomId'] = "${item.uomId}";
		row['description'] = "${abbreviation?if_exists}";
		weightUomData.push(row);
	</#list>
	
	<#assign vehicleStatuses = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "VEHICLE_STATUS"), null, null, null, false)>
	var vehicleStatusData = [];
	<#list vehicleStatuses as item>
		var row = {};
		<#assign vehicleStatusDesc = StringUtil.wrapString(item.get("description", locale)) />
		row['statusId'] = '${item.statusId?if_exists}';
		row['description'] = '${vehicleStatusDesc?if_exists}';
		vehicleStatusData.push(row);
	</#list>
</script>
<div id="detailItems">
	<#assign dataFieldVehicle="[
					{ name: 'vehicleId', type: 'string'},
					{ name: 'vehicleName', type: 'string' },
					{ name: 'fixedAssetTypeId', type: 'string'},
					{ name: 'shipmentMethodTypeId', type: 'string'},
					{ name: 'ownerPartyId', type: 'string' },
	                { name: 'maxWeight', type: 'number'},
	                { name: 'minWeight', type: 'number'},
	                { name: 'weightUomId', type: 'date', other: 'string'},
					{ name: 'statusId', type: 'string' },
					{ name: 'description', type: 'string' },
					{ name: 'groupName', type: 'string' },
					{ name: 'firstName', type: 'string' },
					{ name: 'middleName', type: 'string' },
					{ name: 'lastName', type: 'string' },
					
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
					{ text: '${uiLabelMap.VehicleId}', datafield: 'vehicleId', align: 'left', width: 150, pinned: true},
					{ text: '${uiLabelMap.VehicleName}', datafield: 'vehicleName', align: 'left', minwidth: 150,},
					{ text: '${uiLabelMap.FixedAssetType}', datafield: 'fixedAssetTypeId', align: 'left', width: 150,},
					{ text: '${uiLabelMap.Status}', datafield: 'statusId', align: 'left', width: 150, cellsalign: 'right',
						cellsrenderer: function(row, colum, value){
							for(var i = 0; i < vehicleStatusData.length; i++){
								if(vehicleStatusData[i].statusId == value){
									return '<span style=\"text-align: right;\">'+vehicleStatusData[i].description+'</span>'; 
								}
							}
					    }, 
					},
					{ text: '${uiLabelMap.MaxWeight}', datafield: 'maxWeight', align: 'left', width: 150, cellsalign: 'right',
						cellsrenderer: function(row, colum, value){
							var desc = '';
							var data = $('#jqxgridVehicle').jqxGrid('getrowdata', row);
							for(var i = 0; i < weightUomData.length; i++){
								if(weightUomData[i].uomId == data.weightUomId){
									desc = weightUomData[i].description;
								}
							}
							if(value){
								return '<span style=\"text-align: right;\">' + value.toLocaleString('${localeStr}') + ' ('+ desc + ') ' + '</span>';
							} else {
								return '<span style=\"text-align: right;\">_NA_</span>';
							}
					    }, 
					},
					{ text: '${uiLabelMap.MinWeight}', datafield: 'minWeight', align: 'left', width: 150, cellsalign: 'right',
						cellsrenderer: function(row, colum, value){
							var desc = '';
							var data = $('#jqxgridVehicle').jqxGrid('getrowdata', row);
							for(var i = 0; i < weightUomData.length; i++){
								if(weightUomData[i].uomId == data.weightUomId){
									desc = weightUomData[i].description;
								}
							}
							if(value){
								return '<span style=\"text-align: right;\">' + value.toLocaleString('${localeStr}') + ' ('+ desc + ') ' + '</span>';
							} else {
								return '<span style=\"text-align: right;\">_NA_</span>';
							}
					    }, 
					},
					{ text: '${uiLabelMap.Owner}', datafield: 'ownerPartyId', align: 'left', width: 250, cellsalign: 'left',
						cellsrenderer: function(row, colum, value){
							var data = $('#jqxgridVehicle').jqxGrid('getrowdata', row);
							var fullName = null;
							if (data.lastName){
								if (fullName){
									fullName = fullName + ' ' + data.lastName;
								} else {
									fullName = data.lastName;
								}
							}
							if (data.middleName){
								if (fullName){
									fullName = fullName + ' ' + data.middleName;
								} else {
									fullName = data.middleName;
								}		
							}
							if (data.firstName){
								if (fullName){
									fullName = fullName + ' ' + data.firstName;
								} else {
									fullName = data.firstName;
								}	
							}
							if (data.groupName){
								if (fullName){
									fullName = fullName + ' ' + data.groupName;
								} else {
									fullName = data.groupName;
								}	
							}
							
							return '<span style=\"text-align: right;\">'+fullName+'</span>';
					    }, 
					},
				"/>
	
	<@jqGrid filtersimplemode="true" filterable="true" dataField=dataFieldVehicle columnlist=columnlistVehicle editable="false" showtoolbar="true"
		url="jqxGeneralServicer?sname=getAllVehicles" customTitleProperties="ListVehicles" id="jqxgridVehicle"
	/>
</div>
<script>

</script>