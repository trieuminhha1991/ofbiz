<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/resources/js/DateUtil.js" ></script>
<script type="text/javascript" src="/resources/js/bootbox.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator2.js"></script>
<#include "script/listDeliveryEntryScript.ftl"/>

<div id="jqxNotificationSuccess" style="display: none;">
<div>
	${uiLabelMap.UpdateSuccessfully}. 
</div>
</div>

<#assign dataField="[{ name: 'deliveryEntryId', type: 'string' },
					 { name: 'description', type: 'string'},
					 { name: 'fromDate', type: 'date', other: 'Timestamp'},
					 { name: 'thruDate', type: 'date', other: 'Timestamp'},
					 { name: 'weight', type: 'number'},
					 { name: 'facilityId', type: 'string'},
					 { name: 'driverFullName', type: 'string'},
					 { name: 'delivererFullName', type: 'string'},
					 { name: 'facilityId', type: 'string'},
					 { name: 'deliveryId', type: 'string'},
					 { name: 'orderId', type: 'string'},
					 { name: 'facilityName', type: 'string'},
					 { name: 'fullName', type: 'string'},
					 { name: 'weightUomId', type: 'string'},
					 { name: 'statusId', type: 'string'}
					 ]"/>
<#assign columnlist="
					{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true, 
				        groupable: false, draggable: false, resizable: false,
				        datafield: '', columntype: 'number', width: 50,
				        cellsrenderer: function (row, column, value) {
				            return '<span style=margin:4px;>' + (value + 1) + '</span>';
				        }
					},					
					{ text: '${uiLabelMap.DeliveryEntryId}', datafield: 'deliveryEntryId', width: 120, editable: false, pinned: true,
						cellsrenderer: function(row, column, value){
							var data = $('#jqxgrid').jqxGrid('getrowdata', row);
							return '<span><a href=\"deliveryEntryDetail?deliveryEntryId=' + value + '&facilityId='+data.facilityId+'&fromDate='+data.fromDate.getTime()+'\">' + value + '</a></span>';
						}
					 },
					 { text: '${uiLabelMap.OrderId}', datafield: 'orderId', width: 150, editable: false,
					 },
					 { text: '${uiLabelMap.DeliveryId}', datafield: 'deliveryId', width: 150, editable: false,
					 },
					 { text: '${uiLabelMap.Deliverer}', datafield: 'delivererFullName', width: 150, editable: false,
					 },
					 { text: '${uiLabelMap.Driver}', datafield: 'driverFullName', width: 150, editable: false,
					 },
					 { text: '${uiLabelMap.Address}', datafield: 'fullName', width: 150, editable: false,
					 },
					 { text: '${uiLabelMap.FacilityFrom}', datafield: 'facilityName', width: 150, editable: false,
					 },
					 { text: '${uiLabelMap.DeliveryDate}', datafield: 'fromDate', cellsalign: 'right', width: 150, cellsformat:'dd/MM/yyyy', editable: false, filtertype:'range'}, 
					 "/>
<#if security.hasPermission("DELIVERY_ADMIN", userLogin)> 
	<#assign columnlist= columnlist + "
					 { text: '${uiLabelMap.Status}', datafield: 'statusId', width: 150, editable: true, columntype: 'dropdownlist', filtertype: 'checkedlist',
						 cellsrenderer: function(row, column, value){
							 for(var i = 0; i < statusAllStatusDE.length; i++){
								 if(statusAllStatusDE[i].statusId == value){
									 return '<span title=' + value + '>' + statusAllStatusDE[i].description + '</span>';
								 }
							 }
							 return value;
						 },
						 createfilterwidget: function (column, columnElement, widget) {
							var filterDataAdapter = new $.jqx.dataAdapter(statusDataDE, {
								autoBind: true
							});
							var records = filterDataAdapter.records;
							widget.jqxDropDownList({source: records, displayMember: 'statusId', valueMember: 'statusId',
								renderer: function(index, label, value){
									if (statusDataDE.length > 0) {
										for(var i = 0; i < statusDataDE.length; i++){
											if(statusDataDE[i].statusId == value){
												return '<span>' + statusDataDE[i].description + '</span>';
											}
										}
									}
									return value;
								}
							});
							widget.jqxDropDownList('checkAll');
			   			},
						 cellbeginedit: function (row, datafield, columntype) {
								var data = $('#jqxgrid').jqxGrid('getrowdata', row);
								if(data.statusId == 'DELI_ENTRY_COMPLETED' || data.statusId == 'DELI_ENTRY_DELIVERED' || data.statusId == 'DELI_ENTRY_CANCELLED'){
									tmpEditable = true;
									return false;
								}else{
									tmpEditable = false;
									return true;
								}
						 },
						 createeditor: function(row, cellvalue, editor){
							 var statusList = new Array();
							 switch (cellvalue) {
								case 'DELI_ENTRY_CREATED':
								     var row = {};
                                     row['statusId'] = 'DELI_ENTRY_CREATED';
                                     for (var i = 0; i < statusDataDE.length; i ++){
                                    	 if (statusDataDE[i].statusId == 'DELI_ENTRY_CREATED'){
                                    		 row['description'] = statusDataDE[i].description;
                                    	 }
                                     }
                                     statusList[0] = row;
									 row = {};
									 row['statusId'] = 'DELI_ENTRY_SCHEDULED';
									 for (var i = 0; i < statusDataDE.length; i ++){
                                    	 if (statusDataDE[i].statusId == 'DELI_ENTRY_SCHEDULED'){
                                    		 row['description'] = statusDataDE[i].description;
                                    	 }
                                     }
									 statusList[1] = row;
									 row = {};
									 row['statusId'] = 'DELI_ENTRY_CANCELLED';
									 for (var i = 0; i < statusDataDE.length; i ++){
                                    	 if (statusDataDE[i].statusId == 'DELI_ENTRY_CANCELLED'){
                                    		 row['description'] = statusDataDE[i].description;
                                    	 }
                                     }
									 statusList[2] = row;
									 break;
								case 'DELI_ENTRY_SCHEDULED':
								     var row = {};
                                     row['statusId'] = 'DELI_ENTRY_SCHEDULED';
                                     for (var i = 0; i < statusDataDE.length; i ++){
                                    	 if (statusDataDE[i].statusId == 'DELI_ENTRY_SCHEDULED'){
                                    		 row['description'] = statusDataDE[i].description;
                                    	 }
                                     }
                                     statusList[0] = row;
									 row = {};
									 row['statusId'] = 'DELI_ENTRY_SHIPPING';
									 for (var i = 0; i < statusDataDE.length; i ++){
                                    	 if (statusDataDE[i].statusId == 'DELI_ENTRY_SHIPPING'){
                                    		 row['description'] = statusDataDE[i].description;
                                    	 }
                                     }
									 statusList[1] = row;
									 break;
								case 'DELI_ENTRY_SHIPPING':
								     var row = {};
                                     row['statusId'] = 'DELI_ENTRY_SHIPPING';
                                     for (var i = 0; i < statusDataDE.length; i ++){
                                    	 if (statusDataDE[i].statusId == 'DELI_ENTRY_SHIPPING'){
                                    		 row['description'] = statusDataDE[i].description;
                                    	 }
                                     }
                                     statusList[0] = row;
									 row = {};
									 row['statusId'] = 'DELI_ENTRY_DELIVERED';
									 for (var i = 0; i < statusDataDE.length; i ++){
                                    	 if (statusDataDE[i].statusId == 'DELI_ENTRY_DELIVERED'){
                                    		 row['description'] = statusDataDE[i].description;
                                    	 }
                                     }
									 statusList[1] = row;
									 break;
								default:
									break;
								}
							 editor.jqxDropDownList({source: statusList, valueMember: 'statusId', displayMember: 'description'});
						 },
						 validation: function (cell, value) {
					        var data = $('#jqxgrid').jqxGrid('getrowdata', cell.row);
					        var allPacked = true;
					        var allPicked = true;
					        var allShipped = true;
	        				var allDelivered = true;
	        				var allScheduled = true;
	        				var allCancelled = true;
	        				var newDEStatusId = null;
				        	$.ajax({
				        		type: 'POST',
				        		data: {
				        			deliveryEntryId: data.deliveryEntryId,
				        		},
				        		url: 'getShipmentInDeliveryEntry',
				        		success: function (res){
				        			var listShipmentInDE = res.listShipments;
				        			for (var i = 0; i < listShipmentInDE.length; i ++){
				        				if ('SHIPMENT_DELIVERED' != listShipmentInDE[i].statusId){
				        					allDelivered = false;
				    						if ('SHIPMENT_SHIPPED' != listShipmentInDE[i].statusId){
				    							allShipped = false;
				    							if ('SHIPMENT_SCHEDULED' != listShipmentInDE[i].statusId){
				    								allScheduled = false;
				    								if ('SHIPMENT_PICKED' != listShipmentInDE[i].statusId){
				    									allPicked = false;
				    									if ('SHIPMENT_PACKED' != listShipmentInDE[i].statusId){
				    										allPacked = false;
				    									}
				    								}
				    							}
				    						}
				    					}
				        			}
				        		},
				        		async: false,
				        	});
				        	
				        	if (allDelivered == false && (value == 'DELI_ENTRY_COMPLETED'|| value == 'DELI_ENTRY_DELIVERED')){
		        				return { result: false, message: '${uiLabelMap.CannotChangeBecauseShipmentElementNotDelivered}'};
		        			} else if (allShipped == false  && value == 'DELI_ENTRY_SHIPPING'){
		        				return { result: false, message: '${uiLabelMap.CannotChangeBecauseShipmentElementNotShippedYet}'};
		        			} else if (allScheduled == false && value == 'DELI_ENTRY_SCHEDULED'){
		        				return { result: false, message: '${uiLabelMap.CannotChangeBecauseShipmentElementNotScheduled}'};
		        			} else if (allPicked == false && value == 'DELI_ENTRY_SCHEDULED'){
		        				return { result: false, message: '${uiLabelMap.CannotChangeBecauseShipmentElementNotPackedYet}'};
		        			} else if (allPacked == false && value == 'DELI_ENTRY_SCHEDULED'){
		        				return { result: false, message: '${uiLabelMap.CannotChangeBecauseShipmentElementNotPackedYet}'};
		        			}
					        return true;
						 },
					 },
				 "/>
<#else>
		 <#assign columnlist= columnlist + "
		 	{ text: '${uiLabelMap.Status}', datafield: 'statusId', width: 150, editable: false, columntype: 'dropdownlist',
			 cellsrenderer: function(row, column, value){
				 for(var i = 0; i < statusDataDE.length; i++){
					 if(statusDataDE[i].statusId == value){
						 return '<span title=' + value + '>' + statusDataDE[i].description + '</span>';
					 }
				 }
				 return value;
			 }
		 	},"/>
</#if>
		 <#assign columnlist= columnlist + "
		 { text: '${uiLabelMap.Weight}', datafield: 'weight', width: 150, editable: false,  
				cellsrenderer: function(row, colum, value){
				   	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
				   	var weightUomId = data.weightUomId;
				   	var weightUomIdAbb = '';
				   	for(var i = 0; i < uomData.length; i++){
						 if(uomData[i].weightUomId == weightUomId){
							 weightUomIdAbb = uomData[i].abbreviation;
						 }
					 }
			        return '<span style=\"text-align: right\">' + value.toLocaleString('${localeStr}') +' (' + weightUomIdAbb +  ')</span>';
				}
			 },
		 { text: '${uiLabelMap.RequiredCompletedDate}', datafield: 'thruDate', width: 150, cellsformat:'dd/MM/yyyy', editable: false, filtertype:'range',
			 cellsrenderer: function(row, column, value){
				 if (!value){
					 return '<span style=\"text-align: right\" title=\"_NA_\">_NA_</span>';
				 } else {
					 return '<span style=\"text-align: right\">'+formatFullDate(value)+'</span>';
				 }
			 }, 
		 },
		 { text: '${uiLabelMap.Description}', datafield: 'description', minwidth: 200, editable: false}
 "/>
 <div id="containerNotify" style="width: 100%; overflow: auto;">
 </div>
<div>
<#if security.hasPermission("DELIVERY_ADMIN", userLogin)> 
	 <@jqGrid filtersimplemode="false" addType="popup" dataField=dataField columnlist=columnlist sortable="true" sortdirection="desc" defaultSortColumn="fromDate" clearfilteringbutton="true" showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="" editable="false"
		 url="jqxGeneralServicer?sname=getDeliveryEntry" addrefresh="true" editrefresh="true" 
		 updateUrl="jqxGeneralServicer?sname=updateDeliveryEntry&jqaction=U" editColumns="deliveryEntryId;statusId"
		 createUrl="jqxGeneralServicer?sname=createDeliveryEntry&jqaction=C" contextMenuId="menuDelete" mouseRightMenu="true"
		 addColumns="description;fromDate(java.sql.Timestamp);statusId;thruDate(java.sql.Timestamp);facilityId;weightUomId;listShipmentItems(java.util.List);delivererPartyId;driverPartyId;weight(java.math.BigDecimal);vehicleId;contactMechId" customTitleProperties="ListDeliveryEntry"
		 customcontrol1="icon-plus open-sans@${uiLabelMap.AddNew}@javascript: void(0);@DlvEntryObj.createDeliveryEntry()"/>
<#else>
	<@jqGrid filtersimplemode="false" addType="popup" dataField=dataField columnlist=columnlist sortable="true" sortdirection="desc" defaultSortColumn="fromDate" clearfilteringbutton="true" showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="" editable="true"
	 url="jqxGeneralServicer?sname=getDeliveryEntry" addrefresh="true" editrefresh="true" contextMenuId="menuDelete" mouseRightMenu="true"
	/>
</#if>
</div>
<div id='menuDelete' style="display:none;">
	<ul>
	    <li><i class="fa fa-trash red"></i>${uiLabelMap.CommonCancel}</li>
	    <li><i class="fa fa-edit"></i>${uiLabelMap.CommonEdit}</li>
	</ul>
</div>
<div id="editPopupWindow" class="hide popup-bound">
<div>${uiLabelMap.CommonEdit}</div>
<div class='form-window-container'>
	<div class='form-window-content'>
		<input id="deliveryEntryIdEdit" type="hidden"></input>
        <div class="row-fluid">
    		<div class="span12 margin-top10">
	    		<div class="row-fluid margin-bottom10">	
					<div class="span5" style="text-align: right">
						<div style="margin-right: 10px"> ${uiLabelMap.Driver} </div>
					</div>
					<div class="span7">	
						<div id="driverPartyId" class="green-label"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span5" style="text-align: right">
						<div style="margin-right: 10px"> ${uiLabelMap.Deliverer} </div>
					</div>
					<div class="span7">	
						<div id="delivererPartyId" class="green-label"></div>
					</div>
				</div>
    		</div>
		</div>
	</div>
	<div class="form-action popup-footer">
        <button id="editCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonClose}</button>
        <button id="editSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
	</div>
</div>
</div>