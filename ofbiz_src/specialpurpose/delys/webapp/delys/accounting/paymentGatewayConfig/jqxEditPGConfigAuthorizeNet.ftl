<div id="wdwEditPGCAuthorizeNet" style="display: none;">
	<div id="wdwHeader">
		<span>
		   ${uiLabelMap.EditPGCAuthorizeNet}
		</span>
	</div>
	<div id="wdwContentEdit">
		<div class="basic-form form-horizontal" style="margin-top: 10px">
			<form name="formEdit" id="formEdit">	
				<div class="row-fluid" >
					<div class="span12">
						<div class="span6">
							<div class="control-group no-left-margin">
								<label class="control-label">${uiLabelMap.AccountingAuthorizeNetTransactionUrl}:</label>  
								<div class="controls">
									<div id="transactionUrl"></div>
								</div>
							</div>
							<div class="control-group no-left-margin">
								<label class="control-label">${uiLabelMap.AccountingAuthorizeNetApiVersion}:</label>  
								<div class="controls">
									<div id="apiVersion">
									</div>
								</div>
							</div>
							<div class="control-group no-left-margin">
								<label class="control-label">${uiLabelMap.AccountingAuthorizeNetDelimitedData}:</label>  
								<div class="controls">
									<input id="delimitedData" />
								</div>
							</div>
							<div class="control-group no-left-margin">
								<label class="control-label">${uiLabelMap.AccountingAuthorizeNetDelimiterChar}:</label>  
								<div class="controls">
									<div id="delimiterChar">
									</div>
								</div>
							</div>
							<div class="control-group no-left-margin">
								<label class="control-label">${uiLabelMap.AccountingAuthorizeNetMethod}:</label>  
								<div class="controls">
									<div id="method">
									</div>
								</div>
							</div>
							<div class="control-group no-left-margin">
								<label class="control-label">${uiLabelMap.AccountingAuthorizeNetEmailCustomer}:</label>  
								<div class="controls">
									<input id="emailCustomer">
									</input>
								</div>
							</div>
							<div class="control-group no-left-margin">
								<label class="control-label">${uiLabelMap.AccountingAuthorizeNetEmailMerchant}:</label>  
								<div class="controls">
									<div id="emailMerchant">
									</div>
								</div>
							</div>
							<div class="control-group no-left-margin">
								<label class="control-label">${uiLabelMap.AccountingAuthorizeNetTestMode}:</label>  
								<div class="controls">
									<div id="testMode">
									</div>
								</div>
							</div>
							<div class="control-group no-left-margin">
								<label class="control-label">${uiLabelMap.AccountingAuthorizeNetRelayResponse}:</label>  
								<div class="controls">
									<div id="relayResponse">
									</div>
								</div>
							</div>
						</div>
						<div class="span6">
							<div class="control-group no-left-margin">
								<label class="control-label">${uiLabelMap.AccountingAuthorizeNetCpVersion}:</label>  
								<div class="controls">
									<div id="cpVersion">
									</div>
								</div>
							</div>
							<div class="control-group no-left-margin">
								<label class="control-label">${uiLabelMap.AccountingAuthorizeNetCpMarket}:</label>  
								<div class="controls">
									<div id="cpMarketType">
									</div>
								</div>
							</div>
							<div class="control-group no-left-margin">
								<label class="control-label">${uiLabelMap.AccountingAuthorizeNetCpDevice}:</label>  
								<div class="controls">
									<div id="cpDeviceType">
									</div>
								</div>
							</div>
							<div class="control-group no-left-margin">
								<label class="control-label">${uiLabelMap.AccountingAuthorizeNetTransKey}:</label>  
								<div class="controls">
									<div id="tranKey">
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</form>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="alterCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="alterSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
				</div>
			</div>
		</div>
	</div>
</div>
<script>
$('#wdwNewBillingAcc').jqxWindow({showCollapseButton: false, maxHeight: 1000, autoOpen: false, maxWidth: "75%", height: 400, minWidth: '40%', width: "90%", isModal: true, modalZIndex: 10000,theme:this.theme, collapsed:false, cancelButton: '#alterCancel',
    initContent: function () {
    	
    }
</script>