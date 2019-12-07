<div id='DeliveryMenu' style="display:none;">
	<ul>
	    <li><i class="fa fa-eye"></i>${StringUtil.wrapString(uiLabelMap.BSViewDetail)}</li>
	    <li><i class="fa fa-eye-slash"></i>${StringUtil.wrapString(uiLabelMap.BLQuickView)}</li>
	    <li><i class="fa fa-file-pdf-o"></i>${StringUtil.wrapString(uiLabelMap.ExportPdf)}</li>
	    <li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	</ul>
</div>
<div id="popupDeliveryDetailWindow" class="hide popup-bound">
	<div id="titleDetailId">${uiLabelMap.DeliveryTransferNote}</div>
	<div style="overflow: hidden;">
		<h4 class="row header smaller lighter blue" style="margin: 5px 0px 20px 20px !important;font-weight:500;line-height:20px;font-size:18px;">
		    ${uiLabelMap.GeneralInfo}
		    <a href="#collapse" data-toggle="collapse" style="float: left; font-size: 12px; padding-right: 2px;"><i class="fa-expand" aria-hidden="true"></i></a>
		    <a style="float:right;font-size:14px;margin-left: 10px; cursor: pointer; cursor: hand;" id="cancelDlv" target="_blank" data-rel="tooltip" title="${uiLabelMap.CommonCancel}" data-placement="bottom" data-original-title="${uiLabelMap.CommonCancel}"><i class="icon-trash red"></i>${uiLabelMap.CommonCancel}</a>
		    <a style="float:right;font-size:14px;" id="printPDF" target="_blank" data-rel="tooltip" title="${uiLabelMap.PrintToPDF}" data-placement="bottom" data-original-title="${uiLabelMap.PrintToPDF}"><i class="fa-file-pdf-o"></i></a>
		    <div style="float:right;font-size:14px;margin-right:10px" id="scanfile"></div>
		</h4>
		<div id="collapse" class="collapse">
			<div class='row-fluid'>
				<div class="span4">
					<div class='row-fluid'>
						<div class='span5' style="text-align: right;">
							${uiLabelMap.FacilityFrom}
						</div>
						<div class='span7 green-label'>
							<div id="originFacilityDT">
							</div>
						</div>
					</div>
					<div class='row-fluid'>
						<div class='span5' style="text-align: right;">
							${uiLabelMap.FacilityTo}
						</div>
						<div class='span7 green-label'>
							<div id="destFacilityDT">
							</div>
						</div>
					</div>
				</div>
				<div class="span4">
					<div class='row-fluid'>
						<div class='span5' style="text-align: right; ">
							${uiLabelMap.OriginAddress}
						</div>
						<div class='span7 green-label'>
							<div id="originContactMechIdDT">
							</div>
						</div>
					</div>
					<div class='row-fluid'>
						<div class='span5' style="text-align: right;">
							${uiLabelMap.DestAddress}
						</div>
						<div class='span7 green-label'>
							<div id="destContactMechIdDT">
							</div>
						</div>
					</div>
				</div>
				<div class="span4">
					<div class='row-fluid'>
					    <div class="span5" style="text-align: right;">
					    	<span>${uiLabelMap.EstimatedStartDate}</span>
					    </div>
					    <div class="span7">
					    	<div id="estimatedStartDateDT" class="green-label"></div>
				    	</div>
				    </div>
					<div class='row-fluid'>
					    <div class="span5" style="text-align: right;">
					    	<span>${uiLabelMap.EstimatedArrivalDate}</span>
					    </div>
					    <div class="span7">
					    	<div id="estimatedArrivalDateDT" class="green-label"></div>
					    </div>
				    </div>
				</div>
			</div>
		</div>
		<div class='row-fluid'>
			<div class="span4">
				<div class='row-fluid'>
					<div class='span5' style="text-align: right;">
						${uiLabelMap.DeliveryId}
					</div>
					<div class='span7 green-label'>
						<div id="deliveryIdDT">
						</div>
					</div>
				</div>
				<div class='row-fluid'>
					<div class='span5' style="text-align: right;">
						${uiLabelMap.TransferId}
					</div>
					<div class='span7'>
						<div id="transferIdDT" class="green-label">
							${transfer.transferId}
						</div>
					</div>
				</div>
			</div>
			<div class="span4">
				<div class='row-fluid'>
					<div class='span5' style="text-align: right;">
						${uiLabelMap.Status}
					</div>
					<div class='span7 green-label'>
						<div id="statusDT">
						</div>
					</div>
				</div>
				<div class='row-fluid'>
					<div class='span5' style="text-align: right;">
						${uiLabelMap.RequireDeliveryDate}
					</div>
					<div class='span7 green-label'>
						<div id="deliveryDateDT" style="float: left; width: 300px;">
						</div>
					</div>
				</div>
		    </div>
		    <div class="span4">
				<div class='row-fluid'>
				    <div class="span5" style="text-align: right;" class="asterisk">
				    	<span>${uiLabelMap.ActualExportedDate}</span>
				    </div>
				    <div class="span7">
				    	<div id="actualStartDateDis" class="green-label"></div>
				    	<div id="actualStartDate" class="green-label"></div>
			    	</div>
			    </div>
				<div class='row-fluid'>
				    <div class="span5" style="text-align: right;" class="asterisk">
				    	<span>${uiLabelMap.ActualDeliveredDate}</span>
				    </div>
				    <div class="span7">
				    	<div id="actualArrivalDateDis" class="green-label"></div>
				    	<div id="actualArrivalDate" class="green-label"></div>
				    </div>
			    </div>
		    </div>
	    </div>
		<div class='row-fluid'>
			<h4 class="row header smaller lighter blue" style="margin-left: 20px !important;font-weight:500;line-height:20px;font-size:18px;">
				${uiLabelMap.ListProduct}
				<a style="float:right;font-size:14px; margin-right: 5px" id="addRow" href="javascript:TransferDlvObj.addNewRow()" data-rel="tooltip" title="${uiLabelMap.AddRow}" data-placement="bottom"><i class="icon-plus-sign open-sans"></i></a>
			</h4>
			<div style="margin-left: 20px"><div id="jqxgridDlvItem"></div></div>
		</div>
	    <div class="form-action popup-footer">
            <button id="alterCancel2" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonClose}</button>
            <button id="approveAndContinue" class='btn btn-success form-action-button pull-right'>${uiLabelMap.ApproveAndContinue} <i class='icon-arrow-right'></i></button>
            <button id="approveBtn" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Approve}</button>
            <button id="saveAndContinue" class='btn btn-success form-action-button pull-right'>${uiLabelMap.LogSaveAndContinue} <i class='icon-arrow-right'></i></button>
            <button id="alterSave2" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
	    </div>
	</div>
</div>
<script>

</script>