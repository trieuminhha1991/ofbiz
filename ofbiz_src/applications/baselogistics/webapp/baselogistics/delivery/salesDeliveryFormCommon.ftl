 <div id="DeliveryMenu" style="display:none;">
	<ul>
		<#if !hasOlbPermission('MODULE', 'DISTRIBUTOR', 'ADMIN')>
		<li><i class="fa fa-eye"></i>${StringUtil.wrapString(uiLabelMap.BSViewDetail)}</li>
		<li><i class="fa fa-eye"></i>${StringUtil.wrapString(uiLabelMap.BLQuickView)}</li>
		</#if>
		<li><i class="fa fa-file-pdf-o"></i>${StringUtil.wrapString(uiLabelMap.DeliveryDoc)}</li>
		<#if !hasOlbPermission('MODULE', 'DISTRIBUTOR', 'ADMIN')>
		<li><i class="fa fa-file-pdf-o"></i>${StringUtil.wrapString(uiLabelMap.DeliveryNote)}</li>
		</#if>
		<li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	</ul>
</div>
<div id="container" class="container-noti"></div>
<div id="jqxNotification" style="margin-bottom:5px">
    <div id="notification">
    </div>
</div>	
<div id="popupDeliveryDetailWindow" class="hide popup-bound">
	<div id="titleDetailId">${uiLabelMap.DeliveryNote}</div>
	<div class="form-window-container">
		<div class="form-window-content">
			<div id="popupContainerNotify" style="width: 100%; overflow: auto;"></div>
			<h4 class="row header smaller lighter blue" style="margin-left: 20px !important;font-weight:500;line-height:20px;font-size:18px;">
				${uiLabelMap.GeneralInfo}
				<a href="#collapse" data-toggle="collapse" style="float: left; font-size: 12px; padding-right: 2px;"><i class="fa-expand" aria-hidden="true"></i></a>
				<a style="float:right;font-size:14px; margin-left: 5px; cursor: pointer; cursor: hand;" id="cancelDlv" target="_blank" data-rel="tooltip" title="${uiLabelMap.CommonCancel}" data-placement="bottom" data-original-title="${uiLabelMap.CommonCancel}"><i class="icon-trash red"></i>${uiLabelMap.CommonCancel}</a>
				<a style="float:right;font-size:14px; margin-left: 5px; cursor: pointer; cursor: hand;" id="editDlv" target="_blank" data-rel="tooltip" title="${uiLabelMap.CommonEdit}" data-placement="bottom" data-original-title="${uiLabelMap.CommonEdit}"><i class="icon-edit"></i>${uiLabelMap.CommonEdit}</a>
				<#if !hasOlbPermission("MODULE", "LOG_DELIVERY", "ADMIN")>
					<a style="float:right;font-size:14px;cursor: pointer;" id="sendRequestApprove" target="_blank" data-rel="tooltip" title="${uiLabelMap.SendRequestApprove}" data-placement="bottom" data-original-title="${uiLabelMap.DeliveryNote}">&nbsp; <i style="font-style: normal !important;" class="fa-paper-plane-o"> ${uiLabelMap.CommonSend}</i></a>
				</#if>
				<a style="float:right;font-size:14px; cursor: pointer;" id="printPDF" target="_blank" data-rel="tooltip" title="${uiLabelMap.ExportPdf}" data-placement="bottom" data-original-title="${uiLabelMap.DeliveryNote}"><i class="fa fa-file-pdf-o"></i>${uiLabelMap.DeliveryNote}&nbsp;</a>
				<a style="float:right;font-size:14px;margin-right:10px cursor: pointer;" id="printDeliveryDoc" target="_blank" data-rel="tooltip" title="${uiLabelMap.ExportPdf}" data-placement="bottom" data-original-title="${uiLabelMap.DeliveryDoc}"><i class="fa fa-file-pdf-o"></i>${uiLabelMap.DeliveryDoc}&nbsp;&nbsp;</a>
				<a class="hide" style="float:right;font-size:14px;margin-right:10px; cursor: pointer; cursor: hand;" id="printDlvPooledTax" target="_blank" data-rel="tooltip" title="${uiLabelMap.DeliveryDocPooledTax}" data-placement="bottom" data-original-title="${uiLabelMap.DeliveryDocPooledTax}"><i class="fa fa-file-pdf-o"></i>${uiLabelMap.DeliveryDocPooledTax}</a>
				<a style="float:right;font-size:14px;margin-right:10px; cursor: pointer; cursor: hand;" id="printShipmentInfo" class="hide" target="_blank" data-rel="tooltip" title="${uiLabelMap.ShipmentInfo}" data-placement="bottom" data-original-title="${uiLabelMap.ShipmentInfo}"><i class="fa fa-file-pdf-o"></i>${uiLabelMap.ShipmentInfo}</a>
				<div class="hide"><a style="float:right;font-size:14px;margin-right:10px; cursor: pointer; cursor: hand;" id="debtRecord" target="_blank" data-rel="tooltip" title="${uiLabelMap.BLDebtRecord}" data-placement="bottom" data-original-title="${uiLabelMap.BLDebtRecord}"><i class="fa fa-edit"></i>${uiLabelMap.BLDebtRecord}</a></div>
				<div style="float:right;font-size:14px;margin-right:15px" id="scanfileExpt" class="hide"></div>
				<div style="float:right;font-size:14px;margin-right:15px" id="orderNote"></div>
				<!-- <div style="float:right;font-size:14px;margin-right:15px" id="totalWeight" class="green-label"></div> -->
				<div style="float:right;font-size:14px;margin-right:40px;color: #08c;" id="checkLabel" class="open-sans hide">${uiLabelMap.LabelChecking}</div>
				<div style="float:right;font-size:14px;margin-right:10px" id="scanfile"></div>
			</h4>
			<div id="collapse" class="collapse">
				<div class="row-fluid">
					<div class="span4">
						<div class="row-fluid">
							<div class="span5" style="text-align: right;">${uiLabelMap.ExportFromFacility}:</div>
							<div class="span7"><div id="originFacilityIdDT" class="green-label"></div></div>
						</div>
					</div>
					<div class="span4">
						<div class="row-fluid">
							<div class="span4" style="text-align: right;">${uiLabelMap.RequiredCompletedDate}:</div>
							<div class="span8"><div id="deliveryDateDT" class="green-label"></div></div>
						</div>
					</div>
					<div class="span4">
						<div class="row-fluid">
							<div class="span5" style="text-align: right;">${uiLabelMap.EstimatedStartDate}:</div>
							<div class="span7"><div id="estimatedStartDateDT" class="green-label"></div></div>
						</div>
					</div>
				</div>
				<div class="row-fluid hide">
					<div class="span4 hide">
						<div class="row-fluid">
							<div class="span5" style="text-align: right;">${uiLabelMap.Sender}:</div>
							<div class="span7"><div id="partyIdFromDT" class="green-label"></div></div>
						</div>
					</div>
					<div class="span4">
						<div class="row-fluid hide">
							<div class="span5" style="text-align: right;">${uiLabelMap.OriginAddress}:</div>
							<div class="span7"><div id="originContactMechIdDT" class="green-label"></div></div>
						</div>
					</div>
					<div class="span4 hide">
						<div class="row-fluid">
							<div class="span5" style="text-align: right;">${uiLabelMap.DeliveryBatchNumber}:</div>
							<div class="span7" style="text-align: left;"><div id="noDT" class="green-label"></div></div>
						</div>
					</div>
				</div>
				<div class="row-fluid">
					<div class="span4">
						<div class="row-fluid">
							<div class="span5" style="text-align: right;">${uiLabelMap.DeliveryReason}:</div>
							<div class="span7"><div id="deliveryTypeDT" class="green-label"></div></div>
						</div>
					</div>
					<div class="span4">
						<div class="row-fluid">
							<div class="span4" style="text-align: right;">${uiLabelMap.Receiver}:</div>
							<div class="span8"><div id="partyIdToDT" class="green-label"></div></div>
						</div>
					</div>
					<div class="span4">
						<div class="row-fluid">
							<div class="span5" style="text-align: right;">${uiLabelMap.EstimatedArrivalDate}:</div>
							<div class="span7"><div id="estimatedArrivalDateDT" class="green-label"></div></div>
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span4">
					<div class="row-fluid">
						<div class="span5" style="text-align: right;">${uiLabelMap.DeliveryId}:</div>
						<div class="span7"><div id="deliveryIdDT" class="green-label"></div></div>
					</div>
				</div>
				<div class="span4">
					<div class="row-fluid">
						<div class="span4" style="text-align: right;">${uiLabelMap.Status}:</div>
						<div class="span8"><div id="statusIdDT" class="green-label"></div></div>
					</div>
				</div>
				<div class="span4">
					<div class="row-fluid">
						<div class="span5" id="actualStartLabel" style="text-align: right;" class="asterisk">${uiLabelMap.ActualExportedDate}:</div>
						<div class="span7">
							<div id="actualStartDateDis" class="green-label"></div>
							<div id="actualStartDate" class="green-label"></div>
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span4">
					<div class="row-fluid">
						<div class="span5" style="text-align: right;">${uiLabelMap.DAOrderId}:</div>
						<div class="span7"><div id="orderIdDT"class="green-label"></div></div>
					</div>
				</div>
				<div class="span4">
					<div class="row-fluid">
						<div class="span4" style="text-align: right;">${uiLabelMap.CustomerAddress}:</div>
						<div class="span8"><div id="destContactMechIdDT" class="green-label"></div></div>
					</div>
				</div>
				<div class="span4">
					<div class="row-fluid">
						<div class="span5" id="actualArrivalLabel" style="text-align: right;" class="asterisk">${uiLabelMap.ActualDeliveredDate}:</div>
						<div class="span7">
							<div id="actualArrivalDateDis" class="green-label"></div>
							<div id="actualArrivalDate" class="green-label"></div>
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<h4 class="row header smaller lighter blue" style="margin-left: 20px !important;font-weight:500;line-height:20px;font-size:18px;">
					${uiLabelMap.ListProduct}
					<a style="float:right;font-size:14px; margin-right: 5px" id="addRow" href="javascript:SalesDlvObj.addNewRow()" data-rel="tooltip" title="${uiLabelMap.AddRow}" data-placement="bottom"><i class="icon-plus-sign open-sans"></i></a>
				</h4>
				<div style="margin-left: 20px"><div id="jqxgridDlvItem"></div></div>
			</div>
			<div class="form-action popup-footer">
				<button id="alterCancel2" class="btn btn-danger form-action-button pull-right"><i class="fa-remove"></i> ${uiLabelMap.CommonClose}</button>
				<button id="alterSaveAndContinue" class="btn btn-success form-action-button pull-right">${uiLabelMap.LogSaveAndContinue} <i class="icon-arrow-right"></i></button>
				<button id="alterSave2" class="btn btn-primary form-action-button pull-right"><i class="fa-check"></i> ${uiLabelMap.CommonSave}</button>
				<#if hasOlbPermission("MODULE", "LOG_DELIVERY", "ADMIN")>
					<button id="alterApproveAndContinue" class="btn btn-success form-action-button pull-right">${uiLabelMap.ApproveAndContinue} <i class="icon-arrow-right"></i> </button>
					<button id="alterApprove" class="btn btn-primary form-action-button pull-right"><i class="fa-check"></i> ${uiLabelMap.Approve}</button>
				</#if>
			</div>
		</div>
	</div>
</div>

<div id="debtWindow" class="hide popup-bound">
	<div>${uiLabelMap.DeliveryNoting}</div>
		<div class="form-window-container">
			<div class="form-window-content">
				<div class="row-fluid">
					<h4 class="row header smaller lighter blue" style="margin-left: 20px !important;font-weight:500;line-height:20px;font-size:18px;">
						${uiLabelMap.ListProduct}
					</h4>
					<div style="margin-left: 20px"><div id="debtGrid"></div></div>
				</div>
			</div>
		<div class="form-action popup-footer">
			<button id="debtCancel" class="btn btn-danger form-action-button pull-right"><i class="fa-remove"></i> ${uiLabelMap.CommonClose}</button>
			<button id="debtSave" class="btn btn-primary form-action-button pull-right"><i class="fa-check"></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>
<div id="noteWindow" class="hide popup-bound">
	<div>${uiLabelMap.DeliveryNoting}</div>
		<div class="form-window-container">
			<div class="form-window-content">
				<h4 class="row header smaller lighter blue" style="margin-left: 20px !important;font-weight:500;line-height:20px;font-size:18px;">
					${uiLabelMap.ReceiveReturn} 
				</h4>
				<div class="row-fluid">
					<div class="span6">
						<div class="row-fluid">
							<div class="span5" style="text-align: right;"><div class="asterisk">${uiLabelMap.ReturnToFacility}</div></div>
							<div class="span7"><div id="facilityReturnId" class="green-label"></div></div>
						</div>
					</div>
					<div class="span6">
						<div class="row-fluid">
							<div class="span5" style="text-align: right;"><div class="asterisk">${uiLabelMap.ReceivedDate}</div></div>
							<div class="span7"><div id="datetimeReceived" class="green-label"></div></div>
						</div>
					</div>
				</div>
				<div class="row-fluid margin-top20">
					<h4 class="row header smaller lighter blue" style="margin-left: 20px !important;font-weight:500;line-height:20px;font-size:18px;">
						${uiLabelMap.ListProduct}
					</h4>
					<div style="margin-left: 20px"><div id="noteGrid"></div></div>
				</div>
			</div>
		<div class="form-action popup-footer">
			<button id="noteCancel" class="btn btn-danger form-action-button pull-right"><i class="fa-remove"></i> ${uiLabelMap.CommonClose}</button>
			<button id="noteSave" class="btn btn-primary form-action-button pull-right"><i class="fa-check"></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>
<div id="editWindow" class="hide popup-bound">
	<div>${uiLabelMap.BLDeliveryEdit}</div>
	<div class="form-window-container">
		<div class="form-window-content">
			<div class="row-fluid">
				<a style="float:right;font-size:14px; margin-right: 5px" id="editAddRow" href="javascript:SalesDlvObj.editAddNewProduct()" data-rel="tooltip" title="${uiLabelMap.AddRow}" data-placement="bottom"><i class="icon-plus-sign open-sans"></i></a>
				<div><div id="editGrid"></div></div>
			</div>
		</div>
		<div class="form-action popup-footer">
			<button id="editCancel" class="btn btn-danger form-action-button pull-right"><i class="fa-remove"></i> ${uiLabelMap.CommonClose}</button>
			<button id="editSave" class="btn btn-primary form-action-button pull-right"><i class="fa-check"></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>
<div id="editAddProductWindow" class="hide popup-bound">
	<div>${uiLabelMap.BLAddProducts}</div>
	<div class="form-window-container">
		<div class="form-window-content">
			<div class="row-fluid">
				<div><div id="editAddProductGrid"></div></div>
			</div>
		</div>
		<div class="form-action popup-footer">
			<button id="editAddProductCancel" class="btn btn-danger form-action-button pull-right"><i class="fa-remove"></i> ${uiLabelMap.CommonClose}</button>
			<button id="editAddProductSave" class="btn btn-primary form-action-button pull-right"><i class="fa-check"></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>