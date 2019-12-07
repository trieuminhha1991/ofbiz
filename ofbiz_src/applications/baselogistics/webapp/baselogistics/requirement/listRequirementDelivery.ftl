<#include "script/requirementDeliveryScript.ftl"/>
<div id="updateOrderNoteComplete" style="display: none;">
	<div> ${uiLabelMap.UpdateOrderNoteSuccessfully}. </div>
</div>
<div id="containerNotify" style="width: 100%; overflow: auto;"> </div>
<div id="deliveries-tab" class="tab-pane<#if activeTab?exists && activeTab == "deliveries-tab"> active</#if>">
<#assign columnlist="
		{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
		    groupable: false, draggable: false, resizable: false,
		    datafield: '', columntype: 'number', width: 50,
		    cellsrenderer: function (row, column, value) {
		        return '<div style=margin:4px;>' + (value + 1) + '</div>';
		    }
		},
		{ text: '${uiLabelMap.DeliveryId}', pinned: true, dataField: 'deliveryId', width: 145, editable:false, 
			cellsrenderer: function(row, column, value){
				return '<span><a href=\"javascript:ReqDlvObj.showDetailPopup(&#39;'+value+'&#39;);\"> ' + value  + '</a></span>';
			}
		},
		{ text: '${uiLabelMap.ExportFromFacility}', dataField: 'originFacilityName', minwidth: 150, editable:false,
			 cellsrenderer: function(row, column, value){
				 return '<span title=\"' + value + '\">' + value + '</span>';
			 }
		 },
		 { text: '${uiLabelMap.Status}', dataField: 'statusId', width: 145, editable:false, filtertype: 'checkedlist',
			 cellsrenderer: function(row, column, value){
				 for(var i = 0; i < statusData.length; i++){
					 if(statusData[i].statusId == value){
						 return '<span title=' + value + '>' + statusData[i].description + '</span>'
					 }
				 }
			 },
			 createfilterwidget: function (column, columnElement, widget) {
				 var filterDataAdapter = new $.jqx.dataAdapter(statusData, {
					 autoBind: true
				 });
				 var records = filterDataAdapter.records;
				 widget.jqxDropDownList({source: records, displayMember: 'statusId', valueMember: 'statusId',
					 renderer: function(index, label, value){
						 if (statusData.length > 0) {
							 for(var i = 0; i < statusData.length; i++){
								 if(statusData[i].statusId == value){
									 return '<span>' + statusData[i].description + '</span>';
								 }
							 }
						 }
						 return value;
					 }
 				});
	 		}
		 },
		 { text: '${uiLabelMap.OriginAddress}', dataField: 'originAddress', minwidth: 150, editable:false,
			 cellsrenderer: function(row, column, value){
				 if (value === undefined || value === null || value === '') return '<span>_NA_</span>';
				 return '<span title=\"' + value + '\">' + value + '</span>';
			 }
		 },
		 { text: '${uiLabelMap.DestAddress}', dataField: 'destAddress', minwidth: 150, editable:false,
			 cellsrenderer: function(row, column, value){
				 if (value === undefined || value === null || value === '') return '<span>_NA_</span>';
				 return '<span title=\"' + value + '\">' + value + '</span>'
			 }
		 },
		 { text: '${uiLabelMap.RequireDeliveryDate}', dataField: 'deliveryDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false, cellsalign: 'right',
			 cellsrenderer: function(row, column, value){
				 if (!value){
					 return '<span style=\"text-align: right\" title=\"_NA_\">_NA_</span>';
				 } else {
					 return '<span style=\"text-align: right\">'+ ReqDlvObj.formatFullDate(value)+'</span>';
				 }
			 }
		 },
		 { text: '${uiLabelMap.EstimatedExportDate}', dataField: 'estimatedStartDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false, cellsalign: 'right',
			 cellsrenderer: function(row, column, value){
				 if (!value){
					 return '<span style=\"text-align: right\" title=\"_NA_\">_NA_</span>';
				 } else {
					 return '<span style=\"text-align: right\">'+ ReqDlvObj.formatFullDate(value)+'</span>';
				 }
			 }
		 },
		 { text: '${uiLabelMap.EstimatedDeliveryDate}', dataField: 'estimatedArrivalDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false, cellsalign: 'right',
			 cellsrenderer: function(row, column, value){
				 if (!value){
					 return '<span style=\"text-align: right\" title=\"_NA_\">_NA_</span>';
				 } else {
					 return '<span style=\"text-align: right\">'+ ReqDlvObj.formatFullDate(value)+'</span>';
				 }
			 }
		 },
		 { text: '${uiLabelMap.ActualExportedDate}', dataField: 'actualStartDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false, cellsalign: 'right',
			 cellsrenderer: function(row, column, value){
				 if (!value){
					 return '<span style=\"text-align: right\" title=\"_NA_\">_NA_</span>';
				 } else {
					 return '<span style=\"text-align: right\">'+ ReqDlvObj.formatFullDate(value)+'</span>';
				 }
			 }
		 },
		 { text: '${uiLabelMap.ActualDeliveredDate}', dataField: 'actualArrivalDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false, cellsalign: 'right',
			 cellsrenderer: function(row, column, value){
				 if (!value){
					 return '<span style=\"text-align: right\" title=\"_NA_\">_NA_</span>';
				 } else {
					 return '<span style=\"text-align: right\">'+ ReqDlvObj.formatFullDate(value)+'</span>';
				 }
			 }
		 },
		 { text: '${uiLabelMap.DeliveryBatchNumber}', dataField: 'shipmentId', width: 120, editable:false,
			cellsrenderer: function(row, column, value){
				if (!value) return '<span style=\"text-align: right\" title=\"_NA_\">_NA_</span>';
				return '<span title=\"' + value + '\">' + value + '</span>'
			 }
		 },
		 { text: '${uiLabelMap.CreatedDate}', dataField: 'createDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false, cellsalign: 'right',
			 cellsrenderer: function(row, column, value){
				 if (!value){
					 return '<span style=\"text-align: right\" title=\"_NA_\">_NA_</span>';
				 } else {
					 return '<span style=\"text-align: right\">'+ ReqDlvObj.formatFullDate(value)+'</span>';
				 }
			 }
	 	}"/>
<#assign dataField="[{ name: 'deliveryId', type: 'string' },
				{ name: 'deliveryTypeId', type: 'string' },
				{ name: 'statusId', type: 'string' },
             	{ name: 'partyIdTo', type: 'string' },
             	{ name: 'destContactMechId', type: 'string' },
             	{ name: 'partyIdFrom', type: 'string' },
				{ name: 'originContactMechId', type: 'string' },
				{ name: 'requirementId', type: 'string' },
             	{ name: 'originProductStoreId', type: 'string' },
             	{ name: 'originFacilityId', type: 'string' },
             	{ name: 'destFacilityId', type: 'string' },
             	{ name: 'destFacilityName', type: 'string' },
             	{ name: 'originFacilityName', type: 'string' },
				{ name: 'createDate', type: 'date', other: 'Timestamp' },
				{ name: 'deliveryDate', type: 'date', other: 'Timestamp' },
				{ name: 'estimatedStartDate', type: 'date', other: 'Timestamp' },
				{ name: 'estimatedArrivalDate', type: 'date', other: 'Timestamp' },
				{ name: 'actualStartDate', type: 'date', other: 'Timestamp' },
				{ name: 'actualArrivalDate', type: 'date', other: 'Timestamp' },
				{ name: 'totalAmount', type: 'number' },
				{ name: 'no', type: 'string' },
				{ name: 'shipmentId', type: 'string' },
				{ name: 'destAddress', type: 'string' },
				{ name: 'originAddress', type: 'string' },
				{ name: 'defaultWeightUomId', type: 'string' }]"/>

<#if hasOlbPermission("MODULE", "LOG_DELIVERY", "CREATE") && orderHeader?has_content && orderHeader.statusId == "ORDER_APPROVED" && orderHeader.isFavorDelivery != "Y">
<@jqGrid filtersimplemode="true" id="jqxgridDelivery" addrefresh="true" usecurrencyfunction="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="true" filterable="true" alternativeAddPopup="alterpopupWindow" editable="true" 
	 url="jqxGeneralServicer?sname=getRequirementDelivery&requirementId=${parameters.requirementId?if_exists}&deliveryTypeId=${deliveryTypeId?if_exists}" createUrl="jqxGeneralServicer?sname=createDelivery&jqaction=C" editmode="dblclick"
	 addColumns="listOrderItems(java.util.List);requirementId;shipmentId;currencyUomId;statusId;destFacilityId;originProductStoreId;partyIdTo;partyIdFrom;createDate(java.sql.Timestamp);destContactMechId;originContactMechId;originFacilityId;deliveryDate(java.sql.Timestamp);deliveryTypeId[DELIVERY_SALES];no;estimatedStartDate(java.sql.Timestamp);estimatedArrivalDate(java.sql.Timestamp);defaultWeightUomId;deliveryId;shipmentMethodTypeId;carrierPartyId" 	 
	 updateUrl="" editColumns="" customTitleProperties="DeliveryNoteCommon" 
	 jqGridMinimumLibEnable="true"
	 customcontrol1="icon-plus-sign@${uiLabelMap.QuickCreate}@javascript:ReqDlvObj.showPopupSelectFacility('${parameters.requirementId?if_exists}');"
 />
<#else>
<@jqGrid filtersimplemode="true" id="jqxgridDelivery" addrefresh="true" usecurrencyfunction="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="false" filterable="true" alternativeAddPopup="alterpopupWindow" editable="true" 
	 url="jqxGeneralServicer?sname=jqGetRequirementDelivery&requirementId=${parameters.requirementId?if_exists}&deliveryTypeId=${deliveryTypeId?if_exists}"
	 customTitleProperties="DeliveryNoteCommon" selectionmode="checkbox"
	 jqGridMinimumLibEnable="true"/>
</#if>
</div>
<div id="alterpopupWindow" class="hide popup-bound">
	<div>${uiLabelMap.AddNewSaleDelivery}</div>
	<input type="hidden" name="requirementId"/>
	<input type="hidden" name="currencyUomId"/>
	<input type="hidden" name="statusId"/>
	<input type="hidden" name="orderDate"/>
	<input type="hidden" name="originProductStoreId" value=""/>
	<input type="hidden" id="deliveryTypeId" value="DELIVERY_SALES"/>
	<div class='form-window-container'>
		<div id="containerPopupNotify"></div>
		<div class='form-window-content'>
	    	<h4 class="row header smaller lighter blue" style="margin: 5px 0px 20px 20px !important;font-weight:500;line-height:20px;font-size:18px;">
	            ${uiLabelMap.GeneralInfo}
	        </h4>
	        <div class="row-fluid">
	    		<div class="span4">
	    			<div class="row-fluid margin-bottom10">	
	    				<div class="span5" style="text-align: right">
	    					<div style="margin-right: 10px"> ${uiLabelMap.RequirementId} </div>
						</div>
						<div class="span7">	
							<div id="requirementIdDis" style="width: 100%;" class="green-label"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
	    					<div class="asterisk"> ${uiLabelMap.Sender} </div>
						</div>
						<div class="span7">	
							<div id="partyIdFrom" style="width: 100%;" class="green-label"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
	    				<div class="span5" style="text-align: right">
	    					<div class="asterisk"> ${uiLabelMap.OriginFacility}</div>
						</div>
						<div class="span7">	
					        <div id="originFacilityId" class="green-label"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
	    					<div class="asterisk"> ${uiLabelMap.OriginAddress} </div>
						</div>
						<div class="span7">	
						<#if hasOlbPermission("MODULE", "LOG_DELIVERY", "ADMIN")>
							<div id="originContactMechId" style="width: 100%;" class="green-label pull-left"></div>
						<#else>
							<div id="originContactMechId" style="width: 100%;" class="green-label pull-left"></div>
						</#if>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
	    					<div class="asterisk"> ${uiLabelMap.ShipmentMethod} </div>
						</div>
						<div class="span7">	
						<div id="shipmentMethodTypeId" style="width: 100%;" class="green-label pull-left"></div>
						</div>
					</div>
	    		</div>
	    		<div class="span4">
		    		<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
							<div style="margin-right: 10px"> ${uiLabelMap.DeliveryNoteId} </div>
						</div>
						<div class="span7">	
							<input id="deliveryId" style="width: 100%;" type="text"/>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
	    				<div class="span5" style="text-align: right"> 
	    					<div style="margin-right: 10px"> ${uiLabelMap.DeliveryBatchNumber} </div>
						</div>
						<div class="span7">	
							<input id="noNumber" style="width: 100%;" type="text"/>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
	    					<div class="asterisk"> ${uiLabelMap.Receiver} </div>
						</div>
						<div class="span7">	
							<div id="partyIdTo" style="width: 100%;" class="green-label"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
	    					<div class="asterisk"> ${uiLabelMap.CustomerAddress} </div>
						</div>
						<div class="span7">	
							<div id="destContactMechId" style="width: 100%;" class="green-label pull-left"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
							<div class="asterisk"> ${uiLabelMap.CarrierParty} </div>	
						</div>
						<div class="span7">	
							<div id="partyId" style="width: 100%;" class="green-label pull-left"></div>
						</div>
					</div>
	    		</div>
	    		<div class="span4">
		    		<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
							<div style="margin-right: 10px"> ${uiLabelMap.TotalWeight} </div>
						</div>
						<div class="span7">	
							<div class="row-fluid">
								<div class="span5">
									<div id="totalProductWeight" class="green-label"></div>
								</div>
								<div class="span7">
									<div id="listWeightUomId" class="green-label"></div>
								</div>
							</div>
						</div>
					</div>
		    		<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
							<div style="margin-right: 10px"> ${uiLabelMap.RequireDeliveryDate} </div>
						</div>
						<div class="span7">	
							<div id="deliveryDate" style="width: 100%;"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
	    					<div style="margin-right: 10px"> ${uiLabelMap.EstimatedStartDate} </div>
						</div>
						<div class="span7">	
							<div id="estimatedStartDate" style="width: 100%;"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
	    					<div style="margin-right: 10px"> ${uiLabelMap.EstimatedArrivalDate}</div>
						</div>
						<div class="span7">	
							<div id="estimatedArrivalDate" style="width: 100%;"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
						</div>
						<div class="span7">	
						</div>
					</div>
	    		</div>
			</div>
			<div class="row-fluid">
	    		<h4 class="row header smaller lighter blue" style="margin-left: 20px !important;font-weight:500;line-height:20px;font-size:18px;">
					${uiLabelMap.ListProduct}
				</h4>
				<div style="margin-left: 20px;"><div id="jqxgridRequirementItem"></div></div>
			</div>
		</div>
		<div class="form-action popup-footer">
	        <button id="alterCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonClose}</button>
	        <button id="alterSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>
<div id="popupDeliveryDetailWindow" class="hide popup-bound">
	<div id="titleDetailId">${uiLabelMap.DeliveryNoteCommon}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
	    	<div id="popupContainerNotify" style="width: 100%; overflow: auto;"></div>
			<h4 class="row header smaller lighter blue" style="margin-left: 20px !important;font-weight:500;line-height:20px;font-size:18px;">
				${uiLabelMap.GeneralInfo}
				<a style="float:right;font-size:14px; cursor: pointer;" id="printPDF" target="_blank" data-rel="tooltip" title="${uiLabelMap.ExportPdf}" data-placement="bottom" data-original-title="${uiLabelMap.PDF}"><i class="fa-file-pdf-o"></i>&nbsp;${uiLabelMap.PDF}</a>
				<div style="float:right;font-size:14px;margin-right:15px" id="scanfile"></div>
			</h4>
			<div class="row-fluid">
				<div class="span4">
					<div class="row-fluid">
						<div class="span5" style="text-align: right;">${uiLabelMap.DeliveryId}:</div>
						<div class="span7"><div id="deliveryIdDT" class="green-label"></div></div>
					</div>
				</div>
				<div class="span4">
					<div class="row-fluid">
						<div class="span5" style="text-align: right;">${uiLabelMap.RequirementId}:</div>
		    		    <div class="span7"><div id="requirementIdDT"class="green-label"></div></div>
					</div>
				</div>
				<div class="span4">
		    		<div class="row-fluid">
						<div class="span5" style="text-align: right;">${uiLabelMap.RequireDeliveryDate}:</div>
		    		    <div class="span7"><div id="deliveryDateDT" class="green-label"></div></div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span4">
					<div class="row-fluid">
						<div class="span5" style="text-align: right;">${uiLabelMap.Status}:</div>
					    <div class="span7"><div id="statusIdDT" class="green-label"></div></div>
					</div>
				</div>
				<div class="span4">
					<div class="row-fluid">
						<div class="span5" style="text-align: right;">${uiLabelMap.DeliveryReason}:</div>
					    <div class="span7"><div id="deliveryTypeDT" class="green-label"></div></div>
					</div>
				</div>
				<div class="span4">
					<div class="row-fluid">
						<div class="span5" id="actualStartLabel" style="text-align: right;" class="asterisk">${uiLabelMap.ActualExportedDate}:</div>
					    <div class="span7">
					    	<div id="actualStartDateDis" class="green-label"></div>
				    	</div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
	    		<div class="span4">
					<div class="row-fluid">
						<div class="span5" style="text-align: right;">${uiLabelMap.ExportFromFacility}:</div>
					    <div class="span7"><div id="originFacilityIdDT" class="green-label"></div></div>
					</div>
				</div>
				<div class="span4">
					<div class="row-fluid">
						<div class="span5" style="text-align: right;">${uiLabelMap.Shipment}:</div>
		    		    <div class="span7" style="text-align: left;"><div id="noDT" class="green-label"></div></div>
					</div>
				</div>
				<div class="span4">
					<div class="row-fluid">
						<div class="span5" id="actualArrivalLabel" style="text-align: right;" class="asterisk">${uiLabelMap.ActualDeliveredDate}:</div>
					    <div class="span7">
					    	<div id="actualArrivalDateDis" class="green-label"></div>
					    </div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
	    		<h4 class="row header smaller lighter blue" style="margin-left: 20px !important;font-weight:500;line-height:20px;font-size:18px;">
					${uiLabelMap.ListProduct}
				</h4>
				<div style="margin-left: 20px"><div id="jqxgridDlvItem"></div></div>
			</div>
			<div class="form-action popup-footer">
	            <button id="alterCancel2" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonClose}</button>
	        </div>
		</div>
	</div>
</div>

<div id="jqxFileScanUpload" style="display: none" class="popup-bound">
	<div>
	    <span>
	        ${uiLabelMap.AttachDeliveredScan}
	    </span>
	</div>
	<div style="overflow: hidden; text-align: center">
		<input multiple type="file" id="attachFile">
		</input>
		<div class="form-action popup-footer">
			<button id="uploadCancelButton" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button id="uploadOkButton" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>
<div id="jqxFileScanExptUpload" style="display: none" class="popup-bound">
	<div>
	    <span>
	        ${uiLabelMap.AttachExportedScan}
	    </span>
	</div>
	<div style="overflow: hidden; text-align: center">
		<input multiple type="file" id="attachFileExpt">
		</input>
		<div class="form-action popup-footer">
			<button id="uploadExptCancelButton" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button id="uploadExptOkButton" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>
<script type="text/javascript">
	function loadDeliveryItem(valueDataSoure){
		var sourceProduct =
		    {
		        datafields:[{ name: 'deliveryId', type: 'string' },
		                 	{ name: 'deliveryItemSeqId', type: 'string' },
		                 	{ name: 'fromReqItemSeqId', type: 'string' },
		                 	{ name: 'fromRequirementId', type: 'string' },
		                 	{ name: 'productId', type: 'string' },
		                 	{ name: 'productCode', type: 'string' },
		                 	{ name: 'productName', type: 'string' },
		                 	{ name: 'quantityUomId', type: 'string' },
		                 	{ name: 'actualExportedQuantity', type: 'number' },
		                 	{ name: 'actualDeliveredQuantity', type: 'number' },
		                 	{ name: 'statusId', type: 'string' },
		                 	{ name: 'batch', type: 'string' },
		                 	{ name: 'quantity', type: 'number' },
		                 	{ name: 'inventoryItemId', type: 'string' },
							{ name: 'actualExpireDate', type: 'date', other: 'Timestamp'},
							{ name: 'actualManufacturedDate', type: 'date', other: 'Timestamp'},
							{ name: 'expireDate', type: 'date', other: 'Timestamp'},
		                 	{ name: 'deliveryStatusId', type: 'string'},
							{ name: 'weight', type: 'number'},
							{ name: 'productWeight', type: 'number'},
							{ name: 'weightUomId', type: 'String'},
							{ name: 'defaultWeightUomId', type: 'String'}],
		        localdata: valueDataSoure,
		        datatype: "array",
		    };
		    var dataAdapterProduct = new $.jqx.dataAdapter(sourceProduct);
		    $("#jqxgridDlvItem").jqxGrid({
	        source: dataAdapterProduct,
	        filterable: false,
	        showfilterrow: false,
	        theme: 'olbius',
	        rowsheight: 26,
	        width: '100%',
	        height: 210,
	        enabletooltips: true,
	        autoheight: false,
	        pageable: true,
	        pagesize: 5,
	        editable: false,
	        columnsresize: true,
	        localization: getLocalization(),
		        columns: [	
						{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true, 
						    groupable: false, draggable: false, resizable: false,
						    datafield: '', columntype: 'number', width: 50,
						    cellsrenderer: function (row, column, value) {
						        return '<span style=margin:4px;>' + (value + 1) + '</span>';
						    }
						},
						{ text: '${uiLabelMap.FromOrderItemSeqId}', hidden: true, datafield: 'fromOrderItemSeqId' },
						{ text: '${uiLabelMap.ProductCode}', dataField: 'productCode',  width: 200, editable: false },
						{ text: '${uiLabelMap.ProductName}', dataField: 'productName', minwidth: 200, editable: false,
							cellsrenderer: function(row, column, value){
								var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
								if (!data.productCode){
									return '<span style=\"text-align: right\">...</span>';
								}
							}
						},
						{ text: '${uiLabelMap.RequiredNumber}', dataField: 'quantity', cellsalign: 'right', width: 150, editable: false,
							cellsrenderer: function(row, column, value){
								var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
								if (value === null || value === undefined || value === ""){
									if (data.productCode){
										return '<span style=\"text-align: right;\">_NA_</span>';
									} else {
										return '<span style=\"text-align: right;\">...</span>';
									}
								}
								var descriptionUom = data.quantityUomId;
								for(var i = 0; i < quantityUomData.length; i++){
									if(data.quantityUomId == quantityUomData[i].uomId){
										descriptionUom = quantityUomData[i].description;
								 	}
								}
								return '<span style=\"text-align: right\">' + value +' (' + descriptionUom +  ')</span>';
							 }
						},
						{ text: '${uiLabelMap.ExecutedExpireDate}', dataField: 'actualExpireDate', width: 150, cellsformat: 'dd/MM/yyyy', editable: false, cellsalign: 'right',
						    cellsrenderer: function(row, column, value){
						    	if (!value){
									 return '<span style=\"text-align: right\" title=\"_NA_\">_NA_</span>';
								 } else {
									 return '<span style=\"text-align: right\">'+ ReqDlvObj.getFormattedDate(value)+'</span>';
								 }
						    }
						}, 	
						{ text: '${uiLabelMap.ExecutedQuantity}', dataField: 'actualExportedQuantity', columntype: 'numberinput', width: 150, cellsalign: 'right', editable: true, sortable: false,
						    cellsrenderer: function (row, column, value){
						    	var tmp = null;
							 	var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
							 	if (value === null || value === undefined || value === ''){
							 		if (data.productCode){
							 			if (data.statusId == 'DELI_ITEM_APPROVED'){
							 				var id = data.uid;
	                                		var orderQty = data.quantity;
									 		$('#jqxgridDlvItem').jqxGrid('setcellvaluebyid', id, 'actualExportedQuantity', orderQty);
									 		return '<span style=\"text-align: right;\"  title=' + orderQty.toLocaleString('${localeStr}') + '>' + orderQty.toLocaleString('${localeStr}') + '</span>';
							 				
							 			} else {
							 				return '<span style=\"text-align: right;\"  title=' + 0 + '>' + 0 + '</span>';
							 			}
							 		} else {
										return '<span style=\"text-align: right;\">...</span>';
									}
							 	}
							 	if (data.statusId == 'DELI_ITEM_APPROVED'){
							 		return '<span style=\"text-align: right;\"  title=' + value.toLocaleString('${localeStr}') + '>' + value.toLocaleString('${localeStr}') + '</span>'
							 	} else {
							 		return '<span style=\"text-align: right\" title=' + value.toLocaleString('${localeStr}') + '>' + value.toLocaleString('${localeStr}') + '</span>'
							 	}
					    	}
						 },
						 { text: '${uiLabelMap.RequiredExpireDate}', dataField: 'expireDate', width: 120, cellsformat: 'dd/MM/yyyy HH:mm:ss', editable: false, cellsalign: 'right',
							 cellsrenderer: function(row, column, value){
						    	if (!value){
									 return '<span style=\"text-align: right\" title=\"_NA_\">_NA_</span>';
								 } else {
									 return '<span style=\"text-align: right\">'+ ReqDlvObj.getFormattedDate(value)+'</span>';
								 }
							 }
						},
						{ text: '${uiLabelMap.Status}', dataField: 'statusId', width: 120, editable: false,
							 cellsrenderer: function(row, column, value){
								 var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
								 if (value === null || value === undefined || value === ''){
									 if (data.productCode){
										 return '<span style=\"text-align: right\">_NA_</span>';
									 } else {
										 return '<span style=\"text-align: right;\">...</span>';
									 }
								 } else {
									 for(var i = 0; i < dlvItemStatusData.length; i++){
										 if(value == dlvItemStatusData[i].statusId){
											 return '<span title=' + value + '>' + dlvItemStatusData[i].description + '</span>';
										 }
									 }
								 }
							 }
					 	}
	                 ]
		    });
	}
	
	function loadRequirementItem(valueDataSoure){
		var sourceReqItem =
		    {
	        datafields:[{ name: 'requirementId', type: 'string' },
	                    { name: 'reqItemSeqId', type: 'string' },
	                 	{ name: 'productId', type: 'string' },
	                 	{ name: 'productCode', type: 'string' },
	                 	{ name: 'productName', type: 'string' },
	                 	{ name: 'expireDate', type: 'date', other: 'Timestamp' },
	                 	{ name: 'requiredQuantity', type: 'number' },
	                 	{ name: 'quantity', type: 'number' },
						{ name: 'quantityUomId', type: 'string'},
	                 	{ name: 'facilityId', type: 'string' },
	                 	{ name: 'requiredQuantityTmp', type: 'string' },
						{ name: 'unitPrice', type: 'number'},
						{ name: 'quantityOnHandTotal', type: 'number'},
						{ name: 'availableToPromiseTotal', type: 'number'},
						{ name: 'comments', type: 'string' }],
	        localdata: valueDataSoure,
	        datatype: "array",
	    };
	    var dataAdapterReqItem = new $.jqx.dataAdapter(sourceReqItem);
	    $("#jqxgridRequirementItem").jqxGrid({
        source: dataAdapterReqItem,
        filterable: false,
        showfilterrow: false,
        theme: 'olbius',
        rowsheight: 26,
        width: '100%',
        height: 210,
        enabletooltips: true,
        autoheight: false,
        pageable: true,
        pagesize: 5,
        editable: true,
        columnsresize: true,
        selectionmode: 'checkbox',
        localization: getLocalization(),
        columns: [	
				{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true, 
				    groupable: false, draggable: false, resizable: false,
				    datafield: '', columntype: 'number', width: 50,
				    cellsrenderer: function (row, column, value) {
				        return '<span style=margin:4px;>' + (value + 1) + '</span>';
				    }
				},
				{ text: '${uiLabelMap.ProductId}', dataField: 'productCode', width: 120, editable: false, pinned: true},
				{ text: '${uiLabelMap.ProductName}', dataField: 'productName', width: 205, filtertype:'input', editable: false,},
				{ text: '${uiLabelMap.EXPRequired}', dataField: 'expireDate', width: 120, cellsformat: 'dd/MM/yyyy', cellsalign: 'right', filtertype: 'range', editable: false,
					 cellsrenderer: function(row, column, value){
						 if (!value){
							 return '<span style=\"text-align: right\" title=\"_NA_\">_NA_</span>';
						 } else {
							 return '<span style=\"text-align: right\">'+ PODlvObj.formatFullDate(value)+'</span>';
						 }
					 },
					 rendered: function(element){
				    	$(element).jqxTooltip({content: '${StringUtil.wrapString(uiLabelMap.RequiredExpireDate)}', theme: 'orange' });
					 }
				},
				{ text: '${uiLabelMap.QtyRequired}', dataField: 'requiredQuantity', width: 120, editable: false, 
					cellsrenderer: function(row, column, value){
						var data = $('#jqxgridRequirementItem').jqxGrid('getrowdata', row);
						var description = value;
						for(var i = 0; i < uomData.length; i++){
							if(uomData[i].quantityUomId == data.baseQuantityUomId){
								description = uomData[i].description;
							}
						}
						return '<span style=\"text-align: right\" title=' + value.toLocaleString('${localeStr}') + '>' + value.toLocaleString('${localeStr}') +' ('+ description + ')</span>';
					},
					rendered: function(element){
				    	$(element).jqxTooltip({content: '${StringUtil.wrapString(uiLabelMap.RequiredNumber)}', theme: 'orange' });
				    }
				},
				{ text: '${uiLabelMap.QuantityCreate}', dataField: 'quantity', columntype: 'numberinput', width: 120, editable: true,
					cellsrenderer: function(row, column, value){
						var data = $('#jqxgridRequirementItem').jqxGrid('getrowdata', row);
						var requiredQuantityTmp = data.requiredQuantityTmp;
						if (data.requiredQuantity && data.createdQuantity){
							requiredQuantityTmp = data.requiredQuantity - data.createdQuantity;
						}
						var description="";
						for(var i = 0; i < uomData.length; i++){
							if(uomData[i].quantityUomId == data.baseQuantityUomId){
								description = uomData[i].description;
							}
						}
						return '<span style=\"text-align: right\"  title=' + value.toLocaleString('${localeStr}') + '>' + value.toLocaleString('${localeStr}') +' ('+ description + ')</span>';
					},
					initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
				        editor.jqxNumberInput({ decimalDigits: 0});
				        var data = $('#jqxgridRequirementItem').jqxGrid('getrowdata', row);
				        if (data.requiredQuantity && data.createdQuantity){
				        	editor.jqxNumberInput('val', data.requiredQuantity - data.createdQuantity);
				        }
				    },
				    validation: function (cell, value) {
				        var data = $('#jqxgridRequirementItem').jqxGrid('getrowdata', cell.row);
				        if (value > (data.requiredQuantity - data.createdQuantity)){
				            return { result: false, message: '${uiLabelMap.ExportValueLTZRequireValue}'};
				        } else{
				        	if (value <= 0){
				        		return { result: false, message: '${uiLabelMap.ExportValueMustBeGreaterThanZero}'};
				        	} else {
				        		return true;
				        	}
				        }
				    }
				}
			]
	    });
	}
	
</script>