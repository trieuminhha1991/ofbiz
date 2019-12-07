<script type="text/javascript" src="/logresources/js/util/DateUtil.js" ></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqx.utils.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
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
					 { name: 'carrierFullName', type: 'string'},
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
					{ text: '${uiLabelMap.DeliveryEntryCode}', datafield: 'deliveryEntryId', width: 120, editable: false, pinned: true,
						cellsrenderer: function(row, column, value){
							var data = $('#jqxgrid').jqxGrid('getrowdata', row);
							return '<span><a href=\"deliveryEntryDetail?deliveryEntryId=' + value + '&facilityId='+data.facilityId+'&fromDate='+data.fromDate.getTime()+'\">' + value + '</a></span>';
						}
				 	},
				 	{ text: '${uiLabelMap.OrderId}', datafield: 'orderId', width: 150, editable: false, },
					{ text: '${uiLabelMap.DeliveryDocId}', datafield: 'deliveryId', width: 150, editable: false,
				 	},
				 	{ text: '${uiLabelMap.CarrierParty}', datafield: 'carrierFullName', width: 150, editable: false,
				 	},
				 	{ text: '${uiLabelMap.Deliverer}', datafield: 'delivererFullName', width: 150, editable: false,
				 	},
				 	{ text: '${uiLabelMap.Driver}', datafield: 'driverFullName', width: 150, editable: false,
				 	},
				 	{ text: '${uiLabelMap.ExportFromFacility}', datafield: 'facilityName', width: 150, editable: false,
				 	},
				 	{ text: '${uiLabelMap.Address}', datafield: 'fullName', width: 150, editable: false,
				 	},
				 	{ text: '${uiLabelMap.DeliveryDate}', datafield: 'fromDate', cellsalign: 'right', width: 150, cellsformat:'dd/MM/yyyy', editable: false, filtertype:'range'}, 
					{ text: '${uiLabelMap.Status}', datafield: 'statusId', width: 150, editable: false, columntype: 'dropdownlist',
					 	cellsrenderer: function(row, column, value){
						 	for(var i = 0; i < statusDataDE.length; i++){
							 	if(statusDataDE[i].statusId == value){
								 	return '<span title=' + value + '>' + statusDataDE[i].description + '</span>';
							 	}
						 	}
						 	return value;
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
		 			{ text: '${uiLabelMap.Description}', datafield: 'description', minwidth: 200, editable: false},
 				"/>
 <div id="containerNotify" style="width: 100%; overflow: auto;">
 </div>
<div>
<#if security.hasPermission("DELIVERY_ADMIN", userLogin)> 
	 <@jqGrid filtersimplemode="false" addType="popup" dataField=dataField columnlist=columnlist sortable="true" sortdirection="desc" defaultSortColumn="fromDate" clearfilteringbutton="true" showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="" editable="false"
		 url="jqxGeneralServicer?sname=getDeliveryEntry" addrefresh="true" editrefresh="true" 
		 updateUrl="jqxGeneralServicer?sname=updateDeliveryEntry&jqaction=U" editColumns="deliveryEntryId;statusId"
		 createUrl="jqxGeneralServicer?sname=createDeliveryEntry&jqaction=C" contextMenuId="DeliveryEntryMenu" mouseRightMenu="true"
		 addColumns="description;fromDate(java.sql.Timestamp);statusId;thruDate(java.sql.Timestamp);facilityId;weightUomId;listShipmentItems(java.util.List);delivererPartyId;driverPartyId;weight(java.math.BigDecimal);vehicleId;contactMechId" customTitleProperties="ListDeliveryEntry"
		 customcontrol1="icon-plus open-sans@${uiLabelMap.AddNew}@javascript: void(0);@DlvEntryObj.createDeliveryEntry()"/>
<#else>
	<@jqGrid filtersimplemode="false" addType="popup" dataField=dataField columnlist=columnlist sortable="true" sortdirection="desc" defaultSortColumn="fromDate" clearfilteringbutton="true" showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="" editable="true"
	 url="jqxGeneralServicer?sname=getDeliveryEntry" addrefresh="true" editrefresh="true" contextMenuId="DeliveryEntryMenu" mouseRightMenu="true"
	/>
</#if>
</div>
<div id='DeliveryEntryMenu' style="display:none;">
<ul>
	<li><i class="fa fa-folder-open-o"></i>${uiLabelMap.ViewDetailInNewPage}</li>
    <li><i class="fa fa-eye"></i>${StringUtil.wrapString(uiLabelMap.BSViewDetail)}</li>
    <li><i class="fa fa-trash red"></i>${uiLabelMap.CommonCancel}</li>
    <li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
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