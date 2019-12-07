<#include 'script/transferDetailScript.ftl'/>
<div id="general-tab" class="tab-pane<#if activeTab?exists && activeTab == "general-tab"> active</#if>">				
	<div style="position:relative">
		<div>
			<div class="row-fluid font-bold" id="detailTransfer">
				<div class="form-horizontal form-window-content-custom label-text-left content-description" style="margin:10px">
					<div class='row-fluid'>
						<div class="span4">
							<div class='row-fluid'>
								<div class='span5'> <span style="float: right">${uiLabelMap.TransferId}</span> </div>
								<div class="span7"> <div class="green-label" id="TransferIdDT" name="TransferIdDT"> ${transfer.transferId}</div> </div>
							</div>
							<div class='row-fluid'>
								<div class='span5'> <span style="float: right;">${uiLabelMap.TransferType}</span> </div>
								<div class="span7">
									<div class="green-label" id="transferTypeIdDT" name="transferTypeIdDT">
										<#if transfer?exists>
											<#assign transferType = delegator.findOne("TransferType", {"transferTypeId" : transfer.transferTypeId?if_exists}, false)/>
											<#assign descTransferType = StringUtil.wrapString(transferType.get('description', locale)?if_exists) />
											${descTransferType}
										</#if>
									</div>
						   		</div>
							</div>
							<div class='row-fluid'>
								<div class='span5'>
									<span style="float: right;">${uiLabelMap.OriginFacility}</span>
								</div>
								<div class="span7">
									<div class="green-label" id="originFacilityIdDT" name="originFacilityIdDT">
										<#if transfer?exists>
											<#assign originFacility = delegator.findOne("Facility", {"facilityId" : transfer.originFacilityId?if_exists}, false)/>
											<#if originFacility.facilityCode?has_content>
												${originFacility.facilityCode?if_exists} - ${originFacility.facilityName}
											<#else>
												${originFacility.facilityId?if_exists} - ${originFacility.facilityName}
											</#if>
										</#if>
									</div>
						   		</div>
							</div>
							<div class='row-fluid'>
								<div class='span5'>
									<span style="float: right;">${uiLabelMap.DestFacility}</span>
								</div>
								<div class="span7">
									<div class="green-label" id="transferTypeIdDT" name="transferTypeIdDT">
										<#if transfer?exists>
											<#assign destFacility = delegator.findOne("Facility", {"facilityId" : transfer.destFacilityId?if_exists}, false)/>
											<#if destFacility.facilityCode?has_content>
												${destFacility.facilityCode?if_exists} - ${destFacility.facilityName}															
											<#else>
												${destFacility.facilityId?if_exists} - ${destFacility.facilityName}
											</#if>
										</#if>
									</div>
						   		</div>
							</div>
						</div>
						<div class='span4'>
							<div class='row-fluid'>
								<div class='span6'> <span style="float: right;">${uiLabelMap.Status}</span> </div>
								<div class="span6">
									<div id="statusIdDT" class="green-label" style="float: left;">
										<#if transfer?exists>
											<#assign statusItem = delegator.findOne("StatusItem", {"statusId" : transfer.statusId?if_exists}, false)/>
											<#assign statusDesc = StringUtil.wrapString(statusItem.get('description', locale)?if_exists) />
											${statusDesc}
										</#if>
									</div>
						   		</div>
							</div>
							<div class='row-fluid'>
								<div class='span6'> <span style="float: right;">${uiLabelMap.CarrierParty}</span> </div>
								<div class="span6">
									<div id="carrierPartyIdDT" class="green-label">
										<#if transfer?exists>
											<#assign transferItemShipGroups = delegator.findList("TransferItemShipGroup", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("transferId", transfer.transferId?if_exists)), null, null, null, false) />
											<#assign partyCarrier = delegator.findOne("PartyNameView", {"partyId" : transferItemShipGroups.get(0).carrierPartyId?if_exists}, false)/>
											${partyCarrier.groupName?if_exists}
										</#if>
									</div>
						   		</div>
							</div>
							<div class='row-fluid'>
								<div class='span6'> <span style="float: right;">${uiLabelMap.BLShipmentMethodSum}</span> </div>
								<div class="span6">
									<div id="shipmentMethodTypeIdDT" class="green-label">
										<#if transfer?exists>
											<#assign transferItemShipGroups = delegator.findList("TransferItemShipGroup", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("transferId", transfer.transferId?if_exists)), null, null, null, false) />
											<#assign shipmentMethodType = delegator.findOne("ShipmentMethodType", {"shipmentMethodTypeId" : transferItemShipGroups.get(0).shipmentMethodTypeId?if_exists}, false)/>
											<#assign descShipmentMethodType = StringUtil.wrapString(shipmentMethodType.get('description', locale)?if_exists) />
											${descShipmentMethodType}
										</#if>
									</div>
						   		</div>
							</div>
							<div class='row-fluid'>
								<div class='span6 align-right'>
									<span>${uiLabelMap.RemainingSubTotal}</span>
								</div>
								<div class="span6">
									<div class="green-label" id="grandTotal" name="grandTotal">
										<#if transfer.grandTotal?has_content>
											${transfer.grandTotal?string(",##0.00")}
										</#if>  
									</div>
						   		</div>
							</div>
						</div>
						<div class="span4">
							<div class='row-fluid'>
								<div class='span5'>
									<span style="float: right;">${uiLabelMap.ShipBeforeDate2}</span>
								</div>
								<div class="span7">
									<div id="shipBeforeDateDT" style="float: left;" class="green-label">
									</div>
						   		</div>
							</div>
							<div class='row-fluid'>
								<div class='span5'>
									<span style="float: right;">${uiLabelMap.ShipAfterDate2}</span>
								</div>
								<div class="span7">
									<div id="shipAfterDateDT" style="float: left;" class="green-label">
									</div>
						   		</div>
							</div>
							<div class='row-fluid'>
								<div class='span5'>
									<span style="float: right;">${uiLabelMap.BLCompletedDate}</span>
								</div>
								<div class="span7">
									<div id="completedDateDT" class="green-label">
									</div>
						   		</div>
							</div>
							<div class='row-fluid'>
								<div class='span5'>
									<span style="float: right;">${uiLabelMap.Description}</span>
								</div>
								<div class="span7">
									<div id="descriptionDT" class="green-label">
										${transfer.description?if_exists}
									</div>
						   		</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="row-fluid ">
			<div class="span12">
				<h4 class="row header smaller lighter blue" style="font-weight:500;line-height:20px;font-size:18px;">
				    ${uiLabelMap.ListProduct}
				</h4>
				<div id="jqxgridShipmentItem" style="width: 100%"></div>
			</div>
		</div>
	</div>
	
	<#assign columnlist="
		{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, pinned:true, editable: false, groupable: false, draggable: false, resizable: false, datafield: '', columntype: 'number', width: 50,
	        cellsrenderer: function (row, column, value) {
	            return '<span>' + (value + 1) + '</span>';
	        }
		},
	    { text: '${uiLabelMap.ProductProductId}', dataField: 'productCode', width: 120, pinned:true,
			 },
			 { text: '${uiLabelMap.ProductName}', dataField: 'productName', minwidth: 200,
			 },
			 { text: '${uiLabelMap.RequiredNumberSum}', dataField: 'quantity', width: 120,
				 cellsrenderer: function(row, column, value){
				 	var data = $('#jqxgridProductDetail').jqxGrid('getrowdata', row);
			 		var requireAmount = data.requireAmount;
			 		if (requireAmount && requireAmount == 'Y') {
			 			value = data.amount;
			 		}
					if (value){
						return '<span style=\"text-align: right\" title=' + formatnumber(value) + '>' + formatnumber(value) + '</span>'
					} else {
						return '<span></span>';
					}
				},
			 },
			 { text: '${uiLabelMap.Unit}', dataField: 'quantityUomId', width: 70,
				 cellsrenderer: function (row, column, value){
			 		var data = $('#jqxgridProductDetail').jqxGrid('getrowdata', row);
			 		var requireAmount = data.requireAmount;
			 		if (requireAmount && requireAmount == 'Y') {
			 			value = data.weightUomId;
			 		}
					return '<span title=' + getUomDescription(value) + '>' + getUomDescription(value) + '</span>'
				 }
			 },
			 { text: '${uiLabelMap.EXPRequired}', dataField: 'expiredDate', width: 180, cellsformat: 'dd/MM/yyyy', filtertype: 'date', hidden: true,
				 cellsrenderer: function(row, column, value){
					 var data = $('#jqxgridProductDetail').jqxGrid('getrowdata', row);
					 if (data.fromExpiredDate){
						 var str = '';
						 if (data.fromExpiredDate) {
							 str = DatetimeUtilObj.getFormattedDate(new Date(data.fromExpiredDate));
						 } 
						 if (data.toExpiredDate) {
							str = str + ' - ' + DatetimeUtilObj.getFormattedDate(new Date(data.toExpiredDate));
						 }
						 return '<span style=\"text-align: right;\">'+str+'</span>';
					 } else {
						 if (!value){
							 return '<span style=\"text-align: right\" title=\"${uiLabelMap.NotRequiredExpiredDate}\"></span>';
						 }
					 }
				 },
			 },
			 { text: '${uiLabelMap.QuantityDelivered}', dataField: 'quantityDelivered', width: 115,
				 cellsrenderer: function(row, column, value){
					if (value){
						return '<span style=\"text-align: right\" title=' + formatnumber(value) + '>' + formatnumber(value) + '</span>'
					} else {
						return '<span style=\"text-align: right\">'+0+'</span>';
					}
				},
			 },
			 { text: '${uiLabelMap.QuantityShipping}', dataField: 'quantityShipping', width: 115,
				 cellsrenderer: function(row, column, value){
					if (value){
						return '<span style=\"text-align: right\" title=' + formatnumber(value) + '>' + formatnumber(value) + '</span>'
					} else {
						return '<span style=\"text-align: right\">'+0+'</span>';
					}
				},
			 },
			 { text: '${uiLabelMap.QuantityScheduled}', dataField: 'quantityScheduled', width: 115,
				 cellsrenderer: function(row, column, value){
					if (value){
						return '<span style=\"text-align: right\" title=' + formatnumber(value) + '>' + formatnumber(value) + '</span>'
					} else {
						return '<span style=\"text-align: right\">'+0+'</span>';
					}
				},
			 },
			 { text: '${uiLabelMap.QuantityRemain}', dataField: 'quantityRemain', width: 100,
				 cellsrenderer: function(row, column, value){
					if (value){
						return '<span style=\"text-align: right\" title=' + formatnumber(value) + '>' + formatnumber(value) + '</span>'
					} else {
						return '<span style=\"text-align: right\">'+0+'</span>';
					}
				},
			 },
			 "/>
	<#assign dataField="[{ name: 'productId', type: 'string'},
		{ name: 'productName', type: 'string'},
		{ name: 'productCode', type: 'string'},
		{ name: 'quantityUomId', type: 'string'},
		{ name: 'weightUomId', type: 'string'},
		{ name: 'productPacking', type: 'string'},
		{ name: 'expiredDate', type: 'date', other: 'Timestamp'},
		{ name: 'fromExpiredDate', type: 'date', other: 'Timestamp'},
		{ name: 'toExpiredDate', type: 'date', other: 'Timestamp'},
		{ name: 'expiredDateRequired', type: 'string'},
		{ name: 'quantity', type: 'number'},
		{ name: 'amount', type: 'number'},
		{ name: 'requireAmount', type: 'number'},
		{ name: 'quantityDelivered', type: 'number'},
		{ name: 'quantityRemain', type: 'number'},
		{ name: 'quantityShipping', type: 'number'},
		{ name: 'quantityScheduled', type: 'number'},
		{ name: 'baseQuantityUomId', type: 'string'},
		{ name: 'convertNumber', type: 'string'},
		{ name: 'transferId', type: 'string'},
		{ name: 'transferItemSeqId', type: 'string'},
	]"/>
	<@jqGrid filtersimplemode="true" id="jqxgridProductDetail" filterable="true" dataField=dataField columnlist=columnlist editable="false" showtoolbar="false"
	url="jqxGeneralServicer?sname=JQGetListTransferItem&transferId=${parameters.transferId?if_exists}" editmode='click' selectionmode='multiplecellsadvanced'
	/>
</div>
<div class="row-fluid hide" id="noteContent">
	<div class="span12">
		<h4 class="row header smaller lighter blue" style="font-weight:500;line-height:20px;font-size:18px;">
		    ${uiLabelMap.Notes}
		</h4>
		<div id="listNote"></div>
	</div>
</div>
<div id="noteTransfer" class="hide popup-bound">
	<div>${uiLabelMap.ReasonRejected}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid margin-top10">
				<div class="span3" style="text-align: right">
					<div class="asterisk">${uiLabelMap.Notes}</div>
				</div>
				<div class="span8">	
					<textarea id="note" data-maxlength="250" rows="2" style="resize: vertical;margin-top:0px" class="span12"></textarea>
				</div>
			</div>
		</div>
		<div class="form-action popup-footer">
	        <button id="noteCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	    	<button id="noteSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>