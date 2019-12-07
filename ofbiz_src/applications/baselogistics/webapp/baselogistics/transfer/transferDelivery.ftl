<#assign acceptFile="image/*"/>
<#assign entityName="Delivery"/>
<#include "component://basesalesmtl/webapp/basesalesmtl/common/fileAttachment.ftl"/>
<#include "script/transferDeliveryScript.ftl"/>
<div id="deliveries-tab" class="tab-pane<#if activeTab?exists && activeTab == "deliveries-tab"> active</#if>">
<#assign columnlist="
	{	text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
	    groupable: false, draggable: false, resizable: false,
	    datafield: '', columntype: 'number', width: 50,
	    cellsrenderer: function (row, column, value) {
	    	return '<div style=margin:4px;>' + (value + 1) + '</div>';
	    }
	},			
	{ text: '${uiLabelMap.CommonDeliveryId}', dataField: 'deliveryId', width: 150, filtertype:'input', editable:false, pinned: true,
	cellsrenderer: function(row, column, value){
		 return '<span><a href=\"javascript:TransferDlvObj.showDetailDelivery(&#39;'+value+'&#39;);\"> ' + value  + '</a></span>'
	 }
	},
	{ text: '${uiLabelMap.Status}', dataField: 'statusId', width: 150, editable:true, columntype: 'dropdownlist',
		cellsrenderer: function(row, column, value){
			for(var i = 0; i < statusData.length; i++){
				if(statusData[i].statusId == value){
					return '<span title=' + value + '>' + statusData[i].description + '</span>'
				}
			}
		},
		createeditor: function(row, value, editor){
			editor.jqxDropDownList({ source: statusData, displayMember: 'statusId', valueMember: 'statusId',
				renderer: function(index, label, value) {
					for(var i = 0; i < statusData.length; i++){
						if(value == statusData[i].statusId){
							return '<span>' + statusData[i].description + '</span>'
						}
					}
				}
			});
		},
		filtertype: 'input'
	 },
	 { text: '${uiLabelMap.FacilityFrom}', dataField: 'originFacilityId', width: 150, editable:false,
		 cellsrenderer: function(row, column, value){
			 var data = $('#jqxgridDelivery').jqxGrid('getrowdata', row);
			 return '<span>' + data.originFacilityName + '</span>';
		 },
		 filtertype: 'input'
	 },
	 { text: '${uiLabelMap.FacilityTo}', dataField: 'destFacilityId', editable:false, width: 150,
		 cellsrenderer: function(row, column, value){
			 var data = $('#jqxgridDelivery').jqxGrid('getrowdata', row);
			 return '<span>' + data.destFacilityName + '</span>';
		 },
		 filtertype: 'input'
	 },
	 { text: '${uiLabelMap.ActualExportedDate}', dataField: 'actualStartDate', width: 160, cellsformat: 'dd/MM/yyyy', filtertype: 'date', editable:false,
		 cellsrenderer: function(row, column, value){
			 if (!value){
				 return '<span style=\"text-align: right\" ></span>';
			 } else {
				 return '<span style=\"text-align: right\">'+ DatetimeUtilObj.formatFullDate(value)+'</span>';
			 }
		 }, 
	 },
	 { text: '${uiLabelMap.ActualDeliveredDate}', dataField: 'actualArrivalDate', width: 160, cellsformat: 'dd/MM/yyyy', filtertype: 'date', editable:false,
		 cellsrenderer: function(row, column, value){
			 if (!value){
				 return '<span style=\"text-align: right\" ></span>';
			 } else {
				 return '<span style=\"text-align: right\">'+ DatetimeUtilObj.formatFullDate(value)+'</span>';
			 }
		 }, 
	 },
	 { text: '${uiLabelMap.EstimatedStartDate}', dataField: 'estimatedStartDate', width: 160, cellsformat: 'dd/MM/yyyy', filtertype: 'date', editable:false,
		 cellsrenderer: function(row, column, value){
			 if (!value){
				 return '<span style=\"text-align: right\" ></span>';
			 } else {
				 return '<span style=\"text-align: right\">'+ DatetimeUtilObj.formatFullDate(value)+'</span>';
			 }
		 }, 
	 },
	 { text: '${uiLabelMap.EstimatedArrivalDate}', dataField: 'estimatedArrivalDate', width: 160, cellsformat: 'dd/MM/yyyy', filtertype: 'date', editable:false,
		 cellsrenderer: function(row, column, value){
			 if (!value){
				 return '<span style=\"text-align: right\" ></span>';
			 } else {
				 return '<span style=\"text-align: right\">'+ DatetimeUtilObj.formatFullDate(value)+'</span>';
			 }
		 }, 
	 },
	 { text: '${uiLabelMap.createDate}', dataField: 'createDate', width: 130, cellsformat: 'dd/MM/yyyy', filtertype: 'date', editable:false,
		 cellsrenderer: function(row, column, value){
			 if (!value){
				 return '<span style=\"text-align: right\" ></span>';
			 } else {
				 return '<span style=\"text-align: right\">'+ DatetimeUtilObj.formatFullDate(value)+'</span>';
			 }
		 }, 
	 },
	 "/>
<#assign dataField="[{ name: 'deliveryId', type: 'string' },
		{ name: 'statusId', type: 'string' },
	 	{ name: 'originFacilityId', type: 'string' },
	 	{ name: 'destFacilityId', type: 'string' },
	 	{ name: 'destFacilityName', type: 'string' },
	 	{ name: 'originFacilityName', type: 'string' },
	 	{ name: 'createDate', type: 'date', other: 'Timestamp' },
	 	{ name: 'estimatedStartDate', type: 'date', other: 'Timestamp' },
	 	{ name: 'actualStartDate', type: 'date', other: 'Timestamp' },
	 	{ name: 'estimatedArrivalDate', type: 'date', other: 'Timestamp' },
		{ name: 'actualArrivalDate', type: 'date', other: 'Timestamp' },
		{ name: 'deliveryDate', type: 'date', other: 'Timestamp' },
		{ name: 'defaultWeightUomId', type: 'string' },
]"/>
	<#if transfer.statusId == "TRANSFER_APPROVED" && createdDone == false && hasOlbPermission("MODULE", "LOG_DELIVERY", "CREATE")>
	<@jqGrid filtersimplemode="true" id="jqxgridDelivery" addrefresh="true" usecurrencyfunction="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="true" filterable="true" alternativeAddPopup="alterpopupWindow" editable="false" 
		 url="jqxGeneralServicer?sname=getListTransferDelivery&transferId=${parameters.transferId?if_exists}&deliveryId=${parameters.deliveryId?if_exists}" createUrl="jqxGeneralServicer?sname=createTransferDelivery&jqaction=C" editmode="dblclick"
		 addColumns="listTransferItems(java.util.List);transferId;statusId;destFacilityId;originFacilityId;deliveryDate(java.sql.Timestamp);deliveryTypeId[DELIVERY_TRANSFER];estimatedStartDate(java.sql.Timestamp);estimatedArrivalDate(java.sql.Timestamp);defaultWeightUomId" 	 
		 updateUrl="jqxGeneralServicer?sname=updateTransferDelivery&jqaction=U" functionAfterAddRow="TransferDlvObj.afterAddDelivery()" mouseRightMenu="true" contextMenuId="DeliveryMenu"
		 customcontrol1="icon-plus@${uiLabelMap.QuickCreate}@javascript:TransferDlvObj.quickCreateTransferDelivery('${parameters.transferId?if_exists}');"
		 customTitleProperties="ListTransferNote"/>
	<#else>
	<@jqGrid filtersimplemode="true" id="jqxgridDelivery" addrefresh="true" usecurrencyfunction="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="false" filterable="true" alternativeAddPopup="alterpopupWindow" editable="false" 
		 url="jqxGeneralServicer?sname=getListTransferDelivery&transferId=${parameters.transferId?if_exists}&deliveryId=${parameters.deliveryId?if_exists}" mouseRightMenu="true" contextMenuId="DeliveryMenu"
		 customTitleProperties="ListTransferNote"/>
	</#if>
</div>


<div id="alterpopupWindow" class="hide popup-bound">
	<div>${uiLabelMap.AddDeliveryTransferNote} - ${uiLabelMap.Transfer}: ${transfer.transferId}</div>
	<div>
		<input type="hidden" id="transferId" value="${transfer.transferId}"></input>
		<div class="row-fluid margin-top10">
			<div class="span4">
				<div class="row-fluid">	
					<div class="span5" style="text-align: right">
						<div> ${uiLabelMap.TransferType}: </div>
					</div>
					<div class="span7">
						<div class="green-label">${typeDescriptionTmp}</div>
					</div>
				</div>
				<div class="row-fluid">	
					<div class="span5" style="text-align: right">
						<div> ${uiLabelMap.RequireDeliveryDate}: </div>
					</div>
					<div class="span7">
						<div id="deliveryDate"class="green-label" style="float: left; width: 300px;" ></div>
					</div>
				</div>
			</div>
			<div class="span4">
				<div class="row-fluid">	
					<div class="span4" style="text-align: right">
						<div> ${uiLabelMap.FacilityFrom}: </div>
					</div>
					<div class="span7">
						<#if transfer.transferTypeId == "TRANS_INTERNAL">
							<div class="green-label">${originFacility.facilityName}</div>
		    			<#elseif transfer.transferTypeId = "TRANS_SALES_CHANNEL">
		    				<div class="green-label">${originFacility.facilityName} GT</div> 
		    			</#if>
					</div>
				</div>
				<div class="row-fluid">	
					<div class="span4" style="text-align: right">
						<div> ${uiLabelMap.FacilityTo}: </div>
					</div>
					<div class="span7">
						<#if transfer.transferTypeId == "TRANS_INTERNAL">
							<div class="green-label">${destFacility.facilityName}</div>
		    			<#elseif transfer.transferTypeId = "TRANS_SALES_CHANNEL">
		    				<div class="green-label">${destFacility.facilityName} MT</div>
		    			</#if>
					</div>
				</div>
				<div class="row-fluid hide">	
					<div class="span4" style="text-align: right">
						<div> ${uiLabelMap.OriginAddress}: </div>
					</div>
					<div class="span8">
						<div class="green-label">${originFacilityAddress.fullName}</div>
					</div>
				</div>
				<div class="row-fluid hide">	
					<div class="span4" style="text-align: right">
						<div> ${uiLabelMap.DestAddress}: </div>
					</div>
					<div class="span8">
						<div class="green-label">${destFacilityAddress.fullName}</div>
					</div>
				</div>
			</div>
			<div class="span4">
				<div class="row-fluid margin-bottom10">	
					<div class="span5" style="text-align: right">
						<div> ${uiLabelMap.EstimatedStartDate}: </div>
					</div>
					<div class="span7">	
						<div id="estimatedStartDate" style="width: 100%;"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span5" style="text-align: right">
						<div> ${uiLabelMap.EstimatedArrivalDate}: </div>
					</div>
					<div class="span7">	
						<div id="estimatedArrivalDate" style="width: 100%;"></div>
					</div>
				</div>
			</div>
		</div>
		<div class="margin-top10">
			<div class="margin-left10"><div id="jqxgridTransferItem"></div></div>
		</div> 
		<div class="form-action popup-footer">
			<button id="addButtonCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonClose}</button>
			<button id="addButtonSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>

<div id="jqxFileScanUpload" style="display: none">
	<div>
	    <span>
	        ${uiLabelMap.UploadFileScan}
	    </span>
	</div>
	<div style="overflow: hidden; text-align: center">
		<input multiple type="file" id="attachFile">
		</input>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="uploadCancelButton" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
					<button id="uploadOkButton" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				</div>
			</div>
		</div>
	</div>
</div>

<#include 'component://baselogistics/webapp/baselogistics/delivery/transferDeliveryCommon.ftl'>
<script type="text/javascript">

	$('#document').ready(function(){
		$("#alterpopupWindow").jqxWindow({
			maxWidth: 1500, minWidth: 950, width: 1300, modalZIndex: 10000, zIndex:10000, minHeight: 500, height: 550, maxHeight: 670, resizable: false, cancelButton: $("#addButtonCancel"), keyboardNavigation: true, keyboardCloseKey: 15,  isModal: true, autoOpen: false, modalOpacity: 0.7, theme:theme
		});
		
		$('#jqxFileScanUpload').jqxWindow({ width: 400, modalZIndex: 10000, height: 220, isModal: true, autoOpen: false });
		TransferDlvObj.initAttachFile();
		
		$('#estimatedStartDate').jqxDateTimeInput({width: 200, formatString: 'dd/MM/yyyy HH:mm:ss', disabled: false});
		$('#estimatedArrivalDate').jqxDateTimeInput({width: 200, formatString: 'dd/MM/yyyy HH:mm:ss', disabled: false});
/*		if (transferDate != null && (shipBeforeDate == null || shipAfterDate == null)){
			$("#deliveryDate").text(DatetimeUtilObj.formatFullDate(new Date(transferDate)));
			$("#deliveryDateDT").text(DatetimeUtilObj.formatFullDate(new Date(transferDate)));
			$('#estimatedStartDate').jqxDateTimeInput('val', new Date(transferDate));
			$('#estimatedArrivalDate').jqxDateTimeInput('val', new Date(transferDate));
		} else if (transferDate != null && shipBeforeDate != null && shipAfterDate != null){
			$("#deliveryDate").text(DatetimeUtilObj.formatFullDate(new Date(transferDate)) + " (" + DatetimeUtilObj.formatFullDate(new Date(shipAfterDate)) - DatetimeUtilObj.formatFullDate(new Date(shipBeforeDate)) + ")");
			$("#deliveryDateDT").text(DatetimeUtilObj.formatFullDate(new Date(transferDate)) + " (" + DatetimeUtilObj.formatFullDate(new Date(shipAfterDate)) - DatetimeUtilObj.formatFullDate(new Date(shipBeforeDate)) + ")");
			$('#estimatedStartDate').jqxDateTimeInput('val', new Date(shipAfterDate));
			$('#estimatedArrivalDate').jqxDateTimeInput('val', new Date(shipBeforeDate));
		} else if (transferDate == null && (shipBeforeDate != null && shipAfterDate != null)){
			$("#deliveryDate").text(DatetimeUtilObj.formatFullDate(new Date(shipAfterDate)) + " - " + DatetimeUtilObj.formatFullDate(new Date(shipBeforeDate)));
			$("#deliveryDateDT").text(DatetimeUtilObj.formatFullDate(new Date(shipAfterDate)) + " - " + DatetimeUtilObj.formatFullDate(new Date(shipBeforeDate)));
			$('#estimatedStartDate').jqxDateTimeInput('val', new Date(shipAfterDate));
			$('#estimatedArrivalDate').jqxDateTimeInput('val', new Date(shipBeforeDate));
		}
		*/
	});
</script>