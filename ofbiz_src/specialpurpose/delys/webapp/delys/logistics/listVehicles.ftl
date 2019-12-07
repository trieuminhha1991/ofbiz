<script type="text/javascript" src="/delys/images/js/import/miscUtil.js"></script>
<script type="text/javascript" src="/delys/images/js/import/progressing.js"></script>	
<#assign dataField="[{ name: 'vehicleId', type: 'string'},
						   { name: 'vehicleName', type: 'string'},
						   { name: 'partyCarrierId', type: 'string'},
						   { name: 'statusId', type: 'string'},
						   { name: 'vehicleTypeId', type: 'string'},
						   { name: 'shipmentMethodTypeId',  type: 'string'},
						   { name: 'lengthUomId', type: 'string'},
						   { name: 'unitCost', type: 'number'},
						   { name: 'currencyUomId', type: 'string'},
						   { name: 'maxWeight', type: 'number'},
						   { name: 'minWeight', type: 'number'},
						   { name: 'weightUomId', type: 'string'},
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
						   { text: '${uiLabelMap.Owner}', datafield: 'partyCarrierId', minwidth: 200, editable: false, filtertype: 'checkedlist', cellsrenderer:
						       function(row, colum, value){
						        var data = $('#jqxgrid').jqxGrid('getrowdata', row);
						        var partyCarrierId = data.partyCarrierId;
						        var partyCarrier = getPartyNameView(partyCarrierId);
						        if (partyCarrierId != null) {
							        return '<span>' + partyCarrier + '</span>';
								} else {
									return '';
								}
					        },createfilterwidget: function (column, htmlElement, editor) {
								editor.jqxDropDownList({ autoDropDownHeight: true, source: fixSelectAll(PartyName), displayMember: 'partyId', valueMember: 'partyId' ,
								    renderer: function (index, label, value) {
								    	if (index == 0) {
		                            		return value;
										}
									    return getPartyNameView(value);
								    } });
								editor.jqxDropDownList('checkAll');
								}},
						   { text: '${uiLabelMap.Status}',  datafield: 'statusId', width: 120, editable: true, filtertype: 'checkedlist', cellsrenderer:
						       function(row, colum, value){
						        var data = $('#jqxgrid').jqxGrid('getrowdata', row);
						        var statusId = data.statusId;
						        var status = getVehicleStatus(statusId);
						        if (statusId != null) {
							        return '<span>' + status + '</span>';
								} else {
									return '';
								}
					        },createfilterwidget: function (column, htmlElement, editor) {
								editor.jqxDropDownList({ autoDropDownHeight: true, source: fixSelectAll(VehicleStatus), displayMember: 'statusId', valueMember: 'statusId' ,
								    renderer: function (index, label, value) {
								    	if (index == 0) {
		                            		return value;
										}
									    return getVehicleStatus(value);
								    } });
								editor.jqxDropDownList('checkAll');
								}},
						   { text: '${uiLabelMap.FormFieldTitle_shipmentMethodTypeId}',  datafield: 'shipmentMethodTypeId', minwidth: 180, editable: true, filtertype: 'checkedlist', cellsrenderer:
						       function(row, colum, value){
						        var data = $('#jqxgrid').jqxGrid('getrowdata', row);
						        var shipmentMethodTypeId = data.shipmentMethodTypeId;
						        var shipmentMethodType = getShipmentMethodType(shipmentMethodTypeId);
						        if (shipmentMethodTypeId != null) {
							        return '<span>' + shipmentMethodType + '</span>';
								} else {
									return '';
								}
					        },createfilterwidget: function (column, htmlElement, editor) {
								editor.jqxDropDownList({ autoDropDownHeight: true, source: fixSelectAll(ShipmentMethodType), displayMember: 'shipmentMethodTypeId', valueMember: 'shipmentMethodTypeId' ,
								    renderer: function (index, label, value) {
								    	if (index == 0) {
		                            		return value;
										}
									    return getShipmentMethodType(value);
								    } });
								editor.jqxDropDownList('checkAll');
								}},
						   { text: '${uiLabelMap.LengthUom}',  datafield: 'lengthUomId', width: 140, editable: true, filtertype: 'checkedlist', cellsrenderer:
						       function(row, colum, value){
						        var data = $('#jqxgrid').jqxGrid('getrowdata', row);
						        var lengthUomId = data.lengthUomId;
						        var lengthUom = getLengthUom(lengthUomId);
						        if (lengthUomId != null) {
							        return '<span>' + lengthUom + '</span>';
								} else {
									return '';
								}
					        },createfilterwidget: function (column, htmlElement, editor) {
								editor.jqxDropDownList({ autoDropDownHeight: true, source: fixSelectAll(listUom), displayMember: 'uomId', valueMember: 'uomId' ,
								    renderer: function (index, label, value) {
								    	if (index == 0) {
		                            		return value;
										}
									    return getLengthUom(value);
								    } });
								editor.jqxDropDownList('checkAll');
								}},
						   { text: '${uiLabelMap.UnitCostVehicle}',  datafield: 'unitCost', width: 200, editable: true, cellsalign: 'right', filtertype: 'number'},
						   { text: '${uiLabelMap.currencyUomId}',  datafield: 'currencyUomId', minwidth: 120, editable: true},
						   { text: '${uiLabelMap.maxWeight}',  datafield: 'maxWeight', width: 120, editable: false, cellsalign: 'right', filtertype: 'number'},
						   { text: '${uiLabelMap.minWeight}',  datafield: 'minWeight', width: 120, editable: false, cellsalign: 'right', filtertype: 'number'},
						   { text: '${uiLabelMap.weightUom}',  datafield: 'weightUomId', width: 120, editable: false, filtertype: 'checkedlist', cellsrenderer:
						       function(row, colum, value){
						        var data = $('#jqxgrid').jqxGrid('getrowdata', row);
						        var weightUomId = data.weightUomId;
						        var weightUom = getLengthUomW(weightUomId);
						        if (weightUomId != null) {
							        return '<span>' + weightUom + '</span>';
								} else {
									return '';
								}
					        },createfilterwidget: function (column, htmlElement, editor) {
								editor.jqxDropDownList({ autoDropDownHeight: true, source: fixSelectAll(listUomW), displayMember: 'uomId', valueMember: 'uomId' ,
								    renderer: function (index, label, value) {
								    	if (index == 0) {
		                            		return value;
										}
									    return getLengthUomW(value);
								    } });
								editor.jqxDropDownList('checkAll');
								}},
						   { text: '${uiLabelMap.fromDate}',  datafield: 'fromDate', width: 180, editable: false, filtertype: 'date', cellsformat: 'dd/MM/yyyy'},
						   { text: '${uiLabelMap.thruDate}',  datafield: 'thruDate', width: 180, editable: false, filtertype: 'date', cellsformat: 'dd/MM/yyyy'},
						   "/>
	<@jqGrid filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true"
								showtoolbar="true" addrow="true" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="false" 
								editmode='click'
								url="jqxGeneralServicer?sname=JQGetListVehicles" updateUrl="jqxGeneralServicer?sname=updateVehicles&jqaction=U"
								createUrl="jqxGeneralServicer?sname=updateVehicles&jqaction=C"
								addColumns="vehicleId;vehicleName;vehicleTypeId;partyCarrierId;statusId;shipmentMethodTypeId;lengthUomId;unitCost;currencyUomId;maxWeight;minWeight;weightUomId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp)"
							/>
						   
<div id="alterpopupWindow" style="display:none;">
    <div>${uiLabelMap.Vehicles}</div>
    <div style="overflow-y: scroll;">
    	<input type="hidden" id="vehicleTypeId" value="TRUCK"/>
    	<div class="row-fluid">
    		<div class="span12">
	 			<div class="span3">${uiLabelMap.vehicleName}:</div>
	 			<div class="span3"><input type='text' id="vehicleName1" style="width: 189px; height: 18px"/></div>
	 			<div class="span3">${uiLabelMap.weightUom}:</div>
	 			<div class="span3"><div type='text' id="weightUom1"></div></div>
 			</div>
 			<div class="span12 no-left-margin">
	 			<div class="span3">${uiLabelMap.FormFieldTitle_shipmentMethodTypeId}:</div>
	 			<div class="span3"><div type='text' id="shipmentMethodTypeId1"></div></div>
	 			<div class="span3">${uiLabelMap.maxWeight}:</div>
	 			<div class="span3"><div type='text' id="maxWeight1"></div></div>
 			</div>
 			<div class="span12 no-left-margin">
	 			<div class="span3">${uiLabelMap.Status}:</div>
    	 		<div class="span3"><div type='text' id="Status1"></div></div>
    	 		<div class="span3">${uiLabelMap.minWeight}:</div>
	 			<div class="span3"><div type='text' id="minWeight1"></div></div>
 			</div>
 			<div class="span12 no-left-margin">
	 			<div class="span3">${uiLabelMap.LengthUom}:</div>
    	 		<div class="span3"><div type='text' id="LengthUom1"></div></div>
    	 		<div class="span3">${uiLabelMap.fromDate}:</div>
    	 		<div class="span3"><div type='text' id="fromDate1"></div></div>
 			</div>
 			<div class="span12 no-left-margin">
	 			<div class="span3">${uiLabelMap.UnitCostVehicle}:</div>
    	 		<div class="span3"><div type='text' id="UnitCostVehicle1"></div></div>
    	 		<div class="span3">${uiLabelMap.thruDate}:</div>
    	 		<div class="span3"><div type='text' id="thruDate1"></div></div>
	 		</div>
	 		<div class="span12 no-left-margin">
    	 		<div class="span3">${uiLabelMap.currencyUomId}:</div>
    	 		<div class="span3"><div type='text' id="currencyUomId1"></div></div>
    	 	</div>
    	 	<div class="span12 no-left-margin">
    	 		<div class="span2">${uiLabelMap.description}:</div>
    	 		<div class="span10"><textarea  class="note-area no-resize" id="description123" autocomplete="off"></textarea></div>
	 		</div>
	 		<div class="span12 no-left-margin">
    	 		<div class="span3"></div>
    	 		<div class="span3"></div>
                <div class="span3"><input style="margin-right: 5px;" type="button" id="alterSave" value="${uiLabelMap.CommonSave}" /><input id="alterCancel" type="button" value="${uiLabelMap.CommonCancel}" /></div>
                <div class="span3"></div>
            </div>
         </div>
    </div>
</div>
						   
<script>
$.jqx.theme = 'olbius';
theme = $.jqx.theme;
		function loadEditor(id){
		    var instance = CKEDITOR.instances[id];
		    if(instance)
		    {
		        CKEDITOR.remove(instance);
		    }
		    CKEDITOR.replace(id, { skin: 'office2013'});
		}
		loadEditor("description123");
    	var ck = CKEDITOR.instances;
			$("#alterpopupWindow").jqxWindow({
	            width: 1150, maxWidth: 1100, minHeight: 560, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7
	        });
	        $("#alterCancel").jqxButton();
	        $("#alterSave").jqxButton();
	        $("#fromDate1").jqxDateTimeInput();
	    	$("#thruDate1").jqxDateTimeInput();
	    	$("#maxWeight1").jqxNumberInput({ inputMode: 'simple', spinButtons: true });
	    	$("#minWeight1").jqxNumberInput({ inputMode: 'simple', spinButtons: true });
	    	$("#UnitCostVehicle1").jqxNumberInput({ inputMode: 'simple', spinButtons: true });
	    	var checkFromDate = $('#fromDate1').val();
	        var dateFRM = checkFromDate.split('/');
	        checkFromDate = new Date(dateFRM[2], dateFRM[1] - 1, dateFRM[0], 0, 0, 0, 0);
	    	 $('#fromDate1').on('close', function (event)
		        		{
		        		    var jsDate = event.args.date;
		        		    checkFromDate = jsDate;
		        		});
		        $('#thruDate1').on('close', function (event)
		        		{
		        		    var jsDate = event.args.date;
		        		    if (checkFromDate < jsDate) {
							} else {
								 $('#inputthruDate1').val('');
							}
		        		});
	        
			var PartyName = new Array();
			<#if PartyNameView?exists>
				<#list PartyNameView as item>
					var row = {};
					row['partyId'] = '${item.partyId?if_exists}';
					row['description'] = '${item.firstName?if_exists} ${item.middleName?if_exists} ${item.lastName?if_exists} ${item.groupName?if_exists} [${item.partyId?if_exists}]';
					PartyName[${item_index}] = row;
				</#list>
			</#if>
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
			
			var listUomC = new Array();
			<#if listUomC?exists>
				<#list listUomC as item>
					var row = {};
					<#assign description = StringUtil.wrapString(item.description) />
					row['uomId'] = '${item.uomId?if_exists}';
					row['description'] = "${description}";
					listUomC[${item_index}] = row;
				</#list>
			</#if>
			
			<#assign VehicleStatus = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "VEHICLE_STATUS"), null, null, null, false) />
			var VehicleStatus = new Array();
			<#if VehicleStatus?exists>
				<#list VehicleStatus as item>
					var row = {};
					<#assign description = StringUtil.wrapString(item.description) />
					row['statusId'] = '${item.statusId?if_exists}';
					row['description'] = "${description}";
					VehicleStatus[${item_index}] = row;
				</#list>
			</#if>
			function getVehicleStatus(statusId) {
				for ( var x in VehicleStatus) {
					if (statusId == VehicleStatus[x].statusId) {
						return VehicleStatus[x].description;
					}
				}
			}
			
			<#assign ShipmentMethodType = delegator.findList("ShipmentMethodType", null, null, null, null, false) />
			var ShipmentMethodType = new Array();
			<#if ShipmentMethodType?exists>
				<#list ShipmentMethodType as item>
					var row = {};
					<#assign description = StringUtil.wrapString(item.description) />
					row['shipmentMethodTypeId'] = '${item.shipmentMethodTypeId?if_exists}';
					row['description'] = "${description}";
					ShipmentMethodType[${item_index}] = row;
				</#list>
			</#if>
			function getShipmentMethodType(shipmentMethodTypeId) {
				for ( var x in ShipmentMethodType) {
					if (shipmentMethodTypeId == ShipmentMethodType[x].shipmentMethodTypeId) {
						return ShipmentMethodType[x].description;
					}
				}
			}
			
			var listUom = new Array();
			<#if listUom?exists>
				<#list listUom as item>
					var row = {};
					<#assign description = StringUtil.wrapString(item.description) />
					row['uomId'] = '${item.uomId?if_exists}';
					row['description'] = "${description}";
					listUom[${item_index}] = row;
				</#list>
			</#if>
			function getLengthUom(uomId) {
				for ( var x in listUom) {
					if (uomId == listUom[x].uomId) {
						return listUom[x].description;
					}
				}
			}
			
			var listUomW = new Array();
			<#if listUomW?exists>
				<#list listUomW as item>
					var row = {};
					<#assign description = StringUtil.wrapString(item.description) />
					row['uomId'] = '${item.uomId?if_exists}';
					row['description'] = "${description}";
					listUomW[${item_index}] = row;
				</#list>
			</#if>
			function getLengthUomW(uomId) {
				for ( var x in listUomW) {
					if (uomId == listUomW[x].uomId) {
						return listUomW[x].description;
					}
				}
			}
			$("#shipmentMethodTypeId1").jqxDropDownList({ source: ShipmentMethodType,displayMember: "description", valueMember: "shipmentMethodTypeId", selectedIndex: 0, filterable:true});
	    	$("#weightUom1").jqxDropDownList({ source: listUomW,displayMember: "description", valueMember: "uomId", selectedIndex: 0, filterable:true});
	    	$("#Status1").jqxDropDownList({ source: VehicleStatus,displayMember: "description", valueMember: "statusId", selectedIndex: 0});
	    	$("#LengthUom1").jqxDropDownList({ source: listUom,displayMember: "description", valueMember: "uomId", selectedIndex: 0, filterable:true});
	    	$("#currencyUomId1").jqxDropDownList({ source: listUomC,displayMember: "description", valueMember: "uomId", selectedIndex: 0, filterable:true});
	    	
	    	 $("#alterSave").click(function () {
		        	var row;
     			var tempFrDate = $('#fromDate1').val();
     			var dateFRM1 = tempFrDate.split('/');
 		        var frmDate = new Date(dateFRM1[2], dateFRM1[1] - 1, dateFRM1[0], 0, 0, 0, 0);
 		        var tempThrDate = $('#thruDate1').val();
     			var dateTHR1 = tempThrDate.split('/');
 		        var thrDate = new Date(dateTHR1[2], dateTHR1[1] - 1, dateTHR1[0], 0, 0, 0, 0);
		            row = {
		            		partyCarrierId: "company",
		            		vehicleName:$('#vehicleName1').val(),
		            		vehicleTypeId: $('#vehicleTypeId').val(),
		            		statusId:$('#Status1').val(),
		            		shipmentMethodTypeId:$('#shipmentMethodTypeId1').val(),
		            		lengthUomId:$('#LengthUom1').val(),
		            		currencyUomId:$('#currencyUomId1').val(),
		            		maxWeight: $('#maxWeight1').val(),
		            		minWeight: $('#minWeight1').val(),
		            		weightUomId:$('#weightUom1').val(),
		            		unitCost:$('#UnitCostVehicle1').val(),
		            		description: getDataEditor("description123"),
		            		fromDate:frmDate,
		            		thruDate:thrDate,
		            	  };
		    	   	$("#jqxgrid").jqxGrid('addRow', null, row, "first");
		            $("#jqxgrid").jqxGrid('clearSelection');
		            $("#jqxgrid").jqxGrid('selectRow', 0);
		            $("#alterpopupWindow").jqxWindow('close');
		        });
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