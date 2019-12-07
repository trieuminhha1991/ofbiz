<script src="/salesmtlresources/js/supervisor/addCostByDepartment.js"></script>

<div id="alterpopupWindow" class="hide popup-bound">
	<div class="row-fluid">
	   ${uiLabelMap.CostsRecord}
	</div>
	<div class='form-window-container'>
		<div class="row-fluid">
			<div class="span6">
				<div class="row-fluid margin-bottom10">	
					<div class="span4" style="text-align: right">
						<div class="asterisk"> ${uiLabelMap.InvoiceItemType}: </div>
					</div>
					<div class="span7">	
						<div id="txtInvoiceItemTypeId"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span4" style="text-align: right">
						<div class="asterisk"> ${uiLabelMap.AmountMoney}: </div>
					</div>
					<div class="span7">	
						<div id="txtCostPriceActual"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span4" style="text-align: right">
						<div class="asterisk"> ${uiLabelMap.ProductCurrency}: </div>
					</div>
					<div class="span7">	
						<div id="txtCurrencyUomId"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span4" style="text-align: right">
						<div style="margin-right: 10px"> ${uiLabelMap.Description}: </div>
					</div>
					<div class="span7">	
						<textarea id="txtDescription" class='text-popup' style="width: 600px; height: 60px"></textarea>
					</div>
				</div>
			</div>
			<div class="span6 no-left-margin">
				<div class="row-fluid margin-bottom10">	
					<div class="span5" style="text-align: right">
						<div class="asterisk"> ${uiLabelMap.DatetimeCreated}: </div>
					</div>
					<div class="span7">	
						<div id="txtCostAccDate"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span5" style="text-align: right">
						<div class="asterisk"> ${uiLabelMap.BLPartyName}: </div>
					</div>
					<div class="span7">	
						<div id="divPartyId" style="width: 100%">
							<div id="jqxgridParty">
				            </div>
						</div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span5" style="text-align: right">
						<div class=""> ${uiLabelMap.UploadFileScan}: </div>
					</div>
					<div class="span7">	
						<input type="file" id="txtPathScanFile" accept="image/*" style="width: 218px"/>
					</div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button id="alterCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
			<button id="btnSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
		</div>
	</div>
</div>

<div id="jqxNotification">
	<div id="notificationContent"></div>
</div>

<div id="contextMenu" style="display:none;">
<ul>
	<li id="mitemUpdate"><i class="fa fa-pencil-square-o"></i>&nbsp;&nbsp;${uiLabelMap.CommonUpdate}</li>
</ul>
</div>