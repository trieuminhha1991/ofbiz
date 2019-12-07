<script>
	var locale = '${locale}';

	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.packingListNumber = "${StringUtil.wrapString(uiLabelMap.packingListNumber)}";
	uiLabelMap.AreYouSureSave = "${StringUtil.wrapString(uiLabelMap.AreYouSureSave)}";
	uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
	uiLabelMap.testedDocument = "${StringUtil.wrapString(uiLabelMap.testedDocument)}";
	uiLabelMap.quarantineDocument = "${StringUtil.wrapString(uiLabelMap.quarantineDocument)}";
	
	uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
	uiLabelMap.ProductId = "${StringUtil.wrapString(uiLabelMap.ProductId)}";
	uiLabelMap.ProductName = "${StringUtil.wrapString(uiLabelMap.ProductName)}";
	uiLabelMap.globalTradeItemNumber = "${StringUtil.wrapString(uiLabelMap.globalTradeItemNumber)}";
	uiLabelMap.batchNumber = "${StringUtil.wrapString(uiLabelMap.batchNumber)}";
	uiLabelMap.packingUnits = "${StringUtil.wrapString(uiLabelMap.packingUnits)}";
	uiLabelMap.packingUomId = "${StringUtil.wrapString(uiLabelMap.packingUomId)}";
	uiLabelMap.orderUnits = "${StringUtil.wrapString(uiLabelMap.orderUnits)}";
	uiLabelMap.orderUomId = "${StringUtil.wrapString(uiLabelMap.orderUomId)}";
	uiLabelMap.originOrderUnit = "${StringUtil.wrapString(uiLabelMap.originOrderUnit)}";
	uiLabelMap.dateOfManufacture = "${StringUtil.wrapString(uiLabelMap.dateOfManufacture)}";
	uiLabelMap.ProductExpireDate = "${StringUtil.wrapString(uiLabelMap.ProductExpireDate)}";
	uiLabelMap.CommonDelete = "${StringUtil.wrapString(uiLabelMap.CommonDelete)}";
	uiLabelMap.Product = "${StringUtil.wrapString(uiLabelMap.Product)}";
	uiLabelMap.AddDetailPL = "${StringUtil.wrapString(uiLabelMap.AddDetailPL)}";
	uiLabelMap.AddPL = "${StringUtil.wrapString(uiLabelMap.AddPL)}";
	uiLabelMap.AddProduct = "${StringUtil.wrapString(uiLabelMap.BLAddProducts)}";
	uiLabelMap.BSRefresh = "${StringUtil.wrapString(uiLabelMap.BSRefresh)}";
	
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
</script>
<div id="popupWindowContainer" class="hide popup-bound">
<div>${uiLabelMap.AddNew} ${uiLabelMap.BIEContainer} & ${uiLabelMap.BIEPackingList}</div>
<div class='form-window-container'>
	<div class='form-window-content'>
		<input type="hidden" id="containerId" value=""></input>
		<input type="hidden" id="billId"></input>
		<input type="hidden" id="packingListId"></input>
		<input type="hidden" id="gridDetailId"/>
		<input type="hidden" id="indexGridDetail"/>
		<div class="row-fluid">
			<div class="row-fluid margin-top5">
				<div class="span4">
					<div class="row-fluid">
						<div class="span5 align-right asterisk">${uiLabelMap.containerNumber}</div>
						<div class="span6"><input type='text' id="containerNumber" /></div>
					</div>
				</div>
				<div class="span4">
					<div class="row-fluid">
						<div class="span5 align-right">${uiLabelMap.sealNumber}</div>
						<div class="span6"><input type='text' id="sealNumber" /></div>
					</div>
				</div>
				<div class="span4">
					<div class="row-fluid">
						<div class="span5 align-right asterisk">${uiLabelMap.BIEAgreement}</div>
						<div class="span6"><div type='text' id="orderPurchaseId"></div></div>
					</div>
				</div>
			</div>
			<div class="row-fluid margin-top5">
				<div class="span4">
					<div class="row-fluid">
						<div class="span5 align-right asterisk">${uiLabelMap.packingListNumber}</div>
						<div class="span6"><input type='text' id="packingListNumber" /></div>
					</div>
				</div>
				<div class="span4">
					<div class="row-fluid">
						<div class="span5 align-right asterisk">${uiLabelMap.BIEVendorInvoiceNum}</div>
						<div class="span6"><input type='text' id="invoiceNumber" /></div>
					</div>
				</div>
				<div class="span4">
					<div class="row-fluid">
						<div class="span5 align-right asterisk">${uiLabelMap.orderTypeSupp}</div>
						<div class="span6"><div id="orderTypeSupp"></div></div>
					</div>
				</div>
			</div>
			<div class="row-fluid margin-top5">
				<div class="span4">
					<div class="row-fluid">
						<div class="span5 align-right asterisk">${uiLabelMap.packingListDate}</div>
						<div class="span6"><div id="packingListDate"></div></div>
					</div>
				</div>
				<div class="span4">
					<div class="row-fluid">
						<div class="span5 align-right asterisk">${uiLabelMap.invoiceDate}</div>
						<div class="span6"><div id="invoiceDate"></div></div>
					</div>
				</div>
				<div class="span4">
					<div class="row-fluid">
						<div class="span5 align-right asterisk">${uiLabelMap.BIEVendorOrderNum}</div>
						<div class="span6"><input type='text' id="orderNumberSupp" /></div>
					</div>
				</div>
			</div>
			<div class="row-fluid margin-top5">
				<div class="span4">
					<div class="row-fluid">
						<div class="span5 align-right">${uiLabelMap.totalNetWeight}</div>
						<div class="span6"><div id="totalNetWeight"></div></div>
					</div>
				</div>
				<div class="span4">
					<div class="row-fluid">
						<div class="span5 align-right">${uiLabelMap.totalGrossWeight}</div>
						<div class="span6"><div id="totalGrossWeight"></div></div>
					</div>
				</div>
			</div>
		</div>
		<div class="row-fluid">
			<div id="jqxgridPackingListDetail"></div>
		</div>
        <div class="form-action popup-footer">
	        <button id="alterCancelContainer" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	        <button id="saveAndContinueContainer" class='hide'><i class='fa-remove'></i> ${uiLabelMap.CommonSave}</button>
	    	<button id="alterSaveContainer" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>
</div>
<div id='jqxMenu' style="display: none;">
	<ul>
		<li id="createInvoice"><i class="icon-plus"></i>&nbsp;&nbsp;<a>${uiLabelMap.accCreateInvoice}</a></li>
		<li id="viewQuarantine"><i class="icon-download-alt"></i>&nbsp;&nbsp;<a>${uiLabelMap.DownloadquarantineDocument}</a></li>
		<li id="viewTested"><i class="icon-download-alt"></i>&nbsp;&nbsp;<a>${uiLabelMap.DownloadtestedDocument}</a></li>
		<li id='agreementToQuarantineChild'><i class='icon-download-alt'></i>&nbsp;&nbsp;<a>${uiLabelMap.DownloadAgreementToQuarantine}</a></li>
		<li id='agreementToValidationChild'><i class='icon-download-alt'></i>&nbsp;&nbsp;<a>${uiLabelMap.DownloadAgreementToValidation}</a></li>
	</ul>
</div>
<script>
	var DAYouNotYetChooseProduct = '${uiLabelMap.DAYouNotYetChooseProduct}';
	var NotChosenDateManu = '${uiLabelMap.NotChosenDateManu}';
	var ExistsProductNotDate = '${uiLabelMap.ExistsProductNotDate}';
	var SaveAndConPL = '${uiLabelMap.SaveAndConPL}';
	var LoadFailPO = '${uiLabelMap.LoadFailPO}';
	var ClearPL = '${uiLabelMap.ClearPL}';
</script>
<script type="text/javascript" src="/imexresources/js/import/containerManager.js"></script>