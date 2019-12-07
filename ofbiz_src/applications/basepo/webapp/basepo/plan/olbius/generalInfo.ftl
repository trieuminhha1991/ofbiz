<form class="form-horizontal form-window-content-custom" id="initPurchaseInfo" name="initPurchaseInfo">
	<div class="row-fluid">
		<div class="span12">
			<div class="row-fluid">
				<div class="span6">
					<div class="row-fluid margin-top5">
						<div class="span5">
							<label class="asterisk">${uiLabelMap.Supplier}</label></span>
						</div>
						<div class="span7">
							<div id="supplierDropdown" name="supplierDropdown">
								<div id="supplierListGrid"></div>
							</div>
						</div>
					</div>
					<div class="row-fluid margin-top5">
						<div class="span5"><label class="align-right">${uiLabelMap.SettingPurchaseOrder}&nbsp;&nbsp;&nbsp;</label></div>
						<div class="span7" id="poNum">
							<div style="float: left;  margin-top: 6px;" id="jqxRadioButtonOnePO">${uiLabelMap.OnePO}</div>
							<div style="float: left;  margin-top: 6px;" id="jqxRadioButtonMultiPO">${uiLabelMap.MultiPO}</div>
						</div>
					</div>
					<div class="row-fluid margin-top5">
						<div class="span5"><label id="labelFacility" class="asterisk">${uiLabelMap.ReceiveToFacility}</label></div>
						<div class="span7">
							<div id="jqxDropDownFacility">
							</div>
						</div>
					</div>
				</div>
				<div class="span6">
					<div class="row-fluid margin-top5">
						<div class="span5"><label class="asterisk">${uiLabelMap.ShipAfterDate}</label></div>
						<div class="span7">
							<div id="shipAfterDate">
							</div>
						</div>
					</div>
					<div class="row-fluid margin-top5">
						<div class="span5"><label class="asterisk">${uiLabelMap.ShipBeforeDate}</label></div>
						<div class="span7">
							<div id="shipBeforeDate">
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</form>