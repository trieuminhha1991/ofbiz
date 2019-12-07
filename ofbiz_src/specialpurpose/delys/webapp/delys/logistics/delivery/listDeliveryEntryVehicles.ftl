<script>
	<#assign deliveryMans = delegator.findList("PartyRoleAndPartyDetail", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("roleTypeId", "DELIVERY_MAN"), null, null, null, false) />
	var deliveryManData = new Array();
	<#list deliveryMans as item>
		var row = {};
		row['partyId'] = '${item.partyId}';
		row['partyName'] = '${item.firstName?if_exists} ${item.middleName?if_exists} ${item.lastName?if_exists} ${item.groupName?if_exists} [${item.partyId?if_exists}]';
		deliveryManData[${item_index}] = row;
	</#list>
	<#assign weightUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "WEIGHT_MEASURE"), null, null, null, false)>
	var weightUomData = new Array();
	<#list weightUoms as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.abbreviation) />
		row['uomId'] = '${item.uomId}';
		row['description'] = '${description}';
		weightUomData[${item_index}] = row;
	</#list>
	<#assign drivers = delegator.findList("PartyRoleAndPartyDetail", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("roleTypeId", "DRIVER"), null, null, null, false) />
	var driverData = new Array();
	<#list drivers as item>
		var row = {};
		row['partyId'] = '${item.partyId}';
		row['partyName'] = '${item.firstName?if_exists} ${item.middleName?if_exists} ${item.lastName?if_exists} ${item.groupName?if_exists} [${item.partyId?if_exists}]';
		driverData[${item_index}] = row;
	</#list>
	
	<#assign vehicles = delegator.findList("Vehicle", null, null, null, null, false) />
	var vehicleData = new Array();
	<#list vehicles as item>
		var row = {};
		row['vehicleId'] = '${item.vehicleId}';
		row['vehicleName'] = '${item.vehicleName?if_exists}';
		vehicleData[${item_index}] = row;
	</#list>
	
	<#assign vehicleStatus = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "DE_VEHICLE_STATUS"), null, null, null, false) />
	var vehicleStatusData = new Array();
	<#list vehicleStatus as item>
		var row = {};
		row['statusId'] = '${item.statusId}';
		row['description'] = '${item.description?if_exists}';
		vehicleStatusData[${item_index}] = row;
	</#list>
</script>	

<#assign dataField="[{ name: 'vehicleId', type: 'string'},
						   { name: 'vehicleName', type: 'string'},
						   { name: 'partyCarrierId', type: 'string'},
						   { name: 'statusId', type: 'string'},
						   { name: 'deliveryManId', type: 'string'},
						   { name: 'driverId', type: 'string'},
						   { name: 'vehicleTypeId', type: 'string'},
						   { name: 'shipmentMethodTypeId',  type: 'string'},
						   { name: 'lengthUomId', type: 'string'},
						   { name: 'unitCost', type: 'number'},
						   { name: 'currencyUomId', type: 'string'},
						   { name: 'weight', type: 'number'},
						   { name: 'maxWeight', type: 'number'},
						   { name: 'minWeight', type: 'number'},
						   { name: 'weightUomId', type: 'string'},
						   { name: 'roleTypeId', type: 'string'},
						   { name: 'fromDate', type: 'date', other: 'Timestamp'},
						   { name: 'thruDate', type: 'date', other: 'Timestamp'},
						   ]"/>
						   
<#assign columnlist="
					   { text: '${uiLabelMap.vehicleId}', datafield: 'vehicleId', width: 110, editable: false, cellsrenderer:
					       function(row, colum, value){
					        var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					        var vehicleId = data.vehicleId;
					        var shipmentMethodTypeId = data.shipmentMethodTypeId;
					        var link = 'EditVehicles?vehicleId=' + vehicleId +'&shipmentMethodTypeId=' + shipmentMethodTypeId;
					        return '<span><a href=\"' + link + '\">' + vehicleId + '</a></span>';
					   }},
					   { text: '${uiLabelMap.vehicleName}', datafield: 'vehicleName', minwidth: 150, editable: false},
					   { text: '${uiLabelMap.DeliveryMan}', datafield: 'deliveryManId', minwidth: 150, editable: false,
						   cellsrenderer: function(row, colum, value){
							        var data = $('#jqxgrid').jqxGrid('getrowdata', row);
							        var deliveryManId = data.deliveryManId;
							        var deliveryMan = getPartyNameView(deliveryManId);
							        if (deliveryManId != null) {
								        return '<span>' + deliveryMan + '</span>';
									} else {
										return '';
									}
					        	}
					   },
					   { text: '${uiLabelMap.Driver}', datafield: 'driverId', minwidth: 150, editable: false,
						   cellsrenderer: function(row, colum, value){
						        var data = $('#jqxgrid').jqxGrid('getrowdata', row);
						        var driverId = data.driverId;
						        var driverName = getPartyNameView(driverId);
						        if (driverId != null) {
							        return '<span>' + driverName + '</span>';
								} else {
									return '';
								}
				        	}
					   },
					   { text: '${uiLabelMap.Status}',  datafield: 'statusId', minwidth: 120, editable: true, filtertype: 'checkedlist', cellsrenderer:
					       function(row, colum, value){
						        var data = $('#jqxgrid').jqxGrid('getrowdata', row);
						        for(var i = 0; i < vehicleStatusData.length; i++){
									if(vehicleStatusData[i].statusId == data.statusId){
										return '<div style=\"margin-left:5px;margin-top:5px\"><span>' + vehicleStatusData[i].description + '</span></div>'
									}
								}
				        }},
						{ text: '${uiLabelMap.WeightOfProduct}',  datafield: 'weight', minwidth: 120, editable: false, filterable: false,
							cellsrenderer: 	function(row, colum, value){
								var data = $('#jqxgrid').jqxGrid('getrowdata', row);
								for(var i = 0; i < weightUomData.length; i++){
									if(weightUomData[i].uomId == data.weightUomId){
										return '<div style=\"margin-left:5px;margin-top:5px\"><span>' + value +' ('+ weightUomData[i].description + ')</span></div>'
									}
								}
							}
					   },
					   { text: '${uiLabelMap.Owner}', datafield: 'partyCarrierId', minwidth: 200, editable: false, filterable: false, cellsrenderer:
					       function(row, colum, value){
						        var data = $('#jqxgrid').jqxGrid('getrowdata', row);
						        var partyCarrierId = data.partyCarrierId;
						        var partyCarrier = getPartyNameView(partyCarrierId);
						        if (partyCarrierId != null) {
							        return '<span>' + partyCarrier + '</span>';
								} else {
									return '';
								}
				        	}
					   },
					   "/>
<@jqGrid filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true"
					showtoolbar="true" addrow="true" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="false" 
					url="jqxGeneralServicer?sname=getDeliveryEntryVehicles&deliveryEntryId=${parameters.deliveryEntryId?if_exists}"
					createUrl="jqxGeneralServicer?sname=createDeliveryEntryVehicle&jqaction=C" updateUrl="jqxGeneralServicer?sname=updateDeliveryEntryVehicle&jqaction=U"
					addColumns="deliveryEntryId;vehicleId;statusId;deliveryManId;driverId;listShipmentItems(java.util.List);" otherParams="deliveryManId:S-getDeliveryEntryVehicleRole(deliveryEntryId,vehicleId,roleTypeId*DELIVERY_MAN)<partyId>;driverId:S-getDeliveryEntryVehicleRole(deliveryEntryId,vehicleId,roleTypeId*DRIVER)<partyId>;"
					/>
					   
<div id="alterpopupWindow" style="display:none;">
	<div>${uiLabelMap.accCreateNew}</div>
	<div style='height: 450px;'>
	<input type="hidden" value="${parameters.deliveryEntryId?if_exists}" id="deliveryEntryId"/>
	<div style="overflow: scroll; height: 470px;">
		<div class='row-fluid' style="margin-left: 50px">
			<div class='span12'>
				<h4 class="row header smaller lighter blue">
				${uiLabelMap.GeneralInfo}
				</h4>
			</div>
		</div>
		<div class='row-fluid' style="margin-left: 50px">
			<div class='span3' style="text-align: right; line-height: 25px;">
				${uiLabelMap.Vehicles}:
			</div>
			<div class='span3'>
				<div id="vehicleId">
				</div>
			</div>
		</div>
		<div class='row-fluid' style="margin-left: 50px">
			<div class='span3' style="text-align: right; line-height: 25px;">
				${uiLabelMap.DeliveryMan}:
			</div>
			<div class='span3'>
				<div id="deliveryManId">
				</div>
			</div>
		</div>
		<div class='row-fluid' style="margin-left: 50px">
			<div class='span3' style="text-align: right; line-height: 25px;">
				${uiLabelMap.Driver}:
			</div>
			<div class='span3'>
				<div id="driverId">
				</div>
			</div>
		</div>
		<div class='row-fluid' style="margin-left: 50px">
			<div class='span3' style="text-align: right; line-height: 25px;">
				${uiLabelMap.Status}:
			</div>
			<div class='span3'>
				<div id="statusId">
				</div>
			</div>
		</div>
		<div class='row-fluid'>
			<div class='span12'>
				<div style="margin-left: 50px"><#include "listShipmentItemByDE.ftl" /></div>
			</div>
		</div>   
	</div>
	</div>
	<div class='row-fluid' style="position: absolute; bottom: 10px; z-index: 1000">
		<div class='span6'>
			<div class='pull-right'>
				<input type="button" id="alterSave" value="${uiLabelMap.CommonSave}" />
			</div>
		</div>
		<div class='span6'>
			<input id="alterCancel" type="button" value="${uiLabelMap.CommonCancel}" />
		</div>
	</div>  
</div>
					   
<script>
	$("#alterpopupWindow").jqxWindow({
        width: 1150, maxWidth: 1100, minHeight: 560, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7
    });
    $("#alterCancel").jqxButton();
    $("#alterSave").jqxButton();
    $("#deliveryManId").jqxDropDownList({source: deliveryManData, autoDropDownHeight:true, displayMember:"partyName", selectedIndex: 0, valueMember: "partyId"});
    $("#driverId").jqxDropDownList({source: driverData, autoDropDownHeight:true, displayMember:"partyName", selectedIndex: 0, valueMember: "partyId"});
    $("#vehicleId").jqxDropDownList({source: vehicleData, autoDropDownHeight:true, displayMember:"vehicleName", selectedIndex: 0, valueMember: "vehicleId"});
    $("#statusId").jqxDropDownList({source: vehicleStatusData, autoDropDownHeight:true, displayMember:"description", selectedIndex: 0, valueMember: "statusId"});
    
    $("#alterSave").click(function () {
    	var row;
		var selectedIndexs = $('#jqxgridShipmentItemByDE').jqxGrid('getselectedrowindexes');
		var listShipmentItems = new Array();
		for(var i = 0; i < selectedIndexs.length; i++){
			var data = $('#jqxgridShipmentItemByDE').jqxGrid('getrowdata', selectedIndexs[i]);
			var map = {};
			map['shipmentId'] = data.shipmentId;
			map['shipmentItemSeqId'] = data.shipmentItemSeqId;
			map['quantity'] = data.quantityInVehicle;
			map['weight'] = data.weight;
			map['weightUomId'] = data.weightUomId;
			map['quantityUomId'] = data.quantityUomId;
			listShipmentItems[i] = map;
		}
		var listShipmentItems = JSON.stringify(listShipmentItems);
		row = { 
			deliveryEntryId:$('#deliveryEntryId').val(),
			deliveryManId:$('#deliveryManId').val(),
			driverId:$('#driverId').val(),
			vehicleId:$('#vehicleId').val(),
			statusId:$('#statusId').val(),
			listShipmentItems:listShipmentItems
		};
	   	$("#jqxgrid").jqxGrid('addRow', null, row, "first");
	   	$("#jqxgrid").jqxGrid('updatebounddata');
        $("#jqxgrid").jqxGrid('clearSelection');
        $("#jqxgrid").jqxGrid('selectRow', 0);
        $("#alterpopupWindow").jqxWindow('close');
    });
    <#assign parties = delegator.findList("PartyNameView", null, null, null, null, false) />
	var PartyName = new Array();
	<#list parties as item>
		var row = {};
		row['partyId'] = '${item.partyId}';
		row['description'] = '${item.firstName?if_exists} ${item.middleName?if_exists} ${item.lastName?if_exists} ${item.groupName?if_exists} [${item.partyId?if_exists}]';
		PartyName[${item_index}] = row;
	</#list>
	function getPartyNameView(partyId) {
		for ( var x in PartyName) {
			if (partyId == PartyName[x].partyId) {
				var name = PartyName[x].description;
				if (name == undefined) {
					name = "";
				}
				return name;
			}
		}
	}
	
	function getDataEditor(key) {
        	if (ck[key]) {
        		return ck[key].getData();
        	}
        	return "";
        }
	 function fixSelectAll(dataList) {
        	var sourceST = {
			        localdata: dataList,
			        datatype: "array"
			    };
			var filterBoxAdapter2 = new $.jqx.dataAdapter(sourceST, {autoBind: true});
            var uniqueRecords2 = filterBoxAdapter2.records;
			uniqueRecords2.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
		return uniqueRecords2;
	}
</script>