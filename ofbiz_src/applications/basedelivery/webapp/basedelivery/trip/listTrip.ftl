<#assign localeStr = "VI" />

<#if locale = "en">
    <#assign localeStr = "EN" />
<#elseif locale= "en_US">
    <#assign localeStr = "EN" />
</#if>
<script>

    var locale = '${locale}';
    $(document).ready(function () {
        locale == "vi_VN" ? locale = "vi" : locale = locale;
    });

</script>

<script type="text/javascript">
    <#assign tripStatuses = delegator.findByAnd("StatusItem", {"statusTypeId" : "TRIP_STATUS"}, null, false)/>
    var tripStatusData = [
    <#if tripStatuses?exists>
        <#list tripStatuses as statusItem>
            {
                statusId: '${statusItem.statusId}',
                description: '${StringUtil.wrapString(statusItem.get("description", locale))}'
            },
        </#list>
    </#if>];
</script>

<script type="text/javascript">
    <#assign vehicles = delegator.findByAnd("VehicleV2", null, null, false)/>
    var vehicleData = [
    <#if vehicles?exists>
        <#list vehicles as vehicle>
            {
                vehicleId: '${vehicle.vehicleId}',
                licensePlate: '${StringUtil.wrapString(vehicle.get("licensePlate", locale))}'
            },
        </#list>
    </#if>];
</script>

<#assign customcontrol2 = "fa fa-file-excel-o@@javascript: void(0);@exportExcel()">
<#assign customcontrol3 = "fa fa-file-pdf-o@@javascript: void(0);@printVehicle()">

<#--<script type="text/javascript">-->
    <#--var exportExcel = function () {-->
        <#--;-->
        <#--var form = document.createElement("form");-->
        <#--form.setAttribute("method", "POST");-->
        <#--form.setAttribute("action", "exportExcelVehicle");-->
        <#--document.body.appendChild(form);-->
        <#--form.submit();-->
    <#--};-->

    <#--var printVehicle = function () {-->
        <#--var url = 'VehiclelList.pdf';-->
        <#--var win = window.open(url, '_blank');-->
        <#--win.focus();-->
    <#--};-->
<#--</script>-->

<div id="detailItems">
<#assign dataFieldVehicle="[
					{ name: 'tripId', type: 'string'},
					{ name: 'scLogId', type: 'string' },
					{ name: 'contractorId', type: 'string'},
					{ name: 'vehicleId', type: 'string'},
					{ name: 'driverId', type: 'string'},
					{ name: 'statusId', type: 'String'},
					{ name: 'createdByUserLogin', type: 'String'},
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
					{ text: '${uiLabelMap.BDTripId}', datafield: 'tripId', align: 'left',
					    width: 150, pinned: true, cellsrenderer: function(row, colum, value) {
                        return \"<span><a href='viewTrip?tripId=\" + value + \"'>\" + value +\"</a></span>\";
                        }
                    },
                    { text: '${StringUtil.wrapString(uiLabelMap.BDScLogId)}', dataField: 'scLogId',   width: '20%',cellsrenderer: function(row, column, value){
					  var partyName = value;
					  $.ajax({
							url: 'getPartyName',
							type: 'POST',
							data: {partyId: value},
							dataType: 'json',
							async: false,
							success : function(data) {
								if(!data._ERROR_MESSAGE_){
									partyName = data.partyName;
								}
					        }
						});
					  return '<span title' + value + '>' + partyName + '</span>';}
					 },
                    { text: '${StringUtil.wrapString(uiLabelMap.BDContractorId)}', dataField: 'contractorId',   width: '20%',cellsrenderer: function(row, column, value){
					  var partyName = value;
					  $.ajax({
							url: 'getPartyName',
							type: 'POST',
							data: {partyId: value},
							dataType: 'json',
							async: false,
							success : function(data) {
								if(!data._ERROR_MESSAGE_){
									partyName = data.partyName;
								}
					        }
						});
					  return '<span title' + value + '>' + partyName + '</span>';}
					 },
					{ text: '${uiLabelMap.BDVehicleId}', datafield: 'vehicleId',  width: '13%', cellsrenderer: function(row, column, value){
					    if(!value)
					        return '<span>_NA_</span>';
						if (vehicleData.length > 0) {
							for(var i = 0 ; i < vehicleData.length; i++){
    							if (value == vehicleData[i].vehicleId){
    							return \"<span><a href='editVehicle?vehicleId=\" + value + \"'>\" + vehicleData[i].licensePlate + '[' + vehicleData[i].vehicleId + ']' +\"</a></span>\";
    							}
    						}
						}
						return '<span title=' + value +'>' + value + '</span>';}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.BDDriverId)}', dataField: 'driverId',   width: '20%',cellsrenderer: function(row, column, value){
					    if(!value) {
					        return '<span>_NA_</span>';
					    }
					  var partyName = value;
					  $.ajax({
							url: 'getPartyName',
							type: 'POST',
							data: {partyId: value},
							dataType: 'json',
							async: false,
							success : function(data) {
								if(!data._ERROR_MESSAGE_){
									partyName = data.partyName;
								}
					        }
						});
					  return '<span title' + value + '>' + partyName + '</span>';}
					 },
					{ text: '${uiLabelMap.BDStatusId}', datafield: 'statusId',  width: '13%', cellsrenderer: function(row, column, value){
						if (tripStatusData.length > 0) {
							for(var i = 0 ; i < tripStatusData.length; i++){
    							if (value == tripStatusData[i].statusId){
    								return '<span title =\"' + tripStatusData[i].description +'\">' + tripStatusData[i].description + '</span>';
    							}
    						}
						}
						return '<span title=' + value +'>' + value + '</span>';
				 	}
				 	},
				"/>
	<@jqGrid filtersimplemode="true" filterable="true" dataField=dataFieldVehicle columnlist=columnlistVehicle editable="false" showtoolbar="true"
url="jqxGeneralServicer?sname=JQGetListTrip" customTitleProperties="BDListTrip"
id="jqxgridVehicle" customcontrol1="icon-plus open-sans@${uiLabelMap
.AddNew}@javascript:ListTripObj.prepareCreate('newTrip');"
<#--customcontrol2=customcontrol2 customcontrol3=customcontrol3-->
/>
</div>

<script type="text/javascript" src="/deliresources/js/trip/listTrip.js"></script>