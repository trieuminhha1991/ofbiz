<@jqGridMinimumLib/>
<@jqOlbCoreLib hasCore=false hasValidator=true/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script>
	var facilitySelected = null;
	var containerParamId = null;
	<#if parameters.containerId?has_content>
		containerParamId = '${parameters.containerId?if_exists}';
	</#if>
	
	var billId = null;
	<#if parameters.billId?has_content>
		billId = '${parameters.billId?if_exists}';
		<#assign bill = delegator.findOne("BillOfLading", Static["org.ofbiz.base.util.UtilMisc"].toMap("billId", parameters.billId?if_exists), false)!/>
		<#if bill?has_content>
			if (billSelected == undefined) { 
				var billSelected = null;
			}
			if (billId) {
				billSelected = {"billId": billId, "billNumber": "${bill.billNumber?if_exists}"};
			}
		</#if>
	<#else>
		var billSelected = null;
	</#if>
	var containerTypeData = [];
	<#assign containerTypes = delegator.findList("ContainerType", null, null, null, null, true) />
	
	<#if containerTypes?has_content>
		<#list containerTypes as item>
			var item = {
				containerTypeId: '${item.containerTypeId?if_exists}',
				description: '${item.get('description', locale)?if_exists}'
			}
			containerTypeData.push(item);
		</#list>
	</#if>
	
	var getContainerTypeDesc = function (containerTypeId) {
		for (var i in containerTypeData) {
			var x = containerTypeData[i];
			if (x.containerTypeId == containerTypeId) {
				return x.description;
			}
		}
		return containerTypeId;
	}
	
	var locale = '${locale}';

	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.packingListNumber = "${StringUtil.wrapString(uiLabelMap.packingListNumber)}";
	uiLabelMap.AreYouSureSave = "${StringUtil.wrapString(uiLabelMap.AreYouSureSave)}";
	uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
	uiLabelMap.DAYouNotYetChooseProduct = "${StringUtil.wrapString(uiLabelMap.DAYouNotYetChooseProduct)}";
	uiLabelMap.NotChosenDateManu = "${StringUtil.wrapString(uiLabelMap.NotChosenDateManu)}";
	uiLabelMap.ExistsProductNotDate = "${StringUtil.wrapString(uiLabelMap.ExistsProductNotDate)}";
	uiLabelMap.SaveAndConPL = "${StringUtil.wrapString(uiLabelMap.SaveAndConPL)}";
	uiLabelMap.LoadFailPO = "${StringUtil.wrapString(uiLabelMap.LoadFailPO)}";
	uiLabelMap.ClearPL = "${StringUtil.wrapString(uiLabelMap.ClearPL)}";
	
	uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
	uiLabelMap.BIEContainerId = "${StringUtil.wrapString(uiLabelMap.BIEContainerId)}";
	uiLabelMap.BIEBillId = "${StringUtil.wrapString(uiLabelMap.BIEBillId)}";
	uiLabelMap.BIEBillNumber = "${StringUtil.wrapString(uiLabelMap.BIEBillNumber)}";
	uiLabelMap.BIEContainer = "${StringUtil.wrapString(uiLabelMap.BIEContainer)}";
	uiLabelMap.BIEContainerNumber = "${StringUtil.wrapString(uiLabelMap.BIEContainerNumber)}";
	uiLabelMap.BIESealNumber = "${StringUtil.wrapString(uiLabelMap.BIESealNumber)}";
	uiLabelMap.Description = "${StringUtil.wrapString(uiLabelMap.Description)}";
	uiLabelMap.AddNew = "${StringUtil.wrapString(uiLabelMap.AddNew)}";
	uiLabelMap.BIEContainerType = "${StringUtil.wrapString(uiLabelMap.BIEContainerType)}";
	uiLabelMap.BIEDepartureDate = "${StringUtil.wrapString(uiLabelMap.BIEDepartureDate)}";
	uiLabelMap.BIEArrivalDate = "${StringUtil.wrapString(uiLabelMap.BIEArrivalDate)}";
	
	
	uiLabelMap.BIEPackingListId = "${StringUtil.wrapString(uiLabelMap.BIEPackingListId)}";
	uiLabelMap.BIEAgreementId = "${StringUtil.wrapString(uiLabelMap.BIEAgreementId)}";
	uiLabelMap.OrderPO = "${StringUtil.wrapString(uiLabelMap.OrderPO)}";
	uiLabelMap.BIEVendorInvoiceNum = "${StringUtil.wrapString(uiLabelMap.BIEVendorInvoiceNum)}";
	uiLabelMap.BIEVendorOrderNum = "${StringUtil.wrapString(uiLabelMap.BIEVendorOrderNum)}";
	uiLabelMap.BIESealNumber = "${StringUtil.wrapString(uiLabelMap.BIESealNumber)}";
	uiLabelMap.BIENetWeight = "${StringUtil.wrapString(uiLabelMap.BIENetWeight)}";
	uiLabelMap.BIEGrossWeight = "${StringUtil.wrapString(uiLabelMap.BIEGrossWeight)}";
	uiLabelMap.BIEPackingListDate = "${StringUtil.wrapString(uiLabelMap.BIEPackingListDate)}";
	uiLabelMap.BIEInvoiceDate = "${StringUtil.wrapString(uiLabelMap.BIEInvoiceDate)}";
	uiLabelMap.BIEPackingList = "${StringUtil.wrapString(uiLabelMap.BIEPackingList)}";
	
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
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	
	uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
	uiLabelMap.BIEContainerId = "${StringUtil.wrapString(uiLabelMap.BIEContainerId)}";
	uiLabelMap.BIEBillId = "${StringUtil.wrapString(uiLabelMap.BIEBillId)}";
	uiLabelMap.BIEBillNumber = "${StringUtil.wrapString(uiLabelMap.BIEBillNumber)}";
	uiLabelMap.BIEContainer = "${StringUtil.wrapString(uiLabelMap.BIEContainer)}";
	uiLabelMap.BIEContainerNumber = "${StringUtil.wrapString(uiLabelMap.BIEContainerNumber)}";
	uiLabelMap.BIESealNumber = "${StringUtil.wrapString(uiLabelMap.BIESealNumber)}";
	uiLabelMap.Description = "${StringUtil.wrapString(uiLabelMap.Description)}";
	uiLabelMap.AddNew = "${StringUtil.wrapString(uiLabelMap.AddNew)}";
	uiLabelMap.BIEContainerType = "${StringUtil.wrapString(uiLabelMap.BIEContainerType)}";
	uiLabelMap.BIEDepartureDate = "${StringUtil.wrapString(uiLabelMap.BIEDepartureDate)}";
	uiLabelMap.BIEArrivalDate = "${StringUtil.wrapString(uiLabelMap.BIEArrivalDate)}";
	uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
	uiLabelMap.testedDocument = "${StringUtil.wrapString(uiLabelMap.testedDocument)}";
	uiLabelMap.quarantineDocument = "${StringUtil.wrapString(uiLabelMap.quarantineDocument)}";
	
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	
	uiLabelMap.FacilityId = "${StringUtil.wrapString(uiLabelMap.FacilityId)}";
	uiLabelMap.FacilityName = "${StringUtil.wrapString(uiLabelMap.FacilityName)}";
	
</script>

<div id="packing-tab" class="tab-pane<#if activeTab?exists && activeTab == "packing-tab"> active</#if>">
<div id="jqxGridPackingLists"></div>
<div id="popupWindowAddNewPackingList" class="hide popup-bound">
	<div>${uiLabelMap.AddNew} ${uiLabelMap.BIEPackingList}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="row-fluid margin-top5">
					<div class="span4">
						<div class="row-fluid">
							<div class="span5 align-right asterisk">${uiLabelMap.BIEBillOfLading}</div>
							<div class="span6">	
								<div id="billOfLadingPacking">
									<div id="jqxGridBOL"></div>
								</div>
							</div>
						</div>
					</div>
					<div class="span4">
						<div class="row-fluid">
							<div class="span5 align-right asterisk">${uiLabelMap.BIEContainer}</div>
							<div class="span6">	
								<div id="container">
									<div id="jqxGridContainer"></div>
								</div>
							</div>
						</div>
					</div>
					<div class="span4">
						<div class="row-fluid">
							<div class="span5 align-right asterisk">${uiLabelMap.BIEAgreement}</div>
							<div class="span6"><div type='text' id="agreementId"></div></div>
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
					<div class="span4">
						<div class="row-fluid">
							<div class="span5 align-right asterisk">${uiLabelMap.ReceiveToFacility}</div>
							<div class="span6">
								<div id="facility">
									<div id="gridFacility">
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<div id="jqxgridPackingListDetail"></div>
			</div>
	        <div class="form-action popup-footer">
		        <button id="alterCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
		    	<button id="alterSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>

<div id='jqxMenu' style="display: none;">
	<ul>
		<li id='refreshGridPKL'><i class='icon-refresh'></i><a>${uiLabelMap.BSRefresh}</a></li>
	</ul>
</div>
</div>
<script type="text/javascript" src="/imexresources/js/import/listPackingLists.js"></script>