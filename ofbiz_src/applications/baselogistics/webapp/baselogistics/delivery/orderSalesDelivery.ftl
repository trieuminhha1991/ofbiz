 <#assign acceptFile="image/*"/>
<#assign entityName="Delivery"/>
<#include "component://basesalesmtl/webapp/basesalesmtl/common/fileAttachment.ftl"/>
 <#include "script/salesDeliveryScript.ftl"/>
<div id="notifyIdQuickCreateError" style="display: none;">
	<div>
		${uiLabelMap.NoFacilityEnoughProduct}. ${uiLabelMap.PleaseCreateDeliveryByHand}. 
	</div>
</div>
<div id="notifyCreateSuccessful" style="display: none;">
	<div>
		${uiLabelMap.CreateSuccessfully}. 
	</div>
</div>
<div id="notifyUpdateDeliverySuccessful" style="display: none;">
	<div>
		${uiLabelMap.UpdateSuccessfully}.
	</div>
</div>
<div id="notifyIdNotHaveStorekeeper" style="display: none;">
	<div>
		${uiLabelMap.NoStorekeeperOfSeletedFacility}.  
	</div>
</div>
<div id="notifyIdCheckDelivery" style="display: none;">
	<div>
		${uiLabelMap.DeliveryNoteIdExisted}. 
	</div>
</div>
<div id="updateOrderNoteComplete" style="display: none;">
	<div>
		${uiLabelMap.UpdateOrderNoteSuccessfully}. 
	</div>
</div>
	<div id="containerNotify" style="width: 100%; overflow: auto;">
</div>
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
					var data = $('#jqxgridDelivery').jqxGrid('getrowdata', row);
					return '<span><a href=\"javascript:SalesDlvObj.showDetailDelivery(&#39;'+value+'&#39;);\"> ' + value  + '</a></span>';
				 }
				},
				{ text: '${uiLabelMap.ExportFromFacility}', dataField: 'originFacilityName', minwidth: 150, editable:false,
					 cellsrenderer: function(row, column, value){
						 return '<span title=\"' + value + '\">' + value + '</span>';
					 },
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
							widget.jqxDropDownList('checkAll');
			   			},
					 },
				 { text: '${uiLabelMap.CustomerAddress}', dataField: 'destAddress', minwidth: 150, editable:false,
					 cellsrenderer: function(row, column, value){
						return '<span title=\"' + value + '\">' + value + '</span>'
					 }, 
				 },
				 { text: '${uiLabelMap.RequireDeliveryDate}', dataField: 'deliveryDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false, cellsalign: 'right',
					 cellsrenderer: function(row, column, value){
						 if (!value){
							 return '<span style=\"text-align: right\"></span>';
						 } else {
							 return '<span style=\"text-align: right\">'+ SalesDlvObj.formatFullDate(value)+'</span>';
						 }
					 }, 
				 },
				{ text: '${uiLabelMap.EstimatedExportDate}', dataField: 'estimatedStartDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false, cellsalign: 'right',
					 cellsrenderer: function(row, column, value){
						 if (!value){
							 return '<span style=\"text-align: right\"></span>';
						 } else {
							 return '<span style=\"text-align: right\">'+ SalesDlvObj.formatFullDate(value)+'</span>';
						 }
					 }, 
				},
				{ text: '${uiLabelMap.EstimatedDeliveryDate}', dataField: 'estimatedArrivalDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false, cellsalign: 'right',
					 cellsrenderer: function(row, column, value){
						 if (!value){
							 return '<span style=\"text-align: right\"></span>';
						 } else {
							 return '<span style=\"text-align: right\">'+ SalesDlvObj.formatFullDate(value)+'</span>';
						 }
					 }, 
				},
				{ text: '${uiLabelMap.ActualExportedDate}', dataField: 'actualStartDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false, cellsalign: 'right',
					 cellsrenderer: function(row, column, value){
						 if (!value){
							 return '<span style=\"text-align: right\"></span>';
						 } else {
							 return '<span style=\"text-align: right\">'+ SalesDlvObj.formatFullDate(value)+'</span>';
						 }
					 }, 
				},
				{ text: '${uiLabelMap.ActualDeliveredDate}', dataField: 'actualArrivalDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false, cellsalign: 'right',
					 cellsrenderer: function(row, column, value){
						 if (!value){
							 return '<span style=\"text-align: right\"></span>';
						 } else {
							 return '<span style=\"text-align: right\">'+ SalesDlvObj.formatFullDate(value)+'</span>';
						 }
					 }, 
				},
				{ text: '${uiLabelMap.DeliveryBatchNumber}', dataField: 'shipmentId', width: 120, editable:false, hidden: true,
					cellsrenderer: function(row, column, value){
						return '<span title=\"' + value + '\">' + value + '</span>'
					 },
				 },
				 { text: '${uiLabelMap.CreatedDate}', dataField: 'createDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false, cellsalign: 'right',
					 cellsrenderer: function(row, column, value){
						 if (!value){
							 return '<span style=\"text-align: right\"></span>';
						 } else {
							 return '<span style=\"text-align: right\">'+ SalesDlvObj.formatFullDate(value)+'</span>';
						 }
					 }, 	 
				 },
				 "/>
<#assign dataField="[{ name: 'deliveryId', type: 'string' },
				{ name: 'deliveryTypeId', type: 'string' },
				{ name: 'picklistBinId', type: 'string' },
				{ name: 'statusId', type: 'string' },
             	{ name: 'partyIdTo', type: 'string' },
             	{ name: 'destContactMechId', type: 'string' },
             	{ name: 'partyIdFrom', type: 'string' },
				{ name: 'originContactMechId', type: 'string' },
				{ name: 'orderId', type: 'string' },
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
				{ name: 'defaultWeightUomId', type: 'string' },
	 		 	]"/>
<#if !createdDone && hasOlbPermission("MODULE", "LOG_DELIVERY", "CREATE") && orderHeader?has_content && orderHeader.statusId == "ORDER_APPROVED">
	<#if orderHeader.isFavorDelivery?exists>
		<#if orderHeader.isFavorDelivery != "Y">
		<@jqGrid filtersimplemode="true" id="jqxgridDelivery" addrefresh="true" usecurrencyfunction="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="true" filterable="true" alternativeAddPopup="alterpopupWindow" editable="true" 
			 url="jqxGeneralServicer?sname=getListDelivery&fromOrderId=${parameters.orderId?if_exists}&deliveryTypeId=DELIVERY_SALES" createUrl="jqxGeneralServicer?sname=createDelivery&jqaction=C" editmode="dblclick"
			 addColumns="listOrderItems(java.util.List);orderId;shipmentId;currencyUomId;statusId;destFacilityId;originProductStoreId;partyIdTo;partyIdFrom;createDate(java.sql.Timestamp);destContactMechId;originContactMechId;originFacilityId;deliveryDate(java.sql.Timestamp);deliveryTypeId[DELIVERY_SALES];no;estimatedStartDate(java.sql.Timestamp);estimatedArrivalDate(java.sql.Timestamp);defaultWeightUomId;deliveryId;shipmentMethodTypeId;carrierPartyId" 	 
			 updateUrl="" editColumns="" functionAfterAddRow="SalesDlvObj.afterAddDelivery()" customTitleProperties="ListDelivery" _customMessErr="SalesDlvObj.customMess"
			 jqGridMinimumLibEnable="true" selectionmode="singlecell" mouseRightMenu="true" contextMenuId="DeliveryMenu"
			 customcontrol1="icon-plus@${uiLabelMap.QuickCreate}@javascript:SalesDlvObj.showPopupSelectFacility('${parameters.orderId?if_exists}');"
		 />
	<#else>
	 	<@jqGrid filtersimplemode="true" id="jqxgridDelivery" addrefresh="true" usecurrencyfunction="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="false" filterable="true" alternativeAddPopup="alterpopupWindow" editable="true" 
	 		 url="jqxGeneralServicer?sname=getListDelivery&fromOrderId=${parameters.orderId?if_exists}&deliveryTypeId=DELIVERY_SALES"
	 		 customTitleProperties="ListDelivery" selectionmode="singlecell" mouseRightMenu="true" contextMenuId="DeliveryMenu"
	 		 jqGridMinimumLibEnable="true"/>
		</#if>
	<#else>
		<@jqGrid filtersimplemode="true" id="jqxgridDelivery" addrefresh="true" usecurrencyfunction="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="true" filterable="true" alternativeAddPopup="alterpopupWindow" editable="true" 
		 url="jqxGeneralServicer?sname=getListDelivery&fromOrderId=${parameters.orderId?if_exists}&deliveryTypeId=DELIVERY_SALES" createUrl="jqxGeneralServicer?sname=createDelivery&jqaction=C" editmode="dblclick"
		 addColumns="listOrderItems(java.util.List);orderId;shipmentId;currencyUomId;statusId;destFacilityId;originProductStoreId;partyIdTo;partyIdFrom;createDate(java.sql.Timestamp);destContactMechId;originContactMechId;originFacilityId;deliveryDate(java.sql.Timestamp);deliveryTypeId[DELIVERY_SALES];no;estimatedStartDate(java.sql.Timestamp);estimatedArrivalDate(java.sql.Timestamp);defaultWeightUomId;deliveryId;shipmentMethodTypeId;carrierPartyId" 	 
		 updateUrl="" editColumns="" functionAfterAddRow="SalesDlvObj.afterAddDelivery()" customTitleProperties="ListDelivery" _customMessErr="SalesDlvObj.customMess"
		 jqGridMinimumLibEnable="true" selectionmode="singlecell" mouseRightMenu="true" contextMenuId="DeliveryMenu"
		 customcontrol1="icon-plus@${uiLabelMap.QuickCreate}@javascript:SalesDlvObj.showPopupSelectFacility('${parameters.orderId?if_exists}');"
		 />
	</#if>
<#else>
	<@jqGrid filtersimplemode="true" id="jqxgridDelivery" addrefresh="true" usecurrencyfunction="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="false" filterable="true" alternativeAddPopup="alterpopupWindow" editable="true" 
	 url="jqxGeneralServicer?sname=getListDelivery&fromOrderId=${parameters.orderId?if_exists}&deliveryTypeId=DELIVERY_SALES"
	 customTitleProperties="ListDelivery" selectionmode="singlecell" mouseRightMenu="true" contextMenuId="DeliveryMenu"
	 jqGridMinimumLibEnable="true"/>
</#if>
</div>
<div id="alterpopupWindow" class="hide popup-bound">
	<div>${uiLabelMap.AddNewSaleDelivery} - ${uiLabelMap.OrderId}: ${parameters.orderId?if_exists}</div>
	<input type="hidden" name="orderId"/>
	<input type="hidden" name="currencyUomId"/>
	<input type="hidden" name="statusId"/>
	<input type="hidden" name="orderDate"/>
	<input type="hidden" name="originProductStoreId" value=""/>
	<input type="hidden" id="deliveryTypeId" value="DELIVERY_SALES"/>
	<div class='form-window-container'>
		<div id="containerPopupNotify"></div>
		<div class='form-window-content'>
	        <div class="row-fluid">
	    		<div class="span4">
	    			<!-- <div class="row-fluid margin-bottom5">	
	    				<div class="span5" style="text-align: right">
	    					<div style="margin-right: 10px"> ${uiLabelMap.DAOrderId} </div>
						</div>
						<div class="span7">	
							<div id="orderIdDis" style="width: 100%;" class="green-label"></div>
						</div>
					</div> -->
					<div class="row-fluid hide margin-top10 margin-bottom5">	
						<div class="span5" style="text-align: right">
							<div style="margin-right: 10px"> ${uiLabelMap.DeliveryNoteId} </div>
						</div>
						<div class="span7">	
							<input id="deliveryId" style="width: 100%;" type="text"/>
						</div>
					</div>
					<div class="row-fluid margin-bottom5">	
	    				<div class="span5" style="text-align: right">
	    					<div class="asterisk"> ${uiLabelMap.OriginFacility}</div>
						</div>
						<div class="span7">	
					        <div id="originFacilityId" class="hide green-label"></div>
					        <div id="facilityPopup" class="green-label">
								<div id="jqxgridFacilityPopup"></div>
							</div> 
					        <div class="hide">
					        	<a href="javascript:SalesDlvObj.getFacilityList()" onclick="" style="margin-left:57px;"><i class="icon-search"></i></a>
					        </div>
						</div>
					</div>
					<div class="row-fluid margin-bottom5">	
						<div class="span5" style="text-align: right">
	    					<div class="asterisk"> ${uiLabelMap.OriginAddress} </div>
						</div>
						<div class="span7">	
						<#if hasOlbPermission("MODULE", "LOG_DELIVERY", "ADMIN")>
							<div id="originContactMechId" style="width: 100%;" class="green-label pull-left"></div><div class='hide'><a id="addDestinationAddress" href="javascript:SalesDlvObj.addOriginFacilityAddress()"><i style="padding-left: 10px;" class="icon-plus-sign"></i></a></div>
						<#else>
							<div id="originContactMechId" style="width: 100%;" class="green-label pull-left"></div>
						</#if>
						</div>
					</div>
					<#if !shipmentMethodIds[0]?has_content>
						<div class="row-fluid margin-bottom5">	
							<div class="span5" style="text-align: right">
		    					<div class="asterisk"> ${uiLabelMap.ShipmentMethod} </div>
							</div>
							<div class="span7">	
							<div id="shipmentMethodTypeId" style="width: 100%;" class="green-label pull-left"></div>
							</div>
						</div>
					<#else>
		    		</#if>
	    		</div>
	    		<div class="span4">
					<div class="row-fluid hide margin-top10 margin-bottom5">	
						<div class="span5" style="text-align: right">
	    					<div> ${uiLabelMap.Sender} </div>
						</div>
						<div class="span7">	
							<div id="partyIdFromDis" style="width: 100%;" class="green-label"></div>
							<input id="partyIdFrom" type="hidden"></input>
						</div>
					</div>
			<!--		<div class="row-fluid margin-bottom5">	
	    				<div class="span5" style="text-align: right"> 
	    					<div style="margin-right: 10px"> ${uiLabelMap.DeliveryBatchNumber} </div>
						</div>
						<div class="span7">	
							<input id="noNumber" style="width: 100%;" type="text"/>
						</div>
					</div>
			-->
					<div class="row-fluid margin-bottom5">	
						<div class="span5" style="text-align: right">
	    					<div> ${uiLabelMap.Receiver} </div>
						</div>
						<div class="span7">	
							<div id="partyIdToDis" style="width: 100%;" class="green-label"></div>
							<input id="partyIdTo" type="hidden"></input>
						</div>
					</div>
					<div class="row-fluid margin-top10 margin-bottom5">	
						<div class="span5" style="text-align: right">
							<div style="margin-right: 10px"> ${uiLabelMap.RequireDeliveryDate} </div>
						</div>
						<div class="span7">	
							<div id="deliveryDate" style="width: 100%;"></div>
						</div>
					</div>
					<div class="row-fluid hide margin-bottom5">	
						<div class="span5" style="text-align: right">
	    					<div class="asterisk"> ${uiLabelMap.CustomerAddress} </div>
						</div>
						<div class="span7">	
							<div id="destContactMechId" style="width: 100%;" class="green-label pull-left"></div>
						</div>
					</div>
					<#if !shipmentMethodIds[0]?has_content>
						<div class="row-fluid margin-bottom5">	
							<div class="span5" style="text-align: right">
								<div class="asterisk"> ${uiLabelMap.CarrierParty} </div>	
							</div>
							<div class="span7">	
								<div id="partyId" style="width: 100%;" class="green-label pull-left"></div>
							</div>
						</div>
					<#else>
		    		</#if>
	    		</div>
	    		<div class="span4">
		    		<!-- <div class="row-fluid margin-bottom5">	
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
					-->
					<div class="row-fluid margin-bottom5">	
						<div class="span5" style="text-align: right">
	    					<div style="margin-right: 10px"> ${uiLabelMap.EstimatedStartDate} </div>
						</div>
						<div class="span7">	
							<div id="estimatedStartDate" style="width: 100%;"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom5">	
						<div class="span5" style="text-align: right">
	    					<div style="margin-right: 10px"> ${uiLabelMap.EstimatedArrivalDate}</div>
						</div>
						<div class="span7">	
							<div id="estimatedArrivalDate" style="width: 100%;"></div>
						</div>
					</div>
					<#if !shipmentMethodIds[0]?has_content>
						<div class="row-fluid margin-bottom10">	
							<div class="span5" style="text-align: right">
							</div>
							<div class="span7">	
							</div>
						</div>
		    		</#if>
	    		</div>
			</div>
			<div class="row-fluid margin-top10">
				<div style="margin-left: 20px;"><div id="jqxgridOrderItem"></div></div>
			</div>
		</div>
		<div class="form-action popup-footer">
	        <button id="alterCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonClose}</button>
	        <button id="alterSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>

<#include "salesDeliveryFormCommon.ftl">

<div id="selectFacilityWindow" class="hide popup-bound">
	<div>${uiLabelMap.SelectFacilityToExport}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<input type="hidden" id="defaultOrderId" value=""/>
			<div id="notifyNotFacEnough"></div>
			<div class="row-fluid margin-top20">
	    		<div class="row-fluid margin-bottom10">	
					<div class="span4" style="text-align: right">
						<div class="asterisk">${uiLabelMap.Facility}</div>
					</div>
					<div class="span7">	
						<div id="facility" class="green-label">
							<div id="jqxgridFacility"></div>
						</div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span4" style="text-align: right">
						<div class="asterisk">${uiLabelMap.Address}</div>
					</div>
					<div class="span7">	
						<div id="defaultContactMechId" style="width: 100%;" class="green-label"></div>
					</div>
				</div>
				<#if !shipmentMethodIds[0]?has_content>
				<div class="row-fluid margin-bottom10">	
					<div class="span4" style="text-align: right">
						<div class="asterisk">${uiLabelMap.ShipmentMethod}</div>
					</div>
					<div class="span7">	
						<div id="quickShipmentMethodTypeId" style="width: 100%;" class="green-label"></div>
					</div>
				</div>
				<div class="row-fluid ">	
					<div class="span4" style="text-align: right">
						<div class="asterisk"> ${uiLabelMap.CarrierParty} </div>	
					</div>
					<div class="span7">	
						<div id="quickPartyId" style="width: 100%;" class="green-label"></div>
					</div>
				</div>
				<#else>
	    		</#if>
			</div>
		</div>
		<div class="form-action popup-footer">
	        <button id="quickCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	    	<button id="quickSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>

<#assign columnlistFA="
					{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true, 
				    groupable: false, draggable: false, resizable: false,
				    datafield: '', columntype: 'number', width: 50,
				    cellsrenderer: function (row, column, value) {
				        return '<span style=margin:4px;>' + (value + 1) + '</span>';
				    	}
					},
					{ text: '${uiLabelMap.FacilityId}', pinned: true, dataField: 'facilityId', width: 200, editable:false,
                        cellsrenderer: function(row, column, value){
                            var data = $('#jqxgridFAINV').jqxGrid('getrowdata', row);
                            return '<a href=\"javascript:SalesDlvObj.selectFacility(' + \"'\" + data.facilityId + \"'\" + ');\">' + data.facilityId + '</a>';
                        }},
                    { text: '${uiLabelMap.FacilityName}', pinned: false, dataField: 'facilityName', editable:false}"/>
<#assign dataFieldFA="[{ name: 'facilityId', type: 'string' },{ name: 'facilityName', type: 'string' }]"/>

<div id="facilityWindow" class="hide popup-bound">
	<div>${uiLabelMap.AvailableFacilityList}</div>
	<div style="overflow: hidden; margin-top: -10px;">
		<script>
	    	$("#facilityWindow").jqxWindow({
	            maxWidth: 1500, width: 500, minWidth: 550, minHeight: 220, height: 340, maxHeight: 1200, resizable: false,  isModal: true, modalZIndex: 100000, autoOpen: false, modalOpacity: 0.7, theme:theme           
	        });
		</script>
		<@jqGrid id="jqxgridFAINV" columnlist=columnlistFA dataField=dataFieldFA url="jqxGeneralServicer?sname=getAvailableINV&orderId=${parameters.orderId?if_exists}" bindresize="false" 
			autoheight="false" height="250" isShowTitleProperty="false" showlist="false" viewSize='5'/>
	    <div class="form-action popup-footer">
	        <button id="suggestCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
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
<div id="addPostalAddressWindow" class="hide popup-bound">
	<div class="row-fluid">
		${uiLabelMap.NewFacilityAddress}
	</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class='row-fluid margin-top10'>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<div style="margin-right: 10px">${StringUtil.wrapString(uiLabelMap.Facility)}</div>
					</div>  
					<div class="span7">
						<div id="seletedFacilityId" class="green-label"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<div class="asterisk">${StringUtil.wrapString(uiLabelMap.National)}</div>
					</div>  
					<div class="span7">
						<div id="countryGeoId"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<div class="asterisk">${StringUtil.wrapString(uiLabelMap.City)}</div>
					</div>  
					<div class="span7">
						<div id="stateProvinceGeoId"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<div class="asterisk">${StringUtil.wrapString(uiLabelMap.CityPostalCode)}</div>
					</div>  
					<div class="span7">
						<input id="postalCode" style=""></input>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<div class="asterisk">${StringUtil.wrapString(uiLabelMap.Address)} 1</div>
					</div>  
					<div class="span7">
						<input id="address1">
						</input>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<div style="margin-right: 10px">${StringUtil.wrapString(uiLabelMap.Address)} 2</div>
					</div>  
					<div class="span7">
						<input id="address2">
						</input>
					</div>
				</div>
				<div class="form-action popup-footer">
					<button id="newAddrCancelButton" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
					<button id="newAddrOkButton" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				</div>
			</div>
		</div>
	</div>
</div>
<#include 'loadDeliveryItems.ftl'>	
<script type="text/javascript">
	$("#addPostalAddressWindow").jqxWindow({
		maxWidth: 640, minWidth: 300, width: 600, minHeight: 345, maxHeight: 600, cancelButton: $("#newAddrCancelButton"), resizable: false,  isModal: true, autoOpen: false,
	});
	$("#notifyIdCheckDelivery").jqxNotification({ width: "100%", opacity: 0.9, appendContainer: "#containerNotify",
        autoOpen: false, animationOpenDelay: 800, autoClose: false, template: "error"
    });
	$("#updateOrderNoteComplete").jqxNotification({ width: "100%", opacity: 0.9, appendContainer: "#popupContainerNotify",
        autoOpen: false, animationOpenDelay: 800, autoClose: true, template: "success"
    });
	
	$('#originFacilityId').jqxDropDownList({placeHolder: '${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}', width: 200, selectedIndex: 0, source: facilityData, theme: theme, displayMember: 'description', valueMember: 'facilityId',});
	
	var destContactDataTmp = [];
	$('#defaultContactMechId').jqxDropDownList({placeHolder: '${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}', dropDownHeight: 150, source: [], selectedIndex: 0, width: 350, theme: theme, displayMember: 'description', valueMember: 'contactMechId'});
	
	$('#destContactMechId').jqxDropDownList({placeHolder: '${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}', source: destContactData, selectedIndex: 0, width: 200, theme: theme, displayMember: 'description', valueMember: 'contactMechId'});
	$('#originContactMechId').jqxDropDownList({placeHolder: '${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}', source: originContactData, selectedIndex: 0, width: 200, theme: theme, displayMember: 'description', valueMember: 'contactMechId'});
	if (needsCheckShipmentMethod == true){
		$('#shipmentMethodTypeId').jqxDropDownList({placeHolder: '${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}', source: shipmentMethodData, selectedIndex: 0, width: 200, theme: theme, displayMember: 'description', valueMember: 'shipmentMethodTypeId'});
		$('#partyId').jqxDropDownList({placeHolder: '${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}', source: carrierPartyData, selectedIndex: 0, width: 200, theme: theme, displayMember: 'description', valueMember: 'partyId'});
		$('#quickShipmentMethodTypeId').jqxDropDownList({placeHolder: '${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}', source: shipmentMethodData, selectedIndex: 0, width: 350, theme: theme, displayMember: 'description', valueMember: 'shipmentMethodTypeId'});
		$('#quickPartyId').jqxDropDownList({placeHolder: '${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}', source: carrierPartyData, selectedIndex: 0, width: 350, theme: theme, displayMember: 'description', valueMember: 'partyId'});
	}
	// $('#listWeightUomId').jqxDropDownList({placeHolder: '${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}', source: weightUomData, selectedIndex: 0, width: 100, theme: theme, displayMember: 'description', valueMember: 'uomId'});
	// $('#listWeightUomId').jqxDropDownList('val','WT_kg');
			
	// Create list partyIdto
	// $("#partyIdTo").jqxDropDownList({selectedIndex: 0, width: 200, dropDownWidth: 200, source: listPartyTo, theme: theme, displayMember: 'fullName', valueMember: 'partyId'});
	$("#partyIdTo").val(listPartyTo[0].partyId);
	$("#partyIdToDis").text(listPartyTo[0].fullName);
	
	// Create list partyIdFrom
	// $("#partyIdFrom").jqxDropDownList({selectedIndex: 0, width: 200, dropDownWidth: 200, source: listPartyFrom, theme: theme, displayMember: 'groupName', valueMember: 'partyId'});
	$("#partyIdFrom").val(listPartyFrom[0].partyId);
	$("#partyIdFromDis").text(listPartyFrom[0].fullName);
	
	$("#deliveryDate").jqxDateTimeInput({width: 200, formatString: 'dd/MM/yyyy HH:mm:ss', disabled: true});
	if (estimatedDeliveryDate != null){
		$("#deliveryDate").jqxDateTimeInput('val', estimatedDeliveryDate);
	}
	
	var tmp = $('#deliveryDate').jqxDateTimeInput('getDate');
	
	$('#estimatedStartDate').jqxDateTimeInput({width: 200, formatString: 'dd/MM/yyyy HH:mm:ss'});
	$('#estimatedArrivalDate').jqxDateTimeInput({width: 200, formatString: 'dd/MM/yyyy HH:mm:ss'});
	var now = new Date();
   	if ((typeof(tmp) != 'undefined' && tmp != null && !(/^\s*$/.test(tmp)))) {
	    if (tmp < now) {
	    	var mm = now.getMinutes(); 
	    	now.setMinutes(mm + 10);
	    	$('#estimatedStartDate').val(now);
	    	$('#estimatedArrivalDate').val(now);
	    } else {
	    	$('#estimatedStartDate').val(tmp);
	    	$('#estimatedArrivalDate').val(tmp);
	    }
   	}
	$('#document').ready(function(){
		$("#notifyIdQuickCreateError").jqxNotification({ width: "100%", opacity: 0.9, appendContainer: "#containerNotify",
	        autoOpen: false, animationOpenDelay: 800, autoClose: true, template: "error"
	    });
		$("#notifyCreateSuccessful").jqxNotification({ width: "100%", opacity: 0.9, appendContainer: "#containerNotify",
	        autoOpen: false, animationOpenDelay: 800, autoClose: true, template: "success"
	    });
		$("#notifyIdNotHaveStorekeeper").jqxNotification({ width: "100%", opacity: 0.9, appendContainer: "#containerNotify",
	        autoOpen: false, animationOpenDelay: 800, autoClose: true, template: "error"
	    });
		$("#notifyUpdateDeliverySuccessful").jqxNotification({ width: "100%", opacity: 0.9, appendContainer: "#popupContainerNotify",
	        autoOpen: false, animationOpenDelay: 800, autoClose: true, template: "success"
	    });
		<#if !shipmentMethodIds[0]?has_content>
			$("#selectFacilityWindow").jqxWindow({
				maxWidth: 800, minWidth: 300, width: 600, height: 275, minHeight: 100, maxHeight: 656, resizable: false, isModal: true, autoOpen: false, cancelButton: $("#quickCancel"), modalOpacity: 0.7, theme:theme           
			});
		<#else>
			$("#selectFacilityWindow").jqxWindow({
				maxWidth: 800, minWidth: 300, width: 570, height: 230, minHeight: 100, maxHeight: 656, resizable: false, isModal: true, autoOpen: false, cancelButton: $("#quickCancel"), modalOpacity: 0.7, theme:theme           
			});
		</#if>
		
		// $('#totalProductWeight').text('0');
		var stateProvinceGeoData = new Array();
		$("#countryGeoId").jqxDropDownList({source: countryData, width: 300, displayMember: "geoName", valueMember: "geoId"});
		$("#countryGeoId").jqxDropDownList('val', 'VNM');
		$("#stateProvinceGeoId").jqxDropDownList({source: stateProvinceGeoData, width: 300, displayMember: "value", valueMember: "id"});
		$("#alterApprove").hide();
		$("#postalCode").jqxInput({width: 295});
		$("#address1").jqxInput({width: 295});
		$("#address2").jqxInput({width: 295});
	
		$('#originProductStoreId').val('${originProductStore}');
			
		$('#jqxFileScanUpload').jqxWindow({ width: 400, modalZIndex: 10000, height: 220, isModal: true, autoOpen: false });
		$('#jqxFileScanExptUpload').jqxWindow({ width: 400, modalZIndex: 10000, height: 220, isModal: true, autoOpen: false });
		
	});
	
	// Create orderId
	
	// $("#orderIdDis").text(orderId);
	
	// Set Value for statusId
	$('#alterpopupWindow input[name=statusId]').val('DLV_CREATED');
	
	// Create CurrencyUom
	$('#alterpopupWindow input[name=currencyUomId]').val('${orderHeader.currencyUom?if_exists}');
	
	// Create orderDate
	$('#alterpopupWindow input[name=orderDate]').val('${orderHeader.orderDate?if_exists}');
	
	// Create Order date
	$('#orderDateDisplay').text('${orderDateDisplay?if_exists}');
	
	// Create noNumber
	// $('#noNumber').jqxInput({width: 195});
	$('#deliveryId').jqxInput({width: 195});

	<#if !shipmentMethodIds[0]?has_content>
		$("#alterpopupWindow").jqxWindow({
			maxWidth: 1500, minWidth: 950, width: 1330, minHeight: 400, maxHeight: 1200, maxHeight: 800, height: 580, resizable: true,  isModal: true, modalZIndex: 100000, zIndex: 100000, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme:theme           
		});
	<#else>
		$("#alterpopupWindow").jqxWindow({
			maxWidth: 1500, minWidth: 950, width: 1300, minHeight: 400, maxHeight: 1200, maxHeight: 800, height: 580, resizable: true,  isModal: true, modalZIndex: 100000, zIndex: 100000, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme:theme           
		});
	</#if>
	
	function loadOrderItem(valueDataSoure){
		var sourceOrderItem =
		    {
	        datafields:[{ name: 'orderId', type: 'string' },
	                    { name: 'orderItemSeqId', type: 'string' },
	                 	{ name: 'productId', type: 'string' },
	                 	{ name: 'productCode', type: 'string' },
	                 	{ name: 'productName', type: 'string' },
	                 	{ name: 'expireDate', type: 'date', other: 'Timestamp' },
						{ name: 'prodCatalogId', type: 'string' },
						{ name: 'productCategoryId', type: 'string' },
	                 	{ name: 'orderItemTypeId', type: 'string' },
	                 	{ name: 'requireAmount', type: 'string' },
	                 	{ name: 'resQuantity', type: 'number' },
	                 	{ name: 'selectedAmount', type: 'number' },
	                 	{ name: 'requiredQuantity', type: 'number' },
	                 	{ name: 'createdQuantity', type: 'number' },
						{ name: 'quantityUomId', type: 'string'},
						{ name: 'weightUomId', type: 'string'},
	                 	{ name: 'facilityId', type: 'string' },
	                 	{ name: 'isPromo', type: 'string' },
	                 	{ name: 'requiredQuantityTmp', type: 'string' },
						{ name: 'unitPrice', type: 'number'},
						{ name: 'quantity', type: 'number'},
						{ name: 'quantityOnHandTotal', type: 'number'},
						{ name: 'amountOnHandTotal', type: 'number'},
						{ name: 'availableToPromiseTotal', type: 'number'},
						{ name: 'weight', type: 'number'},
						{ name: 'comments', type: 'string' },
						{ name: 'baseWeightUomId', type: 'string' },
						{ name: 'baseQuantityUomId', type: 'string' },
						{ name: 'quantityUomIds', type: 'string' },
			 		 	],
	        localdata: valueDataSoure,
	        datatype: "array",
	    };
	    var dataAdapterOrderItem = new $.jqx.dataAdapter(sourceOrderItem);
	    $("#jqxgridOrderItem").jqxGrid({
        source: dataAdapterOrderItem,
        filterable: true,
        showfilterrow: true,
        theme: 'olbius',
        rowsheight: 26,
        width: '100%',
        height: 360,
        enabletooltips: true,
        autoheight: false,
        pageable: true,
        pagesize: 10,
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
				{ text: '${uiLabelMap.ProductName}', dataField: 'productName', width: 205, 	editable: false,},
				{ text: '${uiLabelMap.QtyRequired}', filterable: false, dataField: 'requiredQuantity', width: 120, editable: false, 
					cellsrenderer: function(row, column, value){
						return '<span style=\"text-align: right\" title=' + formatnumber(value) + '>' + formatnumber(value) +'</span>';
					},
					rendered: function(element){
				    	$(element).jqxTooltip({content: '${StringUtil.wrapString(uiLabelMap.RequiredNumber)}', theme: 'orange' });
				    }, 
				},
				{ text: '${uiLabelMap.BSSalesUomId}', datafield: 'quantityUomId', align: 'left', width: 120, filtertype: 'checkedlist', columntype: 'dropdownlist', editable: false,
					cellsrenderer: function (row, column, value){
						for (var i = 0; i < quantityUomData.length; i ++){
							if (quantityUomData[i].uomId == value){
								return '<span style=\"text-align: right\">' + quantityUomData[i].description +'</span>';
							}
						}
					},
				 	initeditor: function (row, cellvalue, editor) {
				 		var packingUomData = new Array();
						var data = $('#jqxgridOrderItem').jqxGrid('getrowdata', row);
						var itemSelected = data['quantityUomId'];
						var packingUomIdArray = JSON.parse(data.quantityUomIds);
						for (var i = 0; i < packingUomIdArray.length; i++) {
							var obj = packingUomIdArray[i];
							var quantityUomId = obj.quantityUomId;
							var row = {};
							if (quantityUomId === undefined || quantityUomId === '' || quantityUomId === null) {
								row['description'] = '' + quantityUomId;
							} else {
								row['description'] = '' + getQuantityUomDesc(quantityUomId);
							}
							row['quantityUomId'] = '' + quantityUomId;
							packingUomData[i] = row;
						}
				 		var sourceDataPacking = {
			                localdata: packingUomData,
			                datatype: 'array'
			            };
			            var dataAdapterPacking = new $.jqx.dataAdapter(sourceDataPacking);
			            editor.off('change');
			            editor.jqxDropDownList({source: dataAdapterPacking, displayMember: 'description', valueMember: 'quantityUomId'});
			            editor.jqxDropDownList('selectItem', itemSelected);
			      	}
				},
				{ text: '${uiLabelMap.Weight}', filterable: false, dataField: 'selectedAmount', width: 120, editable: false, 
					cellsrenderer: function(row, column, value){
						if (value) {
							var data = $('#jqxgridOrderItem').jqxGrid('getrowdata', row);
							var weightUomId = data.weightUomId;
							if (!weightUomId) {
								weightUomId = data.baseWeightUomId;
							}
							if (weightUomId) {
								return '<span style=\"text-align: right\" title=' + formatnumber(value) + '>' + formatnumber(value) +' ' + getUomDescription(weightUomId)+'</span>';							
							}
							return '<span style=\"text-align: right\" title=' + formatnumber(value) + '>' + formatnumber(value) + '</span>';
						} else {
							var data = $('#jqxgridOrderItem').jqxGrid('getrowdata', row);
							if (data.weight > 0) {
								return '<span style=\"text-align: right\" title=' + formatnumber(data.weight) + '>' + formatnumber(data.weight) + ' ' + getUomDescription(data.baseWeightUomId) + '</span>';
							}
						}
					},
				},
				{ text: '${uiLabelMap.QuantityCreate}', filterable: false, dataField: 'requiredQuantityTmp', columntype: 'numberinput', width: 120, editable: true,
					cellsrenderer: function(row, column, value){
						// SalesDlvObj.updateTotalWeight();
						var data = $('#jqxgridOrderItem').jqxGrid('getrowdata', row);
						var requiredQuantityTmp = data.requiredQuantityTmp;
						if (data.requiredQuantity && data.createdQuantity){
							requiredQuantityTmp = data.requiredQuantity - data.createdQuantity;
						}
						return '<span style=\"text-align: right\" class=\"focus-color\" title=' + formatnumber(value) + '>' + formatnumber(value) +'</span>';
					},
					initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
				        editor.jqxNumberInput({ decimalDigits: 0});
				        var data = $('#jqxgridOrderItem').jqxGrid('getrowdata', row);
				        if (data.requiredQuantity && data.createdQuantity){
				        	editor.jqxNumberInput('val', data.requiredQuantity - data.createdQuantity);
				        }
				    },
				    validation: function (cell, value) {
				        var data = $('#jqxgridOrderItem').jqxGrid('getrowdata', cell.row);
				        var quantityUomId = data.quantityUomId;
				        var packingUomIdArray = JSON.parse(data.quantityUomIds);
				        var convert = 1;
				        for (var i = 0; i < packingUomIdArray.length; i ++){
				        	if (quantityUomId == packingUomIdArray[i].quantityUomId) {
				        		convert = packingUomIdArray[i].convertNumber;
				        	}
				        }
				        var selectedAmount = 1;
				        if (data.requireAmount && data.requireAmount == "Y")
				        {
				        	selectedAmount = data.selectedAmount;
				        }
				        if (value*convert*selectedAmount > ((data.requiredQuantity)*convert - data.createdQuantity*convert)){
				        	var c = data.requiredQuantity - data.createdQuantity;
				            return { result: false, message: '${uiLabelMap.ExportValueLTZRequireValue}: ' + value*selectedAmount + ' > ' + c};
				        } else{
				        	if (value <= 0){
				        		return { result: false, message: '${uiLabelMap.ExportValueMustBeGreaterThanZero}'};
				        	} else {
				        		return true;
				        	}
				        }
				    },
				},
				{ text: '${uiLabelMap.BLTotalBaseQuantity}', filterable: false, dataField: 'quantity', width: 120, editable: false, 
					cellsrenderer: function(row, column, value){
						var data = $('#jqxgridOrderItem').jqxGrid('getrowdata', row);
						var quantityUomId = data.quantityUomId;
						var convert = 1;
						if (data.requireAmount && data.requireAmount == 'Y') {
							convert = data.selectedAmount;
						} else {
							var packingUomIdArray = JSON.parse(data.quantityUomIds);
							for (var j = 0; j < packingUomIdArray.length; j++) {
								var obj = packingUomIdArray[j];
								var quantityUomIdTmp = obj.quantityUomId;
								if (quantityUomId == quantityUomIdTmp){
									convert = obj.convertNumber;
								}
							}
						}
						value = (data.requiredQuantityTmp)*convert;
						return '<span style=\"text-align: right\" title=' + formatnumber(value) + '>' + formatnumber(value) +'</span>';
					},
				},
				{ text: '${uiLabelMap.QOH}', filterable: false, dataField: 'quantityOnHandTotal', minwidth: 120, editable: false, cellsalign: 'right', align: 'center',
	 				cellsrenderer: function(row, column, value){
	 					var baseUomDes;
	 					var data = $('#jqxgridOrderItem').jqxGrid('getrowdata', row);
	 					var requireAmount = data.requireAmount;
	 					var qty = data.quantityOnHandTotal;
	 					var desc = getUomDescription(data.quantityUomId);
	 					if (requireAmount && requireAmount == 'Y') {
	 						qty = data.amountOnHandTotal;
	 						desc = getUomDescription(data.baseWeightUomId);
	 					} 
	 					if (qty == 0) {
	 						return '<span style=\"text-align: right\" title=' + qty + '>' + qty + ' ' +desc + '</span>';
	 					} else {
	 						return '<span style=\"text-align: right\" title=' + formatnumber(qty) + '>' + formatnumber(qty) + ' ' +desc + '</span>';
	 					}
					},
					rendered: function(element){
				    	$(element).jqxTooltip({content: '${StringUtil.wrapString(uiLabelMap.QuantityOnHandTotal)}', theme: 'orange' });
				    }, 
				},
				{ text: '${uiLabelMap.CreatedOrderNumber}', filterable: false, width: 120, editable: false, 
					cellsrenderer: function(row, column, value){
						var data = $('#jqxgridOrderItem').jqxGrid('getrowdata', row);
						var description="";
						for(var i = 0; i < uomData.length; i++){
							if(uomData[i].quantityUomId == data.baseQuantityUomId){
								description = uomData[i].description;
							}
						}
						var createdQtyTmp = data.createdQuantity;
						return '<span style=\"text-align: right\" title=' + formatnumber(createdQtyTmp)+ '>' + formatnumber(createdQtyTmp) +'</span>';
					}
				},
				/*{ text: '${uiLabelMap.EXPRequired}', dataField: 'expireDate', width: 120, cellsformat: 'dd/MM/yyyy', cellsalign: 'right', filtertype: 'range', editable: false,
					 cellsrenderer: function(row, column, value){
						 if (!value){
							 return '<span style=\"text-align: right\"></span>';
						 } else {
							 return '<span style=\"text-align: right\">'+ PODlvObj.formatFullDate(value)+'</span>';
						 }
					 }, 
					 rendered: function(element){
				    	$(element).jqxTooltip({content: '${StringUtil.wrapString(uiLabelMap.RequiredExpireDate)}', theme: 'orange' });
					 }, 
				},
				*/
//				{ text: '${uiLabelMap.Comments}', dataField: 'comments', width: 200, editable: false}
				{ text: '${uiLabelMap.IsPromo}', filterable: false, dataField: 'isPromo', width: 120, editable: false, 
					cellsrenderer: function (row, column, value) {
						if (value == 'Y') {
							return '<span style=\"text-align: left\">${uiLabelMap.LogYes}</span>';
						}
						if (value == 'N') {
							return '<span style=\"text-align: left\">${uiLabelMap.LogNO}</span>';
						}
					},
				}
			]
	    });
	}
	
	var getQuantityUomDesc = function (uomId){
		for (var i = 0; i < quantityUomData.length; i ++) {
			if (quantityUomData[i].uomId == uomId) {
				return quantityUomData[i].description;
			}
		}
	}
</script>